package com.arttt95.whatsapp.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.arttt95.whatsapp.R
import com.arttt95.whatsapp.databinding.ActivityMensagensBinding
import com.arttt95.whatsapp.models.Mensagem
import com.arttt95.whatsapp.models.Usuario
import com.arttt95.whatsapp.utils.Constantes
import com.arttt95.whatsapp.utils.exibirMensagem
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
    } // BINDING

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    } // AUTH

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    } // FIRESTORE

    // ListenerRegistraion
    private lateinit var listenerRegistration: ListenerRegistration

    // Componentes
    private lateinit var toolbar: MaterialToolbar
    private lateinit var imgMensagensFotoPerfilDestinatario: ImageView
    private lateinit var textMensagensNomeDestinatrio: TextView
    private lateinit var rcMensagens: RecyclerView
    private lateinit var fabMensagensEnviar: FloatingActionButton
    private lateinit var textInputMensagensDigitar: TextInputLayout
    private lateinit var editTextMensagensDigitar: TextInputEditText

    // DADOS Destinatário
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

        inicializarComponentes()
        recuperarDadosUsuarioDestinatario()
        inicializarToolbar()
        inicializarEventosClique()
        inicializarListeners()

    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove() // Parar de copiar a rede de Mensagens
    }

    private fun inicializarListeners() {

        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id

        if(idUsuarioRemetente != null && idUsuarioDestinatario != null) {

            listenerRegistration = firestore
                .collection(Constantes.DB_MENSAGENS)
                .document(idUsuarioRemetente)
                .collection(idUsuarioDestinatario)
                .orderBy("data", Query.Direction.ASCENDING) // Query de firebase.firestore
                .addSnapshotListener { querySnapshot, err ->

                    if(err != null) {
                        exibirMensagem("Erro ao recuperar mensagens")
                    }

                    val listaMensagens = mutableListOf<Mensagem>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach { documentSnapshot ->

                        val mensagem = documentSnapshot.toObject(Mensagem::class.java)

                        if(mensagem != null) {
                            listaMensagens.add(mensagem)
                            Log.i("exibicao_mensagens", mensagem.mensagem)
                        }

                    }

                    // Aqui teremos a nossa listaMensagens
                    if(listaMensagens.isNotEmpty()) {
                        // Carregar os dados no Adapter
                    }

                }

        }

    }

    private fun inicializarEventosClique() {

        fabMensagensEnviar.setOnClickListener {

            val mensagem = editTextMensagensDigitar.text.toString()
            savarMensagem(mensagem)

        }

    }

    private fun savarMensagem(textoMensagem: String) {

        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id

        // -> Mensagens
        //  -> ID do doc = ID remetente
        //      -> Código da coleção = ID destinatário
        //          -> cada doc terá um ID o campo será mensagem | Tipo = string | Valor = MENSAGEM

        if(textoMensagem.isNotEmpty()) {

            if(idUsuarioRemetente != null && idUsuarioDestinatario != null) {

                val mensagem = Mensagem(idUsuarioRemetente, textoMensagem)

                // Salvar msg para o remetente
                salvarMensagemFirestore(idUsuarioRemetente, idUsuarioDestinatario, mensagem)

                // Salvar msg para o destinatário
                salvarMensagemFirestore(idUsuarioDestinatario, idUsuarioRemetente, mensagem)


            }
        }
    }

    private fun salvarMensagemFirestore(
        idUsuarioRemetente: String, idUsuarioDestinatario: String, mensagem: Mensagem
    ) {

        firestore.collection(Constantes.DB_MENSAGENS)
            .document(idUsuarioRemetente)
            .collection(idUsuarioDestinatario)
            .add(mensagem)
            .addOnFailureListener {
                exibirMensagem("Erro ao enviar mensagem")
            }

        editTextMensagensDigitar.setText("")

    }


    private fun inicializarComponentes() {
        toolbar = binding.tbMensagens
        imgMensagensFotoPerfilDestinatario = binding.imgMensagensFotoPerfil
        textMensagensNomeDestinatrio = binding.textMensagensNome
        rcMensagens = binding.rcMensagens
        fabMensagensEnviar = binding.fabMensagensEnviar
        textInputMensagensDigitar = binding.textInputMensagensDigitar
        editTextMensagensDigitar = binding.editTextMensagensDigitar
    }

    private fun inicializarToolbar() {

        val toolbar = binding.tbMensagens

        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = ""

            if(dadosDestinatario != null) {

                // Inserindo o nome do Destinatário no TextView
                binding.textMensagensNome.text = dadosDestinatario!!.nome
                // Utilizando o Picasso para inserir a foto do Destinatário (url) na imgMensagens
                Picasso.get()
                    .load(dadosDestinatario!!.foto)
                    .into(binding.imgMensagensFotoPerfil)

            }

            setDisplayHomeAsUpEnabled(true)
        }

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