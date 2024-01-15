package com.example.quizgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.quizgame.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    lateinit var signUpBinding: ActivitySignUpBinding

    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signUpBinding.root
        setContentView(view)

        signUpBinding.buttonSignUp.setOnClickListener {
            val email = signUpBinding.editTextEmail.text.toString()
            val password = signUpBinding.editTextPassword.text.toString()
            signUpWithFirebase(email, password)
        }
    }

    fun signUpWithFirebase(email: String, password: String){

        signUpBinding.progressBar.isVisible = true
        signUpBinding.buttonSignUp.isClickable = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "User has been created", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                    signUpBinding.progressBar.isVisible = false
                    signUpBinding.buttonSignUp.isClickable = true
                }else{
                    Toast.makeText(applicationContext,task.exception!!.localizedMessage ,Toast.LENGTH_LONG)
                        .show()
                }
            }
    }
}

