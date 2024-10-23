package com.ajiaco.unimatch


import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider

class FiltersActivity : AppCompatActivity() {

    private lateinit var ageRangeSlider: RangeSlider
    private lateinit var ageRangeText: TextView
    private lateinit var distanceSlider: Slider
    private lateinit var distanceText: TextView
    private lateinit var interestsContainer: LinearLayout
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var applyFiltersButton: Button

    private val interests = listOf("Musica", "Deporte", "Peliculas", "Viajar", "Comida", "Arte", "Tecnologia", "Fashion")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        ageRangeSlider = findViewById(R.id.ageRangeSlider)
        ageRangeText = findViewById(R.id.ageRangeText)
        distanceSlider = findViewById(R.id.distanceSlider)
        distanceText = findViewById(R.id.distanceText)
        interestsContainer = findViewById(R.id.interestsContainer)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        applyFiltersButton = findViewById(R.id.applyFiltersButton)

        setupAgeRangeSlider()
        setupDistanceSlider()
        setupInterests()
        setupApplyButton()
    }

    private fun setupAgeRangeSlider() {
        ageRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            val minAge = values[0].toInt()
            val maxAge = values[1].toInt()
            ageRangeText.text = "Rango de Edad: $minAge - $maxAge"
        }
    }

    private fun setupDistanceSlider() {
        distanceSlider.addOnChangeListener { slider, value, _ ->
            val distance = value.toInt()
            distanceText.text = "Distancia máxima: $distance km"
        }
    }

    private fun setupInterests() {
        interests.forEach { interest ->
            val checkBox = CheckBox(this)
            checkBox.text = interest
            interestsContainer.addView(checkBox)
        }
    }

    private fun setupApplyButton() {
        applyFiltersButton.setOnClickListener {
            // Get selected age range
            val ageRange = ageRangeSlider.values
            val minAge = ageRange[0].toInt()
            val maxAge = ageRange[1].toInt()

            // Get selected distance
            val maxDistance = distanceSlider.value.toInt()

            // Get selected interests
            val selectedInterests = mutableListOf<String>()
            for (i in 0 until interestsContainer.childCount) {
                val view = interestsContainer.getChildAt(i)
                if (view is CheckBox && view.isChecked) {
                    selectedInterests.add(view.text.toString())
                }
            }

            // Get selected gender
            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val selectedGender = when (selectedGenderId) {
                R.id.radioMale -> "Male"
                R.id.radioFemale -> "Female"
                R.id.radioNonBinary -> "Non-binary"
                else -> "Not specified"
            }

            // TODO: Save or apply the filters (e.g., save to SharedPreferences or send to a ViewModel)
            // For now, we'll just print the selected filters
            println("Age Range: $minAge - $maxAge")
            println("Maximum Distance: $maxDistance km")
            println("Selected Interests: $selectedInterests")
            println("Selected Gender: $selectedGender")

            // TODO: Navigate back to the main screen or apply the filters
            finish()
        }
    }
}