package com.alquerias.textoyfuente

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Locale
import androidx.preference.PreferenceManager
import com.squareup.picasso.Picasso


class AjustesActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var nombreUsuarioTextView: TextView
    private lateinit var fotoUserImageView: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private val storage = FirebaseStorage.getInstance().reference
 //   val storage_path = "fotos/*";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ajustes_activity)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        nombreUsuarioTextView = findViewById(R.id.user_name)
        fotoUserImageView = findViewById(R.id.user_photo)

        cargarPerfil()

        fotoUserImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        if (findViewById<FrameLayout>(R.id.settings_container) != null) {
            if (savedInstanceState != null) {
                return
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, AjustesPreferencias())
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data

            val userId = auth.currentUser?.uid ?: return
            // Modificar la ruta
            val photoRef = storage.child("fotos/$userId.jpg")

            photoRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        val photoUrl = uri.toString()
                        db.collection("users").document(userId).update("fotoUrl", photoUrl)
                            .addOnSuccessListener {
                                cargarPerfil()
                                Toast.makeText(this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al actualizar foto de perfil", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al subir la foto", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun cargarPerfil() {
        val user = auth.currentUser
        val userId = user?.uid

        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("nombre")
                        nombreUsuarioTextView.text = name ?: getString(R.string.usuario_no_encontrado)
                        val photoUrl = document.getString("fotoUrl")
                        if (!photoUrl.isNullOrEmpty()) {
                            Picasso.get().load(photoUrl).into(fotoUserImageView)
                        } else {
                            Picasso.get().load(R.drawable.planta).into(fotoUserImageView)
                        }
                    }
                }
                .addOnFailureListener {
                    nombreUsuarioTextView.text = getString(R.string.error_cargar_usuario)
                }
        }
    }


    fun idioma(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        editor.putString("language", lang)
        editor.apply()

        recreate()
    }
}
