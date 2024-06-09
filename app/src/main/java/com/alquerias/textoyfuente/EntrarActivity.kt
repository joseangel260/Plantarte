package com.alquerias.textoyfuente

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class EntrarActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entrada)

        val btnEntrar = findViewById<Button>(R.id.BtnEntrar)
        btnEntrar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
