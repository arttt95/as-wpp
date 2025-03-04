package com.arttt95.whatsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arttt95.whatsapp.databinding.ActivityLoginBinding
import com.arttt95.whatsapp.utils.exibirMensagem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { // Binding
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy { // Firebase Auth
        FirebaseAuth.getInstance()
    }

    private lateinit var email: String
    private lateinit var password: String

    private lateinit var textInputLoginEmail: TextInputLayout
    private lateinit var editTextLoginEmail: TextInputEditText
    private lateinit var textInputLoginPassword: TextInputLayout
    private lateinit var editTextLoginPassword: TextInputEditText
    private lateinit var btnLogin: Button

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
        inicializarEventosClick()
//        firebaseAuth.signOut()

    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {

        val usuarioAtual = firebaseAuth.currentUser
        if(usuarioAtual != null) {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }

    }

    private fun inicializarComponentes() {

        textInputLoginEmail = binding.textInputLoginEmail
        editTextLoginEmail = binding.editTextLoginEmail

        textInputLoginPassword = binding.textInputLoginPassword
        editTextLoginPassword = binding.editTextLoginPassword

        btnLogin = binding.btnLogin

    }

    private fun inicializarEventosClick() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }

        btnLogin.setOnClickListener {

            if(validarCampos()) {

                logarUsuario()

            }

        }
    }

    private fun logarUsuario() {

        firebaseAuth.signInWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {

            exibirMensagem("Usuário logado com suceso!")
            startActivity(
                Intent(applicationContext, MainActivity::class.java)
            )

        }.addOnFailureListener { err ->

            try {
                throw err
            } catch (errUsuarioInvalido: FirebaseAuthInvalidUserException) {
                errUsuarioInvalido.printStackTrace()
                exibirMensagem("E-mail não cadastrado!")
            } catch (errCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                errCredenciaisInvalidas.printStackTrace()
                exibirMensagem("E-mail ou senha incorretos")
            }

        }

    }

    private fun validarCampos(): Boolean {

        email = editTextLoginEmail.text.toString()
        password = editTextLoginPassword.text.toString()

        if(email.isNotEmpty()) { // E-mail não vazio
            textInputLoginEmail.error = null

            if(password.isNotEmpty()) { // Password não vazia
                textInputLoginPassword.error = null
                return true
            } else { // Password está vazia
                textInputLoginPassword.error = "O campo senha é obrigatório!"
                return false
            }

        } else { // E-mail está vazio
            textInputLoginEmail.error = "O campo e-mail é obrigatório!"
            return false
        }

    }

}