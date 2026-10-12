package com.henrylee0414.tai_ai_app.data.model

data class RepairOrder(
    val id: String,
    val vehicleDisplayName: String,
    val serviceStage: ServiceStage,
    val items: List<RepairItem>
) {
    init {
        require(id.isNotBlank()) {
            "維修單編號不能為空白"
        }

        require(vehicleDisplayName.isNotBlank()) {
            "車輛名稱不能為空白"
        }

        require(items.map { it.id }.distinct().size == items.size) {
            "維修單內的項目ID不能重複"
        }
    }
}