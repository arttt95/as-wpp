package com.arttt95.whatsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arttt95.whatsapp.databinding.ActivityCadastroBinding
import com.arttt95.whatsapp.models.Usuario
import com.arttt95.whatsapp.utils.exibirMensagem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var password: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var textInputCadastroNome: TextInputLayout
    private lateinit var editTextCadastroNome: TextInputEditText

    private lateinit var textInputCadastroEmail: TextInputLayout
    private lateinit var editTextCadastroEmail: TextInputEditText

    private lateinit var textInputCadastroPassword: TextInputLayout
    private lateinit var editTextCadastroPassword: TextInputEditText

    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inicializarToolbar()
        inicializarComponentes()
        inicializarEventosClick()

    }

    private fun inicializarEventosClick() {

        btnCadastrar.setOnClickListener {

            if(validarCampos()) {
                cadastrarUsuario(nome, email, password)
            }

        }

    }

    private fun cadastrarUsuario(nome: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener { resultado ->
            if(resultado.isSuccessful) {

                // Salvar dados do usuário no Firestore
                // ID, Nome, E-mail, Foto
                val idUsuario = resultado.result.user?.uid

                if(idUsuario != null) {
                    val usuario = Usuario(
                        idUsuario, nome, email,
                    )
                    salvarUsuarioFirestore(usuario)
                }


            }
        }.addOnFailureListener { err ->
            try {
                throw err
            } catch (errEmailExistente: FirebaseAuthUserCollisionException) {
                errEmailExistente.printStackTrace()
                exibirMensagem("Já existe um cadastro com esse e-mail")
            } catch (errWeakPassword: FirebaseAuthWeakPasswordException) {
                errWeakPassword.printStackTrace()
                exibirMensagem("A senha precisa conter letras, números e caracteres especiais.")
            } catch (errCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                errCredenciaisInvalidas.printStackTrace()
                exibirMensagem("O e-mail inserido não é válido")
            }
        }
    }

    private fun salvarUsuarioFirestore(usuario: Usuario) {
        firestore.collection("usuarios")
            .document( usuario.id )
            .set( usuario )
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao cadastrar usuário")

                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )

            }.addOnFailureListener { err ->
                err.printStackTrace()
                exibirMensagem("Erro ao cadastrar usuário")
            }
    }

    private fun validarCampos(): Boolean {

        nome = editTextCadastroNome.text.toString()
        email = editTextCadastroEmail.text.toString()
        password = editTextCadastroPassword.text.toString()

        if(nome.isNotEmpty()) { // Testando o nome de preenchido
            textInputCadastroNome.error = null

            if (email.isNotEmpty()) { // Testando o email se preenchido
                textInputCadastroEmail.error = null

                if(password.isNotEmpty()) { // Testando a senha se preenchida
                    textInputCadastroPassword.error = null
                    return true
                } else {
                    textInputCadastroPassword.error = "Insira uma senha!"
                    return false
                }

            } else {
                textInputCadastroEmail.error = "Preencha seu e-mail!"
                return false
            }

        } else {
            textInputCadastroNome.error = "Preencha o seu nome!"
            return false
        }

    }

    private fun inicializarComponentes() {

        textInputCadastroNome = binding.textInputCadastroNome
        editTextCadastroNome = binding.editTextCadastroNome

        textInputCadastroEmail = binding.textInputCadastroEmail
        editTextCadastroEmail = binding.editTextCadastroEmail

        textInputCadastroPassword = binding.textInputCadastroPassword
        editTextCadastroPassword = binding.editTextCadastroPassword

        btnCadastrar = binding.btnCadastrar

    }

    private fun inicializarToolbar() {

        val toolbar = binding.includeToolbar.tbPrincipal

        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = "Cadastro"
            setDisplayHomeAsUpEnabled(true)
        }

    }
}