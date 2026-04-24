package com.example.authenticationexample.data.user

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val uid: String = "",
    val firstName: String = "",
    val surname: String = "",
    val email: String = "",
    val role: UserRole = UserRole.STAFF
){
    override fun toString(): String = "$firstName \n $surname $email $role"
}