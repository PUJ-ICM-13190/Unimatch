package com.ajiaco.unimatch.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ajiaco.unimatch.EditProfileActivity
import com.ajiaco.unimatch.PremiumSubscriptionActivity
import com.ajiaco.unimatch.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Configurar el botón "Edit Profile"
        binding.btnEditProfile.setOnClickListener {
            // Navegar a EditProfileActivity
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnUpgrade.setOnClickListener {
            try {
                val intent = Intent(requireActivity(), PremiumSubscriptionActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                // Agregar un log para ver el error
                Log.e("PersonalProfile", "Error launching activity: ${e.message}")
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
