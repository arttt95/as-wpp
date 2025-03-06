package com.arttt95.whatsapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arttt95.whatsapp.databinding.ActivityPerfilBinding
import com.arttt95.whatsapp.utils.exibirMensagem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy { // BINDING
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    } // AUTH Reference

    private val storage by lazy {
        FirebaseStorage.getInstance()
    } // STORAGE Reference

    private val firestore by lazy { // FIRESTORE Reference
        FirebaseFirestore.getInstance()
    }

    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false

    private val idUsuario = firebaseAuth.currentUser?.uid

    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if( uri != null) {
            imgPerfil.setImageURI( uri )
            uploadImagemStorage( uri )
        } else {
            exibirMensagem("Nenhuma imagem selecionada")
        }
    }

    private lateinit var imgPerfil: ImageView
    private lateinit var fabSelecionar: FloatingActionButton
    private lateinit var editTextPerfilNome: TextInputEditText
    private lateinit var btnAtualizar: Button


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
        inicializarToolbar()
        solicitarPermissoes()
        inicializarEventosClique()

    }

    override fun onStart() {
        super.onStart()

        recuperarDadosIniciaisUsuarios()

    }

    private fun recuperarDadosIniciaisUsuarios() {

        if(idUsuario != null) {

            firestore.collection("usuarios")
                .document( idUsuario )
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val dadosusuario = documentSnapshot.data
                    if(dadosusuario != null) {

                        val nome = dadosusuario["nome"] as String
                        val foto = dadosusuario["foto"] as String

                        editTextPerfilNome.setText(nome)
                        if(foto.isNotEmpty()) {

                            Picasso.get()
                                .load( foto )
                                .into(imgPerfil)

                        }
                    }
                }

        }

    }

    private fun uploadImagemStorage(uri: Uri) {

        // -> fotos
        //      -> usuarios
        //              -> id_usuario
        //                      -> perfil.jpg


        if(idUsuario != null) {
            storage
                .getReference("fotos")
                .child("usuarios")
                .child(idUsuario)
                .child("perfil.jpg")
                .putFile( uri )
                .addOnSuccessListener { task ->

                    exibirMensagem("Sucesso ao fazer upload da imagem")
                    task.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener { downloadUrl ->

                            val dados = mapOf(
                                "foto" to downloadUrl.toString()
                            )

                            atualizarDadosPerfil( idUsuario, dados )

                        }

                }.addOnFailureListener { err ->
                    exibirMensagem("Erro ao fazer Upload da imagem")
                }
        }



    }

    private fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>) {
        firestore.collection("usuarios")
            .document(idUsuario)
            .update(dados)
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao atualizar imagem perfil")
            }.addOnFailureListener {
                exibirMensagem("Erro ao atualizar imagem de perfil")
            }
    }

    private fun inicializarEventosClique() {

        fabSelecionar.setOnClickListener {

            if( temPermissaoGaleria ) {
                gerenciadorGaleria.launch("image/*")
            } else {
                exibirMensagem("Não tem permissão para acessar a Galeria")
                solicitarPermissoes()
            }

        }

        btnAtualizar.setOnClickListener {

            val nomeUsuario = editTextPerfilNome.text.toString()

            if(nomeUsuario != null) {

                if(idUsuario != null) {

                    val dados = mapOf(
                        "nome" to nomeUsuario
                    )

                    atualizarDadosPerfil( idUsuario, dados )
                    exibirMensagem("Sucesso ao atualizar nome")

                } else {
                    exibirMensagem("Usuario sem ID")
                }

            } else {
                exibirMensagem("Preencha o nome para atualizar")
            }

        }

    }

    private fun inicializarComponentes() {

        imgPerfil = binding.imgPerfil
        fabSelecionar = binding.fabSelecionar
        editTextPerfilNome = binding.editTextPerfilNome
        btnAtualizar = binding.btnAtualizar

    }

    private fun solicitarPermissoes() {

        // Lista de permissoes negadas
        val listaPermissoesNegadas = mutableListOf<String>()

        // Verifico se o usuario já tem essa permissão
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!temPermissaoCamera) {
            listaPermissoesNegadas.add(Manifest.permission.CAMERA)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            temPermissaoGaleria = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

            if(!temPermissaoGaleria) {
                listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)
            }

        } else {
            temPermissaoGaleria = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            if(!temPermissaoGaleria) {
                listaPermissoesNegadas.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        }

        // Solicitar multiplas permissoes
        if(listaPermissoesNegadas.isNotEmpty()) {
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissoes ->

                temPermissaoCamera = permissoes[Manifest.permission.CAMERA] ?: temPermissaoCamera

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    temPermissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES] ?: temPermissaoGaleria

                } else {

                    temPermissaoGaleria = permissoes[Manifest.permission.READ_EXTERNAL_STORAGE] ?: temPermissaoGaleria

                }

            }

            gerenciadorPermissoes.launch( listaPermissoesNegadas.toTypedArray() )
        }

    }

    private fun inicializarToolbar() {

        val toolbar = binding.includeToolbar.tbPrincipal

        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = "Perfil"
            setDisplayHomeAsUpEnabled(true)
        }

    }
}