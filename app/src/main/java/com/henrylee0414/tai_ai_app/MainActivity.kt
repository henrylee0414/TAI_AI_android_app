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
    val items = MockRepairData.initialItems

    val approvedCount = items.count {
        it.approvalStatus == ApprovalStatus.APPROVED
    }

    val awaitingCount = items.count {
        it.approvalStatus == ApprovalStatus.AWAITING_APPROVAL
    }

    val details = items.joinToString(separator = "\n\n") { item ->
        """
            項目: ${item.name}
            施工狀態: ${item.status}
            顧客決定: ${item.approvalStatus}
            追加項目: ${if (item.isAdditional) "是" else "否"}
            檢查發現: ${item.inspectionFinding}
            AI 解說: ${item.explanation?.reason ?: "尚未產生"}
        """.trimIndent()
    }
    Text(
        text = """
            Hello $name!
            【開發測試:模擬維修資料】
            共 ${items.size} 個項目
            已同意: $approvedCount 個
            待確認: $awaitingCount 個
        """.trimIndent() + "\n" + details,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TAI_AI_APPTheme {
        Greeting("Android")
    }
}