package com.example.quizgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quizgame.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        mainBinding.buttonStartQuiz.setOnClickListener {
            val intent = Intent(this@MainActivity, QuizActivity::class.java)
            startActivity(intent)
        }

        mainBinding.buttonSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build()
            val googleSignInClient = GoogleSignIn.getClient(this@MainActivity, gso)
            googleSignInClient.signOut().addOnCompleteListener {task->
                if (task.isSuccessful){
                    Toast.makeText(applicationContext, "Sign Out Successful", Toast.LENGTH_LONG)
                        .show()
                }else{
                    Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
            val intent= Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}