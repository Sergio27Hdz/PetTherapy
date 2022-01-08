package com.example.proyectologin

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectologin.Message
import com.example.proyectologin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_message.view.*


class MessageAdapter(private val email: String): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private var messages: List<Message> = emptyList()

    fun setData(list: List<Message>){
        messages = list
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_message2,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        var auth = FirebaseAuth.getInstance()
        val storageRef = FirebaseStorage.getInstance().reference
        var user2: FirebaseUser?= auth.currentUser
        var user3 = user2?.uid.toString()

        if(email == message.from){

            holder.itemView.myMessageLayout.visibility = View.VISIBLE
            holder.itemView.otherMessageLayout.visibility = View.GONE
            if(message.message == ""){
                holder.itemView.textView8.visibility = View.GONE
                holder.itemView.myMessageTextView.visibility = View.GONE

            }else{
                holder.itemView.textView8.text = message.from
                holder.itemView.myMessageTextView.text = message.message
                holder.itemView.imageView12.visibility = View.GONE
            }
                if(message.imgUrl != ""){
                    var imageRef = storageRef.child("imagenesChat/"+message.from+ "/imagenesChat.jpg")
                    val ONE_MEGABYTE: Long = 1024*1024
                    imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        if(bitmap==null){

                        }else{
                            holder.itemView.imageView12.setImageBitmap(bitmap)
                        }
                    }
                }
                message.opcion = 0


        } else {
            holder.itemView.myMessageLayout.visibility = View.GONE
            holder.itemView.otherMessageLayout.visibility = View.VISIBLE

            if(message.message == ""){
                holder.itemView.othersMessageTextView.visibility = View.GONE
                holder.itemView.textView7.visibility= View.GONE

            }else{
                holder.itemView.textView7.text = message.from
                holder.itemView.othersMessageTextView.text = message.message
                holder.itemView.imageView14.visibility = View.GONE
            }
                if(message.imgUrl != "") {
                    var imageRef = storageRef.child("imagenesChat/" + message.from + "/imagenesChat.jpg")
                    val ONE_MEGABYTE: Long = 1024 * 1024
                    imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        if (bitmap == null) {

                        } else {
                            holder.itemView.imageView14.setImageBitmap(bitmap)
                        }
                    }
                }
                message.opcion = 0



        }



    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}