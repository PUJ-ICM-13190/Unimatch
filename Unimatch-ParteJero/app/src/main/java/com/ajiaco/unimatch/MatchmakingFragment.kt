package com.ajiaco.unimatch

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
import android.view.GestureDetector
import android.view.MotionEvent
import android.content.Intent
import androidx.appcompat.widget.Toolbar
import com.ajiaco.unimatch.*
import kotlin.math.abs
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class MatchmakingFragment : Fragment() {
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
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var profiles = mutableListOf<Profile>()
    private var currentProfileIndex = 0
    private var currentUserProfile: Profile? = null
    private val matchManager = MatchManager()

    // Request code for filters
    private val REQUEST_FILTERS = 1001

    private data class ProfileWithId(
        val profile: Profile,
        val firebaseId: String
    )
    private var profilesWithIds = mutableListOf<ProfileWithId>()
    private lateinit var notificationManager: MatchNotificationManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_matchmaking, container, false)
        notificationManager = MatchNotificationManager(requireContext())
        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference


        initializeViews(view)
        setupListeners()
        loadCurrentUserProfile()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ... código existente ...

        // Escuchar nuevos matches
        auth.currentUser?.uid?.let { currentUserId ->
            matchManager.listenForNewMatches(currentUserId) { matchedProfile ->
                // Opcional: Mostrar una notificación de nuevo match
                showMatchNotification(matchedProfile)
            }
        }
    }

    private fun showMatchNotification(matchedProfile: Profile) {
        // Implementar notificación de nuevo match
        Toast.makeText(requireContext(),
            "¡Nuevo match con ${matchedProfile.name}!",
            Toast.LENGTH_LONG).show()
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

        gestureDetector = GestureDetectorCompat(requireContext(), GestureListener())

        // Ocultar la vista principal hasta que se carguen los perfiles
        cardView.visibility = View.INVISIBLE
    }


    private fun setupListeners() {
        cardView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        filterButton.setOnClickListener {
            val intent = Intent(requireContext(), FiltersActivity::class.java)
            startActivityForResult(intent, REQUEST_FILTERS)
        }

        nameAgeText.setOnClickListener {
            if (currentProfileIndex < profiles.size) {
                val profile = profiles[currentProfileIndex]
                val intent = Intent(requireContext(), PerfilDetalladoActivity::class.java)
                intent.putExtra("profile", profile)
                startActivity(intent)
            }
        }
    }

    private fun loadCurrentUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        database.child("profiles").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentUserProfile = snapshot.getValue(Profile::class.java)
                    loadOtherProfiles()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),
                        "Error al cargar tu perfil: ${error.message}",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadOtherProfiles() {
        val currentUserId = auth.currentUser?.uid ?: return

        database.child("profiles")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        profiles.clear()
                        profilesWithIds.clear()

                        for (profileSnapshot in snapshot.children) {
                            if (profileSnapshot.key != currentUserId) {
                                try {
                                    val name = profileSnapshot.child("name").getValue(String::class.java) ?: ""
                                    val age = profileSnapshot.child("age").getValue(Long::class.java)?.toInt() ?: 0
                                    val bio = profileSnapshot.child("bio").getValue(String::class.java) ?: ""
                                    val gender = profileSnapshot.child("gender").getValue(String::class.java) ?: ""
                                    val imageUrl = profileSnapshot.child("imageUrl").getValue(String::class.java) ?: ""
                                    val location = profileSnapshot.child("location").getValue(String::class.java) ?: ""
                                    val education = profileSnapshot.child("education").getValue(String::class.java) ?: ""
                                    val relationshipStatus = profileSnapshot.child("relationshipStatus").getValue(String::class.java) ?: ""

                                    if (name.isNotBlank() && age > 0 && bio.isNotBlank()) {
                                        val profile = Profile(
                                            id = 0,
                                            name = name,
                                            age = age,
                                            bio = bio,
                                            gender = gender,
                                            imageUrl = imageUrl,
                                            location = location,
                                            education = education,
                                            relationshipStatus = relationshipStatus,
                                            email = "",
                                            distance = 0,
                                            interests = listOf(),
                                            height = "",
                                            smoking = "",
                                            sign = "",
                                            children = "",
                                            petLover = ""
                                        )

                                        profiles.add(profile)
                                        profilesWithIds.add(ProfileWithId(profile, profileSnapshot.key ?: ""))
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }

                        if (profiles.isNotEmpty()) {
                            cardView.visibility = View.VISIBLE
                            // Asegurarse de que ambas listas se mezclen de la misma manera
                            val indices = profiles.indices.toList()
                            val shuffledIndices = indices.shuffled()

                            profiles = shuffledIndices.map { profiles[it] }.toMutableList()
                            profilesWithIds = shuffledIndices.map { profilesWithIds[it] }.toMutableList()

                            currentProfileIndex = 0
                            displayCurrentProfile()
                        } else {
                            Toast.makeText(requireContext(),
                                "No hay perfiles disponibles",
                                Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(),
                            "Error al procesar los perfiles",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),
                        "Error al cargar perfiles: ${error.message}",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun filterProfiles(minAge: Int, maxAge: Int, maxDistance: Int, gender: String, interests: List<String>) {
        val currentUserId = auth.currentUser?.uid ?: return

        database.child("profiles")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    profiles.clear()
                    for (profileSnapshot in snapshot.children) {
                        if (profileSnapshot.key != currentUserId) {
                            try {
                                val age = profileSnapshot.child("age").getValue(Long::class.java)?.toInt() ?: 0
                                val profileGender = profileSnapshot.child("gender").getValue(String::class.java) ?: ""

                                if (age in minAge..maxAge &&
                                    (gender.equals("Todos", ignoreCase = true) ||
                                            profileGender.equals(gender, ignoreCase = true))) {

                                    val name = profileSnapshot.child("name").getValue(String::class.java) ?: ""
                                    val bio = profileSnapshot.child("bio").getValue(String::class.java) ?: ""
                                    val imageUrl = profileSnapshot.child("imageUrl").getValue(String::class.java) ?: ""
                                    val location = profileSnapshot.child("location").getValue(String::class.java) ?: ""
                                    val education = profileSnapshot.child("education").getValue(String::class.java) ?: ""
                                    val relationshipStatus = profileSnapshot.child("relationshipStatus").getValue(String::class.java) ?: ""

                                    val profile = Profile(
                                        id = 0,
                                        name = name,
                                        age = age,
                                        bio = bio,
                                        gender = profileGender,
                                        imageUrl = imageUrl,
                                        location = location,
                                        education = education,
                                        relationshipStatus = relationshipStatus,
                                        email = "",
                                        distance = 0,
                                        interests = listOf(),
                                        height = "",
                                        smoking = "",
                                        sign = "",
                                        children = "",
                                        petLover = ""
                                    )

                                    profiles.add(profile)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    if (profiles.isNotEmpty()) {
                        profiles.shuffle()
                        currentProfileIndex = 0
                        displayCurrentProfile()
                    } else {
                        Toast.makeText(requireContext(),
                            "No hay perfiles que coincidan con los filtros",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),
                        "Error al filtrar perfiles: ${error.message}",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displayCurrentProfile() {
        if (currentProfileIndex < profiles.size) {
            val profile = profiles[currentProfileIndex]

            nameAgeText.text = "${profile.name}, ${profile.age}"
            bioText.text = profile.bio

            val infoList = mutableListOf<String>()
            if (profile.location.isNotBlank()) infoList.add(profile.location)
            if (profile.education.isNotBlank()) infoList.add(profile.education)
            if (profile.relationshipStatus.isNotBlank()) infoList.add(profile.relationshipStatus)

            interestsText.text = if (infoList.isNotEmpty()) {
                infoList.joinToString(" • ")
            } else {
                ""
            }

            if (profile.imageUrl.isNotBlank()) {
                Glide.with(this)
                    .load(profile.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_user)
                    .error(R.drawable.ic_default_user)
                    .into(profileImage)
            } else {
                profileImage.setImageResource(R.drawable.ic_default_user)
            }
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val diffX = e2.x - (e1?.x ?: 0f)
            val diffY = e2.y - (e1?.y ?: 0f)

            // Check for horizontal swipe
            if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD &&
                abs(diffX) > abs(diffY)) {
                if (diffX > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
                return true
            }
            // Check for vertical swipe down
            else if (diffY > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                onSwipeDown()
                return true
            }
            return false
        }

        private fun onSwipeDown() {
            if (currentProfileIndex < profiles.size) {
                val currentProfile = profiles[currentProfileIndex]
                showProfileDetails(currentProfile)
            }
        }
    }
    private fun showProfileDetails(profile: Profile) {
        val intent = Intent(requireContext(), ProfileDetailActivity::class.java)
        intent.putExtra("profile", profile)
        startActivity(intent)
    }

    private fun onSwipeRight() {
        if (currentProfileIndex < profiles.size) {
            val currentProfileWithId = profilesWithIds[currentProfileIndex]

            // Lanzar una corrutina para manejar el swipe
            lifecycleScope.launch {
                try {
                    val isMatch = matchManager.handleSwipeRight(currentProfileWithId.firebaseId)

                    if (isMatch) {
                        // Es un match! Mostrar la pantalla de match
                        showMatchScreen(currentProfileWithId.profile)
                    } else {
                        // Solo mostrar la animación de like y continuar
                        animateSwipe(1000f, likeIcon)
                        loadNextProfile()
                    }
                } catch (e: Exception) {
                    // Manejar el error
                    Toast.makeText(requireContext(),
                        "Error al procesar el like",
                        Toast.LENGTH_SHORT).show()
                    loadNextProfile()
                }
            }
        }
    }
    private fun showMatchScreen(matchedProfile: Profile) {
        val intent = Intent(requireContext(), SuccessfulMatchActivity::class.java).apply {
            putExtra("USER1_IMAGE_URL", currentUserProfile?.imageUrl)
            putExtra("USER2_IMAGE_URL", matchedProfile.imageUrl)
            putExtra("USER2_NAME", matchedProfile.name)
            putExtra("USER2_AGE", matchedProfile.age)
            putExtra("USER2_GENDER", matchedProfile.gender)
        }
        startActivity(intent)
        notificationManager.showMatchNotification(matchedProfile)
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

        cardView.postDelayed({
            resetCard()
            loadNextProfile()
        }, 300)
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
            currentProfileIndex = 0
        }
        displayCurrentProfile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FILTERS && resultCode == AppCompatActivity.RESULT_OK) {
            val minAge = data?.getIntExtra("MIN_AGE", 18) ?: 18
            val maxAge = data?.getIntExtra("MAX_AGE", 100) ?: 100
            val maxDistance = data?.getIntExtra("MAX_DISTANCE", 50) ?: 50
            val selectedGender = data?.getStringExtra("SELECTED_GENDER") ?: "Todos"
            val selectedInterests = data?.getStringArrayExtra("SELECTED_INTERESTS")?.toList() ?: emptyList()

            filterProfiles(minAge, maxAge, maxDistance, selectedGender, selectedInterests)
        }
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}