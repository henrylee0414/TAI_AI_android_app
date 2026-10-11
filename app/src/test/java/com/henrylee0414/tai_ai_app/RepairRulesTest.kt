package com.henrylee0414.tai_ai_app

import com.henrylee0414.tai_ai_app.data.model.ApprovalStatus
import com.henrylee0414.tai_ai_app.data.model.RepairItem
import com.henrylee0414.tai_ai_app.data.model.RepairStatus
import com.henrylee0414.tai_ai_app.domain.RepairRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RepairRulesTest {

    private fun pendingAdditionalItem(): RepairItem {
        return RepairItem(
            id = "test_battery",
            name = "測試用電瓶更換",
            status = RepairStatus.PENDING,
            approvalStatus = ApprovalStatus.AWAITING_APPROVAL,
            isAdditional = true,
            inspectionFinding = "測試用檢查結果"
        )
    }

    @Test //同意追加
    fun approve_keepsRepairPending() {
        val original = pendingAdditionalItem()

        val result = RepairRules.approve(original)

        assertEquals(
            ApprovalStatus.APPROVED,
            result.approvalStatus
        )
        assertEquals(
            RepairStatus.PENDING,
            result.status
        )
        assertEquals(
            ApprovalStatus.AWAITING_APPROVAL,
            original.approvalStatus
        )
    }

    @Test //暫緩追加
    fun defer_keepsRepairPending() {
        val result = RepairRules.defer(pendingAdditionalItem())

        assertEquals(
            ApprovalStatus.DEFERRED,
            result.approvalStatus
        )
        assertEquals(
            RepairStatus.PENDING,
            result.status
        )
        assertFalse(RepairRules.canDecide(result))
    }

    @Test(expected = IllegalArgumentException::class) //再次同意已同意
    fun approve_rejectsAlreadyApprovedItem() {
        val item = pendingAdditionalItem().copy(
            approvalStatus = ApprovalStatus.APPROVED
        )

        RepairRules.approve(item)
    }

    @Test(expected = IllegalArgumentException::class) //暫緩已施工項目
    fun defer_rejectsItemAlreadyInProgress() {
        val item = pendingAdditionalItem().copy(
            approvalStatus = ApprovalStatus.APPROVED,
            status = RepairStatus.IN_PROGRESS
        )

        RepairRules.defer(item)
    }

    @Test(expected = IllegalArgumentException::class) //對非追加項目執行同意
    fun approve_rejectsNonAdditionalItem() {
        val item = pendingAdditionalItem().copy(
            isAdditional = false
        )

        RepairRules.approve(item)
    }

    @Test
    fun approvedItem_canStartAndComplete() {
        val approved = RepairRules.approve(pendingAdditionalItem())

        val started = RepairRules.startRepair(approved)

        assertEquals(RepairStatus.IN_PROGRESS, started.status)
        assertEquals(ApprovalStatus.APPROVED, started.approvalStatus)

        val completed = RepairRules.completeRepair(started)

        assertEquals(RepairStatus.COMPLETED, completed.status)
        assertEquals(ApprovalStatus.APPROVED, completed.approvalStatus)
    }

    @Test(expected = IllegalArgumentException::class)
    fun startRepair_rejectsAwaitingApproval() {
        RepairRules.startRepair(pendingAdditionalItem())
    }

    @Test(expected = IllegalArgumentException::class)
    fun startRepair_rejectsDeferredItem() {
        val deferred = RepairRules.defer(pendingAdditionalItem())

        RepairRules.startRepair(deferred)
    }

    @Test(expected = IllegalArgumentException::class)
    fun completeRepair_rejectsPendingItem() {
        val approved = RepairRules.approve(pendingAdditionalItem())

        RepairRules.completeRepair(approved)
    }

    @Test(expected = IllegalArgumentException::class)
    fun startRepair_rejectsCompletedItem() {
        val completed = pendingAdditionalItem().copy(
            approvalStatus = ApprovalStatus.APPROVED,
            status = RepairStatus.COMPLETED
        )

        RepairRules.startRepair(completed)
    }
}