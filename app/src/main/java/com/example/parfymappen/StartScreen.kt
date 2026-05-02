package com.example.parfymappen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartScreen(onEnterClick: () -> Unit) {
    val commonShape = RoundedCornerShape(100.dp)

    val silver = Color(0xFFEAEAEA) // 🔥 NY färg

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onEnterClick() } // hela skärmen är klickbar för att komma vidare
    ) {
        // bild som täcker hela skärmen med genomskinlighet
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop, //fyller hela skärmen
            alpha = 0.7f
        )

        // Startknapp i en box
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1.4f))

            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(90.dp)
                    .background(silver.copy(alpha = 0.8f), commonShape) // 🔥 ändrad
                    .border(BorderStroke(1.dp, Color.Black), commonShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "START",
                    fontSize = 55.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.weight(0.14f))
        }
    }
}