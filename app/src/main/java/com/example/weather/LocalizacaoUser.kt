package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class LocalizacaoUser : AppCompatActivity() {

    private lateinit var lottieView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.localizacao_user)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        lottieView = findViewById(R.id.localAnim)
        val buttomSubmit = findViewById<Button>(R.id.buttomSubmit)
        val edt_city = findViewById<EditText>(R.id.edt_city)

        buttomSubmit.setOnClickListener {
            val city = edt_city.text.toString()

            if (city.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.Main) {
                    try {
                        // Chame a API de localização aqui
                        val apiKey = "4484e2d97a947678d15e18242d5ff5d4" // Replace with your actual API key
                        val coordinates = obterCoordenadasPorCidade(city, apiKey)

                        // Se as coordenadas foram obtidas com sucesso, inicie a com.example.weather.com.example.weather.com.example.weather.MainActivity
                        if (coordinates != null) {
                            // Criar o intent antes de usá-lo
                            val intent = Intent(this@LocalizacaoUser, MainActivity::class.java)

                            // Adicione a cidade ao intent
                            intent.putExtra("city", city)

                            // Inicie a com.example.weather.com.example.weather.com.example.weather.MainActivity com as informações da cidade
                            startActivity(intent)
                        } else {
                            // Trate o caso em que as coordenadas não puderam ser obtidas
                            Toast.makeText(
                                this@LocalizacaoUser,
                                "Coordenadas não disponíveis",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        // Trate os erros, se necessário
                        Log.e("LocalizacaoUser", "Erro: ${e.message}")
                        Toast.makeText(
                            this@LocalizacaoUser,
                            "Erro: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
suspend fun obterCoordenadasPorCidade(cidade: String, apiKey: String): Pair<Double, Double>? {
    val geocodingUrl =
        "https://api.openweathermap.org/geo/1.0/direct?q=$cidade&limit=1&appid=4484e2d97a947678d15e18242d5ff5d4"

    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(geocodingUrl)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val json = JsonParser.parseString(responseBody).asJsonArray
                if (json != null && json.size() > 0) {
                    // Exemplo genérico de acesso a coordenadas, ajuste conforme a estrutura real do JSON
                    val lat = json[0].asJsonObject["lat"].asDouble
                    val lon = json[0].asJsonObject["lon"].asDouble
                    Pair(lat, lon)
                } else {
                    Log.e("LocalizacaoUser", "Resposta da API de geocodificação inválida: $responseBody")
                    null
                }
            } else {
                Log.e("LocalizacaoUser", "Erro ao obter coordenadas. Código de resposta: ${response.code}")
                null
            }
        } catch (e: Exception) {
            Log.e("LocalizacaoUser", "Erro ao obter coordenadas: ${e.message}")
            null
        }
    }
}

