package com.example.pokemonapi
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var fetchButton: Button
    private lateinit var pokemonNameTextView: TextView
    private lateinit var statsTextView: TextView
    private lateinit var typesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchButton = findViewById(R.id.fetchButton)
        pokemonNameTextView = findViewById(R.id.pokemonNameTextView)
        statsTextView = findViewById(R.id.statsTextView)
        typesTextView = findViewById(R.id.typesTextView)

        fetchButton.setOnClickListener(View.OnClickListener { fetchRandomPokemon() })
    }

    private fun fetchRandomPokemon() {
        val randomPokemonId = Random().nextInt(151) + 1
        val apiUrl = "https://pokeapi.co/api/v2/pokemon/$randomPokemonId"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(apiUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle API request failure
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.use { responseBody ->
                    try {
                        val responseString = responseBody.string()
                        val pokemonData = JSONObject(responseString)

                        val name = pokemonData.getString("name")
                        val statsArray = pokemonData.getJSONArray("stats")
                        val typesArray = pokemonData.getJSONArray("types")

                        runOnUiThread {
                            // Display the name
                            pokemonNameTextView.text = "Name: $name"

                            // Display the stats
                            val stats = StringBuilder("Stats: ")
                            for (i in 0 until statsArray.length()) {
                                val stat = statsArray.getJSONObject(i)
                                val statName = stat.getJSONObject("stat").getString("name")
                                val statValue = stat.getInt("base_stat")
                                stats.append("$statName: $statValue ")
                            }
                            statsTextView.text = stats.toString()

                            // Display the types
                            val types = StringBuilder("Types: ")
                            for (i in 0 until typesArray.length()) {
                                val type = typesArray.getJSONObject(i)
                                val typeName = type.getJSONObject("type").getString("name")
                                types.append("$typeName ")
                            }
                            typesTextView.text = types.toString()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }
}
