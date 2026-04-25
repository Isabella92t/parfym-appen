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
            Text(text = "💧", fontSize = 10.sp)
        }
    }
}

@Composable
fun OilRow(textFromList: String, category: String? = null, isSearch: Boolean = false) {
    val parts = textFromList.split(" | ")
    val name = parts[0]
    val intensity = if (parts.size > 1) parts[1] else "Mellan"
    val textColor = if (isSearch) Color.Gray else Color.Unspecified

    Row(
        verticalAlignment = Alignment.CenterVertically, 
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text("• $name ", style = MaterialTheme.typography.bodyLarge, color = textColor)
        if (category != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(category, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
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
    var selectedIntensity by remember { mutableStateOf(intensities[1]) }
    var intensityExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart)) { Text("<-") }
        }

        Text(
            text = "ETERISKA OLJOR", 
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp), 
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.width(260.dp).padding(bottom = 24.dp),
            placeholder = { Text("Sök olja...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            shape = RoundedCornerShape(24.dp)
        )

        if (searchQuery.isNotEmpty()) {
            val allOils = topnotOils.map { it to "Topnoter" } + mellannotOils.map { it to "Mellannoter" } + basnotOils.map { it to "Basnoter" } + ovrigtOils.map { it to "Övrigt" }
            val matches = allOils.filter { it.first.startsWith(searchQuery, ignoreCase = true) }
            if (matches.isNotEmpty()) {
                Card(modifier = Modifier.width(260.dp).padding(bottom = 24.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        matches.forEach { (text, cat) -> OilRow(text, category = cat, isSearch = true) }
                    }
                }
            }
        }

        Button(
            onClick = { 
                selectedCategoriesForAdd.clear()
                showAddDialog = true 
            },
            modifier = Modifier.width(180.dp).height(50.dp),
            shape = RoundedCornerShape(12.dp)
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
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = category, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showListDialog) {
        AlertDialog(
            onDismissRequest = { showListDialog = false },
            title = { Text("Oljor i $selectedCategory") },
            text = {
                val list = when(selectedCategory) {
                    "Topnoter" -> topnotOils
                    "Mellannoter" -> mellannotOils
                    "Basnoter" -> basnotOils
                    else -> ovrigtOils
                }
                Column(modifier = Modifier.fillMaxWidth()) { list.forEach { OilRow(it) } }
            },
            confirmButton = { TextButton(onClick = { showListDialog = false }) { Text("Stäng") } }
        )
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Lägg till ny olja") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(value = newOilName, onValueChange = { newOilName = it }, label = { Text("Namn") }, modifier = Modifier.fillMaxWidth())
                    Text("Välj kategorier:")
                    categories.forEach { cat ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedCategoriesForAdd[cat] ?: false, 
                                onCheckedChange = { isChecked -> 
                                    selectedCategoriesForAdd[cat] = isChecked 
                                }
                            )
                            Text(cat)
                        }
                    }
                    Text("Intensitet:")
                    ExposedDropdownMenuBox(expanded = intensityExpanded, onExpandedChange = { intensityExpanded = !intensityExpanded }) {
                        OutlinedTextField(
                            value = selectedIntensity, onValueChange = {}, readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(intensityExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = intensityExpanded, onDismissRequest = { intensityExpanded = false }) {
                            intensities.forEach { intensity ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IntensityDrops(intensity)
                                            Spacer(Modifier.width(8.dp))
                                            Text(intensity) 
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
                Button(onClick = {
                    if (newOilName.isNotBlank()) {
                        val savedText = "$newOilName | $selectedIntensity"
                        if (selectedCategoriesForAdd["Topnoter"] == true) topnotOils.add(savedText)
                        if (selectedCategoriesForAdd["Mellannoter"] == true) mellannotOils.add(savedText)
                        if (selectedCategoriesForAdd["Basnoter"] == true) basnotOils.add(savedText)
                        if (selectedCategoriesForAdd["Övrigt"] == true) ovrigtOils.add(savedText)
                    }
                    showAddDialog = false
                    newOilName = ""
                }) { Text("Spara") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Avbryt") } }
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
