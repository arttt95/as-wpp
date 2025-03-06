package com.arttt95.whatsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arttt95.whatsapp.databinding.FragmentContatosBinding
import com.arttt95.whatsapp.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var eventoSnapshot: ListenerRegistration
    private val idUsuarioLogado = firebaseAuth.currentUser?.uid

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

                    if(usuario != null) {
//                        Log.i("fragment_contatos", "Nome: ${usuario.nome} ")
                        if(idUsuarioLogado != null) {
                            if(idUsuarioLogado != usuario.id) {
                                listaContatos.add(usuario)
                                Log.i("fragment_contatos", "Nome: ${usuario.nome} ")
                            }
                        }

                    }

                }

                // Lista de contatos vai servir para alimentar o RecyclerView

            }

    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()

    }

}