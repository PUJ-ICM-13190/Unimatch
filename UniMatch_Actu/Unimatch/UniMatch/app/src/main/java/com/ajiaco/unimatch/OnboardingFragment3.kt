package com.ajiaco.unimatch.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ajiaco.unimatch.MainActivity
import com.ajiaco.unimatch.R
import com.ajiaco.unimatch.databinding.FragmentOnboarding3Binding

class OnboardingFragment3 : Fragment(R.layout.fragment_onboarding_3) {
    private var _binding: FragmentOnboarding3Binding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboarding3Binding.bind(view)

        // Lógica para navegar a la MainActivity al presionar el botón "Empezar"
        binding.buttonStart.setOnClickListener {
            val intent = Intent(activity, SelectionActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
