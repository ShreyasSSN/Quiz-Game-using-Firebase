package com.example.quizgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.quizgame.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding

    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)

        val textOfGoogleButton = loginBinding.buttonGoogleSignIn.getChildAt(0) as TextView
        textOfGoogleButton.text = "Continue with Google"
        textOfGoogleButton.setTextColor(Color.BLACK)
        textOfGoogleButton.textSize = 18F

        registerActivityForGoogleSignIn()

        loginBinding.buttonSignIn.setOnClickListener {

            val userEmail = loginBinding.editTextloginEmail.text.toString()
            val userPassword = loginBinding.editTextLoginPassword.text.toString()
            loginToFirebase(email = userEmail, password = userPassword)

        }
        loginBinding.buttonGoogleSignIn.setOnClickListener {
            signInGoogle()
        }

        loginBinding.textviewSignup.setOnClickListener{

            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)

        }

        loginBinding.textViewForgotPassword.setOnClickListener {

            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)

        }
    }

    private fun signInGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("<Your token id>")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)

        signIn()
    }

    private fun signIn() {
        val signInIntent : Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)

    }

    fun loginToFirebase(email : String, password : String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "You have been Logged In Successfully", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(applicationContext, task.exception!!.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser

        if (user != null){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerActivityForGoogleSignIn(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {result ->
                val resultCode = result.resultCode
                val data = result.data
                if(resultCode == RESULT_OK && data != null){

                    val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                    firebaseSignInWithGoogle(task)
                }
            })

    }

    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val account : GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext, "Signed with Google Successfully", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            firebaseGoogleAccount(account)
        }catch (e: ApiException){
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount) {
        val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(authCredential).addOnCompleteListener {task ->
            if(task.isSuccessful){
                val user = auth.currentUser
            }else{
                Toast.makeText(applicationContext,task.exception!!.localizedMessage ,Toast.LENGTH_LONG)
                    .show()
            }
        }
    }


}