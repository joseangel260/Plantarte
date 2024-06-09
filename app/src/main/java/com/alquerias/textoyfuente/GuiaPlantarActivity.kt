package com.alquerias.textoyfuente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class GuiaPlantarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guia_plantar)

        // Preparación del suelo
        val textViewPaso1 = findViewById<TextView>(R.id.textViewPaso1)
        val imageViewPaso1 = findViewById<ImageView>(R.id.imageViewPaso1)
        textViewPaso1.text = """
            1. PREPARACIÓN DEL SUELO:
            - Remover el suelo con una azada o un rastrillo para airearlo.
            - Añadir abono orgánico o compost para mejorar la fertilidad del suelo.
        """.trimIndent()
        Glide.with(this).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTgPChL2GOHSURsn_0cE5Wc416Z9shNFZn8fA&s").into(imageViewPaso1)

        // Plantación
        val textViewPaso2 = findViewById<TextView>(R.id.textViewPaso2)
        val imageViewPaso2 = findViewById<ImageView>(R.id.imageViewPaso2)
        textViewPaso2.text = """
            2. PlANTACIÓN:
            - Realizar hoyos de 2-3 cm de profundidad con un palillo o un dedo.
            - Colocar las semillas en los hoyos y cubrirlas ligeramente con tierra.
            - Si estás trasplantando, coloca la planta con su cepellón en el hoyo.
        """.trimIndent()
        Glide.with(this).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSG1MpBtiK--7xU5A8Ghy--OEYfFalLN_wQ0Q&s").into(imageViewPaso2)

        // Riego
        val textViewPaso3 = findViewById<TextView>(R.id.textViewPaso3)
        val imageViewPaso3 = findViewById<ImageView>(R.id.imageViewPaso3)
        textViewPaso3.text = """
            3. RIEGO:
            - Mantener el suelo húmedo pero no encharcado.
            - Regar suavemente para no desplazar las semillas o dañar las raíces.
        """.trimIndent()
        Glide.with(this).load("https://centrodejardineriagorbeia.com/wp-content/uploads/2021/08/48176773_l-scaled.jpg").into(imageViewPaso3)

        // Cuidados
        val textViewPaso4 = findViewById<TextView>(R.id.textViewPaso4)
        val imageViewPaso4 = findViewById<ImageView>(R.id.imageViewPaso4)
        textViewPaso4.text = """
            4. CUIDADOS:
            - Eliminar maleza alrededor de las plantas para reducir la competencia por nutrientes.
            - Vigilar plagas y enfermedades, y tomar medidas preventivas.
        """.trimIndent()
        Glide.with(this).load("https://okdiario.com/img/2018/08/19/como-cuidar-plantas-de-interior-pasos-655x368.jpg").into(imageViewPaso4)

        // Cosecha
        val textViewPaso5 = findViewById<TextView>(R.id.textViewPaso5)
        val imageViewPaso5 = findViewById<ImageView>(R.id.imageViewPaso5)
        textViewPaso5.text = """
            5. COSECHA:
            - Cosechar cuando las plantas hayan madurado.
            - Cortar o arrancar las plantas con cuidado para no dañar las raíces de las plantas vecinas.
        """.trimIndent()
        Glide.with(this).load("https://static.eldiario.es/clip/7e1fea74-836f-4d26-aed7-e74cf29e8976_16-9-discover-aspect-ratio_default_0.jpg").into(imageViewPaso5)
    }
}