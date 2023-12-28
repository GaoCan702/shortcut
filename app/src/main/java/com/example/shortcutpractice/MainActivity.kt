package com.example.shortcutpractice

import Keyboard
import ShortcutTextField
import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shortcutpractice.ui.theme.ShortcutPracticeTheme

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


    Scaffold(topBar = {

        TopAppBar(title = {
            TextField(
                value = searchStr,
                onValueChange = { viewModel.updateSearchStr(it) },
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
        }, actions = {
            Row {
                OsRadioGroup(selectedOs = currentKeboardType ?: KeyboardType.WIN,
                    onOsSelected = { os -> viewModel.changeKeyboardType(os) })
            }
        }, colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
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
                        onClick = { viewModel.changeCurrentGroup(groupList[groupIndex])},
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
                    Text(currentShortcut?.desc ?: "", style = MaterialTheme.typography.titleMedium)

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
                            Text(historyEntry.keyCombo, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }


                }
            }


        }
    }
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
fun OsRadioGroup(selectedOs: KeyboardType, onOsSelected: (KeyboardType) -> Unit) {
    Row {
        KeyboardType.values().forEach { os ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = os == selectedOs, onClick = { onOsSelected(os) })
                Text(text = os.name, modifier = Modifier.clickable { onOsSelected(os) })
            }
        }
    }
}