package com.alquerias.textoyfuente

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idioma(idiomaGuardado())
    }


    private fun idioma(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    private fun idiomaGuardado(): String {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getString("language", "en") ?: "en"
    }
}
