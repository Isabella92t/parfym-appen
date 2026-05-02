package com.example.parfymappen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun AddOilDialog( // start av funktion
    onDismiss: () -> Unit,
    topnotOils: SnapshotStateList<String>,
    mellannotOils: SnapshotStateList<String>,
    basnotOils: SnapshotStateList<String>,
    ovrigtOils: SnapshotStateList<String>, //listor där oljor sparas
    commonShape: RoundedCornerShape, //rundade hörn
    buttonBlue: Color, //färg till knappar
    inputTextStyle: TextStyle,
    labelTextStyle: TextStyle
) {
    var newOilName by remember { mutableStateOf("") } // sparar det användaren skriver
    var selectedIntensity by remember { mutableStateOf<String?>(null) } // sparar vald intensitet
    var intensityExpanded by remember { mutableStateOf(false) } // styr dropdown, false=stängd, true=öppen

    val categories = listOf("Topnoter", "Mellannoter", "Basnoter", "Övrigt")
    val selectedCategoriesForAdd = remember { mutableStateMapOf<String, Boolean>() }
    val intensities = listOf("Låg", "Mellan", "Hög") // val för dropdowns

    AlertDialog( // pop-up rutan
        onDismissRequest = onDismiss, // stängs när man klickar utanför
        containerColor = Color(0xFFEDF6FF),
        shape = commonShape, //rundande hörn
        title = {
            Text(
                "Lägg till ny olja",
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = { // text innehåll
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField( // namnfältet
                    value = newOilName, // visar text som finns sparat
                    onValueChange = { newOilName = it }, // varje gång användare skriver ändras newoilname
                    placeholder = {
                        Text("Namn", color = Color.Gray, fontSize = 24.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = commonShape, // rundade hörn
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

                Text(
                    "Välj kategorier:",
                    style = labelTextStyle,
                    fontWeight = FontWeight.Bold
                )

                categories.forEach { cat -> // loop för kategorier
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .clickable {
                                selectedCategoriesForAdd[cat] = // växla mellan vald och inte vald
                                    !(selectedCategoriesForAdd[cat] ?: false) // om kategori ej finns - false
                            } // row lägger kryssruta och text bredvid varandra
                    ) {
                        Box( // kryssrutan
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White, RoundedCornerShape(4.dp))
                                .border(
                                    BorderStroke(1.dp, Color.Black),
                                    RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedCategoriesForAdd[cat] == true) { // visa x om vald
                                Text("X", fontSize = 28.sp, color = Color.Black)
                            }
                        }

                        Text( //kategoritexten
                            text = cat,
                            color = Color.Black,
                            fontSize = 26.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                Text(
                    "Intensitet:", // text intensitet
                    style = labelTextStyle,
                    fontWeight = FontWeight.Bold
                )

                ExposedDropdownMenuBox( //dropdownens box
                    expanded = intensityExpanded,
                    onExpandedChange = {
                        intensityExpanded = !intensityExpanded
                    }
                ) {
                    OutlinedTextField( // textfält visar valt dropdown värde
                        onValueChange = {},
                        readOnly = true,
                        textStyle = TextStyle(fontSize = 26.sp, color = Color.Black),
                        placeholder = {
                            Text("Välj intensitet", color = Color.Gray)
                        },
                        trailingIcon = { // pil ikon
                            ExposedDropdownMenuDefaults.TrailingIcon(intensityExpanded)
                        },
                        value = selectedIntensity?.let { "$it ${"💧".repeat(
                            when (it) {
                                "Låg" -> 1
                                "Mellan" -> 2
                                "Hög" -> 3
                                else -> 1
                            } // vald intensitet och droppar
                        )}" } ?: "",
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = commonShape,
                        colors = OutlinedTextFieldDefaults.colors( // design på dropdownfältet
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black
                        )
                    )

                    ExposedDropdownMenu( // dropdownlistan
                        expanded = intensityExpanded,
                        onDismissRequest = { intensityExpanded = false },
                        containerColor = Color.White
                    ) {
                        intensities.forEach { intensity -> // loop för intensiteter
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = intensity,
                                            fontSize = 26.sp,
                                            color = Color.Black
                                        )

                                        IntensityDrops(intensity, fontSize = 14)
                                    }
                                },
                                onClick = {
                                    selectedIntensity = intensity
                                    intensityExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },

        confirmButton = { // spara knapp
            Button(
                onClick = {
                    if (newOilName.isNotBlank()) {
                        val savedText = // oljan byggs som text
                            "$newOilName | ${selectedIntensity ?: "Mellan"}"

                        if (selectedCategoriesForAdd["Topnoter"] == true) { // lägg till valda kategorier
                            topnotOils.add(savedText)
                        }
                        if (selectedCategoriesForAdd["Mellannoter"] == true) {
                            mellannotOils.add(savedText)
                        }
                        if (selectedCategoriesForAdd["Basnoter"] == true) {
                            basnotOils.add(savedText)
                        }
                        if (selectedCategoriesForAdd["Övrigt"] == true) {
                            ovrigtOils.add(savedText)
                        }

                        onDismiss() // stäng dialogen
                    }
                },
                enabled = newOilName.isNotBlank() // spara knapp - bara aktiv om man skriver namn
            ) {
                Text("Spara")
            }
        },

        dismissButton = { // avbryt knapp stänger utan att spara
            TextButton(onClick = onDismiss) {
                Text("Avbryt")
            }
        }
    )
}