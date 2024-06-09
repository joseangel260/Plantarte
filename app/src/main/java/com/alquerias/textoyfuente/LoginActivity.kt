package com.alquerias.textoyfuente

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {

    private lateinit var correo: EditText
    private lateinit var contrasena: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        correo = findViewById(R.id.editTextEmail2)
        contrasena = findViewById(R.id.editTextPassword)
        mAuth = FirebaseAuth.getInstance()

        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)
        textViewForgotPassword.setOnClickListener {
            recuperarContrasena()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        Log.d("LoginActivity", "Current user: $currentUser")
    }

    fun iniciarSesion(view: View) {
        val email = correo.text.toString()
        val password = contrasena.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.stringCorreoContra), Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    Toast.makeText(this, getString(R.string.iniciadoSesion), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, getString(R.string.falloLogin), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun recuperarContrasena() {
        val email = correo.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, getString(R.string.ingresarCorreo), Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.emailEnviado), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, getString(R.string.errorEnviarEmail), Toast.LENGTH_SHORT).show()
                }
            }
    }
}
