package com.example.proyectologin

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivityAdmin : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_admin)
        auth = FirebaseAuth.getInstance()
    }

    fun changeLanguageEN(view: android.view.View){
        val config: Configuration = Configuration()
        val locale: Locale = Locale("en")
        config.locale = locale
        resources.updateConfiguration(config,null)
        startActivity(Intent(this, MainActivityAdmin::class.java))
    }

    fun changeLanguageES(view: android.view.View){
        val config: Configuration = Configuration()
        val locale: Locale = Locale("es")
        config.locale = locale
        resources.updateConfiguration(config,null)
        startActivity(Intent(this, MainActivityAdmin::class.java))
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

    fun BotonHome(view: View) {
        startActivity(Intent(this, MainActivityAdmin::class.java))//code
    }

    fun BotonChat(view: View){
        startActivity(Intent(this, ListOfChatsActivity::class.java))//code
    }

    fun BotonPerfil(view: View){
        startActivity(Intent(this, ProfileActivityAdmin::class.java))//code
    }

    fun BotonBuscar(view: View){
        startActivity(Intent(this, searchdata::class.java))//code
    }

    fun BotonLogOut(view: View){
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}