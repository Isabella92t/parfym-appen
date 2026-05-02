package com.example.parfymappen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun CategoryDropdown(
    title: String,
    oils: SnapshotStateList<String>, // lista med oljor som visas i dropdown
    commonShape: RoundedCornerShape,// bestämer om raderingsläget är aktivt
    isDeleteMode: Boolean,
    onRequestDelete: (String) -> Unit // funktionen som körs ifall man vill ta bort en olja
) {
    var expanded by remember { mutableStateOf(false) } //öppen eller stängd dropdown?

    // håller reda på vilken som klickats
    var selectedOilForDelete by remember { mutableStateOf<String?>(null) }

    ExposedDropdownMenuBox( // dropdown komponenten
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .width(300.dp)
            .padding(vertical = 10.dp)
    ) {
        OutlinedTextField( // när dropdown stängd
            value = title,
            onValueChange = {},
            readOnly = true, // användaren ska inte kunna skriva, bara se
            textStyle = TextStyle(
                fontSize = 28.sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            ),
            trailingIcon = {// visar pil
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = commonShape,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFFFE8A3).copy(alpha = 0.75f),
                focusedContainerColor = Color(0xFFFFE8A3).copy(alpha = 0.75f),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }, // listan öppnas
            containerColor = Color(0xFFFFF8DC),
            modifier = Modifier
                .heightIn(max = 260.dp)
                .background(Color(0xFFFFF8DC))
                .border(BorderStroke(1.dp, Color.Black), commonShape)
        ) {
            if (oils.isEmpty()) { // om lista tom
                DropdownMenuItem(
                    text = {
                        Text(
                            "Inga oljor ännu",
                            fontSize = 22.sp,
                            color = Color.Gray
                        )
                    },
                    onClick = {}
                )
            } else {
                oils.toList().forEach { oil -> // loopar igenom oljelistan
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                // visa rutan endast i delete mode annars inte
                                if (isDeleteMode) {
                                    Box(
                                        modifier = Modifier // kryssrutan
                                            .size(28.dp)
                                            .background(Color.White, RoundedCornerShape(4.dp))
                                            .border(
                                                BorderStroke(1.dp, Color.Black),
                                                RoundedCornerShape(4.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedOilForDelete == oil) { // X i rutan
                                            Text(
                                                text = "X",
                                                fontSize = 20.sp,
                                                color = Color.Black
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))
                                }

                                Box(modifier = Modifier.weight(1f)) { // oilrow - kompontent som återanvänds överallt
                                    OilRow(textFromList = oil)
                                }
                            }
                        },
                        onClick = {
                            if (isDeleteMode) {
                                selectedOilForDelete = oil
                                onRequestDelete(oil)
                            }
                        }
                    )
                }
            }
        }
    }
}