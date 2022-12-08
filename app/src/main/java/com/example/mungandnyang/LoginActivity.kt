package com.example.mungandnyang

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.mungandnyang.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.util.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    lateinit var userAuth : FirebaseAuth
    var mySharedPreferences: MySharedPreferences = MySharedPreferences()
    var mBackWait: Long = 0
    var checkid: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화
        userAuth = Firebase.auth

        //텍스트 회원가입 이벤트
        binding.tvLARegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        //mySharedPreferences 파일에 ID와 PW값을 가져왔을 때 Null 또는 공백이라면 '일반 로그인' 진행
        if(mySharedPreferences.getUserId(this).isNullOrBlank()
            || mySharedPreferences.getUserPass(this).isNullOrBlank()) {
            binding.btnLALogin.setOnClickListener {
                loginPattern()
            }
        } else { // SharedPreferences 안에 값이 저장되어 있을 때 '자동 로그인' 진행
            binding.ivAutologin.setImageResource(R.drawable.ic_check_box_24)
            //shared 에서 가져온 Email과 Password를 자동으로 입력
            binding.edtLAEmail.setText(mySharedPreferences.getUserId(binding.root.context))
            binding.edtLAPassword.setText(mySharedPreferences.getUserPass(binding.root.context))
            //자동 로그인 진행 -> MainActivity 이동
            userAuth.signInWithEmailAndPassword(binding.edtLAEmail.text.toString(), binding.edtLAPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "${binding.edtLAEmail.text}님 멍앤냥에 오신걸 환영합니다!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
        }

        //자동로그인 설정 체크
        binding.ivAutologin.setOnClickListener {
            when (checkid) {
                0 -> binding.ivAutologin.setImageResource(R.drawable.ic_check_box_blank_24)
                1 -> binding.ivAutologin.setImageResource(R.drawable.ic_check_box_24)
            }
            if (checkid == 0) {
                binding.ivAutologin.setImageResource(R.drawable.ic_check_box_blank_24)
                checkid = 1
                // 체크 해제 시 '일반 로그인'
                binding.btnLALogin.setOnClickListener {
                    loginPattern()
                }
            }else{
                binding.ivAutologin.setImageResource(R.drawable.ic_check_box_24)
                checkid = 0
                // 체크 시 '자동 로그인'
                binding.btnLALogin.setOnClickListener {
                    autoLoginPattern()
                }
            }
        }
    }

    //일반 로그인
    private fun login(email: String, password: String) {
        userAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "${email}님 멍앤냥에 오신걸 환영합니다!", Toast.LENGTH_SHORT).show()
                    finish()
                }else {
                    Toast.makeText(this, "이메일 또는 비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show()
                    Log.d("mungandnyang", "Error: ${task.exception}")
                }
            }
    }

    //자동 로그인
    private fun autologin(email: String, password: String) {
        userAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 사용자의 Email과 Password를 shared에 저장
                        mySharedPreferences.setUserId(this, email)
                        mySharedPreferences.setUserPass(this, password)
                        Toast.makeText(
                            this,
                            "${mySharedPreferences.getUserId(this)}님 멍앤냥에 오신걸 환영합니다!",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "이메일 또는 비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show()
                        Log.d("mungandnyang", "Error: ${task.exception}")
                    }
            }
    }

    private fun loginPattern(){
        val passwordRegex =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[@$!%*#?&.])[A-Za-z[0-9]@$!%*#?&.]{0,15}$")
        val emailRegex =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[@.])[A-Za-z[0-9]@.]{8,30}$")
        // 화면에 나오는 소프트 키보드 제어 객체
        var manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val password = binding.edtLAPassword.text.toString()
        val email = binding.edtLAEmail.text.toString()

        if (email.isEmpty()) {
            binding.edtLAEmail.requestFocus()
            // 입력 표현식이 틀렸을 시, 자동으로 틀린 부분을 보여지게하는
            manager.showSoftInput(binding.edtLAEmail, InputMethodManager.SHOW_IMPLICIT)
            Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            binding.edtLAPassword.requestFocus()
            manager.showSoftInput(
                binding.edtLAPassword,
                InputMethodManager.SHOW_IMPLICIT
            )
            Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
        } else if (!emailRegex.matcher(email).matches()) {
            binding.edtLAEmail.requestFocus()
            manager.showSoftInput(binding.edtLAEmail, InputMethodManager.SHOW_IMPLICIT)
            Toast.makeText(this, "올바르지 않은 이메일 형식입니다.", Toast.LENGTH_SHORT).show()
        } else if (!passwordRegex.matcher(password).matches()) {
            binding.edtLAPassword.requestFocus()
            manager.showSoftInput(
                binding.edtLAPassword,
                InputMethodManager.SHOW_IMPLICIT
            )
            Toast.makeText(this, "잘못된 비밀번호입니다.", Toast.LENGTH_SHORT).show()
        } else {
            login(email, password)
        }
    }

    private fun autoLoginPattern(){
        val passwordRegex =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[@$!%*#?&.])[A-Za-z[0-9]@$!%*#?&.]{0,15}$")
        val emailRegex =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[@.])[A-Za-z[0-9]@.]{8,30}$")
        var manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val password = binding.edtLAPassword.text.toString()
        val email = binding.edtLAEmail.text.toString()

        if (email.isEmpty()) {
            binding.edtLAEmail.requestFocus()
            manager.showSoftInput(binding.edtLAEmail, InputMethodManager.SHOW_IMPLICIT)
            Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            binding.edtLAPassword.requestFocus()
            manager.showSoftInput(
                binding.edtLAPassword,
                InputMethodManager.SHOW_IMPLICIT
            )
            Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
        } else if (!emailRegex.matcher(email).matches()) {
            binding.edtLAEmail.requestFocus()
            manager.showSoftInput(binding.edtLAEmail, InputMethodManager.SHOW_IMPLICIT)
            Toast.makeText(this, "올바르지 않은 이메일 형식입니다.", Toast.LENGTH_SHORT).show()
        } else if (!passwordRegex.matcher(password).matches()) {
            binding.edtLAPassword.requestFocus()
            manager.showSoftInput(
                binding.edtLAPassword,
                InputMethodManager.SHOW_IMPLICIT
            )
            Toast.makeText(this, "잘못된 비밀번호입니다.", Toast.LENGTH_SHORT).show()
        } else {
            autologin(email, password)
        }
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭 - 2초 이내 클릭한다면, 종료
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Snackbar.make(binding.root,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.",Snackbar.LENGTH_LONG).show()
        } else {
            finish()
        }
    }
}