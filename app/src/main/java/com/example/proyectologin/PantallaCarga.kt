package com.example.proyectologin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class PantallaCarga : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_carga)
        //Segundos que dura la pantalla de carga de la app
        val Duracion = 2500
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }, Duracion.toLong())
    }
}