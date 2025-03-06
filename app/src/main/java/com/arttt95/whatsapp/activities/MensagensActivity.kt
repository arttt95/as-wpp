package com.arttt95.whatsapp.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arttt95.whatsapp.R
import com.arttt95.whatsapp.databinding.ActivityMensagensBinding
import com.arttt95.whatsapp.models.Usuario
import com.arttt95.whatsapp.utils.Constantes

class MensagensActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
    }

    private var dadosDestinatario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recuperarDadosUsuarioDestinatario()

    }

    private fun recuperarDadosUsuarioDestinatario() {

        val extras = intent.extras
        if (extras != null) {

            val origem = extras.getString(Constantes.ORIGEM)
            if(origem == Constantes.ORIGEM_CONTATO) {

                // Recuperando dados do Contato BUNDLE
                dadosDestinatario = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable(Constantes.DADOS_DESTINATARIO, Usuario::class.java)
                } else {
                    extras.getParcelable(Constantes.DADOS_DESTINATARIO)
                }

            } else if (origem == Constantes.ORIGEM_CONVERSA) {
                // Recuperar dados da Conversa
            }

        }

    }
}