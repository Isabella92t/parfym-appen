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
    val goldColor = Color(0xFFDAA520)
    val lightGoldBg = Color(0xFFFFF9E3) // Mycket ljus guldig bakgrund

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onEnterClick() } // hela skärmen klickbar för att gå vidare
    ) {
        // bild som täcker hela skärmen med genomskinlighet
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
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
                    .width(300.dp) // kontrollera bredd här istället
                    .height(90.dp) // snygg knapphöjd
                    .background(Color(0xFFFFE8A3).copy(alpha = 0.6f), commonShape)
                    .border(BorderStroke(1.dp, Color.Black), commonShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "START",
                    fontSize = 55.sp, // lite mer balanserad
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.weight(0.14f))


        }
    }
}
