package com.example.proyectologin

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import kotlinx.android.synthetic.main.activity_chat_admin.*
import kotlinx.android.synthetic.main.activity_chat_personal.*
import kotlinx.android.synthetic.main.activity_chat_personal.messageTextField
import kotlinx.android.synthetic.main.activity_chat_personal.messagesRecylerView
import kotlinx.android.synthetic.main.activity_chat_personal.sendMessageButton
import kotlinx.android.synthetic.main.item_message.*
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.MalformedURLException
import java.net.URL

class ChatActivity : AppCompatActivity() {
    private var chatId = " "
    private var user = " "
    private var otherUser = " "
    private var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var DataBase: DatabaseReference
    private var email=""
    private var opcion= 1
    private lateinit var txtEmail: TextView
    private lateinit var txtEmail2: TextView
    private var fileResult = 1
    private var path : String = ""
    private var imgUri : Uri = Uri.parse("")
    var opcion6: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_personal)
        auth = FirebaseAuth.getInstance()
        DataBase = FirebaseDatabase.getInstance().getReference()

        intent.getStringExtra("telefono")?.let { otherUser = it }
        intent.getStringExtra("chatId")?.let { chatId = it }
        intent.getStringExtra("user")?.let { user = it }

        val serverURL: URL
        serverURL = try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            URL("https://meet.jit.si")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                // When using JaaS, set the obtained JWT here
                //.setToken("MyJWT")
                // Different features flags can be set
                //.setFeatureFlag("toolbox.enabled", false)
                //.setFeatureFlag("filmstrip.enabled", false)
                .setWelcomePageEnabled(false)
                .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        imageView4.setOnClickListener{ BotonVideollamada() }



        if(chatId.isNotEmpty() && user.isNotEmpty()) {
            initViews()
        }

    }

    private fun initViews(){
        messagesRecylerView.layoutManager = LinearLayoutManager(this)
        messagesRecylerView.adapter = MessageAdapter(user)
        messagesRecylerView.scrollToPosition(messagesRecylerView.adapter!!.itemCount -1)

        sendMessageButton.setOnClickListener { sendMessage() }

        val chatRef = db.collection("chats").document(chatId)

        chatRef.collection("messages").orderBy("dob", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { messages ->
                val listMessages = messages.toObjects(Message::class.java)
                (messagesRecylerView.adapter as MessageAdapter).setData(listMessages)
            }

        chatRef.collection("messages").orderBy("dob", Query.Direction.ASCENDING)
            .addSnapshotListener { messages, error ->
                if(error == null){
                    messages?.let {
                        val listMessages = it.toObjects(Message::class.java)
                        (messagesRecylerView.adapter as MessageAdapter).setData(listMessages)
                    }
                }
            }
        messagesRecylerView.scrollToPosition(messagesRecylerView.adapter!!.itemCount -1)

    }

    fun BotonVideollamada(){
        val text = "VideoLlamadaPetTherapy"
        if (text.length > 0) {
            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.
            val options = JitsiMeetConferenceOptions.Builder()
                    .setRoom(text)
                    // Settings for audio and video
                    .setAudioMuted(true)
                    .setVideoMuted(true)
                    .build()
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            JitsiMeetActivity.launch(this, options)
        }

    }

    private fun sendMessage(){
        val user2: FirebaseUser?=auth.currentUser
        DataBase.child("User_Person").child(user2?.uid.toString()).child("Telefono").get().addOnSuccessListener {
            if(it.value != null){
                email = it.value as String

                DataBase.child("Bloqueados").child(email).child(otherUser).get().addOnSuccessListener {
                    if(it.value != null){
                        if(it.value as Boolean){
                            val string: String = getString(R.string.please_user_block)
                            Toast.makeText(this,string, Toast.LENGTH_LONG).show()
                        }else{
                            if(path == ""){
                                val message = Message(
                                        message = messageTextField.text.toString(),
                                        from = user,
                                        from2 = otherUser,
                                        by = email,
                                        imgUrl = "",
                                        opcion = 0
                                )

                                db.collection("chats").document(chatId).collection("messages").document().set(message)

                                messageTextField.setText("")
                                messagesRecylerView.scrollToPosition(messagesRecylerView.adapter!!.itemCount -1)
                            }
                            else{
                                opcion6 = 1
                                val message = Message(
                                        message = messageTextField.text.toString(),
                                        from = user,
                                        from2 = otherUser,
                                        by = email,
                                        imgUrl = path,
                                        opcion = opcion6
                                )
                                opcion6 = 0

                                db.collection("chats").document(chatId).collection("messages").document().set(message)

                                messageTextField.setText("")
                                messagesRecylerView.scrollToPosition(messagesRecylerView.adapter!!.itemCount -1)
                            }
                        }
                    }else{
                            opcion6 = 1
                            val message = Message(
                                    message = messageTextField.text.toString(),
                                    from = user,
                                    from2 = otherUser,
                                    by = email,
                                    imgUrl = path,
                                    opcion = opcion6
                            )
                            opcion6 = 0
                            db.collection("chats").document(chatId).collection("messages").document().set(message)

                            messageTextField.setText("")
                            messagesRecylerView.scrollToPosition(messagesRecylerView.adapter!!.itemCount -1)
                    }
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                        }
            }else{
                println("aqui hay error crack en el IF DE ONCREATE")
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        opcion6 = 0
    }

    private fun fileUpload(mUri: Uri){
        val db = FirebaseFirestore.getInstance()
        val docref = db.collection("users").document(user)

        var imagenes : Int
        docref.get().addOnSuccessListener {
            var user2: FirebaseUser?= auth.currentUser
            var user3 = user2?.uid.toString()


            val ref = FirebaseStorage.getInstance().reference
            val folder: StorageReference = FirebaseStorage.getInstance().reference.child("imagenesChat/"+user+ "/imagenesChat.jpg")
            folder.putFile(mUri)
                    .addOnSuccessListener {
                        path=""

                        ref.child("imagenesChat/"+user+ "/imagenesChat.jpg").downloadUrl
                                .addOnSuccessListener {uri ->
                                    path= it.toString()
                                    sendMessage()
                                }.addOnFailureListener{
                                    path=""
                                }


                    }.addOnFailureListener{

                    }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == fileResult){
            if(resultCode == Activity.RESULT_OK && data!=null){
                val clipData = data.clipData
                if(clipData != null){

                }else{
                    val uri = data.data
                    uri?.let { fileUpload(it) }
                }
            }
        }
    }

    fun BotonRegresar2(view: View){
        startActivity(Intent(this, ListOfChatsActivity_User::class.java))//code
    }

    fun Opcion(view: View){
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.choose_option_admin))

            val opciones = arrayOfNulls<CharSequence>(1)

            opciones[0] = getString(R.string.option_send_image)

            setItems(opciones,
                    DialogInterface.OnClickListener { dialog, which ->
                        if(which == 0){
                            EnviarImagen()
                        }else{

                        }
                    })
        }.show()
    }

    private fun action1(){
        startActivity(Intent(this, ListOfChatsActivity::class.java))
    }

    fun EnviarImagen(){
        opcion6 = 1
        BotonEnviarImagen()
    }

    private fun BotonEnviarImagen(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        intent.type = "*/*"
        startActivityForResult(intent, fileResult)
    }
}





