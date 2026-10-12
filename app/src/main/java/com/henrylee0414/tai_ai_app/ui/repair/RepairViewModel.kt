package com.henrylee0414.tai_ai_app.ui.repair

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.henrylee0414.tai_ai_app.data.model.RepairItem
import com.henrylee0414.tai_ai_app.data.repository.InMemoryRepairRepository
import com.henrylee0414.tai_ai_app.data.model.RepairOrder

class RepairViewModel : ViewModel() {

    private val repository = InMemoryRepairRepository()

    var order: RepairOrder by mutableStateOf<RepairOrder>(
        repository.getOrder()
    )
        private set

    val items: List<RepairItem>
        get() = order.items

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun approve(itemId: String) {
        try {
            repository.approve(itemId)
            order = repository.getOrder()
            errorMessage = null
        } catch (error: IllegalArgumentException) {
            errorMessage = error.message ?: "無法同意此項目"
        }
    }

    fun defer(itemId: String) {
        try {
            repository.defer(itemId)
            order = repository.getOrder()
            errorMessage = null
        } catch (error: IllegalArgumentException) {
            errorMessage = error.message ?: "無法暫緩此項目"
        }
    }

    fun reset() {
        repository.reset()
        order = repository.getOrder()
        errorMessage = null
    }
}