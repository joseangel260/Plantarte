package com.alquerias.textoyfuente

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH = 4000L // Duración del logo en milisegundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // usa un handler para abrir la EntradaActivity después del tiempo puesto
        Handler(Looper.getMainLooper()).postDelayed({
            // Crea un Intent para abrir la EntradaActivity
            val intent = Intent(this, EntrarActivity::class.java)
            startActivity(intent)
            // Cierra la MainActivity para que no pueda volver atras después de abrir la EntradaActivity
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }
}
