package com.henrylee0414.tai_ai_app.data.repository

import com.henrylee0414.tai_ai_app.data.model.RepairItem

interface RepairRepository {
    fun getItems(): List<RepairItem> //取得目前維修進度，回傳維修清單

    fun approve(itemId: String): List<RepairItem> //同意指定項目，回傳更新後的清單

    fun defer(itemId: String): List<RepairItem> //暫緩指定項目，回傳更新後的清單
}