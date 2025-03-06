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
import com.google.firebase.storage.FirebaseStorage

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false

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

    private fun uploadImagemStorage(uri: Uri) {

        // -> fotos
        //      -> usuarios
        //              -> id_usuario
        //                      -> perfil.jpg

        val idUsuario = firebaseAuth.currentUser?.uid
        if(idUsuario != null) {
            storage
                .getReference("fotos")
                .child("usuarios")
                .child(idUsuario)
                .child("perfil.jpg")
                .putFile( uri )
                .addOnSuccessListener { task ->

                    exibirMensagem("Sucesso ao fazer upload da imagem")

                }.addOnFailureListener { err ->
                    exibirMensagem("Erro ao fazer Upload da imagem")
                }
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