package com.ajiaco.unimatch.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.view.GestureDetector
import android.view.MotionEvent
import android.content.Intent
import androidx.appcompat.widget.Toolbar
import com.ajiaco.unimatch.Profile
import com.ajiaco.unimatch.R
import com.ajiaco.unimatch.SuccessfulMatchActivity
import com.ajiaco.unimatch.FiltersActivity
import kotlin.math.abs
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import com.ajiaco.unimatch.PerfilDetalladoActivity
import kotlin.math.sqrt

class MatchmakingFragment : Fragment(), GestureDetector.OnGestureListener,  SensorEventListener{
    private lateinit var cardView: View
    private lateinit var likeIcon: View
    private lateinit var dislikeIcon: View
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var profileImage: ImageView
    private lateinit var nameAgeText: TextView
    private lateinit var bioText: TextView
    private lateinit var interestsText: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var backButton: ImageButton
    private lateinit var filterButton: ImageButton
    private lateinit var sensorManager: SensorManager
    
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private val shakeThreshold = 12.0f  // Umbral para detectar sacudidas
    private val shakeCooldown = 1000

    private var profiles: List<Profile> = emptyList()
    private var currentProfileIndex = 0

    private var currentUserImageUrl: String = ""

    // Request code for filters
    private val REQUEST_FILTERS = 1001

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_matchmaking, container, false)

        initializeViews(view)
        setupListeners()

        loadProfiles()
        loadCurrentUserImageUrl()
        displayCurrentProfile()

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        return view
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun initializeViews(view: View) {
        cardView = view.findViewById(R.id.cardView)
        likeIcon = view.findViewById(R.id.likeIcon)
        dislikeIcon = view.findViewById(R.id.dislikeIcon)
        profileImage = view.findViewById(R.id.profileImage)
        nameAgeText = view.findViewById(R.id.nameAgeText)
        bioText = view.findViewById(R.id.bioText)
        interestsText = view.findViewById(R.id.interestsText)
        toolbar = view.findViewById(R.id.toolbar)
        backButton = view.findViewById(R.id.backButton)
        filterButton = view.findViewById(R.id.filterButton)

        gestureDetector = GestureDetectorCompat(requireContext(), this)
    }

    private fun setupListeners() {
        cardView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        filterButton.setOnClickListener {
            // Iniciar la actividad de filtros y esperar resultados
            val intent = Intent(requireContext(), FiltersActivity::class.java)
            startActivityForResult(intent, REQUEST_FILTERS)
        }

        // Aquí añadimos el listener para redirigir al perfil detallado
        nameAgeText.setOnClickListener {
            val profile = profiles[currentProfileIndex]
            val intent = Intent(requireContext(), PerfilDetalladoActivity::class.java)
            intent.putExtra("profile", profile)
            startActivity(intent)
        }

    }

    private fun loadProfiles() {
        val json = requireContext().assets.open("unimatch_profiles.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val profileListType = object : TypeToken<Map<String, List<Profile>>>() {}.type
        val profilesMap: Map<String, List<Profile>> = gson.fromJson(json, profileListType)
        profiles = profilesMap["profiles"] ?: emptyList()
        println("total profiles: ${profiles.size}")
    }

    private fun loadCurrentUserImageUrl() {
        // TODO: Implement this method to load the current user's image URL
        // For now, we'll use a placeholder URL
        currentUserImageUrl = "https://example.com/current_user_image.jpg"
    }

    private fun displayCurrentProfile() {
        if (currentProfileIndex < profiles.size) {
            val profile = profiles[currentProfileIndex]
            nameAgeText.text = "${profile.name}, ${profile.age}"
            bioText.text = profile.bio
            interestsText.text = "Interests: ${profile.interests.joinToString(", ")}"

            Glide.with(this)
                .load(profile.imageUrl)
                .centerCrop()
                .into(profileImage)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calcular la magnitud de la aceleración
            val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH

            // Verificar si la aceleración excede el umbral y si ha pasado suficiente tiempo
            val currentTime = System.currentTimeMillis()
            if (acceleration > shakeThreshold && currentTime - lastShakeTime > shakeCooldown) {
                lastShakeTime = currentTime
                // Acción de "like" al detectar una sacudida fuerte -> Ejecuta onSwipeLeft
                onSwipeRight()
            }
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario implementar esto en nuestro caso
    }

    // Recibir el resultado de los filtros aplicados
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FILTERS && resultCode == AppCompatActivity.RESULT_OK) {
            val minAge = data?.getIntExtra("MIN_AGE", 18) ?: 18
            val maxAge = data?.getIntExtra("MAX_AGE", 100) ?: 100
            val maxDistance = data?.getIntExtra("MAX_DISTANCE", 50) ?: 50
            val selectedGender = data?.getStringExtra("SELECTED_GENDER") ?: "Todos"
            val selectedInterests = data?.getStringArrayExtra("SELECTED_INTERESTS")?.toList() ?: emptyList()

            println("Selected filters: Age: $minAge-$maxAge, Gender: $selectedGender, Interests: $selectedInterests")
            // Aplicar los filtros
            filterProfiles(minAge, maxAge, maxDistance, selectedGender, selectedInterests)
        }
    }

    private fun filterProfiles(minAge: Int, maxAge: Int, maxDistance: Int, selectedGender: String, selectedInterests: List<String>) {
        val filteredProfiles = profiles.filter { profile ->
            // Filtro por edad
            profile.age in minAge..maxAge &&
                    // Filtro por género
                    (selectedGender == "Todos" || profile.gender == selectedGender) &&
                    // Filtro por intereses, si no hay intereses seleccionados, no se aplica el filtro
                    (selectedInterests.isEmpty() || profile.interests.any { it in selectedInterests })
        }

        // Comprobamos si la lista de perfiles filtrados está vacía
        if (filteredProfiles.isNotEmpty()) {
            profiles = filteredProfiles
            currentProfileIndex = 0
            displayCurrentProfile()
            println("Perfiles disponibles: ${filteredProfiles.size}") // Log para verificar perfiles disponibles
        } else {
            showNoProfilesMessage()
            println("No hay perfiles disponibles después de aplicar filtros") // Log para depuración
            }
      }

    private fun showNoProfilesMessage() {
        // Mostrar un mensaje o un UI cuando no hay perfiles que coincidan con los filtros
        Toast.makeText(requireContext(), "No hay perfiles que coincidan con los filtros seleccionados.", Toast.LENGTH_SHORT).show()
       }

    // Gesto de deslizamiento (swipe) hacia la derecha e izquierda
    override fun onDown(e: MotionEvent): Boolean = true

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        val diffX = e2.x - (e1?.x ?: 0f)
        if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffX > 0) {
                onSwipeRight()
            } else {
                onSwipeLeft()
            }
            return true
        }
        return false
    }

    private fun onSwipeRight() {
        if (currentProfileIndex < profiles.size) {
            val currentProfile = profiles[currentProfileIndex]
            animateSwipe(1000f, likeIcon)
            cardView.postDelayed({
                val intent = Intent(requireContext(), SuccessfulMatchActivity::class.java).apply {
                    putExtra("USER1_IMAGE_URL", currentUserImageUrl)
                    putExtra("USER2_IMAGE_URL", currentProfile.imageUrl)
                    putExtra("USER2_NAME", currentProfile.name)
                    putExtra("USER2_AGE", currentProfile.age)
                    putExtra("USER2_GENDER", currentProfile.gender)


                }
                startActivity(intent)
            }, 300)
        }
    }

    private fun onSwipeLeft() {
        animateSwipe(-1000f, dislikeIcon)
    }

    private fun animateSwipe(translationX: Float, icon: View) {
        likeIcon.visibility = View.INVISIBLE
        dislikeIcon.visibility = View.INVISIBLE

        icon.visibility = View.VISIBLE
        icon.alpha = 0f

        val cardAnimator = ObjectAnimator.ofFloat(cardView, View.TRANSLATION_X, translationX)
        cardAnimator.duration = 300

        val iconAnimator = ObjectAnimator.ofFloat(icon, View.ALPHA, 0f, 1f)
        iconAnimator.duration = 300

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(cardAnimator, iconAnimator)
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()

        animateIconPopUp(icon)

        cardView.postDelayed({
            resetCard()
            loadNextProfile()
        }, 300)
    }

    private fun animateIconPopUp(icon: View) {
        val scaleX = ObjectAnimator.ofFloat(icon, View.SCALE_X, 0.8f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(icon, View.SCALE_Y, 0.8f, 1.2f, 1f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.duration = 300
        animatorSet.start()
    }

    private fun resetCard() {
        cardView.animate()
            .setDuration(100)
            .translationX(0f)
            .start()
    }

    private fun loadNextProfile() {
        currentProfileIndex++
        if (currentProfileIndex >= profiles.size) {
            currentProfileIndex = 0 // Loop back to the first profile
        }
        displayCurrentProfile()
    }

    // Unused gesture methods
    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean = false
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean = false
    override fun onLongPress(e: MotionEvent) {}

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}
