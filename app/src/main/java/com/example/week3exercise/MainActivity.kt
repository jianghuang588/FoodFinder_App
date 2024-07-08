package com.example.week3exercise

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var  username: EditText

    private lateinit var  password: EditText

    private lateinit var  confirmPassword: EditText

    private lateinit var login: Button

    private lateinit var signUp: Button

    private lateinit var progressBar: ProgressBar

    private lateinit var shakeManager: ShakeManager

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        shakeManager = ShakeManager(this)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        val preferences: SharedPreferences = getSharedPreferences(
            "android-tweet",
            Context.MODE_PRIVATE
        )

        setContentView(R.layout.activity_main)

        //
        val bars : Toolbar = findViewById(R.id.bar)
        setSupportActionBar(bars)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPass)
        login = findViewById(R.id.login)
        signUp = findViewById(R.id.signUp)
        progressBar = findViewById(R.id.progressBar)

        signUp.setOnClickListener {
            val inputtedUsername : String = username.text.toString()
            val inputtedPassword : String = password.text.toString()



            firebaseAuth.createUserWithEmailAndPassword(inputtedUsername, inputtedPassword)
                .addOnCompleteListener {task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        val email = currentUser?.email

                        firebaseAnalytics.logEvent("login_success", null)
                        Toast.makeText(
                            this,
                            "Registered successfully as $email!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val exception: Exception = task.exception!!
                        val errorType = if (exception is FirebaseAuthInvalidCredentialsException) {
                            "invalid_credentials"
                        } else {
                            "unknown_error"
                        }
                        val bundle = Bundle()
                        bundle.putString("error_type", errorType)

                        firebaseAnalytics.logEvent("login_failed", bundle)
                            //////////

                        if (exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(
                                this,
                                "Bad credential!!!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(
                                this,
                                "Already register!!!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Register Failed:$exception!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
        }

        login.setOnClickListener { view: View ->


            Log.d("MainActivity", "onCreate() call")
            val inputtedUsername : String = username.text.toString()
            val inputtedPassword : String = password.text.toString()
            val inputtedConfirmPassword : String = confirmPassword.text.toString()
            val user = preferences.getString("username", "")


            firebaseAuth
                .signInWithEmailAndPassword(inputtedUsername, inputtedPassword)
                .addOnCompleteListener{ task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        val email = currentUser?.email
                        Toast.makeText(
                            this,
                            "Registered successfully as $email!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val exception = task.exception
                        Toast.makeText(
                            this,
                            "Fail to sign in as $exception!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                if (user == "") {
                    preferences
                        .edit()
                        .putString("username", inputtedUsername)
                        .putString("password", inputtedPassword)
                        .putString("confirmPassword", inputtedConfirmPassword)
                        .apply()
                    val intent: Intent = Intent(this, TweetActivity::class.java)
                    intent.putExtra("Location","Richmond")
                    startActivity(intent)
                }


            if (user != username.text.toString() || preferences.getString("password", "") != password.text.toString()) {
                //Toast.makeText(this, "Wrong username or password", Toast.LENGTH_LONG).show()

            } else {
                //load to second page
                val intent: Intent = Intent(this, MapsActivity::class.java)
                //intent.putExtra("Location","Richmond")
                startActivity(intent)
            }
            //Toast.makeText(this, "onClick() call", Toast.LENGTH_LONG).show()
        }

        login.isEnabled = false


        username.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)
        confirmPassword.addTextChangedListener(textWatcher)

        val saveUsername: String? = preferences.getString("username", "")
        val savePassword: String? = preferences.getString("password", "")
        val saveVerifyPassword: String? = preferences.getString("confirmPassword", "")

        username.setText(saveUsername)
        password.setText(savePassword)
        confirmPassword.setText(saveVerifyPassword)
    }

    override fun onResume() {
        super.onResume()
        shakeManager.detectShake {
            Log.d("ShakeManager", "Shake Detect")
        }
        shakeManager.stopDetectingShakes()
    }
    override fun onPause() {
        super.onPause()
        shakeManager.stopDetectingShakes()
    }


///
    private val textWatcher = object  : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val inputtedUsername : String = username.text.toString()
            val inputtedPassword : String = password.text.toString()
            val inputtedConfirmPassword : String = confirmPassword.text.toString()
            val enable: Boolean = inputtedUsername.trim().isNotEmpty() && inputtedPassword.trim().isNotEmpty() && inputtedConfirmPassword.trim().isNotEmpty()
                login.isEnabled = enable

            if (confirmPassword.text.toString() != password.text.toString() ) {
                login.isEnabled = false
            }
        }
        override fun afterTextChanged(s: Editable) {}
    }
}