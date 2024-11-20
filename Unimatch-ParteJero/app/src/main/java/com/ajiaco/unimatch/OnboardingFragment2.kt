package com.ajiaco.unimatch.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ajiaco.unimatch.R
import com.ajiaco.unimatch.databinding.FragmentOnboarding2Binding

class OnboardingFragment2 : Fragment(R.layout.fragment_onboarding_2) {
    private var _binding: FragmentOnboarding2Binding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboarding2Binding.bind(view)
        // Aquí puedes agregar lógica adicional si es necesario
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
