package com.ajiaco.unimatch.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ajiaco.unimatch.R
import com.ajiaco.unimatch.databinding.FragmentOnboarding1Binding

class OnboardingFragment1 : Fragment(R.layout.fragment_onboarding_1) {
    private var _binding: FragmentOnboarding1Binding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboarding1Binding.bind(view)
        // Aquí puedes agregar lógica adicional si es necesario
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
