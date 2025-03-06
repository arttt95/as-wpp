package com.arttt95.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arttt95.whatsapp.databinding.ItemContatosBinding
import com.arttt95.whatsapp.models.Usuario
import com.squareup.picasso.Picasso

class ContatosAdapter(
    private val onClick: (Usuario) -> Unit // Criando uma função que recebe um param do tipo Usuario e não retorna nada (return Unit)
): Adapter<ContatosAdapter.ContatosViewHolder> () { // Herda de androidx.recyclerview...

    private var listaContatos = emptyList<Usuario>()

    fun adicionarLista(lista: List<Usuario>) {

        listaContatos = lista
        notifyDataSetChanged()

    }
    
    inner class ContatosViewHolder(
        private val binding: ItemContatosBinding
    ) : ViewHolder(binding.root) {

        fun bind(usuario: Usuario) {

            binding.textContatoNome.text = usuario.nome
            Picasso.get()
                .load(usuario.foto)
                .into(binding.imgContatoFoto)

            // Evento de clique em cada contato
            binding.clItemContato.setOnClickListener {
                onClick(usuario) // Ativando a o method onClick passando o usuario que já é recebido pelo method bind
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosViewHolder {

        val inflater = LayoutInflater.from(parent.context) // Gerando um inflater a partir de um context do parent, nesse caso não temos um inflater e foi necessário criar
        val itemView = ItemContatosBinding.inflate(
            inflater, parent, false
        )
        return ContatosViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return listaContatos.size
    }

    override fun onBindViewHolder(holder: ContatosViewHolder, position: Int) {
        val usuario = listaContatos[position]
        holder.bind(usuario)
    }
}