package com.example.nav_components_2_tabs_exercise.model.accounts

import android.database.sqlite.SQLiteDatabase
import com.example.nav_components_2_tabs_exercise.model.AuthException
import com.example.nav_components_2_tabs_exercise.model.EmptyFieldException
import com.example.nav_components_2_tabs_exercise.model.Field
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.Account
import com.example.nav_components_2_tabs_exercise.model.accounts.entities.SignUpData
import com.example.nav_components_2_tabs_exercise.model.settings.AppSettings
import com.example.nav_components_2_tabs_exercise.model.sqlite.wrapSQLiteException
import com.example.nav_components_2_tabs_exercise.utils.AsyncLoader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


/**
 * Simple implementation of [AccountsRepository] which holds accounts data in the app memory.
 */

class SQLiteAccountRepository(
    private val db: SQLiteDatabase,
    private val appSettings: AppSettings,
    private val ioDispatcher: CoroutineDispatcher
): AccountsRepository {

    private val currentAccountIdFlow = AsyncLoader {
        MutableStateFlow(AccountId(appSettings.getCurrentAccountId()))
    }



    override suspend fun isSignedIn(): Boolean {
        delay(2000)
        return appSettings.getCurrentAccountId() != AppSettings.NO_ACCOUNT_ID
}

    override suspend fun signIn(email: String, password: String) = wrapSQLiteException(ioDispatcher){
        if(email.isBlank()) throw EmptyFieldException(Field.Email)
        if(password.isBlank()) throw EmptyFieldException(Field.Password)

        delay(1000)

        val accountId = findAccountByEmailAndPassword(email, password)
        appSettings.setCurrentAccountId(accountId)
        currentAccountIdFlow.get().value = AccountId(accountId)
        return@wrapSQLiteException

    }

    override suspend fun signUp(signUpData: SignUpData) {
        signUpData.validate()
        delay(1000)
        createAccount(signUpData)

    }

    override suspend fun logout() {
       appSettings.setCurrentAccountId(AppSettings.NO_ACCOUNT_ID)
        currentAccountIdFlow.get().value = AccountId(AppSettings.NO_ACCOUNT_ID)
    }

    override suspend fun getAccount(): Flow<Account?> {
        return currentAccountIdFlow.get()
            .map { accountId ->
                getAccountById(accountId.value)
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun updateAccountUsername(newUserName: String) = wrapSQLiteException(ioDispatcher) {
        if(newUserName.isBlank()) throw EmptyFieldException(Field.Username)
        delay(1000)

        val accountId = appSettings.getCurrentAccountId()
        if (accountId == AppSettings.NO_ACCOUNT_ID) throw AuthException()

        updateUsernameForAccountId(accountId, newUserName)

        currentAccountIdFlow.get().value = AccountId(accountId)
        return@wrapSQLiteException

    }




    private fun findAccountByEmailAndPassword(email: String, password: String): Long{
        TODO("#3 \n" +
                "1) fetch account ID by email and password here \n" +
                "2) throw AuthException if there is no account with such email OR password is invalid")
    }
    private fun createAccount(signUpData: SignUpData){
        TODO("#4 \n " +
                "1) Insert a new row into accounts table here using data provided by SignUpData class \n" +
                "2) throw AccountAlreadyExistsException if there is another account with such email in the database \n" +

                "Tip: use SQLiteDatabase.insertOrThrow method and surround it with try-catch(e: SQLiteConstraintException)")
    }


    private fun getAccountById(accountId: Long):Account?{
        TODO("#5 \n " +
                "1) Fetch account data by ID from the database \n" +
                "2) Return NULL if accountId = AppSettings.NO_ACCOUNT_ID or there is no row with such ID in the database \n" +
                "3) Do not forget to close Cursor")
    }


    private fun updateUsernameForAccountId(accountId: Long, newUserName: String){
        TODO("#6 \n " +
                "Update username column of the row with the specified account ID")
    }
    private class AccountId(val value: Long)


}