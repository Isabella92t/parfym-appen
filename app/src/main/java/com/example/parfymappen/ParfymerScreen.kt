package com.example.parfymappen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parfymappen.ui.theme.ParfymAppenTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParfymerScreen(
    onBackClick: () -> Unit,
    savedPerfumes: SnapshotStateList<SavedPerfume>,
    topnotOils: List<String>,
    mellannotOils: List<String>,
    basnotOils: List<String>,
    ovrigtOils: List<String>
) {
    // --- 1. MINNEN ---
    var currentMode by remember { mutableStateOf("list") } 
    var perfumeName by remember { mutableStateOf("") }
    var perfumeNotes by remember { mutableStateOf("") }
    var isDeleteMode by remember { mutableStateOf(false) } 
    var perfumeToDelete by remember { mutableStateOf<SavedPerfume?>(null) }
    
    val selectedOils = remember { mutableStateMapOf<String, Boolean>() }
    val oilDrops = remember { mutableStateMapOf<String, Int>() }

    // Animation som får flaskorna att vibrera
    val infiniteTransition = rememberInfiniteTransition(label = "shake")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    when (currentMode) {
        "create" -> {
            // Skapa - namn och ingredienser
            Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
                Button(onClick = { currentMode = "list" }) { Text("Avbryt") }
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Ny parfym", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(24.dp))
                
                // Namnfältet - namn försvinner såfort man skriver något
                OutlinedTextField(
                    value = perfumeName,
                    onValueChange = { perfumeName = it },
                    placeholder = { Text("Namn") }, 
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Välj ingredienser:", style = MaterialTheme.typography.titleMedium)
                
                MultiSelectDropdown("Topnoter", topnotOils, selectedOils)
                MultiSelectDropdown("Mellannoter", mellannotOils, selectedOils)
                MultiSelectDropdown("Basnoter", basnotOils, selectedOils)
                MultiSelectDropdown("Övrigt", ovrigtOils, selectedOils)

                Spacer(modifier = Modifier.height(40.dp))
                Button(onClick = { currentMode = "summary" }, modifier = Modifier.fillMaxWidth(), enabled = perfumeName.isNotBlank()) {
                    Text("Gå vidare")
                }
            }
        }

        "summary" -> {
            // Receptet som räknar ut procenten
            var totalDrops = 0
            var topDrops = 0
            var mellanDrops = 0
            var basDrops = 0

            // Valda oljor, plussar ihop dropparna
            oilDrops.forEach { (name, drops) ->
                if (selectedOils[name] == true) {
                    totalDrops += drops
                    if (topnotOils.contains(name)) topDrops += drops
                    if (mellannotOils.contains(name)) mellanDrops += drops
                    if (basnotOils.contains(name)) basDrops += drops
                }
            }

            Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
                Button(onClick = { currentMode = "create" }) { Text("Gå tillbaka") }
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = perfumeName, style = MaterialTheme.typography.headlineLarge)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                //Rutan som visar procentfördelningen
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Topp", style = MaterialTheme.typography.labelSmall)
                            Text("${if(totalDrops > 0) (topDrops*100)/totalDrops else 0}%", fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Mellan", style = MaterialTheme.typography.labelSmall)
                            Text("${if(totalDrops > 0) (mellanDrops*100)/totalDrops else 0}%", fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Bas", style = MaterialTheme.typography.labelSmall)
                            Text("${if(totalDrops > 0) (basDrops*100)/totalDrops else 0}%", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                val cats = listOf(topnotOils to "Topnoter", mellannotOils to "Mellannoter", basnotOils to "Basnoter", ovrigtOils to "Övrigt")
                cats.forEach { (list, categoryName) ->
                    list.forEach { oil ->
                        if (selectedOils[oil] == true) {
                            val currentDrops = oilDrops[oil] ?: 0
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    val displayName = oil.split(" | ")[0]
                                    Text(text = displayName, style = MaterialTheme.typography.bodyLarge)
                                    Text(text = categoryName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary, CircleShape).clickable { if (currentDrops > 0) oilDrops[oil] = currentDrops - 1 } ) {
                                    Text(text = "-", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(75.dp).clickable { oilDrops[oil] = currentDrops + 1 }) {
                                    Text(text = "💧", fontSize = 60.sp) 
                                    Text(text = currentDrops.toString(), color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.offset(y = 10.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary, CircleShape).clickable { oilDrops[oil] = currentDrops + 1 }) {
                                    Text(text = "+", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = perfumeNotes, 
                    onValueChange = { perfumeNotes = it }, 
                    placeholder = { Text("Övriga anteckningar") }, 
                    modifier = Modifier.fillMaxWidth(), 
                    minLines = 3
                )
                Button(onClick = {
                    val ingredientsList = mutableListOf<Ingredient>()
                    cats.forEach { (l, cat) -> 
                        l.forEach { o -> 
                            val d = oilDrops[o] ?: 0
                            if (selectedOils[o] == true && d > 0) ingredientsList.add(Ingredient(o, d, cat))
                        }
                    }
                    savedPerfumes.add(SavedPerfume(perfumeName, ingredientsList, perfumeNotes))
                    currentMode = "list"
                }, modifier = Modifier.fillMaxWidth().padding(top = 32.dp)) { Text("Spara och slutför") }
            }
        }

        else -> {
            // Hyllan med parfymer
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.Start) {
                    Button(onClick = onBackClick) { Text("<-") }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    Text(text = "Mina parfymer", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = { currentMode = "create"; perfumeName = ""; perfumeNotes = ""; selectedOils.clear(); oilDrops.clear(); isDeleteMode = false }, modifier = Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = 12.dp)) {
                                Box(modifier = Modifier.size(10.dp, 4.dp).background(Color.White, RoundedCornerShape(1.dp)))
                                Box(modifier = Modifier.size(18.dp, 16.dp).background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp, bottomStart = 4.dp, bottomEnd = 4.dp)))
                            }
                            Text("Skapa ny parfym", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    val chunked = savedPerfumes.chunked(3)
                    chunked.forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Top) {
                            row.forEach { perfume ->
                                PerfumeBottle(
                                    perfume = perfume,
                                    isDeleteMode = isDeleteMode,
                                    rotation = rotation,
                                    onDelete = { perfumeToDelete = perfume },
                                    onOpen = {
                                        perfumeName = perfume.name; perfumeNotes = perfume.notes
                                        selectedOils.clear(); oilDrops.clear()
                                        perfume.ingredients.forEach { selectedOils[it.name] = true; oilDrops[it.name] = it.drops }
                                        currentMode = "summary"
                                    }
                                )
                            }
                            repeat(3 - row.size) { Spacer(modifier = Modifier.width(95.dp)) }
                        }
                        Spacer(modifier = Modifier.height(32.dp)) 
                    }
                }

                // Notisruta för borttagning
                if (perfumeToDelete != null) {
                    AlertDialog(
                        onDismissRequest = { perfumeToDelete = null },
                        confirmButton = {
                            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Button(onClick = { perfumeToDelete = null }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("Avbryt") }
                                Button(onClick = { savedPerfumes.remove(perfumeToDelete); perfumeToDelete = null }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Ta bort") }
                            }
                        },
                        title = { Text(text = "Är du säker på att du vill ta bort '${perfumeToDelete?.name}'?", fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center) },
                    )
                }

                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.BottomEnd) {
                    FloatingActionButton(onClick = { isDeleteMode = !isDeleteMode }, containerColor = if (isDeleteMode) Color.Red else MaterialTheme.colorScheme.secondaryContainer, contentColor = if (isDeleteMode) Color.White else MaterialTheme.colorScheme.onSecondaryContainer, shape = CircleShape) {
                        Icon(Icons.Default.Delete, "Ta bort")
                    }
                }
            }
        }
    }
}

