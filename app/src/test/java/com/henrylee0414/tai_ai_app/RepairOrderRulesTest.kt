package com.henrylee0414.tai_ai_app

import com.henrylee0414.tai_ai_app.data.mock.MockRepairData
import com.henrylee0414.tai_ai_app.data.model.ApprovalStatus
import com.henrylee0414.tai_ai_app.data.model.RepairOrder
import com.henrylee0414.tai_ai_app.data.model.RepairStatus
import com.henrylee0414.tai_ai_app.data.model.ServiceStage
import com.henrylee0414.tai_ai_app.domain.RepairOrderRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RepairOrderRulesTest {

    private fun completedOrder(): RepairOrder {
        return MockRepairData.initialOrder.copy(
            items = MockRepairData.initialItems.map { item ->
                item.copy(
                    approvalStatus = ApprovalStatus.APPROVED,
                    status = RepairStatus.COMPLETED
                )
            }
        )
    }

    @Test
    fun allCompleted_canEnterFinalCheck() {
        val original = completedOrder()

        val result = RepairOrderRules.startFinalCheck(original)

        assertEquals(ServiceStage.FINAL_CHECK, result.serviceStage)
        assertEquals(original.items, result.items)
        assertEquals(
            ServiceStage.INSPECTION_AND_MAINTENANCE,
            original.serviceStage
        )
    }

    @Test
    fun deferredAdditionalItem_doesNotBlockFinalCheck() {
        val order = completedOrder().let { completed ->
            completed.copy(
                items = completed.items.map { item ->
                    if (item.isAdditional) {
                        item.copy(
                            approvalStatus = ApprovalStatus.DEFERRED,
                            status = RepairStatus.PENDING
                        )
                    } else {
                        item
                    }
                }
            )
        }

        val result = RepairOrderRules.startFinalCheck(order)

        assertEquals(ServiceStage.FINAL_CHECK, result.serviceStage)
        assertEquals(order.items, result.items)
    }

    @Test(expected = IllegalArgumentException::class)
    fun awaitingApproval_blocksFinalCheck() {
        val order = completedOrder().let { completed ->
            completed.copy(
                items = completed.items.map { item ->
                    if (item.isAdditional) {
                        item.copy(
                            approvalStatus = ApprovalStatus.AWAITING_APPROVAL,
                            status = RepairStatus.PENDING
                        )
                    } else {
                        item
                    }
                }
            )
        }

        RepairOrderRules.startFinalCheck(order)
    }

    @Test
    fun unfinishedApprovedItem_blocksFinalCheck() {
        val completed = completedOrder()
        val firstId = completed.items.first().id

        val order = completed.copy(
            items = completed.items.map { item ->
                if (item.id == firstId) {
                    item.copy(status = RepairStatus.IN_PROGRESS)
                } else {
                    item
                }
            }
        )

        assertFalse(RepairOrderRules.canStartFinalCheck(order))
    }

    @Test
    fun emptyOrder_blocksFinalCheck() {
        val order = completedOrder().copy(items = emptyList())

        assertFalse(RepairOrderRules.canStartFinalCheck(order))
    }

    @Test
    fun allDeferredOrder_blocksFinalCheck() {
        val completed = completedOrder()
        val order = completed.copy(
            items = completed.items.map {
                it.copy(
                    approvalStatus = ApprovalStatus.DEFERRED,
                    status = RepairStatus.PENDING
                )
            }
        )

        assertFalse(RepairOrderRules.canStartFinalCheck(order))
    }

    @Test
    fun wrongServiceStage_blocksFinalCheck() {
        val order = completedOrder().copy(
            serviceStage = ServiceStage.RECEIVED
        )

        assertFalse(RepairOrderRules.canStartFinalCheck(order))
    }

    @Test
    fun deferredButStartedItem_blocksFinalCheck() {
        val completed = completedOrder()
        val order = completed.copy(
            items = completed.items.map { item ->
                if (item.isAdditional) {
                    item.copy(
                        approvalStatus = ApprovalStatus.DEFERRED,
                        status = RepairStatus.IN_PROGRESS
                    )
                } else {
                    item
                }
            }
        )

        assertFalse(RepairOrderRules.canStartFinalCheck(order))
    }

    @Test
    fun finalCheckPassed_canBecomeReadyThenDelivered() {
        val finalCheckOrder =
            RepairOrderRules.startFinalCheck(completedOrder())

        val readyOrder =
            RepairOrderRules.markReadyForPickup(finalCheckOrder)

        assertEquals(
            ServiceStage.READY_FOR_PICKUP,
            readyOrder.serviceStage
        )

        val deliveredOrder =
            RepairOrderRules.markDelivered(readyOrder)

        assertEquals(
            ServiceStage.DELIVERED,
            deliveredOrder.serviceStage
        )

        // 階段改變，不應改動維修項目。
        assertEquals(finalCheckOrder.items, deliveredOrder.items)

        // 原本的物件仍維持原階段。
        assertEquals(
            ServiceStage.FINAL_CHECK,
            finalCheckOrder.serviceStage
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannotSkipFinalCheck() {
        RepairOrderRules.markReadyForPickup(completedOrder())
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannotDeliverDuringFinalCheck() {
        val order =
            RepairOrderRules.startFinalCheck(completedOrder())

        RepairOrderRules.markDelivered(order)
    }
}