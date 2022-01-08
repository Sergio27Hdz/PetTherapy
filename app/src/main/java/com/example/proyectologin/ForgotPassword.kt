package com.example.proyectologin

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class ForgotPassword : AppCompatActivity() {

    private lateinit var txtEmail: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        txtEmail=findViewById(R.id.txtEmail)
        progressBar = findViewById(R.id.progressBar3)
        auth= FirebaseAuth.getInstance()
    }

    fun changeLanguageEN(view: View){
        val config: Configuration = Configuration()
        val locale: Locale = Locale("en")
        config.locale = locale
        resources.updateConfiguration(config,null)
        startActivity(Intent(this, ForgotPassword::class.java))
    }

    fun changeLanguageES(view: View){
        val config: Configuration = Configuration()
        val locale: Locale = Locale("es")
        config.locale = locale
        resources.updateConfiguration(config,null)
        startActivity(Intent(this, ForgotPassword::class.java))
    }

    fun send(view:View){
        val email=txtEmail.text.toString()

        if(!TextUtils.isEmpty(email)){
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this){
                    task ->
                    if(task.isSuccessful){
                        progressBar.visibility=View.VISIBLE
                        startActivity(Intent(this, LoginActivity::class.java))
                    }else{
                        Toast.makeText(this,"Error al enviar el email", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this,LoginActivity::class.java))//code
        }
        return super.onKeyDown(keyCode, event)
    }

    fun BotonLOGIN2(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}