@Composable
fun PerfumeBottle(perfume: SavedPerfume, isDeleteMode: Boolean, rotation: Float, onDelete: () -> Unit, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.width(95.dp).graphicsLayer { rotationZ = if (isDeleteMode) rotation else 0f }.clickable { if (isDeleteMode) onDelete() else onOpen() }) {
        Box(modifier = Modifier.size(16.dp, 5.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.size(8.dp, 4.dp).background(MaterialTheme.colorScheme.primary.copy(0.3f)))
        Card(modifier = Modifier.fillMaxWidth().height(130.dp), shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp, bottomStart = 20.dp, bottomEnd = 20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(6.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = perfume.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                perfume.ingredients.take(3).forEach { ingredient ->
                    val oilName = ingredient.name.split(" | ")[0]
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 1.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(18.dp)) {
                            Text("💧", fontSize = 16.sp)
                            Text(text = ingredient.drops.toString(), color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.offset(y = 3.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = oilName, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                if (perfume.ingredients.size > 3) Text("...", fontSize = 10.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectDropdown(title: String, options: List<String>, selectedStates: MutableMap<String, Boolean>) {
    var expanded by remember { mutableStateOf(false) }
    val numSelected = options.count { selectedStates[it] == true }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(value = if (numSelected > 0) "$title ($numSelected)" else "Välj $title", onValueChange = {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth().padding(vertical = 4.dp))
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { oil ->
                val displayName = oil.split(" | ")[0]
                DropdownMenuItem(text = { Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = selectedStates[oil] ?: false, onCheckedChange = null); Spacer(modifier = Modifier.width(8.dp)); Text(displayName) } }, onClick = { selectedStates[oil] = !(selectedStates[oil] ?: false) })
            }
        }
    }
}
