package com.arttt95.whatsapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arttt95.whatsapp.fragments.ContatosFragment
import com.arttt95.whatsapp.fragments.ConversasFragment

class ViewPagerAdapter(
    private val tabs: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return tabs.size // listOf("Conversas", "Contatos")
    }

    override fun createFragment(position: Int): Fragment {

        when( position ) {
            1 -> return ContatosFragment()
        }

        return ConversasFragment()

    }

}