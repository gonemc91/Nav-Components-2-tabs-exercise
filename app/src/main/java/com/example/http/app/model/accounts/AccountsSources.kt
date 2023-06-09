package com.example.http.app.model.accounts

import com.example.http.app.model.AppException.*
import com.example.http.app.model.accounts.entities.Account
import com.example.http.app.model.accounts.entities.SignUpData


interface AccountsSources {

     /**
      * Execute sign-in request.
      * @throws ConnectionException
      * @throws BackendException
      * @throws ParseBackendResponseException
      * @return JWT-token
      */


    suspend fun signIn(email: String, password: String): String

    /**
     * Create a new account.
     * @throws ConectionException
     * @throws BackendException
     * @throws ParseBackendResponseException
     */
    suspend fun signUp(signUpData: SignUpData)

     /**
      * Get the account info of the current signed-in user.
      * @throws ConectionException
      * @throws BackendException
      * @throws ParseBackendResponseException
      */
    suspend fun getAccount(): Account

    /**
     * Change the username of the current signed-in user.
     * @throws ConectionException
     * @throws BackendException
     * @throws ParseBackendResponseException
     */

    suspend fun setUsername(username: String)





}
