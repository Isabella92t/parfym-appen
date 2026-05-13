package com.example.parfymappen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PerfumeBottle(
    perfume: SavedPerfume,
    isDeleteMode: Boolean,
    rotation: Float, // behålls för kompatibilitet
    onDelete: () -> Unit,
    onOpen: () -> Unit

    //sparad parfym som en liten flaska
) {
    var selectedForDelete by remember { mutableStateOf(false) } // håller koll på om flaskan markerats för borttagning

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(95.dp)
            .clickable {
                if (isDeleteMode) {
                    selectedForDelete = true
                    onDelete()
                } else {
                    onOpen()
                }
            }
    ) {
        Box( // kork och hals
            modifier = Modifier
                .size(16.dp, 5.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.dp))
        )

        Box(
            modifier = Modifier // flaskans hals
                .size(8.dp, 4.dp)
                .background(MaterialTheme.colorScheme.primary.copy(0.3f))
        )

        Card( // själva flaskkroppen
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(
                topStart = 2.dp,
                topEnd = 2.dp,
                bottomStart = 20.dp,
                bottomEnd = 20.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.35f)),
            elevation = CardDefaults.cardElevation(2.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier // vätskan i flaskan
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .align(Alignment.BottomCenter)
                        .background(
                            Color(0xFFB8E0FF).copy(alpha = 0.75f),
                            RoundedCornerShape(
                                bottomStart = 20.dp,
                                bottomEnd = 20.dp
                            )
                        )
                )

                if (isDeleteMode) { // visas om papperskorgläget är aktivt
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                            .size(24.dp)
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .border(
                                BorderStroke(1.dp, Color.Black),
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedForDelete) {
                            Text(
                                text = "X", // visas när flaskan klickats i
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

                Column( // text i flaskan
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = perfume.name,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    perfume.ingredients.take(3).forEach { ing -> // visa bara 3 först ingredienser
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💧",
                                fontSize = 10.sp,
                                modifier = Modifier.width(14.dp)
                            )

                            Text(
                                text = ing.name.split(" | ")[0], // ingrediensens namn
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
