package com.example.parfymappen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
    ovrigtOils: SnapshotStateList<String> // visar alla eteriska oljor uppdelade i kategorier
) {
    val commonShape = RoundedCornerShape(12.dp) // designvärden
    val buttonBlue = Color(0xFFB6DFFF)
    val inputTextStyle = TextStyle(fontSize = 28.sp, color = Color.Black)
    val labelTextStyle = TextStyle(fontSize = 26.sp, color = Color.Black)

    var showAddDialog by remember { mutableStateOf(false) }
    var newOilName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    var isDeleteMode by remember { mutableStateOf(false) } // papperskorgsläget om aktivt eller inte
    var oilToDelete by remember { mutableStateOf<String?>(null) }
    var oilListToDeleteFrom by remember { mutableStateOf<SnapshotStateList<String>?>(null) }

    val selectedCategoriesForAdd = remember { mutableStateMapOf<String, Boolean>() }

    // state används för att hålla koll på dialoger, sökning och raderingsläge

    Box(modifier = Modifier.fillMaxSize()) { // för att lägga innehåll ovanpå varandra
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally // allt uppifrån och ner
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onBackClick, // bakåt knapp - kör callback onbackclick
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
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            OutlinedTextField( // sökfältet - när man skriver, uppdateras searchquery
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 24.dp),
                placeholder = {
                    Text("Sök olja...", color = Color.Gray, fontSize = 26.sp) // hjälptext när det är tomt
                },
                leadingIcon = { // sökikon till vänster
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

            if (searchQuery.isNotEmpty()) { // om användaren har skrivit något visas sökresultat
                val allOilsMapped = // kopplar oljor till kategorier
                    topnotOils.map { it to "T" } +
                            mellannotOils.map { it to "M" } +
                            basnotOils.map { it to "B" } +
                            ovrigtOils.map { it to "Ö" }

                val groupedOils = allOilsMapped.groupBy(
                    { it.first },
                    { it.second }
                )

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

            Button( // + olja knappen
                onClick = {
                    selectedCategoriesForAdd.clear()
                    newOilName = ""
                    showAddDialog = true
                }, // knappen öppnar addoildialog
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
                commonShape = commonShape,
                isDeleteMode = isDeleteMode,
                onRequestDelete = { oil ->
                    oilToDelete = oil
                    oilListToDeleteFrom = topnotOils
                }
            )

            CategoryDropdown(
                title = "Mellannoter",
                oils = mellannotOils,
                commonShape = commonShape,
                isDeleteMode = isDeleteMode,
                onRequestDelete = { oil ->
                    oilToDelete = oil
                    oilListToDeleteFrom = mellannotOils
                }
            )

            CategoryDropdown(
                title = "Basnoter",
                oils = basnotOils,
                commonShape = commonShape,
                isDeleteMode = isDeleteMode,
                onRequestDelete = { oil ->
                    oilToDelete = oil
                    oilListToDeleteFrom = basnotOils
                }
            )

            CategoryDropdown(
                title = "Övrigt",
                oils = ovrigtOils,
                commonShape = commonShape,
                isDeleteMode = isDeleteMode,
                onRequestDelete = { oil ->
                    oilToDelete = oil
                    oilListToDeleteFrom = ovrigtOils
                }
            )

            Spacer(modifier = Modifier.height(90.dp))
        }

        FloatingActionButton( // papperskorgsknappen
            onClick = { isDeleteMode = !isDeleteMode },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = if (isDeleteMode) Color.Red else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Radera")
        }
    }

    if (showAddDialog) { // dialogen visas bara när showadddialog är true
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

    if (oilToDelete != null) {
        val oilName = oilToDelete!!.split(" | ")[0]

        AlertDialog(
            onDismissRequest = {
                oilToDelete = null
                oilListToDeleteFrom = null
            },
            title = {
                Text(
                    text = "Ta bort '$oilName'?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            oilToDelete = null
                            oilListToDeleteFrom = null
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                        shape = commonShape
                    ) {
                        Text("Avbryt") // valet nollställs och stänger dialog
                    }

                    Button(
                        onClick = {
                            oilListToDeleteFrom?.remove(oilToDelete)
                            oilToDelete = null
                            oilListToDeleteFrom = null
                        }, // tar bort oljan
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                        shape = commonShape
                    ) {
                        Text("Ta bort")
                    }
                }
            },
            containerColor = Color.White,
            shape = commonShape,
            modifier = Modifier.border(1.dp, Color.Black, commonShape)
        )
    }
}