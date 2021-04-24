package com.kangdroid.naviapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
            }
        }
     }
}