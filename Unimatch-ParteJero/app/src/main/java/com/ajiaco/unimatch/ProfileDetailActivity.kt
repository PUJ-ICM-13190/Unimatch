package com.ajiaco.unimatch

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.ajiaco.unimatch.databinding.ActivityProfileDetailBinding
import kotlin.math.abs

class ProfileDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profile = intent.getSerializableExtra("profile") as? Profile
        setupToolbar()
        setupCollapsingToolbar()
        displayProfile(profile)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupCollapsingToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scrollRange = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / scrollRange.toFloat()

            binding.toolbarTitle.alpha = percentage
            binding.expandedTitle.alpha = 1 - percentage
        })
    }

    private fun displayProfile(profile: Profile?) {
        profile?.let { p ->
            // Set profile image
            Glide.with(this)
                .load(p.imageUrl)
                .placeholder(R.drawable.ic_default_user)
                .error(R.drawable.ic_default_user)
                .into(binding.profileImage)

            // Set titles
            binding.toolbarTitle.text = p.name
            binding.expandedTitle.text = "${p.name}, ${p.age}"

            // Basic Info
            binding.bioText.text = p.bio

            // Location info if available
            if (p.location.isNotBlank()) {
                binding.locationSection.visibility = View.VISIBLE
                binding.locationText.text = p.location
            } else {
                binding.locationSection.visibility = View.GONE
            }

            // Education info if available
            if (p.education.isNotBlank()) {
                binding.educationSection.visibility = View.VISIBLE
                binding.educationText.text = p.education
            } else {
                binding.educationSection.visibility = View.GONE
            }

            // Personal details
            val details = mutableListOf<Pair<String, String>>()

            if (p.height.isNotBlank()) details.add("Altura" to p.height)
            if (p.smoking.isNotBlank()) details.add("Fumar" to p.smoking)
            if (p.relationshipStatus.isNotBlank()) details.add("Estado" to p.relationshipStatus)

            if (details.isNotEmpty()) {
                binding.detailsSection.visibility = View.VISIBLE
                binding.detailsContainer.removeAllViews()

                details.forEach { (label, value) ->
                    val detailView = layoutInflater.inflate(
                        R.layout.item_profile_detail,
                        binding.detailsContainer,
                        false
                    )
                    detailView.findViewById<TextView>(R.id.labelText).text = label
                    detailView.findViewById<TextView>(R.id.valueText).text = value
                    binding.detailsContainer.addView(detailView)
                }
            } else {
                binding.detailsSection.visibility = View.GONE
            }

            // Interests
            if (p.interests.isNotEmpty()) {
                binding.interestsSection.visibility = View.VISIBLE
                binding.interestsFlowLayout.removeAllViews()

                p.interests.forEach { interest ->
                    val chip = Chip(this).apply {
                        text = interest
                        isClickable = false
                        setChipBackgroundColorResource(R.color.orange)
                        setTextColor(ContextCompat.getColor(context, R.color.white))
                    }
                    binding.interestsFlowLayout.addView(chip)
                }
            } else {
                binding.interestsSection.visibility = View.GONE
            }
        }
    }
}