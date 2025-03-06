package com.arttt95.whatsapp.fragments

import android.icu.lang.UCharacter.VerticalOrientation
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arttt95.whatsapp.adapters.ContatosAdapter
import com.arttt95.whatsapp.databinding.FragmentContatosBinding
import com.arttt95.whatsapp.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding // BINDING

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    } // AUTH Reference

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    } // FIRESTORE Reference

    private lateinit var eventoSnapshot: ListenerRegistration
    private val idUsuarioLogado = firebaseAuth.currentUser?.uid
    private lateinit var contatosAdapter: ContatosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // MANEIRA binding de executar o inflate do Fragment
        binding = FragmentContatosBinding.inflate(
            inflater,
            container,
            false
        )

        // Criando o Adapter de consumo para o RecyclerView rvContatos
        contatosAdapter = ContatosAdapter()
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        // Criando a divisória entre os items (Vertical)
        binding.rvContatos.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root

        // MANEIRA convencional de fazer o inflate do Fragment sem o binding
        /*// Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_contatos,
            container,
            false
        )*/

    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {

        val eventoSnapshot = firestore
            .collection("usuarios")
            .addSnapshotListener { querySnapshot, error ->

                val listaContatos = mutableListOf<Usuario>()

                val documents = querySnapshot?.documents
                documents?.forEach { documentSnapshot ->

                    val usuario = documentSnapshot.toObject( Usuario::class.java )

                    if(usuario != null && idUsuarioLogado != null) {

                        // Log.i("fragment_contatos", "Nome: ${usuario.nome} ")

                        if(idUsuarioLogado != usuario.id) { // Separa o currentUser

                            listaContatos.add(usuario)

                            // Deve imprimir apenas usuário que não são o currentUser
                            Log.i("fragment_contatos", "Nome: ${usuario.nome} ")

                        }

                    }

                }

                // Lista de contatos vai servir para alimentar o RecyclerView
                if(listaContatos.isNotEmpty()) {

                    contatosAdapter.adicionarLista( listaContatos )

                }

            }

    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()

    }

}