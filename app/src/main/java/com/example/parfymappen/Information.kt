package com.example.parfymappen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InformationScreen(onBackClick: () -> Unit) { //informationsvy, bakåtknapp
    val commonShape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Black),
                shape = commonShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Tillbaka",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Skapa din egen signaturdoft",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Att blanda egen parfym är en kreativ process där du kombinerar intuition med doftens struktur. Genom att använda eteriska oljor skapar du en naturlig och personlig doft utan syntetiska tillsatser.",
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Doftnoternas arkitektur",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "En balanserad parfym är uppbyggd av tre lager, så kallade noter, som avdunstar olika snabbt:",
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "• Toppnoter (30%): Det första intrycket. De är lätta, friska och sprudlande, till exempel citron, bergamott eller pepparmynta. De försvinner oftast efter 15–30 minuter.",
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "• Mellannoter/Hjärtnoter (50%): Doftens karaktär. När toppnoten lagt sig träder hjärtat fram, till exempel lavendel, ros eller kanel. Dessa binder ihop hela doften.",
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "• Basnoter (20%): Doftens fundament. Tunga och rika noter som ger djup och får parfymen att sitta kvar i timmar, till exempel sandelträ, vanilj eller patchouli.",
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Blandningsförhållanden (Eau de Parfum)",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "För en Eau de Parfum bör koncentrationen av doftoljor ligga på ca 15–20%. Resten består av en bärarvätska.",
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Recept för ca 10 ml:",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "• Eteriska oljor: 40–60 droppar totalt för alla noter.",
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "• Bas: Fyll upp resten med ren alkohol, till exempel finsprit eller luktfri vodka, eller en doftfri olja som jojobaolja för en parfymroller.",
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Så gör du:",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "1. Droppa i oljorna: Börja med basnoterna, följt av mellannoterna och avsluta med toppnoterna.",
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "2. Tillsätt basen: Häll i alkoholen eller bäraroljan och skaka försiktigt.",
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "3. Mognad: Låt flaskan vila mörkt och svalt. Doften utvecklas och “gifter sig” bäst om den får stå i minst 48 timmar, gärna flera veckor.",
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}