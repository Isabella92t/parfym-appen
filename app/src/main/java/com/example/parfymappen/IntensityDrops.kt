package com.example.parfymappen

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun IntensityDrops(intensity: String, fontSize: Int = 16) {
    val count = when (intensity.trim()) { // when - val baserat på värde
        "Låg" -> 1
        "Mellan" -> 2
        "Hög" -> 3
        else -> 1
    }

    // bestämmer hur många droppar som ska visas beroende på intensitet
    Row {
        repeat(count) { //repeat - loopar flera gånger
            Text(text = "💧", fontSize = fontSize.sp)
        }
    }
}