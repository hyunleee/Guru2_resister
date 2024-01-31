package guru2_resister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2_mypage.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class FirebaseID {
    companion object {
        const val documentId = "documentId"
        const val email = "email"
        const val password = "password"
        const val name = "name"
        const val user = "user"
    }
}

class Signup : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    private lateinit var back: TextView
    private lateinit var signup: Button
    private lateinit var id: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // 이전 버튼
        back = findViewById(R.id.btn_back)
        // 회원가입 버튼
        signup = findViewById(R.id.btn_signup)
        // 회원정보 editText
        id = findViewById(R.id.et_id)
        password = findViewById(R.id.et_pwd)
        name = findViewById(R.id.et_name)

        // 로그인 화면으로 돌아가기
        back.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        // 회원가입 버튼 클릭 시 회원가입 처리 함수 호출
        signup.setOnClickListener {
            handleSignupClick()
        }
    }

    private fun handleSignupClick() {
        val inputEmail = id.text.toString().trim()
        val inputPassword = password.text.toString().trim()
        val inputName = name.text.toString().trim()

        if (inputEmail.isNotEmpty() && inputPassword.isNotEmpty() && inputName.isNotEmpty()) {
            mAuth.createUserWithEmailAndPassword(id.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val userMap = hashMapOf(
                            FirebaseID.email to id,
                            FirebaseID.password to password,
                            FirebaseID.name to name
                        )
                        //Firestore에 데이터 추가
                        db.collection("users").document()
                            .set(userMap, SetOptions.merge())
                            .addOnSuccessListener {
                                val intent = Intent(this@Signup, Login::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { exception ->
                                val errorMessage = "Firestore 데이터 추가 실패: ${exception.message}"
                                Toast.makeText(this@Signup, errorMessage, Toast.LENGTH_SHORT).show()
                                Log.e("Firestore", errorMessage, exception)
                            }

                    } else {
                        Toast.makeText(this@Signup, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
