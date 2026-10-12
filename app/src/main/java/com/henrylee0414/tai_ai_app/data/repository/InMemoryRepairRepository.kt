package com.henrylee0414.tai_ai_app.data.repository

import com.henrylee0414.tai_ai_app.data.mock.MockRepairData
import com.henrylee0414.tai_ai_app.data.model.RepairItem
import com.henrylee0414.tai_ai_app.domain.RepairRules

//存記憶體
class InMemoryRepairRepository : RepairRepository {
    private var items: List<RepairItem> = MockRepairData.initialItems

    override fun getItems(): List<RepairItem> { //提供介面要求的方法內容
        return items
    }

    override fun approve(itemId: String): List<RepairItem> {
        val item = items.find { it.id == itemId }
        requireNotNull(item) {
            "找不到維修項目:$itemId"
        }

        val updatedItem = RepairRules.approve(item)

        items = items.map { current ->
            if (current.id == itemId) updatedItem else current
        }

        return items
    }

    override fun defer(itemId: String): List<RepairItem> {
        val item = items.find { it.id == itemId }
        requireNotNull(item) {
            "找不到維修項目：$itemId"
        }

        val updatedItem = RepairRules.defer(item)

        items = items.map { current ->
            if (current.id == itemId) updatedItem else current
        }

        return items
    }

    fun reset(): List<RepairItem> {
        items = MockRepairData.initialItems
        return items
    }
}