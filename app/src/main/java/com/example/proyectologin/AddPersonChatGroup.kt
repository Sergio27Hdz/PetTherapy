package com.example.proyectologin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_person_chat_group.*

class AddPersonChatGroup : AppCompatActivity() {
    private var chatId = ""
    private var user = ""
    private var db = Firebase.firestore
    private lateinit var txtEmail: EditText
    private lateinit var DataBase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private  var admin = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_person_chat_group)

        txtEmail = findViewById(R.id.editTextPhone2)
        print(txtEmail)

        DataBase = FirebaseDatabase.getInstance().getReference()

        auth = FirebaseAuth.getInstance()
        val user2: FirebaseUser?=auth.currentUser
        DataBase.child("User_Person").child(user2?.uid.toString()).child("Email").get().addOnSuccessListener { it ->
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
        val user2: FirebaseUser?=auth.currentUser
        DataBase.child("Chat_ID").child(user2?.uid.toString()).get().addOnSuccessListener {
            if(it.value != null){
                chatId = it.value.toString()
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        newChatButton2.setOnClickListener { newChat() }

    }

    private fun chatSelected(chat: Chat){

        val intent = Intent(this, ChatActivityAdmin::class.java)
        intent.putExtra("telefono", chat.phone)
        intent.putExtra("chatId", chat.id)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    fun newChat(){
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

                            val chat = Chat(
                                id = chatId,
                                name = "Chat grupal de $user",
                                users = users as List<String>,
                                phone = otherUser,
                                inicio = inicio
                            )

                            db.collection("chats").document(chatId).set(chat)
                            db.collection("users").document(user).collection("chats").document(chatId).set(chat)
                            db.collection("users").document(user).collection("InicioChat").document(chatId).set(chat)

                            val chatOtherUser = Chat(
                                id = chatId,
                                name = "Chat grupal de $user",
                                users = users,
                                inicio = inicio
                            )

                            db.collection("users").document(emailOtherUser).collection("chats").document(chatId).set(chatOtherUser)

                            val intent = Intent(this, ChatActivityAdmin::class.java)
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

                                        val chat = Chat(
                                            id = chatId,
                                            name = "Chat grupal de $user",
                                            users = users as List<String>,
                                            phone = otherUser,
                                            inicio = inicio
                                        )

                                        db.collection("chats").document(chatId).set(chat)
                                        db.collection("users").document(user).collection("chats").document(chatId).set(chat)
                                        db.collection("users").document(user).collection("InicioChat").document(chatId).set(chat)

                                        val chatOtherUser = Chat(
                                            id = chatId,
                                            name = "Chat grupal de $user",
                                            users = users,
                                            inicio = inicio
                                        )

                                        db.collection("users").document(emailOtherUser).collection("chats").document(chatId).set(chatOtherUser)

                                        val intent = Intent(this, ChatActivityAdmin::class.java)
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
}