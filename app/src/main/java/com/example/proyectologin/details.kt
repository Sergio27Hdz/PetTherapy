package com.example.proyectologin

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_search_profile_admin.*
import kotlinx.android.synthetic.main.item_message.view.*

class details : AppCompatActivity() {
    private var txtname: TextView? = null
    var selectedImage: CircleImageView? = null
    var storageReference: StorageReference? = null
    private var txtemail: TextView? = null
    private var txtlastname: TextView? = null
    private var txtphone: TextView? = null
    var ref: DatabaseReference? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_search_profile_admin)
        txtname = findViewById<View>(R.id.txtname) as TextView
        txtemail = findViewById<View>(R.id.txtemail) as TextView
        txtlastname = findViewById<View>(R.id.txtlastname) as TextView
        txtphone = findViewById<View>(R.id.txtphone) as TextView
        selectedImage = findViewById(R.id.profilePic)
        val intent = intent
        val key = intent.getStringExtra("Nombre")
        storageReference = FirebaseStorage.getInstance().reference
        ref = FirebaseDatabase.getInstance().getReference("User_Person")
        val query = ref!!.orderByChild("Nombre").equalTo(key)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        txtname!!.text = ds.child("Nombre").getValue(String::class.java)
                        txtemail!!.text = ds.child("Email").getValue(String::class.java)
                        txtlastname!!.text = ds.child("Apellido").getValue(String::class.java)
                        txtphone!!.text = ds.child("Telefono").getValue(String::class.java)
                        val userImage = txtemail!!.text.toString()
                        val profileRef = storageReference!!.child("imagenesPerfil/${userImage}/profile.jpg")
                        val ONE_MEGABYTE: Long = 1024*1024
                        profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                            if(bitmap==null){

                            }else{
                                profilePic.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        query.addListenerForSingleValueEvent(eventListener)
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

    fun BotonPerfil(view: View) {
        startActivity(Intent(this, ProfileActivityAdmin::class.java))
    }
    fun BotonBuscar(view: View) {
        startActivity(Intent(this, searchdata::class.java))
    }
    fun BotonChat(view: View) {
        startActivity(Intent(this, ListOfChatsActivity::class.java))
    }
    fun BotonHome(view: View) {
        startActivity(Intent(this, MainActivityAdmin::class.java))
    }
}