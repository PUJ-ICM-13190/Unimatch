package com.ajiaco.unimatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ajiaco.unimatch.databinding.ActivityMainBinding
import com.ajiaco.unimatch.ui.ChatsFragment
import com.ajiaco.unimatch.ui.EventsFragment
import com.ajiaco.unimatch.MatchmakingFragment
import com.ajiaco.unimatch.ui.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de la barra de navegación inferior
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation

        // Cargar el fragmento por defecto (Matchmaking)
        loadFragment(MatchmakingFragment())

        // Configurar la barra de navegación inferior
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_matchmaking -> {
                    loadFragment(MatchmakingFragment())
                    true
                }
                R.id.nav_chats -> {
                    loadFragment(ChatsFragment())
                    true
                }
                R.id.nav_events -> {
                    loadFragment(EventosFragment())
                    true
                }
                R.id.nav_communities -> {
                    loadFragment(GruposFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Función para cargar los fragmentos
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
    }
}
