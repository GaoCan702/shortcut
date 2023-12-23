import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.shortcutpractice.EventBus
import com.example.shortcutpractice.Events
import kotlinx.coroutines.launch

@Composable
fun ShortcutTextField(
    expectedShortcut: String
) {

    var userInput by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    DisposableEffect(Unit) {
        val listener: (Events.KeyPress) -> Unit = { event ->
            scope.launch {
                userInput = event.message
            }
        }

        EventBus.subscribe(Events.KeyPress::class.java, listener)

        onDispose {
            EventBus.unsubscribe(Events.KeyPress::class.java, listener)
        }
    }


    Box(modifier = Modifier) {
        Text(
            text = buildAnnotatedString {
                val correctInput = userInput.take(expectedShortcut.length)
                val remainingExpected = expectedShortcut.drop(userInput.length)

                // 正确输入的部分
                withStyle(style = SpanStyle(color = Color.Green )) {
                    append(correctInput)
                }

                // 错误输入的部分
                if (userInput.length > expectedShortcut.length) {
                    val incorrectInput = userInput.drop(expectedShortcut.length)
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append(incorrectInput)
                    }
                }

                // 剩余的预期快捷键
                withStyle(style = SpanStyle(color = Color.Gray)) {
                    append(remainingExpected)
                }
            },
            fontSize = 18.sp
        )
    }
}
