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
    buttonBlue: Color, //färg till knappar (ANVÄNDS NU SOM GRÖN!)
    inputTextStyle: TextStyle,
    labelTextStyle: TextStyle
) {
    val lightSilver = Color(0xFFF6F6F6)
    val softSilver = Color(0xFFEAEAEA)

    var newOilName by remember { mutableStateOf("") } // sparar det användaren skriver
    var selectedIntensity by remember { mutableStateOf<String?>(null) } // sparar vald intensitet
    var intensityExpanded by remember { mutableStateOf(false) } // styr dropdown, false=stängd, true=öppen

    val categories = listOf("Topnoter", "Mellannoter", "Basnoter", "Övrigt")
    val selectedCategoriesForAdd = remember { mutableStateMapOf<String, Boolean>() }
    val intensities = listOf("Låg", "Mellan", "Hög") // val för dropdowns

    AlertDialog( // pop-up rutan
        onDismissRequest = onDismiss,
        containerColor = lightSilver,
        shape = commonShape,
        title = {
            Text(
                "Lägg till ny olja",
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = newOilName,
                    onValueChange = { newOilName = it },
                    placeholder = {
                        Text("Namn", color = Color.Gray, fontSize = 24.sp)
                    },
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

                Text(
                    "Välj kategorier:",
                    style = labelTextStyle,
                    fontWeight = FontWeight.Bold
                )

                categories.forEach { cat ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .clickable {
                                selectedCategoriesForAdd[cat] =
                                    !(selectedCategoriesForAdd[cat] ?: false)
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White, RoundedCornerShape(4.dp))
                                .border(
                                    BorderStroke(1.dp, Color.Black),
                                    RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedCategoriesForAdd[cat] == true) {
                                Text("X", fontSize = 28.sp, color = Color.Black)
                            }
                        }

                        Text(
                            text = cat,
                            color = Color.Black,
                            fontSize = 26.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                Text(
                    "Intensitet:",
                    style = labelTextStyle,
                    fontWeight = FontWeight.Bold
                )

                ExposedDropdownMenuBox(
                    expanded = intensityExpanded,
                    onExpandedChange = {
                        intensityExpanded = !intensityExpanded
                    }
                ) {
                    OutlinedTextField(
                        onValueChange = {},
                        readOnly = true,
                        textStyle = TextStyle(fontSize = 26.sp, color = Color.Black),
                        placeholder = {
                            Text("Välj intensitet", color = Color.Gray)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(intensityExpanded)
                        },
                        value = selectedIntensity?.let {
                            "$it ${"💧".repeat(
                                when (it) {
                                    "Låg" -> 1
                                    "Mellan" -> 2
                                    "Hög" -> 3
                                    else -> 1
                                }
                            )}"
                        } ?: "",
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
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
                        containerColor = softSilver
                    ) {
                        intensities.forEach { intensity ->
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

        confirmButton = {
            Button(
                onClick = {
                    if (newOilName.isNotBlank()) {
                        val savedText =
                            "$newOilName | ${selectedIntensity ?: "Mellan"}"

                        if (selectedCategoriesForAdd["Topnoter"] == true) {
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

                        onDismiss()
                    }
                },
                enabled = newOilName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBlue, // 🔥 GRÖN / ACCENT
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Black),
                shape = commonShape
            ) {
                Text("Spara")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Avbryt", color = Color.Black)
            }
        }
    )
}