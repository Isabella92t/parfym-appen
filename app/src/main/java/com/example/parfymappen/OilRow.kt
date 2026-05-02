package com.example.parfymappen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OilRow(textFromList: String, category: String? = null, isSearch: Boolean = false) {
    val parts = textFromList.split(" | ") // delan upp olja och intensitet
    val name = parts[0]
    val intensity = if (parts.size > 1) parts[1] else "Mellan"
    val textColor = if (isSearch) Color.Gray else Color.Black // visar olja plus droppar
    // isSearch - true grå text , false svart text

    Row( // namn, kategori, droppar på en rad
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(name, fontSize = 24.sp, color = textColor)

        if (category != null) { // visas bara om den skickas in
            Spacer(modifier = Modifier.width(8.dp))
            Text(category, fontSize = 16.sp, color = textColor)
        }

        Spacer(modifier = Modifier.weight(1f))
        IntensityDrops(intensity)
    }
}