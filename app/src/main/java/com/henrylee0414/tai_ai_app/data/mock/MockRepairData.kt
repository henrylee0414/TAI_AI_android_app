package com.henrylee0414.tai_ai_app.data.mock

import com.henrylee0414.tai_ai_app.data.model.ApprovalStatus
import com.henrylee0414.tai_ai_app.data.model.RepairItem
import com.henrylee0414.tai_ai_app.data.model.RepairStatus

object MockRepairData {

    val initialItems: List<RepairItem> = listOf(
        RepairItem(
            id = "repair_001",
            name = "前輪煞車片更換",
            status = RepairStatus.IN_PROGRESS,
            approvalStatus = ApprovalStatus.APPROVED,
            isAdditional = false,
            inspectionFinding = "【模擬資料】檢查紀錄指出前輪煞車片需更換，已列入本次原定施工。"
        ),

        RepairItem(
            id = "repair_002",
            name = "電瓶更換",
            status = RepairStatus.PENDING,
            approvalStatus = ApprovalStatus.AWAITING_APPROVAL,
            isAdditional = true,
            inspectionFinding = "【模擬資料】電瓶檢測報告建議更換，已提出追加方案，等待顧客確認"
        )
    )
}