package com.henrylee0414.tai_ai_app

import com.henrylee0414.tai_ai_app.data.model.ApprovalStatus
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
}