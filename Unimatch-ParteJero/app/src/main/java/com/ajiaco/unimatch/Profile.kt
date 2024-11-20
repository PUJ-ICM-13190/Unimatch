package com.ajiaco.unimatch

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Profile(
    var id: Int = 0,
    var email: String = "",
    var name: String = "",
    var age: Int = 0,
    var bio: String = "",
    var gender: String = "",
    var distance: Int = 0,
    var interests: List<String> = listOf(),
    var imageUrl: String = "",
    var location: String = "",
    var height: String = "",
    var smoking: String = "",
    var sign: String = "",
    var relationshipStatus: String = "",
    var children: String = "",
    var petLover: String = "",
    var education: String = ""
) : Serializable