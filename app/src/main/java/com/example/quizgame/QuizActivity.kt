package com.example.quizgame

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.quizgame.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class QuizActivity : AppCompatActivity() {

    lateinit var quizBinding: ActivityQuizBinding

    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference.child("questions")

    var question = ""
    var answerA = ""
    var answerB = ""
    var answerC = ""
    var answerD = ""
    var correctAnswer = ""
    var questionCount : Long = 0
    var questionNumber = 1
    var userCorrect = 0
    var userWrong = 0

    lateinit var timer : CountDownTimer
    var totalTime = 25000L
    var leftTime = totalTime
    var timerContinue = false

    val auth =FirebaseAuth.getInstance()
    val user = auth.currentUser
    val scoreReference = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)
        gameLogic()

        quizBinding.buttonFinish.setOnClickListener {
            sendScore()
            finish()
        }

        quizBinding.buttonNext.setOnClickListener {

            resetTimer()
            gameLogic()
            restoreOptions()
        }

        quizBinding.textViewOptionA.setOnClickListener {

            pauseTimer()

            if(correctAnswer == "a"){
                quizBinding.textViewOptionA.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else{
                quizBinding.textViewOptionA.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()

        }

        quizBinding.textViewOptionB.setOnClickListener {

            pauseTimer()

            if(correctAnswer == "b"){
                quizBinding.textViewOptionB.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else{
                quizBinding.textViewOptionB.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()
        }

        quizBinding.textViewOptionC.setOnClickListener {

            pauseTimer()

            if(correctAnswer == "c"){
                quizBinding.textViewOptionC.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else{
                quizBinding.textViewOptionC.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()
        }

        quizBinding.textViewOptionD.setOnClickListener {

            pauseTimer()

            if(correctAnswer == "d"){
                quizBinding.textViewOptionD.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            }else{
                quizBinding.textViewOptionD.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }
            disableClickableOptions()
        }
    }

    private fun gameLogic(){
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                questionCount = snapshot.childrenCount

                if(questionNumber<=questionCount){

                    question = snapshot.child(questionNumber.toString()).child("q").value.toString()
                    answerA = snapshot.child(questionNumber.toString()).child("a").value.toString()
                    answerB = snapshot.child(questionNumber.toString()).child("b").value.toString()
                    answerC = snapshot.child(questionNumber.toString()).child("c").value.toString()
                    answerD = snapshot.child(questionNumber.toString()).child("d").value.toString()
                    correctAnswer = snapshot.child(questionNumber.toString()).child("answer").value.toString()

                    quizBinding.textViewQuestion.text = question
                    quizBinding.textViewOptionA.text = answerA
                    quizBinding.textViewOptionB.text = answerB
                    quizBinding.textViewOptionC.text = answerC
                    quizBinding.textViewOptionD.text = answerD

                    quizBinding.progressBarQuiz.isVisible = false
                    quizBinding.linearLayoutInfo.isVisible = true
                    quizBinding.linearLayoutQuestion.isVisible = true
                    quizBinding.linearLayoutButtons.isVisible = true

                    startTimer()

                }else{
                    showDialog()
                }

                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun findAnswer(){
        when(correctAnswer){
            "a" -> quizBinding.textViewOptionA.setBackgroundColor(Color.GREEN)
            "b" -> quizBinding.textViewOptionB.setBackgroundColor(Color.GREEN)
            "c" -> quizBinding.textViewOptionC.setBackgroundColor(Color.GREEN)
            "d" -> quizBinding.textViewOptionD.setBackgroundColor(Color.GREEN)
        }
    }

    private fun disableClickableOptions(){
        quizBinding.textViewOptionA.isClickable = false
        quizBinding.textViewOptionB.isClickable = false
        quizBinding.textViewOptionC.isClickable = false
        quizBinding.textViewOptionD.isClickable = false
    }

    private fun restoreOptions(){
        quizBinding.textViewOptionA.setBackgroundColor(Color.WHITE)
        quizBinding.textViewOptionB.setBackgroundColor(Color.WHITE)
        quizBinding.textViewOptionC.setBackgroundColor(Color.WHITE)
        quizBinding.textViewOptionD.setBackgroundColor(Color.WHITE)

        quizBinding.textViewOptionA.isClickable = true
        quizBinding.textViewOptionB.isClickable = true
        quizBinding.textViewOptionC.isClickable = true
        quizBinding.textViewOptionD.isClickable = true
    }

    fun startTimer(){
        timer = object : CountDownTimer(leftTime, 1000){
            override fun onTick(millisUntilFinished: Long) {
                leftTime = millisUntilFinished
                updateCounterTimer()

                if (leftTime<=5001){
                    quizBinding.textViewTime.textSize = 20F
                    quizBinding.textView3.textSize = 20F
                }
            }

            override fun onFinish() {

                disableClickableOptions()
                resetTimer()
                updateCounterTimer()
                quizBinding.textViewQuestion.text = "Sorry, Time's up..!!!"
                timerContinue = false
            }
        }.start()
        timerContinue = true
    }

    private fun resetTimer() {
        pauseTimer()
        leftTime=totalTime
        updateCounterTimer()
    }

    private fun pauseTimer(){
        timer.cancel()
        timerContinue  = false
    }

    private fun updateCounterTimer() {
        val remainingTime : Int = (leftTime/1000).toInt()
        quizBinding.textViewTime.text = remainingTime.toString()
    }

    fun sendScore(){

        user?.let {
            val userUID = it.uid
            scoreReference.child("scores").child(userUID).child("correct").setValue(userCorrect)
            scoreReference.child("scores").child(userUID).child("wrong").setValue(userWrong)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Scores sent to database successfully", Toast.LENGTH_SHORT)
                        .show()
                }

            val intent = Intent(this@QuizActivity, ResultActivity::class.java)
            startActivity(intent)
        }
    }

    fun showDialog(){
        val alertDialog = AlertDialog.Builder(this@QuizActivity)
        alertDialog.setTitle(R.string.app_name)
            .setMessage("Congratulations!!\nYou have completed all the questions.")
            .setCancelable(false)
            .setPositiveButton("See Results", DialogInterface.OnClickListener { _, _ ->
                sendScore()
            })
            .setNegativeButton("Play Again", DialogInterface.OnClickListener { _, _ ->
                val intent = Intent(this@QuizActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            })
            .create()
        alertDialog.show()
    }
}