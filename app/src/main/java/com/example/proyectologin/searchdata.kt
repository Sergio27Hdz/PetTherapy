package com.example.proyectologin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class searchdata : AppCompatActivity() {
    var ref: DatabaseReference? = null
    private var txtSearch: AutoCompleteTextView? = null
    private var listData: RecyclerView? = null
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_search_activity_admin)
        auth = FirebaseAuth.getInstance()



        ref = FirebaseDatabase.getInstance().getReference("User_Person")
        txtSearch = findViewById<View>(R.id.txtSearch) as AutoCompleteTextView
        listData = findViewById<View>(R.id.listData) as RecyclerView
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        listData!!.layoutManager = layoutManager
        populateSearch()



    }

    private fun populateSearch() {
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val names = ArrayList<String?>()
                    for (ds in snapshot.children) {
                        val n = ds.child("Nombre").getValue(String::class.java)!!
                        names.add(n)
                    }
                    val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
                        applicationContext,
                        android.R.layout.simple_list_item_1,
                        names as List<Any?>
                    )
                    txtSearch!!.setAdapter(adapter)
                    txtSearch!!.onItemClickListener =
                        OnItemClickListener { parent, view, position, id ->
                            val selection = parent.getItemAtPosition(position).toString()
                            getUsers(selection)
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        ref!!.addListenerForSingleValueEvent(eventListener)
    }

    private fun getUsers(selection: String) {
        val query = ref!!.orderByChild("Nombre").equalTo(selection)
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userInfos = ArrayList<UserInfo>()
                    for (ds in snapshot.children) {
                        val userInfo: UserInfo = UserInfo(
                            ds.child("Nombre").getValue(
                                String::class.java)!!,
                            ds.child("Email").getValue(String::class.java)!!, ds.key!!
                        )
                        userInfos.add(userInfo)
                    }
                    val adapter = CustomAdapter(userInfos, this@searchdata)
                    listData!!.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        query.addListenerForSingleValueEvent(eventListener)
    }

    inner class UserInfo     //no sirve
        (//no sirve
        //no sirve
        var name: String, //no srive
        var email: String, var key: String
    )

    //CODIGO
    class CustomAdapter(
        private val localDataSet: ArrayList<UserInfo>,
        private val mcontext: Context
    ) : RecyclerView.Adapter<com.example.proyectologin.CustomAdapter.ViewHolder>() {
        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            //no sirve
            var name: TextView
            var email: TextView

            init {
                //no sirve
                name = view.findViewById<View>(R.id.name) as TextView
                email = view.findViewById<View>(R.id.email) as TextView
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(
            viewGroup: ViewGroup,
            viewType: Int
        ): com.example.proyectologin.CustomAdapter.ViewHolder {
            val layoutInflater = LayoutInflater.from(viewGroup.context)
            val view = layoutInflater.inflate(R.layout.row_style, viewGroup, false)
            return com.example.proyectologin.CustomAdapter.ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        @RequiresApi(api = Build.VERSION_CODES.M)
        override fun onBindViewHolder(
            viewHolder: com.example.proyectologin.CustomAdapter.ViewHolder,
            position: Int
        ) {
            val user = localDataSet[position]
            viewHolder.name.text = user.name
            viewHolder.email.text = user.email
            viewHolder.itemView.setOnClickListener {
                val intent = Intent(mcontext, details::class.java)
                intent.putExtra("Nombre", user.name)
                mcontext.startActivity(intent)
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount(): Int {
            return localDataSet.size


        }
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
        startActivity(Intent(this, MainActivityAdmin::class.java))
    }
    fun BotonChat(view: View) {
        startActivity(Intent(this, ListOfChatsActivity::class.java))
    }
    fun BotonBuscar(view: View) {
        startActivity(Intent(this, searchdata::class.java))
    }
    fun BotonPerfil(view: View) {
        startActivity(Intent(this, ProfileActivityAdmin::class.java))
    }
}