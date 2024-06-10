package com.alquerias.textoyfuente

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide
import android.graphics.Typeface
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class PlantasFirebase : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var linearLayoutPlantas: LinearLayout
    private lateinit var searchView: SearchView
    private lateinit var btnAgregarFiltro: ImageButton
    private var plantasList: List<Map<String, Any>> = emptyList()
    private val usuarioActual = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantas_fire)

        linearLayoutPlantas = findViewById(R.id.linearLayoutPlantas)
        searchView = findViewById(R.id.searchView)
        btnAgregarFiltro = findViewById(R.id.agregarfiltro)

        leerPlantas()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtroPlantas(newText ?: "")
                return true
            }
        })

        btnAgregarFiltro.setOnClickListener {
            mostrarDialogoFiltroZonas()
        }
    }

    private fun leerPlantas() {
        val docRef = db.collection("plantas")

        docRef.get()
            .addOnSuccessListener { documents ->
                plantasList = documents.map { it.data }
                mostrarPlantas(plantasList)
            }
            .addOnFailureListener { exception ->
                Log.w("PlantasFirebase", "Error al obtener documentos: ", exception)
            }
    }

    private fun mostrarPlantas(plantas: List<Map<String, Any>>) {
        linearLayoutPlantas.removeAllViews()
        for (document in plantas) {
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
            Log.d("PlantasFirebase", "Cargando imagen desde URL: $imageUrl")

            imageUrl?.let {
                // Cargar la imagen desde la URL utilizando Glide
                cargarImagenDesdeURL(imageUrl, linearLayout)
            }

            val nombreCientificoTextView = TextView(this)
            nombreCientificoTextView.text = "Nombre Científico: ${document["nombre_cientifico"]}"
            nombreCientificoTextView.setTypeface(null, Typeface.BOLD)
            linearLayout.addView(nombreCientificoTextView)

            val nombreComunTextView = TextView(this)
            nombreComunTextView.text = "Nombre Común: ${(document["nombre_comun"] as? String)?.toUpperCase()}"
            nombreComunTextView.setTypeface(null, Typeface.BOLD)
            linearLayout.addView(nombreComunTextView)

            val descripcionTextView = TextView(this)
            descripcionTextView.text = document["descripcion"] as? String
            linearLayout.addView(descripcionTextView)

            val button = Button(this)
            button.text = "Añadir Planta"
            button.setBackgroundResource(R.drawable.boton_redon_entrar)
            button.backgroundTintList = resources.getColorStateList(R.color.boton_color_tint, null)
            button.setTextColor(resources.getColor(R.color.boton_text_color, null))
            button.textSize = 24f
            button.setTypeface(resources.getFont(R.font.ghibotalk))
            button.setOnClickListener {
                anyadirPlantaAlUsuario(document)
            }
            linearLayout.addView(button)

            cardView.addView(linearLayout)
            linearLayoutPlantas.addView(cardView)
        }
    }

    private fun cargarImagenDesdeURL(imageUrl: String, linearLayout: LinearLayout) {
        val imageView = ImageView(this@PlantasFirebase)
        try {
            // Agregar registro de depuración para verificar la URL de la imagen
            Log.d("PlantasFirebase", "Cargando imagen desde la URL: $imageUrl")

            Glide.with(this@PlantasFirebase)
                .load(imageUrl)
                .into(imageView)
            linearLayout.addView(imageView)
        } catch (e: Exception) {
            Log.e("PlantasFirebase", "Error al cargar la imagen", e)
        }
    }

    private fun filtroPlantas(query: String) {
        val filteredPlantas = plantasList.filter {
            val nombreCientifico = it["nombre_cientifico"] as? String ?: ""
            val nombreComun = it["nombre_comun"] as? String ?: ""
            nombreCientifico.contains(query, true) || nombreComun.contains(query, true)
        }
        mostrarPlantas(filteredPlantas)
    }

    private fun mostrarDialogoFiltroZonas() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_filtro_zonas, null)
        builder.setView(dialogLayout)

        val checkboxAtlantico = dialogLayout.findViewById<CheckBox>(R.id.checkbox_clima_atlantico)
        val checkboxContinental = dialogLayout.findViewById<CheckBox>(R.id.checkbox_clima_continental)
        val checkboxMediterraneo = dialogLayout.findViewById<CheckBox>(R.id.checkbox_clima_mediterraneo)
        val checkboxSemiarido = dialogLayout.findViewById<CheckBox>(R.id.checkbox_clima_semiarido)
        val checkboxSubtropical = dialogLayout.findViewById<CheckBox>(R.id.checkbox_clima_subtropical)
        val checkboxMontana = dialogLayout.findViewById<CheckBox>(R.id.checkbox_clima_montana)

        val btnAplicarFiltro = dialogLayout.findViewById<Button>(R.id.btn_aplicar_filtro)

        val dialog = builder.create()

        btnAplicarFiltro.setOnClickListener {
            val selectedZones = mutableListOf<String>()

            if (checkboxAtlantico.isChecked) selectedZones.add("clima atlantico")
            if (checkboxContinental.isChecked) selectedZones.add("clima continental")
            if (checkboxMediterraneo.isChecked) selectedZones.add("clima mediterraneo")
            if (checkboxSemiarido.isChecked) selectedZones.add("clima semiarido")
            if (checkboxSubtropical.isChecked) selectedZones.add("clima subtropical")
            if (checkboxMontana.isChecked) selectedZones.add("clima de montaña")

            filtrarPlantasPorZona(selectedZones)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun filtrarPlantasPorZona(selectedZones: List<String>) {
        if (selectedZones.isEmpty()) {
            mostrarPlantas(plantasList) // Si no se selecciona ninguna zona, muestra todas las plantas
            return
        }

        val docRef = db.collection("plantas")
        docRef.whereIn("zona_geografica", selectedZones).get()
            .addOnSuccessListener { documents ->
                val filteredPlantas = documents.map { it.data }
                mostrarPlantas(filteredPlantas)
            }
            .addOnFailureListener { exception ->
                Log.w("PlantasFirebase", "Error al obtener documentos filtrados: ", exception)
            }
    }

    private fun anyadirPlantaAlUsuario(planta: Map<String, Any>) {
        usuarioActual?.let { user ->
            val plantaId = planta["plantaId"] as? String ?: return
            val plantaData = mapOf(
                "plantaId" to plantaId,
                "nombre_comun" to planta["nombre_comun"],
                "imagen" to planta["imagen"]
            )
            db.collection("users").document(user.uid).collection("plantas").document(plantaId)
                .set(plantaData)
                .addOnSuccessListener {
                    Log.d("PlantasFirebase", "Planta añadida al usuario")
                    Toast.makeText(this, "Planta añadida", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Log.w("PlantasFirebase", "Error al añadir planta al usuario: ", exception)
                    Toast.makeText(this, "Error al añadir planta", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
