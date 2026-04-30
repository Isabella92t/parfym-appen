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
import androidx.compose.material.icons.filled.Add
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

// hjälpfunktioner

@Composable
fun IntensityDrops(intensity: String) {
    // räknar ut hur många droppar som ska visas
    val count = when (intensity.trim()) {
        "Låg" -> 1
        "Mellan" -> 2
        "Hög" -> 3
        else -> 1
    }
    Row {
        repeat(count) {
            Text(text = "💧", fontSize = 16.sp) 
        }
    }
}

@Composable
fun OilRow(textFromList: String, category: String? = null, isSearch: Boolean = false) {
    // Delar upp texten "Namn | Intensitet"
    val parts = textFromList.split(" | ")
    val name = parts[0]
    val intensity = if (parts.size > 1) parts[1] else "Mellan"
    val textColor = if (isSearch) Color.Gray else Color.Black

    Row(
        verticalAlignment = Alignment.CenterVertically, 
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        // Visar oljans namn
        Text("$name ", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp), color = textColor)
        
        // Om det är en sökning, visa kategorin bredvid
        if (category != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(category, fontWeight = FontWeight.Normal, style = MaterialTheme.typography.labelSmall.copy(fontSize = 16.sp), color = textColor)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Visar dropparna
        IntensityDrops(intensity)
    }
}

