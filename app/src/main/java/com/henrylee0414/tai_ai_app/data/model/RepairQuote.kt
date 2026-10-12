package com.henrylee0414.tai_ai_app.data.model

data class RepairQuote(
    val partsAmountTWD: Int,
    val laborAmountTWD: Int
) {
    init {
        require(partsAmountTWD >= 0) {
            "零件費不能是負數"
        }
        require(laborAmountTWD >= 0) {
            "工資不能是負數"
        }
    }

    val currency: String
        get() = "TWD"

    val totalAmountTWD: Long
        get() = partsAmountTWD.toLong() + laborAmountTWD.toLong()
}