package com.alquerias.textoyfuente

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView

class PlagasActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var linearLayoutPlagas: LinearLayout
    private lateinit var searchView: SearchView
    private var plagasList: List<Map<String, Any>> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plagas)

        linearLayoutPlagas = findViewById(R.id.linearLayoutPlagas)
        searchView = findViewById(R.id.searchView)  // Inicializar searchView

        leerPlagas()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtroPlagas(newText ?: "")
                return true
            }
        })
    }

    private fun leerPlagas() {
        val docRef = db.collection("plagas")

        docRef.get()
            .addOnSuccessListener { documents ->
                plagasList = documents.map { it.data }
                mostrarPlagas(plagasList)
            }
            .addOnFailureListener { exception ->
                Log.w("PlagasActivity", "Error al obtener documentos: ", exception)
            }
    }

    private fun mostrarPlagas(plagas: List<Map<String, Any>>) {
        linearLayoutPlagas.removeAllViews()
        for (document in plagas) {
            val cardView = CardView(this)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 0, 0, 16)
            cardView.layoutParams = layoutParams
            cardView.radius = 16f
            cardView.setCardBackgroundColor(resources.getColor(R.color.card_background))

            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.setBackgroundResource(R.color.content_background)

            val imageUrl = document["imagen"] as? String
            Log.d("PlagasActivity", "Cargando imagen desde URL: $imageUrl")

            imageUrl?.let {
                // Cargar la imagen desde la URL utilizando Glide
                cargarImagenDesdeURL(it, linearLayout)
            }

            val nombreTextView = TextView(this)
            nombreTextView.text = "Nombre: ${document["nombre"] ?: "N/A"}"
            nombreTextView.setTypeface(null, Typeface.BOLD)
            nombreTextView.textSize = 18f  // Ajusta el tamaño del texto si es necesario
            linearLayout.addView(nombreTextView)


            val infoTextView = TextView(this)
            infoTextView.text = "Info: ${(document["info"] as? String)?.uppercase() ?: "N/A"}"

            linearLayout.addView(infoTextView)

            val solucionTextView = TextView(this)
            solucionTextView.text = "Solución: ${document["solucion"] ?: "N/A"}"
            infoTextView.setTypeface(null, Typeface.BOLD)
            linearLayout.addView(solucionTextView)

            cardView.addView(linearLayout)
            linearLayoutPlagas.addView(cardView)
        }
    }

    private fun cargarImagenDesdeURL(imageUrl: String, linearLayout: LinearLayout) {
        val imageView = ImageView(this@PlagasActivity)
        try {
            Log.d("PlagasActivity", "Cargando imagen desde la URL: $imageUrl")

            Glide.with(this@PlagasActivity)
                .load(imageUrl)
                .into(imageView)
            linearLayout.addView(imageView)
        } catch (e: Exception) {
            Log.e("PlagasActivity", "Error al cargar la imagen", e)
        }
    }

    private fun filtroPlagas(query: String) {
        val filteredPlagas = plagasList.filter {
            val nombre = it["nombre"] as? String ?: ""
            val info = it["info"] as? String ?: ""
            nombre.contains(query, true) || info.contains(query, true)
        }
        mostrarPlagas(filteredPlagas)
    }
}
