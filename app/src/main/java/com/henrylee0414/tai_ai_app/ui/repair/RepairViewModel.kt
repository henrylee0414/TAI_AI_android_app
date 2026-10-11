package com.henrylee0414.tai_ai_app.ui.repair

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.henrylee0414.tai_ai_app.data.mock.MockRepairData
import com.henrylee0414.tai_ai_app.domain.RepairRules

class RepairViewModel : ViewModel() {

    var items by mutableStateOf(MockRepairData.initialItems) //目前畫面使用的維修清單
        private set //只有here可以重新指定items

    fun approve(itemId: String) { //找到指定項目，符合規則才同意
        items = items.map { item ->
            if (item.id == itemId && RepairRules.canDecide(item)) {
                RepairRules.approve(item)
            } else {
                item
            }
        }
    }

    fun defer(itemId: String) { //找到指定項目，符合規則才暫緩
        items = items.map { item ->
            if (item.id == itemId && RepairRules.canDecide(item)) {
                RepairRules.defer(item)
            } else {
                item
            }
        }
    }

    fun reset() { //恢復初始模擬資料
        items = MockRepairData.initialItems
    }
}