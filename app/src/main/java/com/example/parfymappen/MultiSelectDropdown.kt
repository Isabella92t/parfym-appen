package com.example.parfymappen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectDropdown(
    title: String,
    options: List<String>,
    selectedStates: MutableMap<String, Boolean>,
    shape: RoundedCornerShape,
    textStyle: TextStyle
) {
    var expanded by remember { mutableStateOf(false) }
    val numSelected = options.count { selectedStates["$title-$it"] == true }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (numSelected > 0) "$title ($numSelected)" else "Välj $title",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .width(300.dp)
                .padding(vertical = 4.dp),
            shape = shape,
            textStyle = textStyle,
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
            onDismissRequest = { expanded = false },
            containerColor = Color(0xFFFFF8DC),
            modifier = Modifier.border(1.dp, Color.Black, shape)
        ) {
            options.forEach { oil ->
                val key = "$title-$oil"
                val intensity = if (oil.contains(" | ")) oil.split(" | ")[1] else "Mellan"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedStates[key] =
                                !(selectedStates[key] ?: false)
                        }
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .border(BorderStroke(1.dp, Color.Black)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedStates[key] == true) {
                            Text("X", fontSize = 28.sp, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        oil.split(" | ")[0],
                        modifier = Modifier.weight(1f),
                        color = Color.Black,
                        fontSize = 24.sp
                    )


                }
            }
        }
    }
}