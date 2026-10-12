package com.henrylee0414.tai_ai_app.data.repository

import com.henrylee0414.tai_ai_app.data.mock.MockRepairData
import com.henrylee0414.tai_ai_app.data.model.RepairItem
import com.henrylee0414.tai_ai_app.data.model.RepairOrder
import com.henrylee0414.tai_ai_app.domain.RepairRules

class InMemoryRepairRepository : RepairRepository {

    private var order: RepairOrder = MockRepairData.initialOrder

    override fun getOrder(): RepairOrder {
        return order
    }

    override fun getItems(): List<RepairItem> { //提供介面要求的方法內容
        return order.items
    }

    override fun approve(itemId: String): List<RepairItem> {
        val item = order.items.find { it.id == itemId }
        requireNotNull(item) {
            "找不到維修項目：$itemId"
        }

        val updatedItem = RepairRules.approve(item)

        order = order.copy(
            items = order.items.map { current ->
                if (current.id == itemId) updatedItem else current
            }
        )

        return order.items
    }

    override fun defer(itemId: String): List<RepairItem> {
        val item = order.items.find { it.id == itemId }
        requireNotNull(item) {
            "找不到維修項目：$itemId"
        }

        val updatedItem = RepairRules.defer(item)

        order = order.copy(
            items = order.items.map { current ->
                if (current.id == itemId) updatedItem else current
            }
        )

        return order.items
    }

    fun reset(): List<RepairItem> {
        order = MockRepairData.initialOrder
        return order.items
    }
}