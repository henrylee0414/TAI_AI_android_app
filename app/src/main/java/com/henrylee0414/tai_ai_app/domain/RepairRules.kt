package com.henrylee0414.tai_ai_app.domain

import com.henrylee0414.tai_ai_app.data.model.ApprovalStatus
import com.henrylee0414.tai_ai_app.data.model.RepairItem
import com.henrylee0414.tai_ai_app.data.model.RepairStatus

object RepairRules {

    fun canDecide(item: RepairItem): Boolean { //檢查能否做決定
        return item.isAdditional &&
                item.approvalStatus == ApprovalStatus.AWAITING_APPROVAL &&
                item.status == RepairStatus.PENDING
    }

    fun approve(item: RepairItem): RepairItem { //產生已同意的資料
        require(canDecide(item)) {
            "只有等待確認且尚未施工的追加項目可以同意"
        }

        return item.copy(
            approvalStatus = ApprovalStatus.APPROVED
        )
    }

    fun defer(item: RepairItem): RepairItem { //產生已暫緩的資料
        require(canDecide(item)) {
            "只有等待確認且尚未施工的追加項目可以暫緩"
        }

        return item.copy(
            approvalStatus = ApprovalStatus.DEFERRED
        )
    }

    fun startRepair(item: RepairItem): RepairItem {
        require(item.approvalStatus == ApprovalStatus.APPROVED) {
            "只有已同意的項目可以開始施工"
        }

        require(item.status == RepairStatus.PENDING) {
            "只有待維修的項目可以開始施工"
        }

        return item.copy(
            status = RepairStatus.IN_PROGRESS
        )
    }

    fun completeRepair(item: RepairItem): RepairItem {
        require(item.approvalStatus == ApprovalStatus.APPROVED) {
            "只有已同意的項目可以完成施工"
        }

        require(item.status == RepairStatus.IN_PROGRESS) {
            "只有施工中的項目可以標記完工"
        }

        return item.copy(
            status = RepairStatus.COMPLETED
        )
    }
}