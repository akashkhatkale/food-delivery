package com.arabiannights.arabiannights.ui.start

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.arabiannights.arabiannights.R
import com.arabiannights.arabiannights.database.FirebaseRepository
import com.arabiannights.arabiannights.ui.MainActivity
import com.arabiannights.arabiannights.utils.goToActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

class LogoActivity : AppCompatActivity() {

    private val repo = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)


        checkUser()

    }

    private fun checkUser() {
        FirebaseAuth.getInstance().currentUser?.let{
            GlobalScope.launch(Dispatchers.IO) {
                async {
                    repo.getCurrentUser(it)
                    withContext(Dispatchers.Main){
                        goToActivity(MainActivity::class.java)
                        finish()
                    }
                }
            }
        } ?: kotlin.run {
            goToActivity(LoginActivity::class.java)
            finish()
        }
    }
}