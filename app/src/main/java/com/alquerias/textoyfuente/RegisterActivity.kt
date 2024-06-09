package com.alquerias.textoyfuente

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var nombre: EditText
    private lateinit var correo: EditText
    private lateinit var contrasena: EditText
    private lateinit var contrasenaConfirmacion: EditText

    private val fotoDefaultUser = "https://www.shutterstock.com/image-vector/blank-avatar-photo-place-holder-600nw-1095249842.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nombre = findViewById(R.id.editTextName)
        correo = findViewById(R.id.editTextEmail)
        contrasena = findViewById(R.id.editTextPassword)
        contrasenaConfirmacion = findViewById(R.id.editTextRepeatPassword)

        val textViewLoginLink = findViewById<View>(R.id.textViewLoginLink)
        textViewLoginLink.setOnClickListener {
            openLoginActivity()
        }
    }

    fun registrarUsuario(view: View) {
        if (contrasena.text.toString() == contrasenaConfirmacion.text.toString()) {
            mAuth.createUserWithEmailAndPassword(correo.text.toString(), contrasena.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        val userId = user?.uid

                        val userMap = hashMapOf(
                            "nombre" to nombre.text.toString(),
                            "correo" to correo.text.toString(),
                            "fotoUrl" to fotoDefaultUser
                        )

                        userId?.let {
                            db.collection("users").document(it).set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, getString(R.string.usuarioCreado), Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, getString(R.string.error_guardar_usuario), Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, getString(R.string.notSame_pass), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}