package com.alquerias.textoyfuente

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : BaseActivity() {

    private lateinit var textViewConsejo: TextView
    private val handler = Handler()
    private var contador = 0

    private val consejos: List<String> by lazy {
        listOf(
            getString(R.string.consejo_1),
            getString(R.string.consejo_2),
            getString(R.string.consejo_3),
            getString(R.string.consejo_4)
        )
    }

    private val cambioConsejoRunnable = object : Runnable {
        override fun run() {
            contador = (contador + 1) % consejos.size
            textViewConsejo.text = consejos[contador]
            handler.postDelayed(this, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        textViewConsejo = findViewById(R.id.textViewConsejo)
        handler.post(cambioConsejoRunnable)

        val buttonTuplanta = findViewById<Button>(R.id.buttonTuplanta)
        buttonTuplanta.setOnClickListener {
            val intent = Intent(this, PlantasFirebase::class.java)
            startActivity(intent)
        }

        val buttonTuJardin = findViewById<Button>(R.id.buttonTuJardin)
        buttonTuJardin.setOnClickListener {
            val intent = Intent(this, TusPlantasActivity::class.java)
            startActivity(intent)
        }

        val buttonGuiaPlantar = findViewById<Button>(R.id.buttonGuiaPlantar)
        buttonGuiaPlantar.setOnClickListener {
            val intent = Intent(this, GuiaPlantarActivity::class.java)
            startActivity(intent)
        }

        // botón para Plagas
        val buttonPlagas = findViewById<Button>(R.id.buttonPlagas)
        buttonPlagas.setOnClickListener {
            val intent = Intent(this, PlagasActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_user -> {
                    val intent = Intent(this, AjustesActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_plants -> {
                    val intent = Intent(this, PlantasFirebase::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_garden -> {
                    val intent = Intent(this, JardinActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(cambioConsejoRunnable)
    }
}
