package com.example.proyectologin

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var txtNombre: EditText
    private lateinit var txtApellido: EditText
    private lateinit var txtTelefono: EditText
    private lateinit var txtConfirmarContraseña: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtContraseña: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    var option=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txtEmail=findViewById(R.id.txtEmail)
        txtContraseña=findViewById(R.id.txtContraseña)
        txtNombre=findViewById(R.id.txtNombre)
        txtApellido=findViewById(R.id.txtApellido)
        txtTelefono=findViewById(R.id.txtTelefono)
        txtConfirmarContraseña=findViewById(R.id.txtConfirmarContraseña)

        progressBar = findViewById(R.id.progressBar)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        dbReference = database.reference.child("User_Person")

    }

    fun register(view: android.view.View) {
        createNewAccount()
    }
    private fun createNewAccount(){
        val name:String=txtNombre.text.toString()
        val lastname:String=txtApellido.text.toString()
        val phone:String=txtTelefono.text.toString()
        val confirmPassword:String=txtConfirmarContraseña.text.toString()
        val email:String=txtEmail.text.toString()
        val contraseña:String=txtContraseña.text.toString()
        val option:Boolean=option

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(contraseña)){
            if(contraseña==confirmPassword){
                progressBar.visibility=android.view.View.VISIBLE
                auth.createUserWithEmailAndPassword(email,contraseña)
                        .addOnCompleteListener(this){
                            task ->

                            if(task.isComplete){
                                val user:FirebaseUser?=auth.currentUser
                                verifyEmail(user)

                                val userBD=dbReference.child(user?.uid.toString())

                                userBD.child("Nombre").setValue(name)
                                userBD.child("Apellido").setValue(lastname)
                                userBD.child("Telefono").setValue(phone)
                                userBD.child("Email").setValue(email)
                                userBD.child("Password").setValue(contraseña)
                                userBD.child("Admin").setValue(option)

                                action()

                            }
                        }
            }else {
                val string: String = getString(R.string.toast_confirm_password)
                Toast.makeText(this,string, Toast.LENGTH_LONG).show()

            }
        }
    }

    fun changeLanguageEN(view: android.view.View){
        val config: Configuration = Configuration()
        val locale: Locale = Locale("en")
        config.locale = locale
        resources.updateConfiguration(config,null)
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun changeLanguageES(view: android.view.View){
        val config: Configuration = Configuration()
        val locale: Locale = Locale("es")
        config.locale = locale
        resources.updateConfiguration(config,null)
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun action(){
        val intent: Intent= Intent(this, LoginActivity::class.java)
        intent.putExtra("option" ,option)
        startActivity(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this,LoginActivity::class.java))//code
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun verifyEmail(user:FirebaseUser?){
        user?.sendEmailVerification()
                ?.addOnCompleteListener(this){
                    task ->
                    if(task.isComplete){
                        val string: String = getString(R.string.toast_send_email)
                        Toast.makeText(this,string, Toast.LENGTH_LONG).show()
                    }else{
                        val string: String = getString(R.string.toast_not_send_email)
                        Toast.makeText(this,string, Toast.LENGTH_LONG).show()}
                }

    }

    fun CheckBoxClicked(view: View){
        if(view is CheckBox){
            val checked: Boolean= view.isChecked
            when (view.id){
                R.id.checkBox ->{
                    if(checked){
                        var string3: String = getString(R.string.option_admin_title)
                        var string4: String = getString(R.string.option_admin) + '\n'+ getString(R.string.option_admin2)
                        AlertDialog.Builder(this).apply {
                            setTitle(string3)
                            setMessage(string4)
                            setPositiveButton(android.R.string.yes){ _: DialogInterface, _: Int ->
                                option=true
                            }
                            setNegativeButton(android.R.string.no, null)
                        }.show()

                    }
                }
            }
        }
    }


    fun CheckBoxClicked2(view: View){
        if(view is CheckBox){
            val checked: Boolean= view.isChecked
            when (view.id){
                R.id.checkBox2 ->{
                    if(checked){
                        var string3: String = getString(R.string.option_user_title)
                        var string4: String = getString(R.string.option_user) + '\n'+ getString(R.string.option_user2)
                        AlertDialog.Builder(this).apply {
                            setTitle(string3)
                            setMessage(string4)
                            setPositiveButton(android.R.string.yes){ _: DialogInterface, _: Int ->
                                option=false
                            }
                            setNegativeButton(android.R.string.no, null)
                        }.show()

                    }
                }
            }
        }
    }

    fun BotonLOGIN(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

}



