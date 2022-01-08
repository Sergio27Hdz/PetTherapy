package com.example.proyectologin

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CustomAdapterUser(private val localDataSet: ArrayList<user>, private val mcontext: Context) :
    RecyclerView.Adapter<CustomAdapterUser.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var email: TextView

        init {
            name = view.findViewById<View>(R.id.name) as TextView
            email = view.findViewById<View>(R.id.email) as TextView
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(R.layout.row_style_user, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val user = localDataSet[position]
        val key = user.key
        viewHolder.name.text = user.name
        viewHolder.email.text = user.email
        viewHolder.itemView.setOnClickListener {
            val intent = Intent(mcontext, details::class.java)
            intent.putExtra("Nombre", user.name)
            intent.putExtra("Email", user.email)
            mcontext.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return localDataSet.size
    }
}