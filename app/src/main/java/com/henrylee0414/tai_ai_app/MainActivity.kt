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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.henrylee0414.tai_ai_app.domain.RepairRules

import com.henrylee0414.tai_ai_app.data.mock.MockRepairData
import com.henrylee0414.tai_ai_app.data.model.ApprovalStatus

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TAI_AI_APPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "henrylee0414",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var items by remember {
        mutableStateOf(MockRepairData.initialItems) //使compose能觀察變化、更新畫面
    }

    val approvedCount = items.count {
        it.approvalStatus == ApprovalStatus.APPROVED
    }

    val awaitingCount = items.count {
        it.approvalStatus == ApprovalStatus.AWAITING_APPROVAL
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Hello $name!")
        Text("【開發測試：追加維修互動】")
        Text(
            "共 ${items.size} 個項目\n" +
                    "已同意：$approvedCount 個\n" +
                    "待確認：$awaitingCount 個"
        )

        Button(
            onClick = {
                items = MockRepairData.initialItems
            }
        ) {
            Text("重設測試情境")
        }

        items.forEach { item ->
            Text(
                text = """
                    項目：${item.name}
                    施工狀態：${item.status}
                    顧客決定：${item.approvalStatus}
                    追加項目：${if (item.isAdditional) "是" else "否"}
                    檢查發現：${item.inspectionFinding}
                    AI 解說：${item.explanation?.reason ?: "尚未產生"}
                """.trimIndent()
            )

            if (RepairRules.canDecide(item)) {
                Button(
                    onClick = {
                        items = items.map { current ->
                            if (current.id == item.id) {
                                RepairRules.approve(current)
                            } else {
                                current
                            }
                        }
                    }
                ) {
                    Text("同意：${item.name}")
                }

                Button(
                    onClick = {
                        items = items.map { current ->
                            if (current.id == item.id) {
                                RepairRules.defer(current)
                            } else {
                                current
                            }
                        }
                    }
                ) {
                    Text("暫緩：${item.name}")
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