package com.example.parfymappen

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parfymappen.ui.theme.ParfymAppenTheme
import org.json.JSONArray
import org.json.JSONObject

data class Ingredient(val name: String, val drops: Int, val category: String) //ingredienser i parfym, namn, droppar, kategori
data class SavedPerfume(val name: String, val ingredients: List<Ingredient>, val notes: String) // sparad parfym

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("ParfymAppenPrefs", Context.MODE_PRIVATE) // sparar data lokalt i mobilen

        enableEdgeToEdge()
        setContent {
            ParfymAppenTheme {
                var currentScreen by remember { mutableStateOf("welcome") }  // listor

                val topnotOils = remember { mutableStateListOf<String>() }
                val mellannotOils = remember { mutableStateListOf<String>() }
                val basnotOils = remember { mutableStateListOf<String>() }
                val ovrigtOils = remember { mutableStateListOf<String>() }
                val savedPerfumes = remember { mutableStateListOf<SavedPerfume>() } // minne medan appen körs

                LaunchedEffect(Unit) {
                    loadAppData(sharedPrefs, topnotOils, mellannotOils, basnotOils, ovrigtOils, savedPerfumes) // ladda data när appen startar
                }

                LaunchedEffect(topnotOils.size, mellannotOils.size, basnotOils.size, ovrigtOils.size, savedPerfumes.size) {
                    saveAppData(sharedPrefs, topnotOils, mellannotOils, basnotOils, ovrigtOils, savedPerfumes) // sparar datan automatiskt när listorna ändras
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {   // appens bakgrundsyta
                    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent) { innerPadding -> // standarstruktur
                        Box(modifier = Modifier.padding(innerPadding)) { // för att lägga bakgrundsbilden bakom skärmarna
                            Image(
                                painter = painterResource(id = R.drawable.img),  // bakgrundsbild
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                alpha = 0.2f // genomskinlig
                            )

                            when (currentScreen) { // navigation, visar rätt skärm beroende på värde i currentscreen
                                "welcome" -> StartScreen(
                                    onEnterClick = { currentScreen = "start" }
                                )

                                "start" -> HomeScreen(
                                    onEteriskaOljorClick = { currentScreen = "eteriskaOljor" },
                                    onParfymerClick = { currentScreen = "parfymer" },
                                    onInformationClick = { currentScreen = "information" }
                                )

                                "eteriskaOljor" -> EssensoljorScreen(
                                    onBackClick = { currentScreen = "start" },
                                    topnotOils = topnotOils,
                                    mellannotOils = mellannotOils,
                                    basnotOils = basnotOils,
                                    ovrigtOils = ovrigtOils
                                )

                                "parfymer" -> ParfymerScreen(
                                    onBackClick = { currentScreen = "start" },
                                    savedPerfumes = savedPerfumes,
                                    topnotOils = topnotOils,
                                    mellannotOils = mellannotOils,
                                    basnotOils = basnotOils,
                                    ovrigtOils = ovrigtOils
                                )

                                "information" -> InformationScreen(
                                    onBackClick = { currentScreen = "start" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveAppData(  // sparar appens data i mobilen
        prefs: SharedPreferences, //platsen där datan skapas
        top: List<String>,
        mellan: List<String>,
        bas: List<String>,
        ovrigt: List<String>,   // listor med oljor
        perfumes: List<SavedPerfume> // listan med sparade parfymer
    ) {
        val oilsJson = JSONObject().apply { // här sparas oljorna - de görs om till JSON
            put("top", JSONArray(top))
            put("mellan", JSONArray(mellan))
            put("bas", JSONArray(bas))
            put("ovrigt", JSONArray(ovrigt))
        }

        val perfumesArray = JSONArray() // sparar parfymerna

        perfumes.forEach { p ->
            val pObj = JSONObject().apply {
                put("name", p.name)
                put("notes", p.notes)

                val ingArray = JSONArray() // skapar ett JSON-objekt för en parfym
                p.ingredients.forEach {
                    ingArray.put(
                        JSONObject().apply { // JSON-lista med parfymens ingredienser
                            put("name", it.name)
                            put("drops", it.drops)
                            put("category", it.category)
                        }
                    )
                }

                put("ingredients", ingArray)
            }

            perfumesArray.put(pObj) // lägger ingredienslista i parfymobjektet
        }

        prefs.edit()
            .putString("oils_data", oilsJson.toString())
            .putString("perfumes_data", perfumesArray.toString())
            .apply() // här sparas datan i SharedPreferences, eftersom den sparar text så används .toString
    }

    private fun loadAppData(  // läser tillbaka data från mobilen och fyller listorna igen
        prefs: SharedPreferences,
        top: SnapshotStateList<String>,
        mellan: SnapshotStateList<String>,
        bas: SnapshotStateList<String>,
        ovrigt: SnapshotStateList<String>,
        perfumes: SnapshotStateList<SavedPerfume>
    ) {
        prefs.getString("oils_data", null)?.let { // hämtar text från sparat sen innan, null om inget finns sparat
            val json = JSONObject(it)

            val fill = { arr: JSONArray, list: SnapshotStateList<String> ->
                list.clear()
                for (i in 0 until arr.length()) {
                    list.add(arr.getString(i)) // funktion som tömmer listan, lägger in alla värden från JSON
                }
            }

            fill(json.getJSONArray("top"), top) // fyll listorna - tar JSON - lägger in i listorna
            fill(json.getJSONArray("mellan"), mellan)
            fill(json.getJSONArray("bas"), bas)
            fill(json.getJSONArray("ovrigt"), ovrigt)
        } ?: run { // om ingen data finns så kör detta:
            top.add("Citron | Mellan")
            top.add("Apelsin | Låg")
            mellan.add("Lavendel | Mellan")
            mellan.add("Jasmin | Hög")
            bas.add("Cederträ | Mellan")
            bas.add("Vanilj | Mellan")
        }

        prefs.getString("perfumes_data", null)?.let { // ladda parfymer, hämtar sparade
            val array = JSONArray(it)
            perfumes.clear() // gör text till JSON-array

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i) // loopar genom varje parfym, hämtar en i taget
                val ingredients = mutableListOf<Ingredient>()
                val ingArray = obj.getJSONArray("ingredients") //hämtar ingredienslistan

                for (j in 0 until ingArray.length()) {
                    val ing = ingArray.getJSONObject(j) // loopar genom ingredienser
                    ingredients.add(
                        Ingredient(
                            ing.getString("name"),
                            ing.getInt("drops"),
                            ing.getString("category") //skapar ingrediens
                        )
                    )
                }

                perfumes.add( // skapar parfym
                    SavedPerfume(
                        obj.getString("name"),
                        ingredients,
                        obj.getString("notes")
                    )
                )
            }
        }
    }
}

@Composable
fun HomeScreen( // appens huvudmeny efter startsida, funktioner som körs när användaren klickar på knapparna
    onEteriskaOljorClick: () -> Unit,
    onParfymerClick: () -> Unit,
    onInformationClick: () -> Unit
) {
    val commonShape = RoundedCornerShape(100.dp) // rundande hörn

    Column( // placerar menyvalen vertikalt och centrerat
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.8f))

        Box(   // menyknapp
            modifier = Modifier
                .width(320.dp)
                .clickable { onEteriskaOljorClick() }
                .background(Color(0xFFFFE8A3).copy(alpha = 0.6f), commonShape)
                .border(BorderStroke(1.dp, Color.Black), commonShape)
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ETERISKA OLJOR",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box( // menyknapp
            modifier = Modifier
                .width(320.dp)
                .clickable { onParfymerClick() }
                .background(Color(0xFFFFE8A3).copy(alpha = 0.6f), commonShape)
                .border(BorderStroke(1.dp, Color.Black), commonShape)
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PARFYMER",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box( // menyknapp
            modifier = Modifier
                .width(320.dp)
                .clickable { onInformationClick() }
                .background(Color(0xFFFFE8A3).copy(alpha = 0.6f), commonShape)
                .border(BorderStroke(1.dp, Color.Black), commonShape)
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "INFORMATION",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.weight(1.2f))
    }
}

