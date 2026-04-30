package com.example.parfymappen

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete

// Hjälpfunktion för att visa droppar baserat på intensitetstext
@Composable
fun IntensityDropsSmall(intensity: String) {
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
    val commonShape = RoundedCornerShape(12.dp)
    val lightBlue = Color(0xFFB6DFFF) 
    val inputTextStyle = TextStyle(fontSize = 28.sp, color = Color.Black)

    var currentMode by remember { mutableStateOf("list") } 
    var perfumeName by remember { mutableStateOf("") }
    var perfumeNotes by remember { mutableStateOf("") }
    var isDeleteMode by remember { mutableStateOf(false) } 
    var perfumeToDelete by remember { mutableStateOf<SavedPerfume?>(null) }
    var isEditingExisting by remember { mutableStateOf(false) } 
    
    // Vi använder unika nycklar för att skilja på samma olja i olika kategorier
    val selectedOils = remember { mutableStateMapOf<String, Boolean>() }
    val oilDrops = remember { mutableStateMapOf<String, Int>() }

    val rotation by rememberInfiniteTransition(label = "shake").animateFloat(
        initialValue = -1.5f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(100, easing = LinearEasing), RepeatMode.Reverse),
        label = "rotation"
    )

    when (currentMode) {
        "create" -> CreatePerfumeView(
            onBack = { currentMode = "list" },
            name = perfumeName,
            onNameChange = { perfumeName = it },
            onNext = { currentMode = "summary" },
            topnotOils = topnotOils,
            mellannotOils = mellannotOils,
            basnotOils = basnotOils,
            ovrigtOils = ovrigtOils,
            selectedOils = selectedOils,
            shape = commonShape,
            textStyle = inputTextStyle,
            buttonColor = lightBlue
        )

        "summary" -> SummaryView(
            onBack = { if (isEditingExisting) currentMode = "list" else currentMode = "create" },
            onEditOils = { currentMode = "create" },
            name = perfumeName,
            notes = perfumeNotes,
            onNotesChange = { perfumeNotes = it },
            onSave = {
                val ingredients = mutableListOf<Ingredient>()
                listOf(topnotOils to "Topnoter", mellannotOils to "Mellannoter", basnotOils to "Basnoter", ovrigtOils to "Övrigt").forEach { (list, cat) ->
                    list.forEach { oilString ->
                        val key = "$cat-$oilString"
                        if (selectedOils[key] == true) {
                            val drops = oilDrops[key] ?: 0
                            if (drops > 0) ingredients.add(Ingredient(oilString, drops, cat))
                        }
                    }
                }
                if (isEditingExisting) savedPerfumes.removeAll { it.name == perfumeName }
                savedPerfumes.add(SavedPerfume(perfumeName, ingredients, perfumeNotes))
                currentMode = "list"
                isEditingExisting = false
            },
            selectedOils = selectedOils,
            oilDrops = oilDrops,
            topnotOils = topnotOils,
            mellannotOils = mellannotOils,
            basnotOils = basnotOils,
            ovrigtOils = ovrigtOils,
            shape = commonShape,
            textStyle = inputTextStyle,
            buttonColor = lightBlue
        )

        else -> {
            ShelfView(
                onBack = onBackClick,
                onNew = { 
                    currentMode = "create"; perfumeName = ""; perfumeNotes = ""
                    selectedOils.clear(); oilDrops.clear(); isDeleteMode = false; isEditingExisting = false 
                },
                savedPerfumes = savedPerfumes,
                isDeleteMode = isDeleteMode,
                onToggleDelete = { isDeleteMode = !isDeleteMode },
                rotation = rotation,
                onDeletePerfume = { perfumeToDelete = it },
                onOpenPerfume = { p ->
                    perfumeName = p.name; perfumeNotes = p.notes; isEditingExisting = true
                    selectedOils.clear(); oilDrops.clear()
                    p.ingredients.forEach { 
                        val key = "${it.category}-${it.name}"
                        selectedOils[key] = true
                        oilDrops[key] = it.drops 
                    }
                    currentMode = "summary"
                },
                shape = commonShape,
                buttonColor = lightBlue
            )

            if (perfumeToDelete != null) {
                AlertDialog(
                    onDismissRequest = { perfumeToDelete = null },
                    confirmButton = {
                        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(onClick = { perfumeToDelete = null }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = lightBlue, contentColor = Color.Black), border = BorderStroke(1.dp, Color.Black), shape = commonShape) { Text("Avbryt") }
                            Button(onClick = { savedPerfumes.remove(perfumeToDelete); perfumeToDelete = null }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White), border = BorderStroke(1.dp, Color.Black), shape = commonShape) { Text("Ta bort") }
                        }
                    },
                    title = { Text("Ta bort '${perfumeToDelete?.name}'?", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black) },
                    containerColor = Color.White, shape = commonShape, modifier = Modifier.border(1.dp, Color.Black, commonShape)
                )
            }
        }
    }
}

