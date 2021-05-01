package com.kangdroid.naviapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.kangdroid.naviapp.data.LoginRequest
import com.kangdroid.naviapp.databinding.ActivityLoginBinding
import com.kangdroid.naviapp.server.ServerManagement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    lateinit var binding  : ActivityLoginBinding
    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!ServerManagement.initServerCommunication()) {
            Log.wtf(this::class.java.simpleName, "Server initiation failed!")
            Log.wtf(this::class.java.simpleName, "This should NOT be happened!")
        }

        init()
    }

    private fun init() {
        binding.button.setOnClickListener {

            val login = LoginRequest(binding.idLogin.text.toString(),binding.pwLogin.text.toString())

            Log.i("LOGIN_ACTIVITY", "${login.userId}+${login.userPassword}")
            coroutineScope.launch {
                val response: String = ServerManagement.login(login)

                Log.d(LoginActivity::class.java.simpleName, "Response: $response")

                if(response == "-1") {
                    // login fail
                } else {
                    // login success
                    val userToken = getTokenFromResponse(response)
                    Log.d("GetRootToken form Response", userToken)
                    val mainActivityIntentWithUserToken: Intent = Intent(this@LoginActivity, MainActivity::class.java)
                        .putExtra("userToken", userToken)
                    startActivity(mainActivityIntentWithUserToken)
                }
            }
        }

        binding.textView2.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
     }

    private fun getTokenFromResponse(response: String): String {
        // Response Must have only "userToken" for now
        return response.split(":")[1].replace(Regex("[\"{}]"), "")
    }
}