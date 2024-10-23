package com.ajiaco.unimatch


// PremiumSubscriptionActivity.kt

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class PremiumSubscriptionActivity : AppCompatActivity() {
    private lateinit var viewModel: PremiumSubscriptionViewModel
    private lateinit var monthlyPlanCard: MaterialCardView
    private lateinit var yearlyPlanCard: MaterialCardView
    private lateinit var subscribeButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium_subscription)

        viewModel = ViewModelProvider(this)[PremiumSubscriptionViewModel::class.java]
        initializeViews()
        setupUI()
        observeViewModel()
    }

    private fun initializeViews() {
        monthlyPlanCard = findViewById(R.id.monthlyPlanCard)
        yearlyPlanCard = findViewById(R.id.yearlyPlanCard)
        subscribeButton = findViewById(R.id.subscribeButton)
    }

    private fun setupUI() {
        monthlyPlanCard.setOnClickListener {
            viewModel.selectPlan(PremiumPlan.MONTHLY)
            updatePlanSelection(isMonthly = true)
        }

        yearlyPlanCard.setOnClickListener {
            viewModel.selectPlan(PremiumPlan.YEARLY)
            updatePlanSelection(isMonthly = false)
        }

        subscribeButton.setOnClickListener {
            viewModel.selectedPlan.value?.let { plan ->
                viewModel.subscribe(plan)
            } ?: run {
                Toast.makeText(this, "Por favor selecciona un plan", Toast.LENGTH_SHORT).show()
            }
        }

        // Deshabilitar el botón de suscripción hasta que se seleccione un plan
        subscribeButton.isEnabled = false
    }

    private fun updatePlanSelection(isMonthly: Boolean) {
        // Actualizar el estado visual de las tarjetas
        monthlyPlanCard.isChecked = isMonthly
        yearlyPlanCard.isChecked = !isMonthly

        // Habilitar el botón de suscripción
        subscribeButton.isEnabled = true
    }

    private fun observeViewModel() {
        viewModel.subscriptionStatus.observe(this) { status ->
            when (status) {
                is SubscriptionStatus.Success -> {
                    showSuccessUI()
                    updateUserPreferences(true)
                }
                is SubscriptionStatus.Error -> {
                    showError(status.message)
                }
                is SubscriptionStatus.Loading -> {
                    showLoadingUI()
                }
            }
        }

        viewModel.selectedPlan.observe(this) { plan ->
            // Actualizar UI basado en el plan seleccionado
            when (plan) {
                PremiumPlan.MONTHLY -> updatePlanSelection(isMonthly = true)
                PremiumPlan.YEARLY -> updatePlanSelection(isMonthly = false)
                null -> {
                    monthlyPlanCard.isChecked = false
                    yearlyPlanCard.isChecked = false
                    subscribeButton.isEnabled = false
                }
            }
        }
    }

    private fun showSuccessUI() {
        Toast.makeText(
            this,
            "¡Bienvenido a Premium! Disfruta de todos los beneficios",
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    private fun showLoadingUI() {
        subscribeButton.isEnabled = false
        subscribeButton.text = "Procesando..."
    }

    private fun showError(message: String) {
        subscribeButton.isEnabled = true
        subscribeButton.text = "Suscribirse"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun updateUserPreferences(isPremium: Boolean) {
        getSharedPreferences("user_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("is_premium", isPremium)
            .apply()
    }
}


