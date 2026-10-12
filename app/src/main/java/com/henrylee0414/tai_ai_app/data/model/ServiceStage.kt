package com.henrylee0414.tai_ai_app.data.model

enum class ServiceStage {
    RECEIVED, //已接待
    INSPECTION_AND_MAINTENANCE, //檢查與保養中
    FINAL_CHECK, //完工確認中
    READY_FOR_PICKUP, //可交車
    DELIVERED //已交車
}