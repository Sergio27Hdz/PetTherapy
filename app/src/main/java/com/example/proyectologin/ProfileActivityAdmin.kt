package com.example.proyectologin

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*



class ProfileActivityAdmin : AppCompatActivity() {
    var selectedImage: CircleImageView? = null
    lateinit var cameraBtn: FloatingActionButton
    lateinit var galleryBtn: FloatingActionButton
    var currentPhotoPath: String? = null
    var storageReference: StorageReference? = null
    var fAuth: FirebaseAuth? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var DataBase: DatabaseReference
    private lateinit var txtNombre: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtApellido: TextView
    private lateinit var txtTelefono: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile_activity_admin)
        selectedImage = findViewById(R.id.profilePic)
        cameraBtn = findViewById(R.id.btn_foto)
        galleryBtn = findViewById(R.id.btn_galeria)
        auth = FirebaseAuth.getInstance()
        DataBase = FirebaseDatabase.getInstance().getReference()
        fAuth = FirebaseAuth.getInstance()


        MostrarDatos()


        //Agregado
        storageReference = FirebaseStorage.getInstance().reference
        val profileRef = storageReference!!.child("imagenesPerfil/" + fAuth!!.currentUser!!.email + "/profile.jpg")
        profileRef.downloadUrl.addOnSuccessListener { uri -> Picasso.get().load(uri).into(selectedImage) }


        //Abrir la camara
        cameraBtn.setOnClickListener(View.OnClickListener { //Permiso de la camara
            askCameraPermissions()
        })

        //Abrir la galeria
        galleryBtn.setOnClickListener(View.OnClickListener {
            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(gallery, GALLERY_REQUEST_CODE)
        })
    }

    private fun askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERM_CODE
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()

                //openCamera(); Empieza
            } else {
                Toast.makeText(this@ProfileActivityAdmin, "Permiso para usar la camara", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val f = File(currentPhotoPath)
                selectedImage!!.setImageURI(Uri.fromFile(f))
                Log.d("tag", "Absolute Url of Image is" + Uri.fromFile(f))
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri = Uri.fromFile(f)
                mediaScanIntent.data = contentUri
                this.sendBroadcast(mediaScanIntent)
                uploadImageToFirebase(f.name, contentUri)
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val contentUri = data!!.data
                val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val imageFileName =
                    "JPEG_" + timeStamp + "." + getFileExt(contentUri)
                Log.d("tag", "onActivityResult: Gallery Image Uri: $imageFileName")
                selectedImage!!.setImageURI(contentUri)
                uploadImageToFirebase(imageFileName, contentUri)
            }
        }
    }


    //Subir a Firebase
    private fun uploadImageToFirebase(name: String, contentUri: Uri?) {
        val image = storageReference!!.child("imagenesPerfil/" + fAuth!!.currentUser!!.email + "/profile.jpg")
        image.putFile(contentUri!!).addOnSuccessListener {
            image.downloadUrl.addOnSuccessListener { uri -> Picasso.get().load(uri).into(selectedImage) }
            Toast.makeText(this@ProfileActivityAdmin, "Image Uploaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { Toast.makeText(this@ProfileActivityAdmin, "Upload Failed", Toast.LENGTH_SHORT).show() }
    }


    private fun getFileExt(contentUri: Uri?): String? {
        val c = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(c.getType(contentUri!!))
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        //Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); //Si no lo quiero en la galería
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /*prefix*/
            ".jpg",  /*suffix*/
            storageDir /*Directory*/
        )

        //Save File: path for use with ACTION_VIEW Intents
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun MostrarDatos(){

        txtNombre = findViewById(R.id.txtNombre3)
        var extra = txtNombre.text.toString()
        txtApellido=findViewById(R.id.txtApellido3)
        var extra1 = txtApellido.text.toString()
        txtEmail=findViewById(R.id.txtEmail3)
        var extra2 = txtEmail.text.toString()
        txtTelefono=findViewById(R.id.txtTelefono3)
        var extra3 = txtApellido.text.toString()


        var user: FirebaseUser?=auth.currentUser
        DataBase.child("User_Person").child(user?.uid.toString()).child("Nombre").get().addOnSuccessListener {
            if(it.value != null){

                extra = it.value as String
                txtNombre.text = extra

            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        DataBase.child("User_Person").child(user?.uid.toString()).child("Apellido").get().addOnSuccessListener {
            if(it.value != null){

                extra1 = it.value as String
                txtApellido.text = extra1

            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        DataBase.child("User_Person").child(user?.uid.toString()).child("Email").get().addOnSuccessListener {
            if(it.value != null){

                extra2 = it.value as String
                txtEmail.text = extra2

            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        DataBase.child("User_Person").child(user?.uid.toString()).child("Telefono").get().addOnSuccessListener {
            if(it.value != null){

                extra3 = it.value as String
                txtTelefono.text = extra3

            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            //Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "com.example.android.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
            }
        }
    }

    companion object {
        const val CAMERA_PERM_CODE = 101
        const val CAMERA_REQUEST_CODE = 102
        const val GALLERY_REQUEST_CODE = 105
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
}