package com.kangdroid.naviapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.kangdroid.naviapp.data.RegisterRequest
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
    lateinit var email : String
    lateinit var name : String
    var email_flag = false
    var pw_flag = false
    var id_flag = false

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

        binding.Email.addTextChangedListener {
            if (binding.Email.text.toString().contains("@")) {
                binding.textInputLayout.error = null
                email_flag = true
            }else{
                binding.textInputLayout.error = "이메일 형식이 올바르지 않습니다."
                email_flag = false
            }
        }

        binding.passwordRe.addTextChangedListener {
            if (binding.Textpassword.text.toString() == binding.passwordRe.text.toString()){
                binding.textInputLayout4.error = null
                pw_flag = true
           }else{
                binding.textInputLayout4.error = "비밀번호가 서로 맞지 않습니다."
                pw_flag = false
            }
        }

        binding.button2.setOnClickListener {

            pw = binding.Textpassword.text.toString()
            repw = binding.passwordRe.text.toString()
            email = binding.Email.toString()
            id = binding.TextId.text.toString()
            name = binding.Name.text.toString()

            //약관동의 체크
            if(!binding.checkbox.isChecked)
                Toast.makeText(this, "약관 동의를 하지 않았습니다.", Toast.LENGTH_SHORT).show()

            else if(name=="" || pw=="" || repw=="" || id=="")
                Toast.makeText(this,"양식을 모두 채우지 않았습니다.",Toast.LENGTH_SHORT).show()

            else if(!pw_flag || !email_flag)
                Toast.makeText(this,"양식을 올바르게 채우지 않았습니다.",Toast.LENGTH_SHORT).show()

            else {
                //아이디 중복 체크
                binding.button.setOnClickListener {
                    id = binding.TextId.text.toString()
                }

                val register = RegisterRequest(
                    binding.Name.text.toString(),
                    binding.TextId.text.toString(),
                    binding.Email.text.toString(),
                    binding.Textpassword.text.toString()
                )

                coroutineScope.launch {

                    val response: String = ServerManagement.register(register)

                    Log.d(RegisterActivity::class.java.simpleName, "Response: $response")
                }

                finish()
            }
        }
    }
}