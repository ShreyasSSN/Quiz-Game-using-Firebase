package com.example.quizgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.quizgame.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var forgotBinding : ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        forgotBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = forgotBinding.root
        setContentView(view)

        forgotBinding.buttonForgotPassword.setOnClickListener {
            val userEmail = forgotBinding.editTextForgotEmail.text.toString()
            FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail).addOnCompleteListener {task->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "Link has been sent", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }else{
                    Toast.makeText(applicationContext, task.exception!!.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}