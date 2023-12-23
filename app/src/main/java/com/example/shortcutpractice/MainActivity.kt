package com.example.shortcutpractice

import Keyboard
import ShortcutTextField
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shortcutpractice.data.appShortcuts
import com.example.shortcutpractice.ui.theme.ShortcutPracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShortcutPracticeTheme {
                ShortcutPractice()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ShortcutPractice(viewModel: ShortcutViewModel) {

    val (currentOs, setCurrentOs) = remember { mutableStateOf(Os.MAC) }

    val appShortcuts by remember { mutableStateOf(appShortcuts) } //留着以后做app端的修改,新增等功能
    var searchQuery by remember { mutableStateOf("") }

    var currentApp: AppShortcut by remember { mutableStateOf(appShortcuts[0]) }
    var currentLesson: String by remember {
        mutableStateOf(appShortcuts[0].shortcuts[0].group)
    }
    var currentShortcut: `Shortcut.kt` by remember {
        mutableStateOf(appShortcuts[0].shortcuts.filter { it.group == currentLesson }[0])
    }

    var shortcutHistory by remember { mutableStateOf(listOf<`Shortcut.kt`>()) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("search supported app") },
                        singleLine = true,
                        maxLines = 1,
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                actions = {
                    Row {
                        OsRadioGroup(selectedOs = currentOs, onOsSelected = setCurrentOs)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                items(appShortcuts) { appShortcut ->
                    SupportedAppChoiceButton(
                        editorName = appShortcut.appName,
                        isSelected = appShortcut == currentApp,
                        onSelected = { currentApp = appShortcut }
                    )
                }
            }


            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp)) // 设置圆角
                    .padding(8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)) // 设置边框和圆角
                    .background(MaterialTheme.colorScheme.surface) // 设置背景色
                    .padding(2.dp) // 设置内边距

            ) {
                // 按钮的内容

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        currentApp.shortcuts.map { item -> item.group }.distinct().toList()
                    ) { group ->
                        Button(
                            onClick = { currentLesson = group },
                            modifier = Modifier
                                .height(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (currentLesson == group) Color.Black else Color.LightGray),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentLesson == group) Color.Black else Color.White
                            ),
                        ) {
                            Text(
                                text = group,
                                fontSize = 16.sp,
                                color = if (currentLesson == group) Color.White else Color.Black
                            )
                        }
                    }
                }
            }


            Surface(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                val progress = shortcutHistory.size / currentLesson.length.toFloat()
                //定义并初始化上面的数字为百分比字符串


                Column(Modifier.padding(2.dp)) {
                    Text("Progress ${ (progress * 100).toInt().toString() + "%" }", style = MaterialTheme.typography.titleMedium)
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = Color.Green
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(currentShortcut.description, style = MaterialTheme.typography.titleMedium)

                    ShortcutTextField(currentShortcut.key.replace("+", "").replace(" ", ""))
                    Keyboard(currentOs)

                    Spacer(Modifier.height(2.dp))
                    Text("Shortcut History", style = MaterialTheme.typography.titleSmall)
                    shortcutHistory.forEach { historyEntry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(historyEntry.key, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }


                }
            }


        }
    }
}

@Composable
fun SupportedAppChoiceButton(
    editorName: String,
    isSelected: Boolean,
    onSelected: (String) -> Unit
) {
    Button(
        onClick = { onSelected(editorName) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(start = 2.dp)
    ) {
        Text(
            text = editorName,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun OsRadioGroup(selectedOs: Os, onOsSelected: (Os) -> Unit) {
    Row {
        Os.values().forEach { os ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = os == selectedOs,
                    onClick = { onOsSelected(os) }
                )
                Text(text = os.name, modifier = Modifier.clickable { onOsSelected(os) })
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ShortcutPracticePreview() {
    ShortcutPracticeTheme {
        ShortcutPractice()
    }
}