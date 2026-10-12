package com.henrylee0414.tai_ai_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.henrylee0414.tai_ai_app.ui.theme.TAI_AI_APPTheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp
import com.henrylee0414.tai_ai_app.domain.RepairRules

//for RepairViewModel
import androidx.lifecycle.ViewModelProvider
import com.henrylee0414.tai_ai_app.data.model.RepairItem
import com.henrylee0414.tai_ai_app.ui.repair.RepairViewModel

//for MockRepairData
import com.henrylee0414.tai_ai_app.data.mock.MockRepairData
import com.henrylee0414.tai_ai_app.data.model.ApprovalStatus

import com.henrylee0414.tai_ai_app.data.model.RepairOrder

import com.henrylee0414.tai_ai_app.data.model.RepairStatus
import com.henrylee0414.tai_ai_app.domain.RepairOrderRules
import com.henrylee0414.tai_ai_app.data.model.ServiceStage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repairViewModel = ViewModelProvider(this)[RepairViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            TAI_AI_APPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "henrylee0414",
                        order = repairViewModel.order,
                        modifier = Modifier.padding(innerPadding),
                        items = repairViewModel.items,
                        errorMessage = repairViewModel.errorMessage,
                        onApprove = { itemId -> repairViewModel.approve(itemId) },
                        onDefer = { itemId -> repairViewModel.defer(itemId) },
                        onReset = { repairViewModel.reset() },
                        onStartRepair = { repairViewModel.startRepair(it) },
                        onCompleteRepair = { repairViewModel.completeRepair(it) },
                        onStartFinalCheck = { repairViewModel.startFinalCheck() },
                        onMarkReadyForPickup = { repairViewModel.markReadyForPickup() },
                        onMarkDelivered = { repairViewModel.markDelivered() }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    order: RepairOrder = MockRepairData.initialOrder,
    modifier: Modifier = Modifier,
    items: List<RepairItem> = MockRepairData.initialItems,
    errorMessage: String? = null,
    onApprove: (String) -> Unit = {},
    onDefer: (String) -> Unit = {},
    onReset: () -> Unit = {},
    onStartRepair: (String) -> Unit = {},
    onCompleteRepair: (String) -> Unit = {},
    onStartFinalCheck: () -> Unit = {},
    onMarkReadyForPickup: () -> Unit = {},
    onMarkDelivered: () -> Unit = {}
) {

    val approvedCount = items.count {
        it.approvalStatus == ApprovalStatus.APPROVED
    }

    val awaitingCount = items.count {
        it.approvalStatus == ApprovalStatus.AWAITING_APPROVAL
    }

    val approvedItems = items.filter { //filter挑出符合條件的項目
        it.approvalStatus == ApprovalStatus.APPROVED
    }

    val awaitingItems = items.filter {
        it.approvalStatus == ApprovalStatus.AWAITING_APPROVAL
    }

    val deferredItems = items.filter {
        it.approvalStatus == ApprovalStatus.DEFERRED
    }

    val approvedAmount = approvedItems.sumOf { //挑出項目的金額加總
        it.quote?.totalAmountTWD ?: 0L
    }

    val awaitingAmount = awaitingItems.sumOf {
        it.quote?.totalAmountTWD ?: 0L
    }

    val deferredAmount = deferredItems.sumOf {
        it.quote?.totalAmountTWD ?: 0L
    }

    val missingQuoteCount = items.count { //沒有報價項目不貢獻金額
        it.quote == null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Hello $name!")
        Text(
            text = """
                維修單：${order.id}
                車輛：${order.vehicleDisplayName}
                服務階段：${order.serviceStage}
            """.trimIndent()
        )
        Text("【開發測試：追加維修互動】")
        if (errorMessage != null) {
            Text(text = "操作未完成：$errorMessage")
        }
        Text(
            "共 ${items.size} 個項目\n" +
                    "已同意：$approvedCount 個\n" +
                    "待確認：$awaitingCount 個"
        )
        Text(
            text = """
                【模擬費用摘要・TWD】
                已同意項目已提供報價合計：$approvedAmount 元
                待確認項目已提供報價合計：$awaitingAmount 元
                暫緩項目已提供報價合計：$deferredAmount 元
            """.trimIndent()
        )
        Text("開發用模擬控制：維修單")

        Button(
            onClick = onStartFinalCheck,
            enabled = RepairOrderRules.canStartFinalCheck(order)
        ) {
            Text("模擬進入完工確認")
        }

        when (order.serviceStage) {
            ServiceStage.FINAL_CHECK -> {
                Button(
                    onClick = onMarkReadyForPickup
                ) {
                    Text("模擬最終檢查通過，可取車")
                }
            }

            ServiceStage.READY_FOR_PICKUP -> {
                Button(
                    onClick = onMarkDelivered
                ) {
                    Text("模擬顧客取車，已交車")
                }
            }

            ServiceStage.DELIVERED -> {
                Text("此維修單已交車")
            }

            else -> Unit
        }

        if (missingQuoteCount > 0) {
            Text(
                text = "有 $missingQuoteCount 個項目尚未提供報價，" + "以上僅加總已知金額，並非完整總價。"
            )
        }

        Button(
            onClick = {
                onReset()
            }
        ) {
            Text("重設測試情境")
        }

        items.forEach { item ->
            val quote = item.quote

            val quoteText = if (quote == null) {
                "尚未提供報價"
            }
            else {
                """
                    【模擬報價】
                    零件費：${quote.partsAmountTWD}元
                    工資：${quote.laborAmountTWD}元
                    合計：${quote.totalAmountTWD}元(${quote.currency})
                """.trimIndent()
            }
            Text(
                text = """
                    項目：${item.name}
                    施工狀態：${item.status}
                    顧客決定：${item.approvalStatus}
                    追加項目：${if (item.isAdditional) "是" else "否"}
                    檢查發現：${item.inspectionFinding}
                    AI 解說：${item.explanation?.reason ?: "尚未產生"}
                    報價：$quoteText
                """.trimIndent()
            )

            if (RepairRules.canDecide(item)) {
                Button(
                    onClick = {
                        onApprove(item.id)
                    }
                ) {
                    Text("同意：${item.name}")
                }

                Button(
                    onClick = {
                        onDefer(item.id)
                    }
                ) {
                    Text("暫緩：${item.name}")
                }
            }

            if (item.approvalStatus == ApprovalStatus.APPROVED) {
                Text("開發用模擬控制")

                when (item.status) {
                    RepairStatus.PENDING -> {
                        Button(
                            onClick = { onStartRepair(item.id) }
                        ) {
                            Text("模擬開始施工")
                        }
                    }

                    RepairStatus.IN_PROGRESS -> {
                        Button(
                            onClick = { onCompleteRepair(item.id) }
                        ) {
                            Text("模擬施工完成")
                        }
                    }

                    RepairStatus.COMPLETED -> {
                        Text("此項目已施工完成")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TAI_AI_APPTheme {
        Greeting("ALLEN")
    }
}