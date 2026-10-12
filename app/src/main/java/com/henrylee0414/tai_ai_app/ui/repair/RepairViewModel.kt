package com.henrylee0414.tai_ai_app.ui.repair

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.henrylee0414.tai_ai_app.data.repository.InMemoryRepairRepository

class RepairViewModel : ViewModel() {

    private val repository = InMemoryRepairRepository()

    var items by mutableStateOf(repository.getItems())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun approve(itemId: String) {
        try {
            items = repository.approve(itemId)
            errorMessage = null
        } catch (error: IllegalArgumentException) {
            errorMessage = error.message ?: "無法同意此項目"
        }
    }

    fun defer(itemId: String) {
        try {
            items = repository.defer(itemId)
            errorMessage = null
        } catch (error: IllegalArgumentException) {
            errorMessage = error.message ?: "無法暫緩此項目"
        }
    }

    fun reset() {
        items = repository.reset()
        errorMessage = null
    }
}