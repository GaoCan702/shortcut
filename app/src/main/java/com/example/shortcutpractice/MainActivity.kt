package com.example.shortcutpractice

import Keyboard
import ShortcutTextField
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shortcutpractice.ui.theme.ShortcutPracticeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShortcutPracticeTheme {
                ShortcutPractice(ShortcutViewModel())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ShortcutPractice(viewModel: ShortcutViewModel) {


    val currentKeboardType by viewModel.currentKeyboardType.observeAsState()
    val currentShortcut by viewModel.currentShortcut.collectAsState()
    val searchStr by viewModel.searchStr.collectAsState("")
    val recommendAppList by viewModel.currentRecommendAppList.observeAsState(listOf())
    val groupList by viewModel.getGroupNameInAppByCurrentShortcut().collectAsState(listOf())
    val shortcutList by viewModel.getShortcutsInAppByCurrentShortcut().collectAsState(listOf())
    val shortcutHistory by viewModel.shortcutHistory.observeAsState(listOf())


    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SettingsDrawerContent(viewModel)
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            TextField(
                                value = searchStr,
                                onValueChange = { viewModel.addUserInput(it) },
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
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "drawer menu"
                                )
                            }
                        },
                        actions = {},
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                    )
                }) { innerPadding ->
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
                        items(recommendAppList.size) { appIndex ->
                            SupportedAppChoiceButton(editorName = recommendAppList[appIndex],
                                isSelected = recommendAppList[appIndex] == currentShortcut?.appName,
                                onSelected = { viewModel.changeCurrentApp(recommendAppList[appIndex]) })
                        }
                    }
                    // group
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            groupList.size
                        ) { groupIndex ->
                            Button(
                                onClick = { viewModel.changeCurrentGroup(groupList[groupIndex]) },
                                modifier = Modifier
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (groupList[groupIndex] == currentShortcut?.groupName) Color.Black else Color.LightGray),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (groupList[groupIndex] == currentShortcut?.groupName) Color.Black else Color.White
                                ),
                            ) {
                                Text(
                                    text = groupList[groupIndex],
                                    fontSize = 16.sp,
                                    color = if (groupList[groupIndex] == currentShortcut?.groupName) Color.White else Color.Black
                                )
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

                        val groupSize = groupList.size.toFloat()
                        val progress = if (groupSize > 0) shortcutHistory.size / groupSize else 0f
                        //定义并初始化上面的数字为百分比字符串

                        Column(Modifier.padding(2.dp)) {
                            Text(
                                "Group Progress ${(progress * 100).toInt().toString() + "%"}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                color = Color.Green
                            )
                            Spacer(Modifier.height(8.dp))

//                    WheelTimePicker(timeFormat = TimeFormat.AM_PM) { snappedTime -> }

                            Spacer(Modifier.height(8.dp))
                            Text(
                                currentShortcut?.desc ?: "",
                                style = MaterialTheme.typography.titleMedium
                            )

                            ShortcutTextField(viewModel)
                            Keyboard(viewModel)

                            Spacer(Modifier.height(2.dp))
                            Text("Shortcut History", style = MaterialTheme.typography.titleSmall)
                            shortcutHistory.forEach { historyEntry ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        historyEntry.keyCombo,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }


                        }
                    }


                }
            }

            //////////////////
        }
    )


    /////////////////////


}

@Composable
fun SupportedAppChoiceButton(
    editorName: String, isSelected: Boolean, onSelected: (String) -> Unit
) {
    Button(
        onClick = { onSelected(editorName) }, colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
        ), modifier = Modifier.padding(start = 2.dp)
    ) {
        Text(
            text = editorName,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SettingsDrawerContent(viewModel: ShortcutViewModel) {

    var isEnglishSelected by remember { mutableStateOf(true) }
    val currentKeboardType by viewModel.currentKeyboardType.observeAsState()

    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("语言 (English/中文)", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f)) // 这将使文本和开关分布在两端
                Switch(
                    checked = isEnglishSelected,
                    onCheckedChange = { isEnglishSelected = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Keyboard Type Setting
                Text("Keyboard Type", style = MaterialTheme.typography.bodyLarge)
                Row()
                {
                    RadioButton(selected = KeyboardType.WIN == currentKeboardType, onClick = { viewModel.changeKeyboardType(KeyboardType.WIN) })
                    Text("Win", modifier = Modifier.align(Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(1.dp))
                    RadioButton(selected = KeyboardType.MAC== currentKeboardType, onClick = { viewModel.changeKeyboardType(KeyboardType.MAC) })
                    Text("Mac", modifier = Modifier.align(Alignment.CenterVertically))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Mode Selection
            Text("Mode", style = MaterialTheme.typography.bodyLarge)
            val modes = listOf("Learn", "Exam", "Browser")
            modes.forEach { mode ->
                Row {
                    RadioButton(
                        selected = false, // replace with actual state logic
                        onClick = { /* Handle Mode Selection */ }
                    )
                    Text(mode, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Browsing Speed Setting
            var sliderPosition by remember { mutableFloatStateOf(0f) }
            Text("Browsing Speed", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0f..100f
            )
            Text("${sliderPosition.toInt()}", style = MaterialTheme.typography.bodyLarge)
        }
    }

}
