package com.henrylee0414.tai_ai_app.domain

import com.henrylee0414.tai_ai_app.data.model.ApprovalStatus
import com.henrylee0414.tai_ai_app.data.model.RepairOrder
import com.henrylee0414.tai_ai_app.data.model.RepairStatus
import com.henrylee0414.tai_ai_app.data.model.ServiceStage

object RepairOrderRules {

    fun canStartFinalCheck(order: RepairOrder): Boolean {
        val isInService =
            order.serviceStage ==
                    ServiceStage.INSPECTION_AND_MAINTENANCE

        val hasAwaitingApproval = order.items.any {
            it.approvalStatus == ApprovalStatus.AWAITING_APPROVAL
        }

        val approvedItems = order.items.filter {
            it.approvalStatus == ApprovalStatus.APPROVED
        }

        val allApprovedCompleted = approvedItems.all {
            it.status == RepairStatus.COMPLETED
        }

        val deferredItemsNotStarted = order.items
            .filter {
                it.approvalStatus == ApprovalStatus.DEFERRED
            }
            .all {
                it.status == RepairStatus.PENDING
            }

        return isInService && //目前在檢查保養階段
                !hasAwaitingApproval && //沒有尚未回覆的項目
                approvedItems.isNotEmpty() && //至少有一個已同意的項目
                allApprovedCompleted && //已同意的項目全部完工
                deferredItemsNotStarted //暫緩項目未施工
    }

    fun startFinalCheck(order: RepairOrder): RepairOrder {
        require(canStartFinalCheck(order)) {
            "需在檢查與保養階段，完成所有已同意項目、處理待確認事項，且暫緩項目不得已施工"
        }

        return order.copy(
            serviceStage = ServiceStage.FINAL_CHECK
        )
    }

    fun markReadyForPickup(order: RepairOrder): RepairOrder {
        require(order.serviceStage == ServiceStage.FINAL_CHECK) {
            "只有正在完工確認的維修單，才能標記為可取車"
        }

        return order.copy(
            serviceStage = ServiceStage.READY_FOR_PICKUP
        )
    }

    fun markDelivered(order: RepairOrder): RepairOrder {
        require(order.serviceStage == ServiceStage.READY_FOR_PICKUP) {
            "只有已可取車的維修單，才能標記為已交車"
        }

        return order.copy(
            serviceStage = ServiceStage.DELIVERED
        )
    }
}