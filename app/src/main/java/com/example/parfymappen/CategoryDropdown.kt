package com.example.parfymappen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    oils: SnapshotStateList<String>,
    commonShape: RoundedCornerShape
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .width(300.dp)
            .padding(vertical = 10.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = {},
            readOnly = true,
            textStyle = TextStyle(
                fontSize = 28.sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            ),
            trailingIcon = {
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
            onDismissRequest = { expanded = false },
            containerColor = Color(0xFFFFF8DC),
            modifier = Modifier
                .heightIn(max = 260.dp)
                .background(Color(0xFFFFF8DC))
                .border(BorderStroke(1.dp, Color.Black), commonShape)
        ) {
            if (oils.isEmpty()) {
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
                oils.forEach { oil ->
                    DropdownMenuItem(
                        text = {
                            OilRow(textFromList = oil)
                        },
                        onClick = {}
                    )
                }
            }
        }
    }
}