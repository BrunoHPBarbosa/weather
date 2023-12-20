package com.example.weather

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var loadingView: LottieAnimationView
    private lateinit var card1: CardView
    private lateinit var cidadeTextView: TextView
    private lateinit var diaSemanaTextView: TextView
    private lateinit var temperaturaAtualTextView: TextView
    private lateinit var descricaoClimaTextView: TextView
    private lateinit var tempMinTextView: TextView
    private lateinit var tempMaxTextView: TextView
    private lateinit var linearLayout: LinearLayout
    private lateinit var mainLayout: ConstraintLayout

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        card1 = findViewById(R.id.card1)
        cidadeTextView = findViewById(R.id.cidade_do_user)
        diaSemanaTextView = findViewById(R.id.textView)
        temperaturaAtualTextView = findViewById(R.id.temp_atual)
        descricaoClimaTextView = findViewById(R.id.desc_clima)
        tempMinTextView = findViewById(R.id.min_tela)
        tempMaxTextView = findViewById(R.id.max_tela)
        linearLayout = findViewById(R.id.linear)
        loadingView = LoadingAnimationUtil.showLoadingAnimation(this, findViewById(android.R.id.content), R.layout.load2)
        mainLayout = findViewById(R.id.mainLayout)

        // Tornando o mainLayout invisível inicialmente
        mainLayout.visibility = View.INVISIBLE
        val cidade = intent.getStringExtra("city")
        cidadeTextView.text = cidade

        // Defina o clique do botão programaticamente
        val botaoVoltar = findViewById<ImageView>(R.id.botao_voltar)
        botaoVoltar.setOnClickListener {
            changeLocation(it)
        }

        if (cidade != null) {
            Log.d("MainActivity", "Iniciando processamento para a cidade: $cidade")
            lifecycleScope.launch {
                try {
                    delay(5000)
                    val coordenadas = obterCoordenadasPorCidade(cidade)
                    val dadosClimaAtual = coordenadas?.let { obterDadosClimaPorCoordenadas(it) }
                    val previsaoSemanal = coordenadas?.let { obterPrevisaoSemanalPorCoordenadas(it) }

                    processarDadosClimaAtual(dadosClimaAtual)
                    processarPrevisaoSemanal(previsaoSemanal)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Erro durante processamento assíncrono: ${e.message}", e)
                } finally {
                    // Após a conclusão do processamento, tornar o mainLayout visível e ocultar o LottieAnimationView
                    mainLayout.visibility = View.VISIBLE
                    LoadingAnimationUtil.hideLoadingAnimation(loadingView)
                }
            }
        } else {
            Log.e("MainActivity", "Erro: cidade é nula")
        }
    }

    fun changeLocation(view: View) {


        // Encerra a atividade atual (MainActivity) e volta para LocalizacaoUserActivity
        val intent = Intent(this, LocalizacaoUser::class.java)
        startActivity(intent)
        finish()
    }

    private suspend fun obterCoordenadasPorCidade(cidade: String): Pair<Double, Double>? {
        Log.d("MainActivity", "Obtendo coordenadas para a cidade: $cidade")
        val apiKey = "0893cb7831be8dc702007025fb6e46d6"
        val response = criarRetrofit().create(OpenWeatherMapService::class.java)
            .obterCoordenadas(cidade, 1, apiKey)

        if (response.isSuccessful && response.body() != null) {
            val lat = response.body()!![0].asJsonObject["lat"].asDouble
            val lon = response.body()!![0].asJsonObject["lon"].asDouble
            Log.d("MainActivity", "Coordenadas obtidas: ($lat, $lon)")
            return Pair(lat, lon)
        } else {
            Log.e("LocalizacaoUser", "Erro ao obter coordenadas. Código de resposta: ${response.code()}")
            return null
        }
    }

    private suspend fun obterDadosClimaPorCoordenadas(coordenadas: Pair<Double, Double>): CurrentWeatherResponse? {
        val apiKey = "a216f84bcd62edc0125d3e1d00a7383d"
        val service = criarRetrofit().create(OpenWeatherMapService::class.java)
        val response = service.obterDadosClimaAtual(coordenadas.first, coordenadas.second, apiKey)

        return if (response.isSuccessful && response.body() != null) {
            val responseData = response.body()
            Log.d("MainActivity", "Dados climáticos recebidos: $responseData")
            responseData
        } else {
            Log.e("MainActivity", "Erro ao obter dados climáticos. Código de resposta: ${response.code()}")
            null
        }
    }

    private suspend fun obterPrevisaoSemanalPorCoordenadas(coordenadas: Pair<Double, Double>): FiveDayWeatherResponse? {
        val apiKey = "a216f84bcd62edc0125d3e1d00a7383d"
        val service = criarRetrofit().create(OpenWeatherMapService::class.java)
        val response = service.obterPrevisaoSemanal(coordenadas.first, coordenadas.second, apiKey)

        return if (response.isSuccessful && response.body() != null) {
            val responseData = response.body()
            Log.d("MainActivity", "Previsão semanal recebida: $responseData")
            responseData
        } else {
            Log.e("MainActivity", "Erro ao obter previsão semanal. Código de resposta: ${response.code()}")
            null
        }
    }

    private fun processarDadosClimaAtual(responseDataAtual: CurrentWeatherResponse?) {
        if (responseDataAtual != null) {
            val cidade = responseDataAtual.name ?: "Nome da Cidade Indisponível"
            val primeiroItem = responseDataAtual.weather.firstOrNull()

            if (primeiroItem != null) {
                val temperaturaAtual = responseDataAtual.main.temp.formatTemperature()
                val descricaoClima = primeiroItem.description
                val tempMinHoje = responseDataAtual.main.temp_min.formatTemperature()
                val tempMaxHoje = responseDataAtual.main.temp_max.formatTemperature()
                val diaSemanaAtual: String = obterDiaSemana(System.currentTimeMillis())
                val dataAtual: String = obterDataFormatada(responseDataAtual.dt * 1000)


                findViewById<TextView>(R.id.temp_atual).text = "$temperaturaAtual °C"
                findViewById<TextView>(R.id.cidade_do_user).text = cidade
                findViewById<TextView>(R.id.textView).text = "$diaSemanaAtual, $dataAtual"
                findViewById<TextView>(R.id.desc_clima).text = descricaoClima
                findViewById<TextView>(R.id.min_tela).text = " $tempMinHoje °C"
                findViewById<TextView>(R.id.max_tela).text = " $tempMaxHoje °C"

                atualizarBackgroundLayout(responseDataAtual)

                // Atualizar o primeiro card
                val minCodeTextView = card1.findViewById<TextView>(R.id.mincode)
                val maxCodeTextView = card1.findViewById<TextView>(R.id.maxcode)
                val codeImageView = card1.findViewById<ImageView>(R.id.codeimg)

                minCodeTextView.text = "${responseDataAtual.main.temp_min.formatTemperature()} °C"
                maxCodeTextView.text = "${responseDataAtual.main.temp_max.formatTemperature()} °C"
                atualizarImageClimaCard(codeImageView, primeiroItem.id)
            } else {
                Log.e("MainActivity", "Lista de previsões vazia")
            }
        } else {
            Log.e("MainActivity", "Erro ao obter dados climáticos")
        }
    }


    private fun processarPrevisaoSemanal(previsoes: FiveDayWeatherResponse?) {
        if (previsoes != null && previsoes.list != null) {
            val linearLayout = findViewById<LinearLayout>(R.id.linear)
            linearLayout.removeAllViews() // Limpa todos os cards existentes antes de adicionar os novos

            val cal = Calendar.getInstance()

            for (i in 0 until previsoes.list.size) {
                val previsao = previsoes.list[i]

                // Configurar o calendário com a data da previsão atual
                cal.timeInMillis = previsao.dt * 1000

                // Inflar o CardView dinamicamente
                val cardView = LayoutInflater.from(this).inflate(R.layout.card_layout, null) as CardView
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(16, 0, 16, 16) // Adiciona margem inferior entre os cards
                cardView.layoutParams = layoutParams
                cardView.radius = 10f
                cardView.cardElevation = 8f
                cardView.setContentPadding(16, 16, 16, 16)
                cardView.id = View.generateViewId()

                // Adicione os elementos ao CardView
                val minCodeTextView = cardView.findViewById<TextView>(R.id.mincode)
                val maxCodeTextView = cardView.findViewById<TextView>(R.id.maxcode)
                val codeImageView = cardView.findViewById<ImageView>(R.id.codeimg)
                val descTextView = cardView.findViewById<TextView>(R.id.desc)

                // Obter o nome do dia da semana e a descrição do clima
                val nomeDiaSemana = SimpleDateFormat("EEEE", Locale.getDefault()).format(cal.time)


                // Exibir o nome do dia da semana e a descrição do clima no mesmo TextView (id desc)
                descTextView.text = "$nomeDiaSemana"
                minCodeTextView.text = "${previsao.main?.temp_min?.formatTemperature()} °C"
                maxCodeTextView.text = "${previsao.main?.temp_max?.formatTemperature()} °C"

                // Certifique-se de ajustar para usar as propriedades corretas de weather (se existirem)
                atualizarImageClimaCard(codeImageView, previsao.weather?.firstOrNull()?.id ?: 0)

                linearLayout.addView(cardView)
            }
        }
    }

    private fun atualizarBackgroundLayout(responseDataAtual: CurrentWeatherResponse?) {
        if (responseDataAtual != null) {
            val isDayTime = isDayTime(responseDataAtual.sys?.sunrise, responseDataAtual.sys?.sunset)
            val codigoClimaticoAtual = responseDataAtual.weather.firstOrNull()?.id
            val resourceId = when {
                //dados dia
                isDayTime -> when (codigoClimaticoAtual) {
                    in 200..232 -> R.drawable.diachuvoso
                    in 300..321 -> R.drawable.garoadia
                    in 500..531 -> R.drawable.chuvalevedia
                    in 600..622 -> R.drawable.nevedia
                    in 701..781 -> R.drawable.dianublado
                    800 -> R.drawable.ceulimpodia
                    in 801..804 -> R.drawable.poucasnuvensdia
                    else -> R.drawable.dia
                }
                //dados noite
                else -> when (codigoClimaticoAtual) {
                    in 200..232 -> R.drawable.noitechuvosa1
                    in 300..321 -> R.drawable.tardedechuva
                    in 500..531 -> R.drawable.chuvalevenoite
                    in 600..622 -> R.drawable.noitedeinverno
                    in 701..781 -> R.drawable.nevoanoite
                    800 -> R.drawable.poucasnuvensnoite1
                    in 801..804 -> R.drawable.ceunoite1
                    else -> R.drawable.noitedecalor1
                }
            }

            findViewById<ConstraintLayout>(R.id.mainLayout).setBackgroundResource(resourceId)
        }
    }

    private fun isDayTime(sunrise: Long?, sunset: Long?): Boolean {
        val currentTime = System.currentTimeMillis() / 1000
        return currentTime in (sunrise ?: 0)..(sunset ?: 0)
    }






    private fun atualizarImageClimaCard(imageView: ImageView, codigoClimatico: Int) {
        when (codigoClimatico) {
            in 200..232 -> imageView.setImageResource(R.drawable.thunderstorm)
            in 300..321 -> imageView.setImageResource(R.drawable.poucachivaicon)
            in 500..531 -> imageView.setImageResource(R.drawable.raining)
            in 600..622 -> imageView.setImageResource(R.drawable.snowicon)
            in 701..781 -> imageView.setImageResource(R.drawable.nubladoicon)
            800 -> imageView.setImageResource(R.drawable.sunicon)
            in 801..804 -> imageView.setImageResource(R.drawable.poucanuvenicon)
            else -> imageView.setImageResource(R.drawable.sunicon)
        }
    }

    private fun criarRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun Double.formatTemperature(): String {
        val roundedValue = this.toInt()
        val formattedValue = if (roundedValue >= 10) {
            roundedValue.toString().substring(0, 2)
        } else {
            "0$roundedValue"
        }
        return formattedValue
    }



    private fun obterDataFormatada(timestamp: Long): String {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formato.format(Date(timestamp))
    }
    private fun obterDiaSemana(timestamp: Long): String {
        val formato = SimpleDateFormat("EEEE", Locale.getDefault())
        return formato.format(Date(timestamp))
    }
}