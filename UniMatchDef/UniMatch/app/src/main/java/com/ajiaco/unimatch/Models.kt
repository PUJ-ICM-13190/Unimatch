package com.ajiaco.unimatch

enum class PremiumPlan {
    MONTHLY,
    YEARLY
}

sealed class SubscriptionStatus {
    object Success : SubscriptionStatus()
    data class Error(val message: String) : SubscriptionStatus()
    object Loading : SubscriptionStatus()
}