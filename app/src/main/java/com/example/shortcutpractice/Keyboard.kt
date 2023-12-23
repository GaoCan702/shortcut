import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.shortcutpractice.EventBus
import com.example.shortcutpractice.Events
import com.example.shortcutpractice.Os


@Composable
fun Keyboard(osType: Os) {

    var userInput by remember { mutableStateOf(mutableListOf<String>()) }

    fun onKeyPress(key: String) {
        if (key == "Backspace") {
            userInput.removeLastOrNull()
        } else {
            userInput.add(key)
        }
        //打印list
        println("userInput: ${userInput.joinToString("")}")
        EventBus.publish(Events.KeyPress(userInput.joinToString("")))
    }

    val keyRows = when(osType) {
        Os.WIN -> listOf(
            listOf("Esc", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12"),
            listOf("~", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "="),
            listOf("Tab", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]"),
            listOf("Caps Lock", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'"),
            listOf("Shift", "Z", "X", "C", "V", "B", "N", "M", ",", ".", "/", "\\"),
            listOf("Ctrl", "Fn", "Win", "Alt", "Space", "Enter", "Backspace")
        )
        Os.MAC -> listOf(
            listOf("Esc", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12"),
            listOf("~", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "="),
            listOf("Tab", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]"),
            listOf("Caps Lock", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'"),
            listOf("⇧", "Z", "X", "C", "V", "B", "N", "M", ",", ".", "/", "\\"),
            listOf("Ctrl", "Fn", "⌘", "⌥", "Space", "Enter", "Backspace")
        )
    }

    LazyRow(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Column(
                    modifier = Modifier
                        .padding(1.dp)
                        .background(Color.DarkGray)
                ) {
                    keyRows.forEach { row ->
                        KeyboardRow(row, ::onKeyPress)
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun KeyboardRow(keys: List<String>, onKeyPress: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        keys.forEach { key ->
            KeyText(key, onKeyPress)
        }
    }
}

@Composable
fun KeyText(key: String, onKeyPress: (String) -> Unit) {

    val width = when (key) {
        "Caps Lock", "Shift", "⇧" -> 75.dp
        "Ctrl", "Fn", "Windows", "Alt", "Menu", "⌘", "⌥" -> 45.dp
        "Space" -> 147.dp
        "Enter" -> 73.dp
        "Backspace" -> 82.dp
        else -> 35.dp
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(width)
            .height(35.dp)
            .background(Color.Black, RoundedCornerShape(4.dp))
            .clickable { onKeyPress(key) }
    ) {
        Text(
            text = key,
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}










