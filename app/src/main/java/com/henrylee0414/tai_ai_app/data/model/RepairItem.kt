package com.henrylee0414.tai_ai_app.data.model

data class RepairItem(
    val id: String, //項目代碼
    val name: String, //顯示名稱
    val status: RepairStatus, //維修狀態
    val approvalStatus: ApprovalStatus, //審核狀態
    val isAdditional: Boolean, //是否為追加項目
    val inspectionFinding: String,
    val explanation: RepairExplanation? = null
)