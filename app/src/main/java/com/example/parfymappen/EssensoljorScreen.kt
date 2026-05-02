package com.example.parfymappen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp







@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EssensoljorScreen(
    onBackClick: () -> Unit,
    topnotOils: SnapshotStateList<String>,
    mellannotOils: SnapshotStateList<String>,
    basnotOils: SnapshotStateList<String>,
    ovrigtOils: SnapshotStateList<String>
) {
    val commonShape = RoundedCornerShape(12.dp)
    val buttonBlue = Color(0xFFB6DFFF)
    val inputTextStyle = TextStyle(fontSize = 28.sp, color = Color.Black)
    val labelTextStyle = TextStyle(fontSize = 26.sp, color = Color.Black)

    var showAddDialog by remember { mutableStateOf(false) }
    var newOilName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("Topnoter", "Mellannoter", "Basnoter", "Övrigt")
    val selectedCategoriesForAdd = remember { mutableStateMapOf<String, Boolean>() }

    val intensities = listOf("Låg", "Mellan", "Hög")
    var selectedIntensity by remember { mutableStateOf<String?>(null) }
    var intensityExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Black),
                shape = commonShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Bakåt",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Text(
            text = "ETERISKA OLJOR",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp),
            fontWeight = FontWeight.Black,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 24.dp),
            placeholder = {
                Text("Sök olja...", color = Color.Gray, fontSize = 26.sp)
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            },
            singleLine = true,
            shape = commonShape,
            textStyle = inputTextStyle,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black
            )
        )

        if (searchQuery.isNotEmpty()) {
            val allOilsMapped =
                topnotOils.map { it to "T" } +
                        mellannotOils.map { it to "M" } +
                        basnotOils.map { it to "B" } +
                        ovrigtOils.map { it to "Ö" }

            val groupedOils = allOilsMapped.groupBy({ it.first }, { it.second })

            val matches = groupedOils.keys.filter {
                val nameOnly = it.split(" | ")[0]
                nameOnly.contains(searchQuery, ignoreCase = true)
            }

            if (matches.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .padding(bottom = 24.dp)
                        .border(1.dp, Color.Black, commonShape),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF6FF)),
                    shape = commonShape
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .heightIn(max = 200.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        matches.forEach { oilText ->
                            val cats = groupedOils[oilText] ?: emptyList()
                            val letters = cats.distinct().joinToString(" ")

                            val parts = oilText.split(" | ")
                            val name = parts[0]
                            val intensity = if (parts.size > 1) parts[1] else "Mellan"

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(name, fontSize = 24.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    letters,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IntensityDrops(intensity)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                selectedCategoriesForAdd.clear()
                selectedIntensity = null
                newOilName = ""
                showAddDialog = true
            },
            modifier = Modifier
                .width(110.dp)
                .height(50.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF81C784),
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text("+ Olja", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        CategoryDropdown(
            title = "Topnoter",
            oils = topnotOils,
            commonShape = commonShape
        )

        CategoryDropdown(
            title = "Mellannoter",
            oils = mellannotOils,
            commonShape = commonShape
        )

        CategoryDropdown(
            title = "Basnoter",
            oils = basnotOils,
            commonShape = commonShape
        )

        CategoryDropdown(
            title = "Övrigt",
            oils = ovrigtOils,
            commonShape = commonShape
        )
    }
    if (showAddDialog) {
        AddOilDialog(
            onDismiss = { showAddDialog = false },
            topnotOils = topnotOils,
            mellannotOils = mellannotOils,
            basnotOils = basnotOils,
            ovrigtOils = ovrigtOils,
            commonShape = commonShape,
            buttonBlue = buttonBlue,
            inputTextStyle = inputTextStyle,
            labelTextStyle = labelTextStyle
        )
    }
}

