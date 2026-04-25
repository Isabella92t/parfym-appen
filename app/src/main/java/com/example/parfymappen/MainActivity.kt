package com.example.parfymappen

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.parfymappen.ui.theme.ParfymAppenTheme
import org.json.JSONArray
import org.json.JSONObject

// Mallar för att spara data
data class Ingredient(val name: String, val drops: Int, val category: String)
data class SavedPerfume(val name: String, val ingredients: List<Ingredient>, val notes: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sharedPrefs = getSharedPreferences("ParfymAppenPrefs", Context.MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            ParfymAppenTheme {
                var currentScreen by remember { mutableStateOf("start") }
                
                // MINNEN
                val topnotOils = remember { mutableStateListOf<String>() }
                val mellannotOils = remember { mutableStateListOf<String>() }
                val basnotOils = remember { mutableStateListOf<String>() }
                val ovrigtOils = remember { mutableStateListOf<String>() }
                val savedPerfumes = remember { mutableStateListOf<SavedPerfume>() }

                // LADDA DATA
                LaunchedEffect(Unit) {
                    val savedOils = sharedPrefs.getString("oils_data", null)
                    if (savedOils != null) {
                        val json = JSONObject(savedOils)
                        jsonToList(json.getJSONArray("top"), topnotOils)
                        jsonToList(json.getJSONArray("mellan"), mellannotOils)
                        jsonToList(json.getJSONArray("bas"), basnotOils)
                        jsonToList(json.getJSONArray("ovrigt"), ovrigtOils)
                    } else {
                        topnotOils.addAll(listOf("Citron | Mellan", "Basilika | Hög"))
                        mellannotOils.addAll(listOf("Ros | Låg", "Kanel | Mellan"))
                        basnotOils.addAll(listOf("Cederträ | Hög", "Vanilj | Mellan"))
                        ovrigtOils.addAll(listOf("Peppar | Hög"))
                    }

                    val savedPerf = sharedPrefs.getString("perfumes_data", null)
                    if (savedPerf != null) {
                        val array = JSONArray(savedPerf)
                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            val ingredients = mutableListOf<Ingredient>()
                            val ingArray = obj.getJSONArray("ingredients")
                            for (j in 0 until ingArray.length()) {
                                val ingObj = ingArray.getJSONObject(j)
                                ingredients.add(Ingredient(ingObj.getString("name"), ingObj.getInt("drops"), ingObj.getString("category")))
                            }
                            savedPerfumes.add(SavedPerfume(obj.getString("name"), ingredients, obj.getString("notes")))
                        }
                    }
                }

                // SPARA AUTOMATISKT
                LaunchedEffect(topnotOils.size, mellannotOils.size, basnotOils.size, ovrigtOils.size, savedPerfumes.size) {
                    val oilsJson = JSONObject()
                    oilsObjToJSON(oilsJson, topnotOils, mellannotOils, basnotOils, ovrigtOils)
                    
                    val perfumesArray = JSONArray()
                    savedPerfumes.forEach { p ->
                        val pObj = JSONObject()
                        pObj.put("name", p.name)
                        pObj.put("notes", p.notes)
                        val ingArray = JSONArray()
                        p.ingredients.forEach { 
                            val iObj = JSONObject()
                            iObj.put("name", it.name); iObj.put("drops", it.drops); iObj.put("category", it.category)
                            ingArray.put(iObj)
                        }
                        pObj.put("ingredients", ingArray)
                        perfumesArray.put(pObj)
                    }
                    sharedPrefs.edit().putString("oils_data", oilsJson.toString()).putString("perfumes_data", perfumesArray.toString()).apply()
                }

                // BAKGRUNDSFÄRG, LAYOUT
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (currentScreen) {
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

    private fun jsonToList(array: JSONArray, targetList: SnapshotStateList<String>) {
        targetList.clear()
        for (i in 0 until array.length()) { targetList.add(array.getString(i)) }
    }

    private fun oilsObjToJSON(obj: JSONObject, top: List<String>, mellan: List<String>, bas: List<String>, ovrigt: List<String>) {
        obj.put("top", JSONArray(top))
        obj.put("mellan", JSONArray(mellan))
        obj.put("bas", JSONArray(bas))
        obj.put("ovrigt", JSONArray(ovrigt))
    }
}

@Composable
fun HomeScreen(onEteriskaOljorClick: () -> Unit, onParfymerClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Button(onClick = onEteriskaOljorClick, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) { Text("Eteriska oljor") }
        Button(onClick = onParfymerClick, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) { Text("Parfymer") }
    }
}
