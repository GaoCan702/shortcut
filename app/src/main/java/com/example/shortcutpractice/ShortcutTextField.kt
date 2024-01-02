import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shortcutpractice.ShortcutViewModel
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShortcutTextField(
    viewModel: ShortcutViewModel
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val userInput by viewModel.userInput.collectAsState(mutableListOf<String>())
    val currentShortcut by viewModel.currentShortcut.collectAsState()
    var correctInput by remember { mutableStateOf(listOf<String>()) }
    var errorInput by remember { mutableStateOf(listOf<String>()) }
    var isCorrect by remember { mutableStateOf(false) }


    val targetKeyCombo = currentShortcut
        ?.keyCombo
        ?.replace("\\s".toRegex(), "")
        ?.split("+")
        ?: listOf()


    LaunchedEffect(userInput) {
        userInput.lastOrNull()?.let { lastInput ->
            if (lastInput.trim().equals(targetKeyCombo[correctInput.size].trim(), ignoreCase = true)) {
                correctInput =  correctInput + lastInput
            } else {
                errorInput = errorInput + lastInput
            }
            if (correctInput.size == targetKeyCombo.size) {
                isCorrect = true
                isCorrect = true
            }
        }
    }

    LaunchedEffect(isCorrect) {
        if (isCorrect) {
            viewModel.nextShortcut()
            correctInput = listOf()
            errorInput = listOf()
            isCorrect = false
        }
    }


    OutlinedTextField(
        value = "",
        onValueChange = {
            keyboardController?.hide()
        },
        //展示输入正确的部分
        prefix = {
            Text(
                text = correctInput.joinToString("+"),
                color = Color.Green,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
        },
        //展示输入错误的部分
        suffix = {
            Text(
                text = errorInput.lastOrNull() ?: "",
                color = Color.Red,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
        },
        label = { Text(currentShortcut?.keyCombo ?: "") },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                viewModel.setKeyboardEnable(it.isFocused)
            }

    )
}
