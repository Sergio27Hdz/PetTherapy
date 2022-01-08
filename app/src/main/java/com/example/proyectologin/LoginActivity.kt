package com.example.proyectologin

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.math.log


class LoginActivity : AppCompatActivity() {

    private lateinit var txtEmail: EditText
    private lateinit var txtContraseña: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var DataBase: DatabaseReference
    private var user2 = ""
    private var db = Firebase.firestore
    private var admin = true
    private var email = ""
// ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide();
        setContentView(R.layout.activity_login)
        txtEmail=findViewById(R.id.txtEmail)
        txtContraseña=findViewById(R.id.txtContraseña)
        progressBar = findViewById(R.id.progressBar2)
        auth = FirebaseAuth.getInstance()
        DataBase = FirebaseDatabase.getInstance().getReference()
    }

    fun forgotpassword(view:View){
        startActivity(Intent(this, ForgotPassword::class.java))
    }

    fun menuregistro(view:View){
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun loggin(view:View){
        loginUser()
    }

    fun changeLanguageEN(view: View){
        val config: Configuration = Configuration()
        val locale: Locale = Locale("en")
        config.locale = locale
        resources.updateConfiguration(config,null)
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun changeLanguageES(view: View){
        val config: Configuration = Configuration()
        val locale: Locale = Locale("es")
        config.locale = locale
        resources.updateConfiguration(config,null)
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun loginUser(){
        val user:String=txtEmail.text.toString()
        val contraseña:String=txtContraseña.text.toString()
        val chatId = UUID.randomUUID().toString()

        if(!TextUtils.isEmpty(user) && !TextUtils.isEmpty(contraseña)){
            progressBar.visibility=View.VISIBLE
            auth.signInWithEmailAndPassword(user, contraseña)
                .addOnCompleteListener(this){
                        task ->
                    if(task.isSuccessful && auth.currentUser?.isEmailVerified == true){
                        val user: FirebaseUser?=auth.currentUser
                        intent.putExtra("user", auth.currentUser)

                        DataBase.child("User_Person").child(user?.uid.toString()).child("Telefono").get().addOnSuccessListener {
                            if (it.value != null){
                                user2 = it.value as String
                            }else{
                                println("aqui hay error crack en el IF DE ONCREATE")
                            }
                        }.addOnFailureListener{
                            Log.e("firebase", "Error getting data", it)
                        }
                        DataBase.child("User_Person").child(user?.uid.toString()).child("Admin").get().addOnSuccessListener {
                            if(it.value as Boolean){
                                admin = it.value as Boolean
                                var userDB = DataBase.child("Admin").child(user2)
                                userDB.child("admin").setValue(admin)
                                intent.putExtra("chatid",chatId)
                                action3()
                            }else{
                                action4()
                                admin = it.value as Boolean
                                var userDB = DataBase.child("Admin").child(user2)
                                userDB.child("admin").setValue(admin)
                                intent.putExtra("chatid",chatId)
                            }
                        }.addOnFailureListener{
                            Log.e("firebase", "Error getting data", it)
                        }
                        DataBase.child("User_Person").child(user?.uid.toString()).child("Email").get().addOnSuccessListener {
                            if(it.value != null){
                                email = it.value as String
                                var userDB = DataBase.child("Admin").child(user2)
                                userDB.child("email").setValue(email)
                            }else{
                                println("aqui hay error crack en el IF DE ONCREATE")
                            }

                        }.addOnFailureListener{
                            Log.e("firebase", "Error getting data", it)
                        }
                    }else{
                        val string: String = getString(R.string.toast_error_auth_message)
                        Toast.makeText(this,string, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }


    private fun action3(){
        startActivity(Intent(this, MainActivityAdmin::class.java))
    }

    private fun action4(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            var string3: String = getString(R.string.close_app)
            var string4: String = getString(R.string.question_close_app)
            AlertDialog.Builder(this).apply {
                setTitle(string3)
                setMessage(string4)
                setPositiveButton(android.R.string.yes){ _: DialogInterface, _: Int ->
                    auth.signOut()
                    finishAffinity();
                    System.exit(0);
                }
                setNegativeButton(android.R.string.no, null)
            }.show()
        }
        return super.onKeyDown(keyCode, event)
    }


}

