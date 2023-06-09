package com.example.http.app.model.accounts.entities

/**
 * Information about the user.
 */

data class Account (
    val id: Long,
    val userName: String,
    val email: String,
    val createdAt: Long = UNKNOWN_CREATED_AT
){

    companion object{
        const val UNKNOWN_CREATED_AT = 0L
    }
}