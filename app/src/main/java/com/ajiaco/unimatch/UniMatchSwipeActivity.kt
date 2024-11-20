package com.ajiaco.unimatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.GestureDetector
import androidx.core.view.GestureDetectorCompat
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import android.content.Intent
import android.view.MotionEvent
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.math.abs

class UniMatchSwipeActivity : AppCompatActivity() {
    private lateinit var cardView: View
    private lateinit var likeIcon: View
    private lateinit var dislikeIcon: View
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var profileImage: ImageView
    private lateinit var nameAgeText: TextView
    private lateinit var bioText: TextView
    private lateinit var interestsText: TextView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var profiles = mutableListOf<Profile>()
    private var currentProfileIndex = 0
    private var currentUserProfile: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uni_match_swipe)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        initialize()
        loadCurrentUserProfile()
    }

    private fun initialize() {
        cardView = findViewById(R.id.cardView)
        likeIcon = findViewById(R.id.likeIcon)
        dislikeIcon = findViewById(R.id.dislikeIcon)
        profileImage = findViewById(R.id.profileImage)
        nameAgeText = findViewById(R.id.nameAgeText)
        bioText = findViewById(R.id.bioText)
        interestsText = findViewById(R.id.interestsText)

        findViewById<View>(R.id.backButton).setOnClickListener { onBackPressed() }

        findViewById<ImageButton>(R.id.filterButton).setOnClickListener {
            val intent = Intent(this, FiltersActivity::class.java)
            startActivity(intent)
        }

        gestureDetector = GestureDetectorCompat(this, SwipeGestureListener())
        cardView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        // Ocultar la vista principal hasta que se carguen los perfiles
        cardView.visibility = View.INVISIBLE
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
                    Toast.makeText(this@UniMatchSwipeActivity,
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
                    profiles.clear()
                    for (profileSnapshot in snapshot.children) {
                        // No incluir el perfil del usuario actual
                        if (profileSnapshot.key != currentUserId) {
                            try {
                                val profile = profileSnapshot.getValue(Profile::class.java)
                                profile?.let {
                                    profiles.add(it)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    if (profiles.isNotEmpty()) {
                        cardView.visibility = View.VISIBLE
                        displayCurrentProfile()
                    } else {
                        Toast.makeText(this@UniMatchSwipeActivity,
                            "No hay perfiles disponibles",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UniMatchSwipeActivity,
                        "Error al cargar perfiles: ${error.message}",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun filterProfiles(minAge: Int, maxAge: Int, maxDistance: Int, gender: String, interests: List<String>) {
        val currentUserId = auth.currentUser?.uid ?: return

        database.child("profiles")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    profiles.clear()
                    for (profileSnapshot in snapshot.children) {
                        if (profileSnapshot.key != currentUserId) {
                            val profile = profileSnapshot.getValue(Profile::class.java)
                            profile?.let {
                                if (it.age in minAge..maxAge &&
                                    (gender == "Todos" || it.gender == gender) &&
                                    it.distance <= maxDistance &&
                                    (interests.isEmpty() || it.interests.any { interest -> interests.contains(interest) })) {
                                    profiles.add(it)
                                }
                            }
                        }
                    }

                    currentProfileIndex = 0
                    if (profiles.isNotEmpty()) {
                        displayCurrentProfile()
                    } else {
                        Toast.makeText(this@UniMatchSwipeActivity,
                            "No hay perfiles que coincidan con los filtros",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@UniMatchSwipeActivity,
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
            interestsText.text = "Interests: ${profile.interests.joinToString(", ")}"

            Glide.with(this)
                .load(profile.imageUrl)
                .centerCrop()
                .into(profileImage)
        }
    }

    private inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
        // ... (el resto del código del SwipeGestureListener permanece igual)
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val diffX = e2.x - (e1?.x ?: 0f)
            if (abs(diffX) > 100 && abs(velocityX) > 100) {
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
                val matchedProfile = profiles[currentProfileIndex]
                animateSwipe(1000f, likeIcon)
                cardView.postDelayed({
                    val intent = Intent(this@UniMatchSwipeActivity, SuccessfulMatchActivity::class.java).apply {
                        putExtra("USER1_IMAGE_URL", currentUserProfile?.imageUrl)
                        putExtra("USER2_IMAGE_URL", matchedProfile.imageUrl)
                        putExtra("USER2_NAME", matchedProfile.name)
                        putExtra("USER2_AGE", matchedProfile.age)
                        putExtra("USER2_GENDER", matchedProfile.gender)
                    }
                    startActivity(intent)
                }, 300)
            }
        }

        private fun onSwipeLeft() {
            animateSwipe(-1000f, dislikeIcon)
        }

        private fun animateSwipe(translationX: Float, icon: View) {
            icon.visibility = View.VISIBLE
            icon.alpha = 0f

            val cardAnimator = ObjectAnimator.ofFloat(cardView, View.TRANSLATION_X, translationX)
            val iconAnimator = ObjectAnimator.ofFloat(icon, View.ALPHA, 0f, 1f)

            AnimatorSet().apply {
                playTogether(cardAnimator, iconAnimator)
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }

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
            likeIcon.visibility = View.INVISIBLE
            dislikeIcon.visibility = View.INVISIBLE
        }

        private fun loadNextProfile() {
            currentProfileIndex++
            if (currentProfileIndex >= profiles.size) {
                currentProfileIndex = 0
            }
            displayCurrentProfile()
        }
    }
}


