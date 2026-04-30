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

//mallar - INGREDIENT - oljans namn, antal droppar och vilken kategori den tillhör
//mallar - SAVEDPERFUME - parfymens mall, namn, ingredienser och anteckningar
data class Ingredient(val name: String, val drops: Int, val category: String)
data class SavedPerfume(val name: String, val ingredients: List<Ingredient>, val notes: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("ParfymAppenPrefs", Context.MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            ParfymAppenTheme {
                var currentScreen by remember { mutableStateOf("welcome") }

                val topnotOils = remember { mutableStateListOf<String>() }
                val mellannotOils = remember { mutableStateListOf<String>() }
                val basnotOils = remember { mutableStateListOf<String>() }
                val ovrigtOils = remember { mutableStateListOf<String>() }
                val savedPerfumes = remember { mutableStateListOf<SavedPerfume>() }

                LaunchedEffect(Unit) {
                    loadAppData(sharedPrefs, topnotOils, mellannotOils, basnotOils, ovrigtOils, savedPerfumes)
                }

                LaunchedEffect(topnotOils.size, mellannotOils.size, basnotOils.size, ovrigtOils.size, savedPerfumes.size) {
                    saveAppData(sharedPrefs, topnotOils, mellannotOils, basnotOils, ovrigtOils, savedPerfumes)
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            Image(
                                painter = painterResource(id = R.drawable.img),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                alpha = 0.2f
                            )
                            
                            when (currentScreen) {
                                "welcome" -> StartScreen(
                                    onEnterClick = { currentScreen = "start" }
                                )
                                "start" -> HomeScreen(
                                    onEteriskaOljorClick = { currentScreen = "eteriskaOljor" },
                                    onParfymerClick = { currentScreen = "parfymer" }
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
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveAppData(prefs: SharedPreferences, top: List<String>, mellan: List<String>, bas: List<String>, ovrigt: List<String>, perfumes: List<SavedPerfume>) {
        val oilsJson = JSONObject().apply {
            put("top", JSONArray(top)); put("mellan", JSONArray(mellan)); put("bas", JSONArray(bas)); put("ovrigt", JSONArray(ovrigt))
        }
        val perfumesArray = JSONArray()
        perfumes.forEach { p ->
            val pObj = JSONObject().apply {
                put("name", p.name); put("notes", p.notes)
                val ingArray = JSONArray()
                p.ingredients.forEach { 
                    ingArray.put(JSONObject().apply { put("name", it.name); put("drops", it.drops); put("category", it.category) })
                }
                put("ingredients", ingArray)
            }
            perfumesArray.put(pObj)
        }
        prefs.edit().putString("oils_data", oilsJson.toString()).putString("perfumes_data", perfumesArray.toString()).apply()
    }

    private fun loadAppData(prefs: SharedPreferences, top: SnapshotStateList<String>, mellan: SnapshotStateList<String>, bas: SnapshotStateList<String>, ovrigt: SnapshotStateList<String>, perfumes: SnapshotStateList<SavedPerfume>) {
        prefs.getString("oils_data", null)?.let {
            val json = JSONObject(it)
            val fill = { arr: JSONArray, list: SnapshotStateList<String> ->
                list.clear()
                for (i in 0 until arr.length()) list.add(arr.getString(i))
            }
            fill(json.getJSONArray("top"), top); fill(json.getJSONArray("mellan"), mellan); fill(json.getJSONArray("bas"), bas); fill(json.getJSONArray("ovrigt"), ovrigt)
        } ?: run {
            // Här är oljorna som finns med från start
            top.add("Citron | Mellan")
            top.add("Apelsin | Låg")
            mellan.add("Lavendel | Mellan")
            mellan.add("Jasmin | Hög")
            bas.add("Cederträ | Mellan")
            bas.add("Vanilj | Mellan")
        }

        prefs.getString("perfumes_data", null)?.let {
            val array = JSONArray(it)
            perfumes.clear()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val ingredients = mutableListOf<Ingredient>()
                val ingArray = obj.getJSONArray("ingredients")
                for (j in 0 until ingArray.length()) {
                    val ing = ingArray.getJSONObject(j)
                    ingredients.add(Ingredient(ing.getString("name"), ing.getInt("drops"), ing.getString("category")))
                }
                perfumes.add(SavedPerfume(obj.getString("name"), ingredients, obj.getString("notes")))
            }
        }
    }
}

@Composable
fun HomeScreen(onEteriskaOljorClick: () -> Unit, onParfymerClick: () -> Unit) {
    val commonShape = RoundedCornerShape(12.dp)
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.weight(0.8f)) 
        Box(modifier = Modifier.width(320.dp).clickable { onEteriskaOljorClick() }.background(Color.White.copy(alpha = 0.2f), commonShape).border(BorderStroke(1.dp, Color.Black), commonShape).padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
            Text(text = "ETERISKA OLJOR", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color.Black)
        }
        Spacer(modifier = Modifier.height(40.dp))
        Box(modifier = Modifier.width(320.dp).clickable { onParfymerClick() }.background(Color.White.copy(alpha = 0.2f), commonShape).border(BorderStroke(1.dp, Color.Black), commonShape).padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
            Text(text = "PARFYMER", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color.Black)
        }
        Spacer(modifier = Modifier.weight(1.2f))
    }
}