@Composable
fun CreatePerfumeView(onBack: () -> Unit, name: String, onNameChange: (String) -> Unit, onNext: () -> Unit, topnotOils: List<String>, mellannotOils: List<String>, basnotOils: List<String>, ovrigtOils: List<String>, selectedOils: MutableMap<String, Boolean>, shape: RoundedCornerShape, textStyle: TextStyle, buttonColor: Color) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onBack, modifier = Modifier.size(48.dp), colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = Color.Black), border = BorderStroke(1.dp, Color.Black), shape = shape, contentPadding = PaddingValues(0.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Ny parfym", style = MaterialTheme.typography.headlineLarge, color = Color.Black)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = name, onValueChange = onNameChange, placeholder = { Text("Namn", color = Color.Gray, fontSize = 24.sp) }, modifier = Modifier.width(300.dp), shape = shape, textStyle = textStyle, colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Black))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Välj ingredienser:", style = MaterialTheme.typography.titleMedium.copy(fontSize = 26.sp), color = Color.Black)
        MultiSelectDropdown("Topnoter", topnotOils, selectedOils, shape, textStyle)
        MultiSelectDropdown("Mellannoter", mellannotOils, selectedOils, shape, textStyle)
        MultiSelectDropdown("Basnoter", basnotOils, selectedOils, shape, textStyle)
        MultiSelectDropdown("Övrigt", ovrigtOils, selectedOils, shape, textStyle)
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = onNext, modifier = Modifier.width(200.dp), enabled = name.isNotBlank(), shape = shape, colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = Color.Black), border = BorderStroke(1.dp, Color.Black)) { Text("Gå vidare", fontSize = 22.sp) }
    }
}

@Composable
fun SummaryView(onBack: () -> Unit, onEditOils: () -> Unit, name: String, notes: String, onNotesChange: (String) -> Unit, onSave: () -> Unit, selectedOils: MutableMap<String, Boolean>, oilDrops: MutableMap<String, Int>, topnotOils: List<String>, mellannotOils: List<String>, basnotOils: List<String>, ovrigtOils: List<String>, shape: RoundedCornerShape, textStyle: TextStyle, buttonColor: Color) {
    var total = 0; var t = 0; var m = 0; var b = 0; var o = 0
    
    listOf(topnotOils to "Topnoter", mellannotOils to "Mellannoter", basnotOils to "Basnoter", ovrigtOils to "Övrigt").forEach { (list, catName) ->
        list.forEach { oil ->
            val key = "$catName-$oil"
            if (selectedOils[key] == true) {
                val d = oilDrops[key] ?: 0
                total += d
                when (catName) {
                    "Topnoter" -> t += d
                    "Mellannoter" -> m += d
                    "Basnoter" -> b += d
                    "Övrigt" -> o += d
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Button(onClick = onBack, modifier = Modifier.size(48.dp), colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = Color.Black), border = BorderStroke(1.dp, Color.Black), shape = shape, contentPadding = PaddingValues(0.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        Spacer(modifier = Modifier.height(24.dp))
        Text(name, style = MaterialTheme.typography.headlineLarge, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black, shape), shape = shape, colors = CardDefaults.cardColors(containerColor = buttonColor.copy(alpha = 0.4f))) {
            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Topp" to t, "Mellan" to m, "Bas" to b, "Övrigt" to o).forEach { (label, drops) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(label, style = MaterialTheme.typography.labelSmall); Text("${if(total > 0) (drops*100)/total else 0}%", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        listOf(topnotOils to "Topnoter", mellannotOils to "Mellannoter", basnotOils to "Basnoter", ovrigtOils to "Övrigt").forEach { (list, cat) ->
            list.filter { selectedOils["$cat-$it"] == true }.forEach { oil ->
                val key = "$cat-$oil"
                val current = oilDrops[key] ?: 0
                val intensity = if (oil.contains(" | ")) oil.split(" | ")[1] else "Mellan"
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(oil.split(" | ")[0], fontSize = 24.sp, color = Color.Black)
                            Spacer(Modifier.width(8.dp))
                            IntensityDropsSmall(intensity)
                        }
                        Text(cat, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black) 
                    }
                    IconButton(onClick = { if (current > 0) oilDrops[key] = current - 1 }) { Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black) }
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(75.dp)) { Text("💧", fontSize = 60.sp); Text(current.toString(), color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.offset(y = 10.dp)) }
                    IconButton(onClick = { oilDrops[key] = current + 1 }) { Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black) }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = notes, onValueChange = onNotesChange, placeholder = { Text("Övriga anteckningar", color = Color.Gray, fontSize = 24.sp) }, modifier = Modifier.fillMaxWidth(), minLines = 3, shape = shape, textStyle = textStyle, colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Black))
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth().padding(top = 32.dp), shape = shape, colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = Color.Black), border = BorderStroke(1.dp, Color.Black)) { Text("Spara", fontSize = 20.sp) }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onEditOils, modifier = Modifier.fillMaxWidth(), shape = shape, colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = Color.Black), border = BorderStroke(1.dp, Color.Black)) { Text("Redigera oljor", fontSize = 20.sp) }
    }
}

