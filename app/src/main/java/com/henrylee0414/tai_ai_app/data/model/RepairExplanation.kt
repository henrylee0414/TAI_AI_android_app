package com.henrylee0414.tai_ai_app.data.model

data class RepairExplanation(
    val reason: String, //維修原因
    val functionDescription: String, //零件作用
    val riskIfDeferred: String //暫緩風險
)