// Huvudsida

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EssensoljorScreen(
    onBackClick: () -> Unit,
    topnotOils: SnapshotStateList<String>,
    mellannotOils: SnapshotStateList<String>,
    basnotOils: SnapshotStateList<String>,
    ovrigtOils: SnapshotStateList<String>
) {
    // Gemensamma inställningar för designen
    val commonShape = RoundedCornerShape(12.dp)
    val buttonBlue = Color(0xFFB6DFFF)
    val inputTextStyle = TextStyle(fontSize = 28.sp, color = Color.Black)
    val labelTextStyle = TextStyle(fontSize = 26.sp, color = Color.Black)
    
    // Variabler som appen minns
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
        // bakåt-knapp
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onBackClick, 
                modifier = Modifier.align(Alignment.TopStart).size(56.dp), 
                colors = ButtonDefaults.buttonColors(containerColor = buttonBlue, contentColor = Color.Black),
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

        // rubrik
        Text(
            text = "ETERISKA OLJOR", 
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp), 
            fontWeight = FontWeight.Black,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // sökfält
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.width(300.dp).padding(bottom = 24.dp),
            placeholder = { Text("Sök olja...", color = Color.Gray, fontSize = 26.sp) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Black, modifier = Modifier.size(28.dp)) },
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

        // sökresultat (visar 3 stycken åt gången med rullning)
        if (searchQuery.isNotEmpty()) {
            val allOilsMapped = (topnotOils.map { it to "T" } + 
                               mellannotOils.map { it to "M" } + 
                               basnotOils.map { it to "B" } + 
                               ovrigtOils.map { it to "Ö" })
            
            val groupedOils = allOilsMapped.groupBy({ it.first }, { it.second })
            // Söker nu smidigare genom att bara kolla på namnet
            val matches = groupedOils.keys.filter { 
                val nameOnly = it.split(" | ")[0]
                nameOnly.contains(searchQuery, ignoreCase = true) 
            }
            
            if (matches.isNotEmpty()) {
                Card(
                    modifier = Modifier.width(300.dp).padding(bottom = 24.dp).border(1.dp, Color.Black, commonShape),
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
                            
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                Text(name, fontSize = 24.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(letters, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                                Spacer(modifier = Modifier.weight(1f))
                                IntensityDrops(intensity)
                            }
                        }
                    }
                }
            }
        }

        // "+ OLJA"-knapp
        Button(
            onClick = { 
                selectedCategoriesForAdd.clear()
                selectedIntensity = null
                newOilName = ""
                showAddDialog = true 
            },
            modifier = Modifier.width(135.dp).height(50.dp),
            shape = commonShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784), contentColor = Color.Black),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text("+ Olja", fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // kategoriknappar
        categories.forEach { category ->
            Button(
                onClick = { selectedCategory = category; showListDialog = true },
                modifier = Modifier.width(220.dp).padding(vertical = 12.dp),
                shape = commonShape,
                colors = ButtonDefaults.buttonColors(containerColor = buttonBlue, contentColor = Color.Black),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(text = category, fontSize = 28.sp, fontWeight = FontWeight.Normal)
            }
        }
    }

    if (showListDialog) {
        AlertDialog(
            onDismissRequest = { showListDialog = false },
            containerColor = Color.White,
            shape = commonShape,
            title = { Text("Oljor i $selectedCategory", color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold) },
            text = {
                val list = when(selectedCategory) {
                    "Topnoter" -> topnotOils
                    "Mellannoter" -> mellannotOils
                    "Basnoter" -> basnotOils
                    else -> ovrigtOils
                }
                Column(modifier = Modifier.fillMaxWidth()) {
                    list.forEach { oil ->
                        OilRow(textFromList = oil)
                    }
                }
            },
            confirmButton = { 
                TextButton(onClick = { showListDialog = false }) { 
                    Text("Stäng", color = Color.Black, fontSize = 18.sp)
                } 
            },
            modifier = Modifier.border(BorderStroke(1.dp, Color.Black), commonShape)
        )
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFFEDF6FF),
            shape = commonShape,
            title = { Text("Lägg till ny olja", color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // NAMN
                    OutlinedTextField(
                        value = newOilName, 
                        onValueChange = { newOilName = it }, 
                        placeholder = { Text("Namn", color = Color.Gray, fontSize = 24.sp) }, 
                        modifier = Modifier.fillMaxWidth(),
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
                    
                    Text("Välj kategorier:", style = labelTextStyle, fontWeight = FontWeight.Bold)
                    
                    // kategorival med X
                    categories.forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp).clickable { 
                                selectedCategoriesForAdd[cat] = !(selectedCategoriesForAdd[cat] ?: false)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White, RoundedCornerShape(4.dp)) 
                                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedCategoriesForAdd[cat] == true) {
                                    Text("X", color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Normal)
                                }
                            }
                            Text(text = cat, color = Color.Black, fontSize = 26.sp, modifier = Modifier.padding(start = 16.dp))
                        }
                    }
                    
                    Text("Intensitet:", style = labelTextStyle, fontWeight = FontWeight.Bold)
                    
                    // intensitet - dropdown
                    ExposedDropdownMenuBox(expanded = intensityExpanded, onExpandedChange = { intensityExpanded = !intensityExpanded }) {
                        OutlinedTextField(
                            value = selectedIntensity ?: "", 
                            onValueChange = {}, 
                            readOnly = true,
                            textStyle = inputTextStyle,
                            placeholder = { Text("Välj intensitet", color = Color.Gray, fontSize = 18.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(intensityExpanded) },
                            leadingIcon = {
                                if (selectedIntensity != null) {
                                    Box(modifier = Modifier.padding(start = 12.dp)) {
                                        IntensityDrops(selectedIntensity!!)
                                    }
                                }
                            },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                            shape = commonShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Black
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = intensityExpanded, 
                            onDismissRequest = { intensityExpanded = false },
                            containerColor = Color.White,
                            shape = commonShape,
                            modifier = Modifier.border(BorderStroke(1.dp, Color.Black), commonShape)
                        ) {
                            intensities.forEach { intensity ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically, 
                                            modifier = Modifier.fillMaxWidth(), 
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(intensity, fontSize = 24.sp, color = Color.Black) 
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
                        if (newOilName.isNotBlank()) {
                            val savedText = "$newOilName | ${selectedIntensity ?: "Mellan"}"
                            if (selectedCategoriesForAdd["Topnoter"] == true) topnotOils.add(savedText)
                            if (selectedCategoriesForAdd["Mellannoter"] == true) mellannotOils.add(savedText)
                            if (selectedCategoriesForAdd["Basnoter"] == true) basnotOils.add(savedText)
                            if (selectedCategoriesForAdd["Övrigt"] == true) ovrigtOils.add(savedText)
                            showAddDialog = false
                            newOilName = ""
                            selectedIntensity = null
                        }
                    }, 
                    enabled = newOilName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonBlue, contentColor = Color.Black),
                    border = BorderStroke(1.dp, Color.Black),
                    shape = commonShape
                ) { Text("Spara", fontSize = 24.sp) }
            },
            dismissButton = { 
                TextButton(onClick = { showAddDialog = false }) { 
                    Text("Avbryt", color = Color.Black, fontSize = 24.sp)
                } 
            },
            modifier = Modifier.border(BorderStroke(1.dp, Color.Black), commonShape)
        )
    }
}
