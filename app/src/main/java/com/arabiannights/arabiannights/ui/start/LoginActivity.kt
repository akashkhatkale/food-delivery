package com.arabiannights.arabiannights.ui.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.database.FirebaseMethods
import com.arabiannights.arabiannights.ui.MainActivity
import com.arabiannights.arabiannights.utils.constants.LOGINLOG
import com.arabiannights.arabiannights.utils.goToActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private var storedVerificationId : String = ""
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken


    private var isOtpSent = false

    private var mobile : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        submitButton_phone.setOnClickListener {
            if(!isOtpSent){
                mobile = phoneText_phone.text.toString()
                if(mobile.trim().length == 10){
                    setEnabled(false,0.5f,View.VISIBLE)
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91$mobile", // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        this, // Activity (for callback binding)
                        callbacks)
                    resendOtp_phone.alpha = 0.7f
                    resendOtp_phone.isEnabled = false
                    Handler().postDelayed({
                        resendOtp_phone.alpha = 1.0f
                        resendOtp_phone.isEnabled = true
                    }, 60000)
                }else{
                    Toasty.error(this,"Enter a valid mobile number").show()
                }
            }else{
                if(otpText_phone.text != null && otpText_phone.text!!.length == 6){
                    setEnabled(false,0.5f,View.VISIBLE)
                    val cred = PhoneAuthProvider.getCredential(storedVerificationId,otpText_phone.text!!.trim().toString())
                    signInWithPhoneAuthCredential(cred)
                }else{
                    Toasty.error(this,"Enter otp").show()
                }
            }
        }

        changePhone_phone.setOnClickListener {
            if(extraButton_phone.visibility == View.VISIBLE){
                isOtpSent = false
                updateUi()
            }
        }

        resendOtp_phone.setOnClickListener{
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91$mobile", // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                callbacks,
                resendToken)
            resendOtp_phone.alpha = 0.7f
            resendOtp_phone.isEnabled = false
            Handler().postDelayed({
                resendOtp_phone.alpha = 1.0f
                resendOtp_phone.isEnabled = true
            }, 60000)
        }

    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(LOGINLOG, "signInWithCredential:success")

                    val user = task.result?.user
                    GlobalScope.launch(Dispatchers.IO) {
                        async {
                            if(FirebaseMethods().checkUserInDatabase("",user)){
                                withContext(Dispatchers.Main){
                                    finish()
                                    goToActivity(MainActivity::class.java)
                                }
                            }else{
                                withContext(Dispatchers.Main){
                                    Toasty.error(this@LoginActivity, "Try again later").show()
                                }
                            }
                        }
                    }
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d(LOGINLOG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toasty.error(this,"Invalid otp").show()
                    }
                    setEnabled(true,1.0f,View.INVISIBLE)
                }
            }
    }




    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(LOGINLOG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
            setEnabled(false,0.5f, View.VISIBLE)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(LOGINLOG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toasty.error(this@LoginActivity,"Invalid otp").show()
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }
            setEnabled(true,1.0f, View.INVISIBLE)

        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d(LOGINLOG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
            isOtpSent = true
            updateUi()
            setEnabled(true,1.0f, View.INVISIBLE)
        }
    }



    private fun updateUi() {
        if(isOtpSent){
            // otp sent
            otpText_phone.visibility = View.VISIBLE
            phoneText_phone.visibility = View.GONE
            extraButton_phone.visibility = View.VISIBLE
        }else{
            // phone number
            otpText_phone.visibility = View.GONE
            phoneText_phone.visibility = View.VISIBLE
            extraButton_phone.visibility = View.INVISIBLE
        }
    }

    private fun setEnabled(enabled : Boolean, alpha : Float, progress : Int){
        submitButton_phone.isEnabled = enabled
        submitButton_phone.alpha = alpha
        submitProgress_phone.visibility = progress
    }



}