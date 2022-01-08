package com.example.proyectologin

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_list_of_chats.*
import java.util.*

class ListOfChatsActivity : AppCompatActivity() {
    private var user = ""
    private var db = Firebase.firestore
    private lateinit var txtEmail: EditText
    private lateinit var DataBase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private  var admin = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_list_of_chats)

        txtEmail= findViewById(R.id.editTextPhone)
        print(txtEmail)

        DataBase = FirebaseDatabase.getInstance().getReference()

        auth = FirebaseAuth.getInstance()
        val user2: FirebaseUser?=auth.currentUser

        DataBase.child("User_Person").child(user2?.uid.toString()).child("Email").get().addOnSuccessListener {
            if (it.value != null){
                user = it.value as String
                println("user = ${user}")
                println("admin = ${admin}")

                initViews()
            }else{
                println("aqui hay error crack en el IF DE ONCREATE")
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun initViews(){
        newChatButton.setOnClickListener { newChat() }

        listChatsRecyclerView.layoutManager = LinearLayoutManager(this)
        listChatsRecyclerView.adapter =
            ChatAdapter { chat: Chat ->
                chatSelected(chat)
            }

        val userRef = db.collection("users").document(user)

        userRef.collection("chats")
            .get()
            .addOnSuccessListener { chats ->
                val listChats = chats.toObjects(Chat::class.java)
                print(chats)

                (listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
            }

        userRef.collection("chats")
            .addSnapshotListener { chats, error ->
                if(error == null){
                    chats?.let {
                        val listChats = it.toObjects(Chat::class.java)

                        (listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
                    }
                }else{
                    println("tenemos un error  EN EL IF DEL CHAT")
                }
            }
    }

    private fun chatSelected(chat: Chat){
        val user2: FirebaseUser?=auth.currentUser
        var pasarUser = user2?.uid.toString()
        val intent = Intent(this, ChatActivityAdmin::class.java)
        intent.putExtra("PasarUser", pasarUser)
        intent.putExtra("telefono", chat.phone)
        intent.putExtra("chatId", chat.id)
        intent.putExtra("user", user)
        startActivity(intent)
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

    fun newChat(){
        val chatId = UUID.randomUUID().toString()
        var otherUser = txtEmail.text.toString()

        val user2: FirebaseUser?=auth.currentUser


        DataBase.child("User_Person").child(user2?.uid.toString()).child("Admin").get().addOnSuccessListener {
            if(it.value != null){
                var admin = it.value as Boolean
                if(admin){
                    DataBase.child("Admin").child(otherUser).child("email").get().addOnSuccessListener {
                        if(it.value != null){
                            var emailOtherUser = it.value as String
                            val users = listOf(user,emailOtherUser)
                            val string: String = getString(R.string.chat_with_message)
                            val inicio = 1
                            DataBase.child("Chat_ID").child(user2?.uid.toString()).setValue(chatId)
                            var pasarUser = user2?.uid.toString()
                            DataBase.child("Inicio_Chat").child(user2?.uid.toString()).child(chatId).setValue(inicio)
                            val chat = Chat(
                                id = chatId,
                                name = "$string"+" $emailOtherUser",
                                users = users as List<String>,
                                phone = otherUser,
                                inicio = inicio
                            )

                            db.collection("chats").document(chatId).set(chat)
                            db.collection("users").document(user).collection("chats").document(chatId).set(chat)
                            db.collection("users").document(user).collection("InicioChat").document(chatId).set(chat)

                            val chatOtherUser = Chat(
                                id = chatId,
                                name = "$string"+" $user",
                                users = users,
                                inicio = inicio
                            )

                            db.collection("users").document(emailOtherUser).collection("chats").document(chatId).set(chatOtherUser)

                            val intent = Intent(this, ChatActivityAdmin::class.java)
                            intent.putExtra("PasarUser", pasarUser)
                            intent.putExtra("telefono", otherUser)
                            intent.putExtra("chatId", chatId)
                            intent.putExtra("user", user)
                            startActivity(intent)
                        }else{
                            println("aqui hay error crack en el IF DE ONCREATE")
                        }

                    }.addOnFailureListener{
                        Log.e("firebase", "Error getting data", it)
                    }
                }else{
                    DataBase.child("Admin").child(otherUser).child("admin").get().addOnSuccessListener {
                        if(it.value != null){
                            var admin = it.value as Boolean
                            if(admin){
                                DataBase.child("Admin").child(otherUser).child("email").get().addOnSuccessListener {
                                    if(it.value != null){
                                        var emailOtherUser = it.value as String
                                        val users = listOf(user,emailOtherUser)
                                        val string: String = getString(R.string.chat_with_message)
                                        val inicio = 1
                                        var pasarUser = user2?.uid.toString()
                                        val chat = Chat(
                                            id = chatId,
                                            name = "$string"+" $emailOtherUser",
                                            users = users as List<String>,
                                            phone = otherUser,
                                            inicio = inicio
                                        )

                                        db.collection("chats").document(chatId).set(chat)
                                        db.collection("users").document(user).collection("chats").document(chatId).set(chat)
                                        db.collection("users").document(user).collection("InicioChat").document(chatId).set(chat)

                                        val chatOtherUser = Chat(
                                            id = chatId,
                                            name = "$string"+" $user",
                                            users = users,
                                            inicio = inicio
                                        )

                                        db.collection("users").document(emailOtherUser).collection("chats").document(chatId).set(chatOtherUser)

                                        val intent = Intent(this, ChatActivityAdmin::class.java)
                                        intent.putExtra("PasarUser", pasarUser)
                                        intent.putExtra("telefono", otherUser)
                                        intent.putExtra("chatId", chatId)
                                        intent.putExtra("user", user)
                                        startActivity(intent)
                                    }else{
                                        println("aqui hay error crack en el IF DE ONCREATE")
                                    }

                                }.addOnFailureListener{
                                    Log.e("firebase", "Error getting data", it)
                                }
                            }else{
                                val string: String = getString(R.string.user_not_found)
                                Toast.makeText(this,string, Toast.LENGTH_LONG).show()
                            }
                        }else{
                            print("error no se encontro el dato")
                        }
                    }.addOnFailureListener{
                        Log.e("firebase", "Error getting data", it)
                    }
                }
            }else{
                print("error no se encontro el dato")
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

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
}