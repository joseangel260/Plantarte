package com.alquerias.textoyfuente

import android.graphics.Typeface
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.airbnb.lottie.LottieAnimationView
import androidx.cardview.widget.CardView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TusPlantasActivity : AppCompatActivity() {

    private lateinit var gridLayoutTusPlantas: GridLayout
    private val db = FirebaseFirestore.getInstance()
    private val usuarioActual = FirebaseAuth.getInstance().currentUser
    private var plantasList: List<Map<String, Any>> = emptyList()
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tusplantas_activity)

        gridLayoutTusPlantas = findViewById(R.id.gridLayoutTusPlantas)
        val animationView: LottieAnimationView = findViewById(R.id.animationView)
        val addPhotoButton: ImageButton = findViewById(R.id.agregarfoto)

        leerPlantasUsuario()

        animationView.setOnClickListener {
            compartirPlantas()
        }

        addPhotoButton.setOnClickListener {
            mostrarOpcionesFoto()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }
    }

    private fun mostrarOpcionesFoto() {
        val items = arrayOf<CharSequence>("Tomar foto", "Elegir de la galería", "Cancelar")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Foto")
        builder.setItems(items) { dialog, item ->
            when (item) {
                0 -> dispatchTakePictureIntent()
                1 -> chooseImageFromGallery()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Log.e("TusPlantasActivity", "Error creando el archivo de la foto", ex)
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.alquerias.textoyfuente.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val file = File(currentPhotoPath)
                    if (file.exists()) {
                        val uri = Uri.fromFile(file)
                        solicitarNombrePlanta(uri)
                    }
                }
                REQUEST_IMAGE_GALLERY -> {
                    val uri = data?.data
                    if (uri != null) {
                        solicitarNombrePlanta(uri)
                    }
                }
            }
        }
    }

    private fun solicitarNombrePlanta(uri: Uri) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Nombre de la Planta")

        val input = EditText(this)
        input.hint = "Ingrese el nombre de la planta"
        builder.setView(input)

        builder.setPositiveButton("Aceptar") { dialog, _ ->
            val nombrePlanta = input.text.toString()
            subirImagenAFirebase(uri, nombrePlanta)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun subirImagenAFirebase(uri: Uri, nombrePlanta: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val photoRef = storageRef.child("fotos_plantas/${uri.lastPathSegment}")
        val uploadTask = photoRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            photoRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                guardarEnFirestore(downloadUrl.toString(), nombrePlanta)
            }
        }.addOnFailureListener { exception ->
            Log.e("TusPlantasActivity", "Error subiendo la imagen", exception)
        }
    }

    private fun guardarEnFirestore(url: String, nombrePlanta: String) {
        val planta = hashMapOf(
            "imagen" to url,
            "nombre_comun" to nombrePlanta,
            "nombre_cientifico" to "Nombre Científico",
            "descripcion" to "Descripción",
            "riego" to "Cada semana",
            "plantaId" to (plantasList.size + 1).toString()
        )
        usuarioActual?.let { user ->
            db.collection("users").document(user.uid).collection("plantas")
                .add(planta)
                .addOnSuccessListener {
                    leerPlantasUsuario()
                }
                .addOnFailureListener { exception ->
                    Log.w("TusPlantasActivity", "Error agregando documento", exception)
                }
        }
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
                    Log.w("TusPlantasActivity", "Error al obtener documentos: ", exception)
                }
        }
    }

    private fun mostrarPlantas(plantas: List<Map<String, Any>>) {
        gridLayoutTusPlantas.removeAllViews()
        for (document in plantas) {
            val cardView = CardView(this)
            val layoutParams = GridLayout.LayoutParams()
            layoutParams.width = 0
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT
            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            layoutParams.setMargins(8, 8, 8, 8)
            cardView.layoutParams = layoutParams
            cardView.radius = 16f
            cardView.setCardBackgroundColor(resources.getColor(R.color.card_background))

            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.setBackgroundResource(R.color.content_background)

            val imageUrl = document["imagen"] as? String
            Log.d("TusPlantasActivity", "Cargando imagen desde URL: $imageUrl")

            imageUrl?.let {
                cargarImagenDesdeURL(imageUrl, linearLayout)
            }

            val nombreComunTextView = TextView(this)
            nombreComunTextView.text = "Nombre: ${(document["nombre_comun"] as? String)?.toUpperCase()}"
            nombreComunTextView.setTypeface(null, Typeface.BOLD)
            linearLayout.addView(nombreComunTextView)

            cardView.addView(linearLayout)
            gridLayoutTusPlantas.addView(cardView)
        }
    }

    private fun cargarImagenDesdeURL(imageUrl: String, linearLayout: LinearLayout) {
        val imageView = ImageView(this@TusPlantasActivity)
        try {
            // Agregar registro de depuración para verificar la URL de la imagen
            Log.d("TusPlantasActivity", "Cargando imagen desde la URL: $imageUrl")

            Glide.with(this@TusPlantasActivity)
                .load(imageUrl)
                .fitCenter() // Ajustar la imagen
                .into(imageView)
            linearLayout.addView(imageView)
        } catch (e: Exception) {
            Log.e("TusPlantasActivity", "Error al cargar la imagen", e)
        }
    }

    private fun compartirPlantas() {
        val mensajeBase = "¡Mira las plantas que tengo en mi jardín!\n\n"
        val plantasInfo = plantasList.joinToString(separator = "\n\n") { planta ->
            "- ${planta["nombre_comun"] as? String ?: "Nombre no Disponible"}"
        }

        val mensajeCompleto = mensajeBase + plantasInfo

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, mensajeCompleto)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}
