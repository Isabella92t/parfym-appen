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
    val lightBlue = Color(0xFFFFE8A3).copy(alpha = 0.75f)
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
                            Button(onClick = { perfumeToDelete = null }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Black), border = BorderStroke(1.dp, Color.Black), shape = commonShape) { Text("Avbryt") }
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
            Button(onClick = onBack, modifier = Modifier.size(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Black), border = BorderStroke(1.dp, Color.Black), shape = shape, contentPadding = PaddingValues(0.dp)) {
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
fun ShelfView(onBack: () -> Unit, onNew: () -> Unit, savedPerfumes: List<SavedPerfume>, isDeleteMode: Boolean, onToggleDelete: () -> Unit, rotation: Float, onDeletePerfume: (SavedPerfume) -> Unit, onOpenPerfume: (SavedPerfume) -> Unit, shape: RoundedCornerShape, buttonColor: Color) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 16.dp).verticalScroll(rememberScrollState())) {
            Button(onClick = onBack, modifier = Modifier.size(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Black), border = BorderStroke(1.dp, Color.Black), shape = shape, contentPadding = PaddingValues(0.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onNew,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(
                    "+ Parfym",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text("Mina parfymer", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            savedPerfumes.chunked(3).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { p -> PerfumeBottle(p, isDeleteMode, rotation, { onDeletePerfume(p) }, { onOpenPerfume(p) }) }
                    repeat(3 - row.size) { Spacer(modifier = Modifier.width(95.dp)) }
                }
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
        FloatingActionButton(onClick = onToggleDelete, modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp), containerColor = if (isDeleteMode) Color.Red else MaterialTheme.colorScheme.secondaryContainer, contentColor = Color.Black, shape = CircleShape) { Icon(Icons.Default.Delete, null) }
    }
}

