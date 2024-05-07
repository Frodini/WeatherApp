package com.example.weatherapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler

@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val splashTimeOut: Long = 3000 // Tiempo en milisegundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Usamos un Handler para retrasar la apertura de la siguiente actividad
        Handler().postDelayed({
            // Creamos un Intent para abrir la siguiente actividad
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish() // Finalizamos esta actividad para evitar volver atrás
        }, splashTimeOut)
    }
}