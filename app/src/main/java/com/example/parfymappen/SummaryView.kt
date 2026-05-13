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
fun SummaryView( // paramentrar, onback, onedit, onsave - gå tillbaka, gå tillbaka och ändra, spara
    onBack: () -> Unit,
    onEditOils: () -> Unit,
    name: String,
    notes: String, // notes styr parfymens namn och anteckningar
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    selectedOils: MutableMap<String, Boolean>, // vilja oljor som är valda
    oilDrops: MutableMap<String, Int>, // håller koll på hur många droppar av varje olja
    topnotOils: List<String>,
    mellannotOils: List<String>,
    basnotOils: List<String>,
    ovrigtOils: List<String>,
    shape: RoundedCornerShape,
    textStyle: TextStyle,
    buttonColor: Color
) {
    val lightSilver = Color(0xFFF6F6F6)
    val softSilver = Color(0xFFEAEAEA)

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
    ).forEach { (list, catName) -> // här skapas key
        list.forEach { oil ->
            val key = "$catName-$oil"
            if (selectedOils[key] == true) {
                val d = oilDrops[key] ?: 0 // droppas hämtas
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

    Column( // uppifrån och ner
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Button( //bakåtknapp
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
            text = name, //namn på parfym
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card( //box med design
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, shape),
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = lightSilver
            )
        ) {
            Row( // saker bredvid varandra
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Topp" to t, "Mellan" to m, "Bas" to b, "Övrigt" to o)  // procentdelen
                    .forEach { (label, drops) -> // loopar koden igenom varje kategori, först topnoter med droppar, sen mellannoter med droppar osv.
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = label,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black
                            )
                            Text(
                                "${if (total > 0) (drops * 100) / total else 0}%", //procent
                                fontSize = 18.sp
                            )
                        }
                    }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        listOf( // går igenom alla oljekategorier
            topnotOils to "Topnoter",
            mellannotOils to "Mellannoter",
            basnotOils to "Basnoter",
            ovrigtOils to "Övrigt"
        ).forEach { (list, cat) -> // loopar igenom varje kategori
            list.filter { selectedOils["$cat-$it"] == true }.forEach { oil -> // filtrer fram endast valda oljor
                val key = "$cat-$oil" // skapar key för oljan
                val current = oilDrops[key] ?: 0 // antal droppar hämtas för just den oljan
                val intensity = if (oil.contains(" | ")) { // här hämtas intensitet från oljetexten
                    oil.split(" | ")[1]
                } else {
                    "Mellan"
                }

                Row( // här skapas en rad för varje olja
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = oil.split(" | ")[0],  //oljenamn och kategori
                                fontSize = 24.sp,
                                color = Color.Black
                            )

                            Spacer(Modifier.width(8.dp))
                        }

                        Text(
                            text = cat,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }

                    IconButton(
                        onClick = {
                            if (current > 0) oilDrops[key] = current - 1 // minusknappen vid droppen
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
                        Text("💧", fontSize = 50.sp) // stora droppen

                        Text(
                            text = current.toString(),
                            color = Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.offset(y = 10.dp) // visar antal droppar
                        )
                    }

                    IconButton(
                        onClick = {
                            oilDrops[key] = current + 1 // plusknappen vid droppen
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
            onValueChange = onNotesChange, // anteckningar om parfymen
            placeholder = {
                Text("Övriga anteckningar", color = Color.Gray, fontSize = 24.sp)
            },
            modifier = Modifier.fillMaxWidth(),  // textfältet
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
            ) // colors styr hur textfältet ser ut beroende på om det är aktivt eller inte
        )

        Button( // spara knappen
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

        Button( // redigera oljor knapp
            onClick = onEditOils,
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = softSilver,
                contentColor = Color.Black
            ),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text("Redigera parfym", fontSize = 20.sp)
        }
    }
}