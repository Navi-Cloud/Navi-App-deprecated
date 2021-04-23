package com.kangdroid.naviapp

import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kangdroid.naviapp.databinding.ActivityLoginBinding
import com.kangdroid.naviapp.databinding.ActivityRegisterBinding
import com.kangdroid.naviapp.server.ServerManagement
import org.w3c.dom.Text

class RegisterActivity : AppCompatActivity()  {

    lateinit var binding  : ActivityRegisterBinding

    lateinit var id : String
    lateinit var pw : String
    lateinit var repw : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {


        pw = binding.Textpassword.text.toString()
        repw = binding.passwordRe.text.toString()


        //아이디 중복 체크
        binding.button.setOnClickListener {
            id = binding.TextId.text.toString()
        }

        if(!binding.Email.toString().contains("@")){
            binding.textInputLayout.error = null
        }else{
            binding.textInputLayout.error = "이메일 형식이 올바르지 않습니다."
        }

        if(pw==repw) {
            binding.button2.isEnabled = true
        }else{
            binding.textInputLayout4.error = "비밀번호가 서로 맞지 않습니다."
        }

        binding.button2.setOnClickListener {
            ServerManagement.register(
                binding.Name.text.toString(),
                binding.TextId.text.toString(),
                binding.Email.text.toString(),
                binding.Textpassword.text.toString()
            )
        }
    }
}