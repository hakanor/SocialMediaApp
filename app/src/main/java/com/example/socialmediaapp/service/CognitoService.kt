package com.example.socialmediaapp.service

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import com.example.myapplication.service.ApiService
import com.example.socialmediaapp.Constants

interface CognitoServiceCallback {
    fun onLoginSuccess()
    fun onSignOut()
    fun onRegisterSuccess()
}

class CognitoService(private val appContext: Context, private val serviceCallback: CognitoServiceCallback) {
    // Information about Cognito Pool
    private val poolID = "us-east-2_LIkUhE6Po"
    private val clientID = "4fucata6lk081t7vmqqqb1i1hd"
    private val clientSecret = "18vq76he8uq8a3k8d0rok2pu46f4sv9rfnbb1d845jqgrfiod618"
    private val awsRegion = Regions.US_EAST_2 // Place your Region

    // End of Information about Cognito Pool
    val userPool: CognitoUserPool
    private val userAttributes : CognitoUserAttributes
    private var userPassword: String? = null // Used for Login

    private var name: String? = null
    private var surname: String? = null
    private var userId: String? = null
    init {
        userPool = CognitoUserPool(appContext, poolID, clientID, clientSecret, awsRegion)
        userAttributes = CognitoUserAttributes()
    }
    fun signUpInBackground(userId: String?, password: String?, name:String?, surname:String?) {
        this.userId = userId
        this.name = name
        this.surname = surname
        userPool.signUpInBackground(userId, password, userAttributes, null, signUpCallback)
        //userPool.signUp(userId, password, this.userAttributes, null, signUpCallback);
    }

    var signUpCallback: SignUpHandler = object : SignUpHandler {
        override fun onSuccess(user: CognitoUser?, signUpResult: SignUpResult?) {
            // Sign-up was successful
            registerUserToDatabase()
            Toast.makeText(appContext, "Sign-up success", Toast.LENGTH_LONG).show()
            serviceCallback.onRegisterSuccess()
            // Check if this user (cognitoUser) needs to be confirmed
            if (true) {
                // This user must be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user
            } else {
                // The user has already been confirmed
            }
        }

        override fun onFailure(exception: Exception) {
            Toast.makeText(appContext, "Sign-up failed: $exception", Toast.LENGTH_LONG).show()
        }
    }

    fun confirmUser(userId: String?, code: String?) {
        val cognitoUser = userPool.getUser(userId)
        cognitoUser.confirmSignUpInBackground(code, true, confirmationCallback)
        //cognitoUser.confirmSignUp(code,false, confirmationCallback);
    }

    // Callback handler for confirmSignUp API
    var confirmationCallback: GenericHandler = object : GenericHandler {
        override fun onSuccess() {
            // User was successfully confirmed
            Toast.makeText(appContext, "User Confirmed", Toast.LENGTH_LONG).show()
        }

        override fun onFailure(exception: Exception) {
            // User confirmation failed. Check exception for the cause.
            Toast.makeText(appContext, "User Not Confirmed ${exception.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    fun addAttribute(key: String?, value: String?) {
        userAttributes.addAttribute(key, value)
    }

    fun userLogin(userId: String?, password: String?) {
        val cognitoUser = userPool.getUser(userId)
        userPassword = password
        cognitoUser.getSessionInBackground(authenticationHandler)
    }
    fun userSignOut() {
        val currentUser = userPool.currentUser
        currentUser?.signOut()
        var sp = SharedPreferencesService(appContext)
        sp.userSignOut()
        serviceCallback.onSignOut()
    }
    fun getCurrentUserId (): String {
        return this.userPool.currentUser.userId
    }

    fun registerUserToDatabase () {
        val apiService = ApiService()
        val url = Constants.URL_USERS
        val method = "POST"
        val requestBody = "{\"userId\":\"$userId\",\"name\":\"$name\", \"surname\": \"$surname\"}"
        apiService.sendHttpRequestWithApiKey(url, method, requestBody) { responseBody, responseCode,error ->
            if (error != null) {
                error.printStackTrace()
            } else {
                responseBody?.let {
                    Log.d("Register", "Register successful.")
                }
            }
        }
    }

    // Callback handler for the sign-in process
    var authenticationHandler: AuthenticationHandler = object : AuthenticationHandler {
        override fun authenticationChallenge(continuation: ChallengeContinuation) {}
        override fun onSuccess(userSession: CognitoUserSession, newDevice: CognitoDevice?) {
            //Toast.makeText(appContext, "Sign in success", Toast.LENGTH_LONG).show()
            //var sp = SharedPreferencesService(appContext)
            //sp.userLoggedIn()
            serviceCallback.onLoginSuccess()
        }

        override fun getAuthenticationDetails(
            authenticationContinuation: AuthenticationContinuation,
            userId: String
        ) {
            // The API needs user sign-in credentials to continue
            val authenticationDetails = AuthenticationDetails(userId, userPassword, null)
            // Pass the user sign-in credentials to the continuation
            authenticationContinuation.setAuthenticationDetails(authenticationDetails)
            // Allow the sign-in to continue
            authenticationContinuation.continueTask()
        }

        override fun getMFACode(multiFactorAuthenticationContinuation: MultiFactorAuthenticationContinuation) {
            // Multi-factor authentication is required; get the verification code from user
            //multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
            // Allow the sign-in process to continue
            //multiFactorAuthenticationContinuation.continueTask();
        }

        override fun onFailure(exception: Exception) {
            // Sign-in failed, check exception for the cause
            Toast.makeText(appContext, "Sign in Failed", Toast.LENGTH_LONG).show()
            Toast.makeText(appContext, exception.toString(), Toast.LENGTH_LONG).show()
        }
    }
}