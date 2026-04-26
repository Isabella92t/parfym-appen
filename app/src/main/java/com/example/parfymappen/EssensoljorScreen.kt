package com.example.parfymappen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parfymappen.ui.theme.ParfymAppenTheme

// HJÄLPFUNKTIONER

@Composable
fun IntensityDrops(intensity: String) {
    val count = when (intensity.trim()) {
        "Låg" -> 1
        "Mellan" -> 2
        "Hög" -> 3
        else -> 1
    }
    Row {
        repeat(count) {
            Text(text = "💧", fontSize = 12.sp) // Något större droppar
        }
    }
}

@Composable
fun OilRow(textFromList: String, category: String? = null, isSearch: Boolean = false) {
    val parts = textFromList.split(" | ")
    val name = parts[0]
    val intensity = if (parts.size > 1) parts[1] else "Mellan"
    val textColor = if (isSearch) Color.Gray else Color.Black

    Row(
        verticalAlignment = Alignment.CenterVertically, 
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text("• $name ", style = MaterialTheme.typography.bodyLarge, color = textColor)
        if (category != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(category, fontWeight = FontWeight.Normal, style = MaterialTheme.typography.labelSmall, color = textColor)
        }
        Spacer(modifier = Modifier.weight(1f))
        IntensityDrops(intensity)
    }
}

// HUVUDSIDA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EssensoljorScreen(
    onBackClick: () -> Unit,
    topnotOils: SnapshotStateList<String>,
    mellannotOils: SnapshotStateList<String>,
    basnotOils: SnapshotStateList<String>,
    ovrigtOils: SnapshotStateList<String>
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showListDialog by remember { mutableStateOf(false) }
    var newOilName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Topnoter") }
    
    val categories = listOf("Topnoter", "Mellannoter", "Basnoter", "Övrigt")
    val selectedCategoriesForAdd = remember { mutableStateMapOf<String, Boolean>() }

    val intensities = listOf("Låg", "Mellan", "Hög")
    var selectedIntensity by remember { mutableStateOf<String?>(null) }
    var intensityExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onBackClick, 
                modifier = Modifier.align(Alignment.TopStart),
                colors = ButtonDefaults.buttonColors(contentColor = Color.Black)
            ) { Text("<-") }
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
            modifier = Modifier.width(260.dp).padding(bottom = 24.dp),
            placeholder = { Text("Sök olja...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Black) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        if (searchQuery.isNotEmpty()) {
            val allOils = topnotOils.map { it to "Topnoter" } + mellannotOils.map { it to "Mellannoter" } + basnotOils.map { it to "Basnoter" } + ovrigtOils.map { it to "Övrigt" }
            val matches = allOils.filter { it.first.startsWith(searchQuery, ignoreCase = true) }
            if (matches.isNotEmpty()) {
                Card(
                    modifier = Modifier.width(260.dp).padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF6FF))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        matches.forEach { (text, cat) -> OilRow(text, category = cat, isSearch = true) }
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
            modifier = Modifier.width(180.dp).height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(contentColor = Color.Black)
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("Lägg till ny")
        }

        Spacer(modifier = Modifier.height(40.dp))

        categories.forEach { category ->
            Button(
                onClick = { selectedCategory = category; showListDialog = true }, 
                modifier = Modifier.width(180.dp).padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(contentColor = Color.Black)
            ) {
                Text(text = category, fontSize = 22.sp, fontWeight = FontWeight.Normal)
            }
        }
    }

    if (showListDialog) {
        AlertDialog(
            onDismissRequest = { showListDialog = false },
            containerColor = Color.White,
            title = { Text("Oljor i $selectedCategory", color = Color.Black) },
            text = {
                val list = when(selectedCategory) {
                    "Topnoter" -> topnotOils
                    "Mellannoter" -> mellannotOils
                    "Basnoter" -> basnotOils
                    else -> ovrigtOils
                }
                Column(modifier = Modifier.fillMaxWidth()) { list.forEach { OilRow(it) } }
            },
            confirmButton = { TextButton(onClick = { showListDialog = false }) { Text("Stäng", color = Color.Black) } }
        )
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color.White,
            title = { Text("Lägg till ny olja", color = Color.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newOilName, 
                        onValueChange = { newOilName = it }, 
                        label = { Text("Namn", color = Color.Gray) }, 
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    Text("Välj kategorier:", color = Color.Black)
                    categories.forEach { cat ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedCategoriesForAdd[cat] ?: false, 
                                onCheckedChange = { isChecked -> 
                                    selectedCategoriesForAdd[cat] = isChecked 
                                }
                            )
                            Text(cat, color = Color.Black)
                        }
                    }
                    Text("Intensitet:", color = Color.Black)
                    ExposedDropdownMenuBox(expanded = intensityExpanded, onExpandedChange = { intensityExpanded = !intensityExpanded }) {
                        OutlinedTextField(
                            value = selectedIntensity ?: "", 
                            onValueChange = {}, 
                            readOnly = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, color = Color.Black),
                            placeholder = { Text("Välj intensitet", color = Color.Gray) },
                            trailingIcon = { 
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                                    if (selectedIntensity != null) {
                                        IntensityDrops(selectedIntensity!!)
                                        Spacer(Modifier.width(8.dp))
                                    }
                                    ExposedDropdownMenuDefaults.TrailingIcon(intensityExpanded)
                                }
                            },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = intensityExpanded, 
                            onDismissRequest = { intensityExpanded = false },
                            containerColor = Color.White
                        ) {
                            intensities.forEach { intensity ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(intensity, fontSize = 18.sp, color = Color.Black) 
                                            IntensityDrops(intensity)
                                        }
                                    }, 
                                    onClick = { selectedIntensity = intensity; intensityExpanded = false }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newOilName.isNotBlank() && selectedIntensity != null) {
                            val savedText = "$newOilName | $selectedIntensity"
                            if (selectedCategoriesForAdd["Topnoter"] == true) topnotOils.add(savedText)
                            if (selectedCategoriesForAdd["Mellannoter"] == true) mellannotOils.add(savedText)
                            if (selectedCategoriesForAdd["Basnoter"] == true) basnotOils.add(savedText)
                            if (selectedCategoriesForAdd["Övrigt"] == true) ovrigtOils.add(savedText)
                            showAddDialog = false
                            newOilName = ""
                            selectedIntensity = null
                        }
                    }, 
                    enabled = newOilName.isNotBlank() && selectedIntensity != null,
                    colors = ButtonDefaults.buttonColors(contentColor = Color.Black)
                ) { Text("Spara") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Avbryt", color = Color.Black) } }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EssensoljorScreenPreview() { 
    val top = remember { mutableStateListOf("Lavendel | Mellan", "Citron | Hög") }
    val mellan = remember { mutableStateListOf<String>() }
    val bas = remember { mutableStateListOf<String>() }
    val ovrigt = remember { mutableStateListOf<String>() }
    ParfymAppenTheme { 
        EssensoljorScreen(onBackClick = {}, topnotOils = top, mellannotOils = mellan, basnotOils = bas, ovrigtOils = ovrigt)
    }
}
