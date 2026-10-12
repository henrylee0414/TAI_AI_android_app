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

    fun startRepair(itemId: String) {
        try {
            repository.startRepair(itemId) //Repository檢查規則並保存變更
            order = repository.getOrder() //更新畫面正在觀察的資料
            errorMessage = null //成功後清除錯誤訊息
        } catch (error: IllegalArgumentException) { //操作被規則拒絕時，畫面顯示原因
            errorMessage = error.message ?: "無法開始施工"
        }
    }

    fun completeRepair(itemId: String) {
        try {
            repository.completeRepair(itemId)
            order = repository.getOrder()
            errorMessage = null
        } catch (error: IllegalArgumentException) {
            errorMessage = error.message ?: "無法完成施工"
        }
    }

    fun startFinalCheck() {
        try {
            order = repository.startFinalCheck()
            errorMessage = null
        } catch (error: IllegalArgumentException) {
            errorMessage = error.message ?: "無法進入完工確認"
        }
    }

    fun markReadyForPickup() {
        try {
            order = repository.markReadyForPickup()
            errorMessage = null
        } catch (error: IllegalArgumentException) {
            errorMessage = error.message ?: "無法標記為可取車"
        }
    }

    fun markDelivered() {
        try {
            order = repository.markDelivered()
            errorMessage = null
        } catch (error: IllegalArgumentException) {
            errorMessage = error.message ?: "無法標記為已交車"
        }
    }

    fun reset() {
        repository.reset()
        order = repository.getOrder()
        errorMessage = null
    }
}