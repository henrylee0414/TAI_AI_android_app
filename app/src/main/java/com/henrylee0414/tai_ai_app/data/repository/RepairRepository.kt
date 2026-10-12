package com.henrylee0414.tai_ai_app.data.repository

import com.henrylee0414.tai_ai_app.data.model.RepairItem
import com.henrylee0414.tai_ai_app.data.model.RepairOrder

interface RepairRepository {

    fun getOrder(): RepairOrder //取得維修單
    fun getItems(): List<RepairItem> //取得目前維修進度，回傳維修清單

    fun approve(itemId: String): List<RepairItem> //同意指定項目，回傳更新後的清單

    fun defer(itemId: String): List<RepairItem> //暫緩指定項目，回傳更新後的清單

    fun startRepair(itemId: String): List<RepairItem> //開始修理

    fun completeRepair(itemId: String): List<RepairItem> //完成修理

    fun startFinalCheck(): RepairOrder //開始最後檢查

    fun markReadyForPickup(): RepairOrder //準備取車

    fun markDelivered(): RepairOrder //已交車
}