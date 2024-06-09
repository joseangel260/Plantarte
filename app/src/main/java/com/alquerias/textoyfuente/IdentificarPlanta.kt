package com.alquerias.textoyfuente

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class IdentificarPlanta : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private val apiKey = "2b10vR3oRpKTLhPnr5DkNc8vje"

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.identificarplantas)

        // Inicializar los elementos de la interfaz de usuario
        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.textView)

        // Configurar el botón para tomar la foto
        val buttonTomarFoto: Button = findViewById(R.id.identificarfoto)
        buttonTomarFoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        // Configurar el botón para identificar la planta
        val buttonIdentificar: Button = findViewById(R.id.buttonIdentificar)
        buttonIdentificar.setOnClickListener {
            val imageBitmap = imageView.drawable.toBitmap()
            identificarPlanta(imageBitmap)
        }
    }

    private fun identificarPlanta(image: Bitmap) {
        val file = bitmapToFile(image)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("images", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url("https://my-api.plantnet.org/v2/identify/all?api-key=$apiKey")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    textView.text = "Error al identificar la planta. Por favor, inténtelo de nuevo."
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val results = jsonResponse.getJSONArray("results")
                        if (results.length() > 0) {
                            val bestMatch = results.getJSONObject(0)
                            val plantName = bestMatch.getJSONObject("species").getString("scientificNameWithoutAuthor")
                            runOnUiThread {
                                textView.text = "Planta identificada: $plantName"
                            }
                        } else {
                            runOnUiThread {
                                textView.text = "No se pudo identificar la planta."
                            }
                        }
                    } else {
                        runOnUiThread {
                            textView.text = "Error en la respuesta del servidor."
                        }
                    }
                } else {
                    runOnUiThread {
                        textView.text = "Error al identificar la planta. Código: ${response.code}"
                    }
                }
            }
        })
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val filesDir = applicationContext.filesDir
        val imageFile = File(filesDir, "temp_image.jpg")
        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return imageFile
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
        }
    }
}
