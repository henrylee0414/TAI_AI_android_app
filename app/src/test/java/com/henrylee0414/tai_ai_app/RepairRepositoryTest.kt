package com.henrylee0414.tai_ai_app

import com.henrylee0414.tai_ai_app.data.model.ApprovalStatus
import com.henrylee0414.tai_ai_app.data.model.RepairStatus
import com.henrylee0414.tai_ai_app.data.model.ServiceStage
import com.henrylee0414.tai_ai_app.data.repository.InMemoryRepairRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class RepairRepositoryTest {

    @Test
    fun approve_savesChangeAndPreservesOtherItems() {
        val repository = InMemoryRepairRepository()
        val before = repository.getItems()

        val target = before.first {
            it.isAdditional &&
                    it.approvalStatus == ApprovalStatus.AWAITING_APPROVAL
        }

        val returnedItems = repository.approve(target.id)
        val savedItems = repository.getItems()

        assertEquals(returnedItems, savedItems)

        assertEquals(
            target.copy(approvalStatus = ApprovalStatus.APPROVED),
            savedItems.first { it.id == target.id }
        )

        assertEquals(
            before.filter { it.id != target.id },
            savedItems.filter { it.id != target.id }
        )
    }

    @Test
    fun unknownId_doesNotChangeData() {
        val repository = InMemoryRepairRepository()
        val before = repository.getItems()

        try {
            repository.approve("missing_item")
            fail("找不到項目時，應該拒絕操作")
        } catch (error: IllegalArgumentException) {
            assertEquals(before, repository.getItems())
        }
    }

    @Test
    fun repeatedDecision_doesNotOverwritePreviousDecision() {
        val repository = InMemoryRepairRepository()

        val target = repository.getItems().first {
            it.isAdditional &&
                    it.approvalStatus == ApprovalStatus.AWAITING_APPROVAL
        }

        repository.approve(target.id)
        val beforeFailure = repository.getItems()

        try {
            repository.defer(target.id)
            fail("已同意的項目，不應直接改成暫緩")
        } catch (error: IllegalArgumentException) {
            assertEquals(beforeFailure, repository.getItems())
        }
    }

    @Test
    fun reset_restoresInitialData() {
        val repository = InMemoryRepairRepository()
        val initialItems = repository.getItems()

        val target = initialItems.first {
            it.isAdditional &&
                    it.approvalStatus == ApprovalStatus.AWAITING_APPROVAL
        }

        repository.defer(target.id)
        val resetItems = repository.reset()

        assertEquals(initialItems, resetItems)
        assertEquals(initialItems, repository.getItems())
    }

    @Test
    fun completedRepairs_saveOrderAndEnterFinalCheck() {
        val repository = InMemoryRepairRepository()

        // 煞車片原本已在施工，直接標記完成。
        repository.completeRepair("repair_001")

        // 電瓶需要先取得同意，才能開始並完成施工。
        repository.approve("repair_002")
        repository.startRepair("repair_002")

        assertEquals(
            RepairStatus.IN_PROGRESS,
            repository.getItems().first { it.id == "repair_002" }.status
        )

        repository.completeRepair("repair_002")

        // 確認兩個項目的完工狀態已存入 Repository。
        val items = repository.getItems()
        assertEquals(2, items.size)
        items.forEach { item ->
            assertEquals(RepairStatus.COMPLETED, item.status)
        }

        val result = repository.startFinalCheck()

        assertEquals(ServiceStage.FINAL_CHECK, result.serviceStage)
        assertEquals(result, repository.getOrder())
    }

    @Test
    fun unfinishedOrder_cannotEnterFinalCheckAndKeepsOriginalData() {
        val repository = InMemoryRepairRepository()
        val originalOrder = repository.getOrder()

        try {
            repository.startFinalCheck()
            fail("尚未完成施工，不應允許進入完工確認")
        } catch (error: IllegalArgumentException) {
            assertEquals(originalOrder, repository.getOrder())
        }
    }
}