package com.example.parfymappen

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun IntensityDrops(intensity: String, fontSize: Int = 16) {
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