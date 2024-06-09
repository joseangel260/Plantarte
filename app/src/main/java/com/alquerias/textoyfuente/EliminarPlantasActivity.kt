package com.alquerias.textoyfuente

import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Typeface

class EliminarPlantasActivity : AppCompatActivity() {

    private lateinit var gridEliminarPlantas: GridLayout
    private val db = FirebaseFirestore.getInstance()
    private val usuarioActual = FirebaseAuth.getInstance().currentUser
    private var plantasList: List<Map<String, Any>> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eliminar_plantas_activity)

        gridEliminarPlantas = findViewById(R.id.gridLayoutEliminarPlantas)

        leerPlantasUsuario()
    }

    private fun leerPlantasUsuario() {
        usuarioActual?.let { user ->
            db.collection("users").document(user.uid).collection("plantas")
                .get()
                .addOnSuccessListener { documents ->
                    plantasList = documents.map { it.data }
                    mostrarPlantas(plantasList)
                }
                .addOnFailureListener { exception ->
                    Log.w("EliminarPlantasActivity", "Error al obtener documentos: ", exception)
                }
        }
    }

    private fun mostrarPlantas(plantas: List<Map<String, Any>>) {
        gridEliminarPlantas.removeAllViews()
        for (document in plantas) {
            val nombreComunTextView = TextView(this).apply {
                text = (document["nombre_comun"] as? String)?.toUpperCase()
                setTypeface(null, Typeface.BOLD)
                textSize = 18f
                setPadding(16, 16, 16, 16)
                setOnClickListener {
                    eliminarPlanta(document["nombre_comun"] as? String)
                }
            }

            gridEliminarPlantas.addView(nombreComunTextView)
        }
    }

    private fun eliminarPlanta(nombre: String?) {
        usuarioActual?.let { user ->
            nombre?.let {
                db.collection("users").document(user.uid).collection("plantas")
                    .whereEqualTo("nombre_comun", it)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            db.collection("users").document(user.uid).collection("plantas").document(document.id)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Planta eliminada.", Toast.LENGTH_SHORT).show()
                                    leerPlantasUsuario()  // Refrescar la lista de plantas
                                }
                                .addOnFailureListener { e ->
                                    Log.w("EliminarPlantasActivity", "Error eliminando documento", e)
                                    Toast.makeText(this, "Error eliminando planta", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w("EliminarPlantasActivity", "Error obteniendo documento", e)
                        Toast.makeText(this, "Error obteniendo planta", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
