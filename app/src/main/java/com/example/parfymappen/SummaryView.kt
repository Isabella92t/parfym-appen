package com.example.parfymappen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryView(
    onBack: () -> Unit,
    onEditOils: () -> Unit,
    name: String,
    notes: String,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    selectedOils: MutableMap<String, Boolean>,
    oilDrops: MutableMap<String, Int>,
    topnotOils: List<String>,
    mellannotOils: List<String>,
    basnotOils: List<String>,
    ovrigtOils: List<String>,
    shape: RoundedCornerShape,
    textStyle: TextStyle,
    buttonColor: Color
) {
    var total = 0
    var t = 0
    var m = 0
    var b = 0
    var o = 0

    listOf(
        topnotOils to "Topnoter",
        mellannotOils to "Mellannoter",
        basnotOils to "Basnoter",
        ovrigtOils to "Övrigt"
    ).forEach { (list, catName) ->
        list.forEach { oil ->
            val key = "$catName-$oil"
            if (selectedOils[key] == true) {
                val d = oilDrops[key] ?: 0
                total += d
                when (catName) {
                    "Topnoter" -> t += d
                    "Mellannoter" -> m += d
                    "Basnoter" -> b += d
                    "Övrigt" -> o += d
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier.size(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Black),
            shape = shape,
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, shape),
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Topp" to t, "Mellan" to m, "Bas" to b, "Övrigt" to o)
                    .forEach { (label, drops) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = label,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Text(
                                "${if (total > 0) (drops * 100) / total else 0}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        listOf(
            topnotOils to "Topnoter",
            mellannotOils to "Mellannoter",
            basnotOils to "Basnoter",
            ovrigtOils to "Övrigt"
        ).forEach { (list, cat) ->
            list.filter { selectedOils["$cat-$it"] == true }.forEach { oil ->
                val key = "$cat-$oil"
                val current = oilDrops[key] ?: 0
                val intensity = if (oil.contains(" | ")) {
                    oil.split(" | ")[1]
                } else {
                    "Mellan"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = oil.split(" | ")[0],
                                fontSize = 24.sp,
                                color = Color.Black
                            )

                            Spacer(Modifier.width(8.dp))


                        }

                        Text(
                            text = cat,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    IconButton(
                        onClick = {
                            if (current > 0) oilDrops[key] = current - 1
                        }
                    ) {
                        Text(
                            "-",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(75.dp)
                    ) {
                        Text("💧", fontSize = 60.sp)

                        Text(
                            text = current.toString(),
                            color = Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.offset(y = 10.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            oilDrops[key] = current + 1
                        }
                    ) {
                        Text(
                            "+",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = {
                Text("Övriga anteckningar", color = Color.Gray, fontSize = 24.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = shape,
            textStyle = textStyle,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black
            )
        )

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text("Spara", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onEditOils,
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE0E0E0),
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text("Redigera oljor", fontSize = 20.sp)
        }
    }
}