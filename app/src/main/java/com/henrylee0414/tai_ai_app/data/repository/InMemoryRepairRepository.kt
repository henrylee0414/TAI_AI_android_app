package com.henrylee0414.tai_ai_app.data.repository

import com.henrylee0414.tai_ai_app.data.mock.MockRepairData
import com.henrylee0414.tai_ai_app.data.model.RepairItem
import com.henrylee0414.tai_ai_app.data.model.RepairOrder
import com.henrylee0414.tai_ai_app.domain.RepairRules
import com.henrylee0414.tai_ai_app.domain.RepairOrderRules

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

    override fun startRepair(itemId: String): List<RepairItem> {
        val item = order.items.find { it.id == itemId }
        requireNotNull(item) {
            "找不到維修項目：$itemId"
        }

        val updatedItem = RepairRules.startRepair(item)

        order = order.copy(
            items = order.items.map { current ->
                if (current.id == itemId) updatedItem else current
            }
        )

        return order.items
    }

    override fun completeRepair(itemId: String): List<RepairItem> {
        val item = order.items.find { it.id == itemId }
        requireNotNull(item) {
            "找不到維修項目：$itemId"
        }

        val updatedItem = RepairRules.completeRepair(item)

        order = order.copy(
            items = order.items.map { current ->
                if (current.id == itemId) updatedItem else current
            }
        )

        return order.items
    }

    override fun startFinalCheck(): RepairOrder {
        order = RepairOrderRules.startFinalCheck(order)
        return order
    }

    override fun markReadyForPickup(): RepairOrder {
        order = RepairOrderRules.markReadyForPickup(order)
        return order
    }

    override fun markDelivered(): RepairOrder {
        order = RepairOrderRules.markDelivered(order)
        return order
    }

    fun reset(): List<RepairItem> {
        order = MockRepairData.initialOrder
        return order.items
    }
}