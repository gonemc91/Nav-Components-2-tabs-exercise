package com.example.http.app.screens.main.auth

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.http.R
import com.example.http.app.model.AccountAlreadyExistsException
import com.example.http.app.model.EmptyFieldException
import com.example.http.app.model.Field
import com.example.http.app.model.PasswordMismatchException
import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.model.accounts.entities.SignUpData
import com.example.http.app.utils.MutableUnitLiveEvent
import com.example.http.app.utils.publishEvent
import com.example.http.app.utils.requireValue
import com.example.http.app.utils.share
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val accountsSources: AccountsSources
) : ViewModel(){
    private val _showSuccessSignUpMessageEvent = MutableUnitLiveEvent()
    val showSuccessSignUpMessageEvent = _showSuccessSignUpMessageEvent.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()


    private val _state = MutableLiveData(State())
    val state = _state.share()


    fun signUp(signUpData : SignUpData){
        viewModelScope.launch {
            viewModelScope.launch {
                showProgress()
                try {
                    accountsSources.signUp(signUpData)
                    showSuccessSignUpMessage()
                    goBack()
                }catch (e: EmptyFieldException){
                    processEmptyFieldException(e)
                }catch (e: PasswordMismatchException){
                    processPasswordMismatchException()
                }catch (e: AccountAlreadyExistsException){
                    processAccountAlreadyExistsException()
                }finally {
                    hideProgress()
                }
            }
        }
    }




    private fun processEmptyFieldException(e: EmptyFieldException){
        _state.value = when(e.field){
            Field.Email -> _state.requireValue()
                .copy(emailErrorMessageRes = R.string.field_is_empty)
            Field.Username -> _state.requireValue()
                .copy(usernameErrorMessageRes = R.string.field_is_empty)
            Field.Password -> state.requireValue()
                .copy(passwordErrorMessageRes = R.string.field_is_empty)
            else -> throw IllegalStateException("Unknown field")
        }
    }

    private fun processPasswordMismatchException(){
        _state.value = _state.requireValue()
            .copy(repeatPasswordErrorMessageRes = R.string.password_mismatch)
    }

    private fun processAccountAlreadyExistsException(){
        _state.value = _state.requireValue()
            .copy(emailErrorMessageRes = R.string.account_already_exists)
    }

    private fun showProgress(){
        _state.value = State(signUpInProgress = true)
    }
    private fun hideProgress(){
        _state.value = _state.requireValue().copy(signUpInProgress = false)
    }

    private fun showSuccessSignUpMessage() = _showSuccessSignUpMessageEvent.publishEvent()


    private fun goBack() = _goBackEvent.publishEvent()


    data class State(
        @StringRes val emailErrorMessageRes: Int = NO_ERROR_MESSAGE,
        @StringRes val passwordErrorMessageRes: Int = NO_ERROR_MESSAGE,
        @StringRes val repeatPasswordErrorMessageRes: Int = NO_ERROR_MESSAGE,
        @StringRes val usernameErrorMessageRes: Int = NO_ERROR_MESSAGE,
        val signUpInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = signUpInProgress
        val enableViews: Boolean get() = !signUpInProgress
    }
        companion object {
            const val NO_ERROR_MESSAGE = 0
        }
    }