@Composable
fun ShelfView(onBack: () -> Unit, onNew: () -> Unit, savedPerfumes: List<SavedPerfume>, isDeleteMode: Boolean, onToggleDelete: () -> Unit, rotation: Float, onDeletePerfume: (SavedPerfume) -> Unit, onOpenPerfume: (SavedPerfume) -> Unit, shape: RoundedCornerShape, buttonColor: Color) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 16.dp).verticalScroll(rememberScrollState())) {
            Button(onClick = onBack, modifier = Modifier.size(48.dp), colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = Color.Black), border = BorderStroke(1.dp, Color.Black), shape = shape, contentPadding = PaddingValues(0.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Spacer(modifier = Modifier.height(48.dp))
            Button(onClick = onNew, modifier = Modifier.fillMaxWidth().height(64.dp), shape = shape, colors = ButtonDefaults.buttonColors(containerColor = buttonColor, contentColor = Color.Black), border = BorderStroke(1.dp, Color.Black)) { Text("Skapa ny parfym", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontSize = 24.sp) }
            Spacer(modifier = Modifier.height(32.dp))
            Text("Mina parfymer", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            savedPerfumes.chunked(3).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { p -> PerfumeBottle(p, isDeleteMode, rotation, { onDeletePerfume(p) }, { onOpenPerfume(p) }) }
                    repeat(3 - row.size) { Spacer(modifier = Modifier.width(95.dp)) }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        FloatingActionButton(onClick = onToggleDelete, modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp), containerColor = if (isDeleteMode) Color.Red else MaterialTheme.colorScheme.secondaryContainer, contentColor = Color.Black, shape = CircleShape) { Icon(Icons.Default.Delete, null) }
    }
}

@Composable
fun PerfumeBottle(perfume: SavedPerfume, isDeleteMode: Boolean, rotation: Float, onDelete: () -> Unit, onOpen: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(95.dp).graphicsLayer { rotationZ = if (isDeleteMode) rotation else 0f }.clickable { if (isDeleteMode) onDelete() else onOpen() }) {
        Box(modifier = Modifier.size(16.dp, 5.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.size(8.dp, 4.dp).background(MaterialTheme.colorScheme.primary.copy(0.3f)))
        Card(modifier = Modifier.fillMaxWidth().height(130.dp), shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp, bottomStart = 20.dp, bottomEnd = 20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), border = BorderStroke(1.dp, Color.Black)) {
            Column(modifier = Modifier.padding(6.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(perfume.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                perfume.ingredients.take(3).forEach { ing ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💧", fontSize = 10.sp); Spacer(Modifier.width(2.dp))
                        Text(ing.name.split(" | ")[0], fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectDropdown(title: String, options: List<String>, selectedStates: MutableMap<String, Boolean>, shape: RoundedCornerShape, textStyle: TextStyle) {
    var expanded by remember { mutableStateOf(false) }
    val numSelected = options.count { selectedStates["$title-$it"] == true }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(value = if (numSelected > 0) "$title ($numSelected)" else "Välj $title", onValueChange = {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).width(300.dp).padding(vertical = 4.dp), shape = shape, textStyle = textStyle, colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, focusedBorderColor = Color.Black, unfocusedBorderColor = Color.Black))
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = Color.White, modifier = Modifier.border(1.dp, Color.Black, shape)) {
            options.forEach { oil ->
                val key = "$title-$oil"
                val intensity = if (oil.contains(" | ")) oil.split(" | ")[1] else "Mellan"
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedStates[key] = !(selectedStates[key] ?: false) }.padding(16.dp)) {
                    Box(modifier = Modifier.size(36.dp).background(Color.White, RoundedCornerShape(4.dp)).border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                        if (selectedStates[key] == true) Text("X", fontSize = 28.sp, fontWeight = FontWeight.Normal, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(oil.split(" | ")[0], color = Color.Black, fontSize = 24.sp, modifier = Modifier.weight(1f))
                    IntensityDropsSmall(intensity)
                }
            }
        }
    }
}
