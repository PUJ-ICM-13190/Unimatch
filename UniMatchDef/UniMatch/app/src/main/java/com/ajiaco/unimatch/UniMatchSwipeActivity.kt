package com.ajiaco.unimatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.GestureDetector
import androidx.core.view.GestureDetectorCompat
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.Intent
import android.view.MotionEvent
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
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

    private var profiles: List<Profile> = emptyList()
    private var currentProfileIndex = 0

    private var currentUserImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uni_match_swipe)

        initialize()
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

        loadProfiles()
        loadCurrentUserImageUrl()
        displayCurrentProfile()
    }

    private fun loadProfiles() {
        val json = assets.open("unimatch_profiles.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val profileListType = object : TypeToken<Map<String, List<Profile>>>() {}.type
        val profilesMap: Map<String, List<Profile>> = gson.fromJson(json, profileListType)
        profiles = profilesMap["profiles"] ?: emptyList()
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

    private inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val swipeThreshold = 100
        private val swipeVelocityThreshold = 100

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val diffX = e2.x - (e1?.x ?: 0f)
            if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
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
            animateSwipe(1000f, likeIcon)
            cardView.postDelayed({
                val currentProfile = profiles[currentProfileIndex-1]
                val intent = Intent(this@UniMatchSwipeActivity, SuccessfulMatchActivity::class.java).apply {
                    putExtra("USER1_IMAGE_URL", currentUserImageUrl)
                    putExtra("USER2_IMAGE_URL", currentProfile.imageUrl)
                }
                startActivity(intent)
            }, 300)
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
            // Implement icon pop-up animation here
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
    }
}


