package com.example.weather

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class LoadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load) // Certifique-se de que R seja importado corretamente.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Defina um temporizador para controlar o tempo de exibição da tela de abertura.
        Handler().postDelayed({
            val sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
            val userInfoExists = sharedPreferences.getBoolean("UserInfoExists", false)

            if (userInfoExists) {
                // Informações do usuário já inseridas, inicie a com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.com.example.weather.MainActivity.
                val intent = Intent(this@LoadActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // Finalize a atividade de carregamento.
            } else {
                // Informações do usuário ainda não inseridas, mostre a tela de informações.
                val intent = Intent(this@LoadActivity, LocalizacaoUser::class.java)
                startActivity(intent)
                finish() // Finalize a atividade de carregamento.
            }
        }, 6000) // Tempo em milissegundos (7 segundos no exemplo).
    }
}



