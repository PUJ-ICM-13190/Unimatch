package com.ajiaco.unimatch

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MatchManager {
    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    suspend fun handleSwipeRight(otherUserId: String): Boolean {
        val currentUserId = auth.currentUser?.uid ?: return false

        // Verificar si ya existe un match potencial
        val existingMatch = findExistingMatch(currentUserId, otherUserId)

        return if (existingMatch != null) {
            // Si existe, actualizar el match existente
            updateExistingMatch(existingMatch, currentUserId, otherUserId)
        } else {
            // Si no existe, crear un nuevo match potencial
            createNewMatch(currentUserId, otherUserId)
        }
    }

    private suspend fun findExistingMatch(currentUserId: String, otherUserId: String): Match? {
        return suspendCoroutine { continuation ->
            database.child("matches")
                .orderByChild("user1Id")
                .equalTo(otherUserId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var match: Match? = null
                        for (childSnapshot in snapshot.children) {
                            val potentialMatch = childSnapshot.getValue(Match::class.java)
                            if (potentialMatch?.user2Id == currentUserId) {
                                match = potentialMatch.copy(id = childSnapshot.key ?: "")
                                break
                            }
                        }
                        if (match == null) {
                            database.child("matches")
                                .orderByChild("user1Id")
                                .equalTo(currentUserId)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(innerSnapshot: DataSnapshot) {
                                        var innerMatch: Match? = null
                                        for (childSnapshot in innerSnapshot.children) {
                                            val potentialMatch = childSnapshot.getValue(Match::class.java)
                                            if (potentialMatch?.user2Id == otherUserId) {
                                                innerMatch = potentialMatch.copy(id = childSnapshot.key ?: "")
                                                break
                                            }
                                        }
                                        continuation.resume(innerMatch)
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        continuation.resumeWithException(error.toException())
                                    }
                                })
                        } else {
                            continuation.resume(match)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                })
        }
    }

    private suspend fun updateExistingMatch(match: Match, currentUserId: String, otherUserId: String): Boolean {
        val updatedMatch = when {
            currentUserId == match.user1Id -> match.copy(user1Liked = true)
            currentUserId == match.user2Id -> match.copy(user2Liked = true)
            else -> return false
        }

        val isNowMatch = updatedMatch.user1Liked && updatedMatch.user2Liked
        val finalMatch = updatedMatch.copy(isMatch = isNowMatch)

        return try {
            database.child("matches").child(match.id)
                .setValue(finalMatch).await()
            isNowMatch
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun createNewMatch(currentUserId: String, otherUserId: String): Boolean {
        val newMatch = Match(
            user1Id = currentUserId,
            user2Id = otherUserId,
            user1Liked = true,
            user2Liked = false,
            isMatch = false,
            timestamp = System.currentTimeMillis()
        )

        return try {
            val newMatchRef = database.child("matches").push()
            newMatchRef.setValue(newMatch).await()
            false // Retorna false porque aún no es un match completo
        } catch (e: Exception) {
            false
        }
    }

    fun listenForNewMatches(currentUserId: String, onNewMatch: (Profile) -> Unit) {
        database.child("matches")
            .orderByChild("isMatch")
            .equalTo(true)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val match = snapshot.getValue(Match::class.java) ?: return
                    if (match.user1Id == currentUserId || match.user2Id == currentUserId) {
                        // Obtener el ID del otro usuario
                        val otherUserId = if (match.user1Id == currentUserId) match.user2Id else match.user1Id

                        // Obtener el perfil del otro usuario
                        database.child("profiles").child(otherUserId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.getValue(Profile::class.java)?.let { profile ->
                                        onNewMatch(profile)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Manejar error
                                }
                            })
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }
}