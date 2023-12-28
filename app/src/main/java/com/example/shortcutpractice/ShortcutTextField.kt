import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shortcutpractice.ShortcutViewModel

@Composable
fun ShortcutTextField(
    viewModel: ShortcutViewModel
) {
    val userInput by viewModel.userInput.observeAsState(mutableListOf<String>())
    val currentShortcut by viewModel.currentShortcut.collectAsState()

    // 处理用户输入，移除空白字符和 "+"
    val processedInput : String = userInput.joinToString( "") { it.filterNot { it.isWhitespace() || it == '+' } }

    // 检查用户输入是否为占位符的前缀
    val isPrefix = currentShortcut?.keyCombo?.startsWith(processedInput) ?: false

    // 检查用户输入是否与占位符完全匹配
    val isCompleteMatch = processedInput == currentShortcut?.keyCombo?.filterNot { it.isWhitespace() || it == '+' }

    // 设置文本颜色
    val textColor = when {
        isCompleteMatch -> Color.Green // 完全匹配时为绿色
        isPrefix -> Color.Green        // 前缀匹配时为绿色
        else -> Color.Red              // 不匹配时为红色
    }

    // 当完全匹配时触发 ViewModel 中的方法
    LaunchedEffect(isCompleteMatch) {
        if (isCompleteMatch) {
            viewModel.onCorrectInputShortcut() // 假定这是 ViewModel 中的方法
        }
    }

    // 使用 TextField 显示处理过的输入
    TextField(
        value = userInput.joinToString(""),
        onValueChange = {}, // TextField 是只读的，不处理值更改
        readOnly = true, // 设置为只读
        textStyle = androidx.compose.ui.text.TextStyle(color = textColor, fontSize = 18.sp),
        modifier = Modifier
            .border(1.dp, Color.Gray)
            .padding(1.dp)
            .fillMaxWidth(),
        singleLine = true // 如果希望文本在一行内显示
    )
}
