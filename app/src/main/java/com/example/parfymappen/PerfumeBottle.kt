
package com.example.parfymappen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PerfumeBottle(
    perfume: SavedPerfume,
    isDeleteMode: Boolean,
    rotation: Float,
    onDelete: () -> Unit,
    onOpen: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(95.dp)
            .graphicsLayer {
                rotationZ = if (isDeleteMode) rotation else 0f
            }
            .clickable {
                if (isDeleteMode) onDelete() else onOpen()
            }
    ) {
        Box(
            modifier = Modifier
                .size(16.dp, 5.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(1.dp))
        )

        Box(
            modifier = Modifier
                .size(8.dp, 4.dp)
                .background(MaterialTheme.colorScheme.primary.copy(0.3f))
        )

        Card(
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

                // VÄTSKA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f) // nivå vätska (85%)
                        .align(Alignment.BottomCenter)
                        .background(
                            Color(0xFFB8E0FF).copy(alpha = 0.75f),
                            RoundedCornerShape(
                                bottomStart = 20.dp,
                                bottomEnd = 20.dp
                            )
                        )
                )

                // 📝 TEXT ovanpå
                Column(
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

                    perfume.ingredients.take(3).forEach { ing ->
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("💧", fontSize = 10.sp)

                            Text(
                                text = ing.name.split(" | ")[0],
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
