package com.alquerias.textoyfuente

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class JardinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tujardin)


        val buttonAddPlant = findViewById<Button>(R.id.buttonAddPlant)
        buttonAddPlant.setOnClickListener {
            val intent = Intent(this, PlantasFirebase::class.java)
            startActivity(intent)
        }

        val buttonIdentificarPlant = findViewById<Button>(R.id.buttonidentificarPlant)
        buttonIdentificarPlant.setOnClickListener {
            val intent = Intent(this, IdentificarPlanta::class.java)
            startActivity(intent)
        }

        val botonTusPlantas = findViewById<Button>(R.id.buttonTuJardin)
        botonTusPlantas.setOnClickListener {
            val intent = Intent(this, TusPlantasActivity::class.java)
            startActivity(intent)
        }

        val buttonEliminarPlantas = findViewById<Button>(R.id.buttonidentificararPlant)
        buttonEliminarPlantas.setOnClickListener {
            val intent = Intent(this, EliminarPlantasActivity::class.java)
            startActivity(intent)
        }
    }
}
