package com.alquerias.textoyfuente

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AjustesPreferencias : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var db: FirebaseFirestore

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferencias_usuario, rootKey)

        db = FirebaseFirestore.getInstance()

        // obtener la preferencia de cambio de nombre de usuario
        val changeUsernamePreference = findPreference<Preference>("change_username")

        // establecer un listener para la preferencia de cambio de username
        changeUsernamePreference?.setOnPreferenceClickListener {
            // Mostrar mensaje emergente
            dialogoCambiarUsername()
            true
        }

        //  preferencia cambio de contraseña
        val changePasswordPreference = findPreference<Preference>("change_password")

        // listener para la preferencia de cambio de contraseña
        changePasswordPreference?.setOnPreferenceClickListener {
            // mensaje emergente para cambiar la contraseña
            cambiarContrasenya()
            true
        }

        // eliminar cuenta
        val deleteAccountPreference = findPreference<Preference>("delete_account")

        // un listener para eliminar cuenta
        deleteAccountPreference?.setOnPreferenceClickListener {
            eliminarCuenta()
            true
        }
    }

    private fun cambiarContrasenya() {
        // Construir un cuadro de diálogo emergente para cambiar la contraseña
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cambiar Contraseña")

        // Crear un layout para el cuadro de diálogo
        val dialogLayout = layoutInflater.inflate(R.layout.dialogo_cambiar_password, null)
        builder.setView(dialogLayout)

        val contrasenyaActualInput = dialogLayout.findViewById<EditText>(R.id.current_password)
        val nuevaContrasenyaInput = dialogLayout.findViewById<EditText>(R.id.new_password)
        val confirmarContrasenyaInput = dialogLayout.findViewById<EditText>(R.id.confirm_new_password)

        builder.setPositiveButton("Cambiar") { _, _ ->
            val contrasenyaActual = contrasenyaActualInput.text.toString()
            val nuevaContrasenya = nuevaContrasenyaInput.text.toString()
            val confirmarContrasenya = confirmarContrasenyaInput.text.toString()

            if (nuevaContrasenya != confirmarContrasenya) {
                showToast("Las contraseñas no coinciden")
                return@setPositiveButton
            }

            // Verificar la contraseña actual y cambiar a la nueva contraseña
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null && contrasenyaActual.isNotEmpty() && nuevaContrasenya.isNotEmpty()) {
                val credential = EmailAuthProvider.getCredential(user.email!!, contrasenyaActual)
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            user.updatePassword(nuevaContrasenya)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        showToast("Contraseña cambiada exitosamente")
                                    } else {
                                        showToast("Error al cambiar la contraseña")
                                    }
                                }
                        } else {
                            showToast("Error al verificar la contraseña actual")
                        }
                    }
            } else {
                showToast("Por favor, ingrese ambas contraseñas")
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun eliminarCuenta() {
        //cuadro emergente para eliminar la cuenta
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Cuenta")
        builder.setMessage("¿Estás seguro de que deseas eliminar tu cuenta permanentemente?")

        builder.setPositiveButton("Eliminar") { _, _ ->
            eliminarUserAccount()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun dialogoCambiarUsername() {
        // cuadro para cambiar el nombre de usuario
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cambiar Nombre de Usuario")

        // layout para el cuadro de diálogo
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_change_username_custom, null)
        builder.setView(dialogLayout)

        val nuevoNombreUsuarioInput = dialogLayout.findViewById<EditText>(R.id.new_username)

        builder.setPositiveButton("Cambiar") { _, _ ->
            val nuevoUsername = nuevoNombreUsuarioInput.text.toString()
            actualizarUsername(nuevoUsername)
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun actualizarUsername(newUserName: String) {
        // Actualizar el nombre de usuario en la base de datos
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        userId?.let {
            val userRef = db.collection("users").document(it)

            userRef.update("nombre", newUserName)
                .addOnSuccessListener {
                    showToast("Nombre de usuario actualizado correctamente")
                }
                .addOnFailureListener {
                    showToast("Error al actualizar el nombre de usuario")
                }
        }
    }

    private fun eliminarUserAccount() {
        // Implementar la lógica para eliminar la cuenta
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Cuenta eliminada exitosamente")
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    showToast("Error al eliminar la cuenta")
                }
            }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key == "language") {
            val selectedLanguage = sharedPreferences.getString(key, "en") ?: "en"
            (activity as? AjustesActivity)?.idioma(selectedLanguage)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
