package com.arttt95.whatsapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.arttt95.whatsapp.R
import com.arttt95.whatsapp.adapters.ViewPagerAdapter
import com.arttt95.whatsapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var tabLayoutPrincipal: TabLayout
    private lateinit var viewPagerPrincipal: ViewPager2

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
        inicializarNavegacaoAbas()

    }

    private fun inicializarComponentes() {

        tabLayoutPrincipal = binding.tabLayoutPrincipal
        viewPagerPrincipal = binding.viewPagerPrincipal

    }


    private fun inicializarNavegacaoAbas() {

        // Adapter

        val tabs = listOf("Conversas", "Contatos")

        viewPagerPrincipal.adapter = ViewPagerAdapter(
            tabs, supportFragmentManager, lifecycle
        )

        tabLayoutPrincipal.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayoutPrincipal, viewPagerPrincipal) { tab, position ->

            tab.text = tabs[position]

        }.attach()

    }

    private fun inicializarToolbar() {

        val toolbar = binding.includeMainToolbar.tbPrincipal

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "WhatsApp"
        }

        addMenuProvider(
            object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.item_perfil -> {
                            startActivity(
                                Intent(applicationContext, PerfilActivity::class.java)
                            )
                        }
                        R.id.item_sair -> {
                            deslogarUsuario()
                        }
                    }

                    return true

                }

            }
        )

    }

    private fun deslogarUsuario() {
        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Cancelar") { dialog, posicao ->
            }.setPositiveButton("Sim") { dialog, posicao ->
                firebaseAuth.signOut()
                startActivity(
                    Intent(applicationContext, LoginActivity::class.java)
                )
            }.create()
            .show()
    }
}