package com.kangdroid.naviapp

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kangdroid.naviapp.data.RegisterRequest
import com.kangdroid.naviapp.databinding.ActivityLoginBinding
import com.kangdroid.naviapp.databinding.ActivityRegisterBinding
import com.kangdroid.naviapp.server.ServerManagement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class RegisterActivity : AppCompatActivity()  {

    private val coroutineScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    lateinit var binding  : ActivityRegisterBinding
    lateinit var id : String
    lateinit var pw : String
    lateinit var repw : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        if(!ServerManagement.initServerCommunication()){
            Log.e("","Server initiation failed")
        }
        setContentView(binding.root)
        init()
    }

    private fun init() {

        binding.button2.isEnabled = false

        binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) binding.button2.isEnabled = true
        }

        binding.button2.setOnClickListener {

            pw = binding.Textpassword.text.toString()
            repw = binding.passwordRe.text.toString()

            //아이디 중복 체크
            binding.button.setOnClickListener {
                id = binding.TextId.text.toString()
            }

            // 이메일 @ 체크
            if(!binding.Email.toString().contains("@")){
                binding.textInputLayout.error = null
            }else{
                binding.textInputLayout.error = "이메일 형식이 올바르지 않습니다."
            }

            //재입력한 비밀번호 == 비밀번호 확인
            if(pw==repw) {
                binding.button2.isEnabled = true
            }else{
                binding.textInputLayout4.error = "비밀번호가 서로 맞지 않습니다."
            }

            val register = RegisterRequest(
                binding.Name.text.toString(),
                binding.TextId.text.toString(),
                binding.Email.text.toString(),
                binding.Textpassword.text.toString())

            coroutineScope.launch {

                val response: String =  ServerManagement.register(register)

                Log.d(RegisterActivity::class.java.simpleName, "Response: $response")
            }

            finish()
        }
    }
}