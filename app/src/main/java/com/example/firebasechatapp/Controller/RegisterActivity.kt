package com.example.firebasechatapp.Controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.firebasechatapp.Model.User
import com.example.firebasechatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {


    lateinit var auth:FirebaseAuth
//    var db :FirebaseD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        progressView.visibility = View.INVISIBLE

    }

    fun btnCreateUserClicked(view: View){
        spinnerEnability(true)
        var error =""
        if(txtEmail.text.toString().isNotEmpty() && txtpass.text.toString().isNotEmpty() )
        {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(txtEmail.text.toString(),txtpass.text.toString())
            .addOnCompleteListener{
                if(!it.isSuccessful) {
                    println("User:  ${txtEmail.text.toString()},  ${txtUserName.text.toString()}")
                    return@addOnCompleteListener
                }

                else{
                    Log.d("Main","Successfully Created account ${it.result?.user?.uid}")
                    uploadImageToFirebase()
                }
            }
            .addOnFailureListener{
                Log.d("Main","failed to create user ${it.message}")
                error = it.localizedMessage
                spinnerEnability(false)
                Toast.makeText(this,"Error: $error",Toast.LENGTH_LONG).show()
            }
        }
        else{
            spinnerEnability(false)
            Toast.makeText(this,"Error: $error",Toast.LENGTH_LONG).show()
        }
    }

    var selectedPhoto: Uri? = null

    fun uploadImg(view:View){
        val intentUpload = Intent(Intent.ACTION_GET_CONTENT)
        intentUpload.type = "image/*"
        startActivityForResult(intentUpload,0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if(requestCode ==0 && resultCode == Activity.RESULT_OK && data != null){

            println("photo was selected")
            selectedPhoto = data.data
            val bitMap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhoto)

            profile_image.setImageBitmap(bitMap)
            selectImage.alpha = 0.toFloat()

        }
        else {
            spinnerEnability(false)
            Toast.makeText(this, "Error: Could get any Image!", Toast.LENGTH_LONG).show()
        }
    }

    fun uploadImageToFirebase(){
        if(selectedPhoto!=null){
            val fileName = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("image/$fileName")

            ref.putFile(selectedPhoto!!)
                .addOnSuccessListener {
                   ref.downloadUrl.addOnSuccessListener{
                       println("image uploaded  ${it.toString()}")

                       saveUserToDatabase(it.toString())

                   }

                }

        }
    }

    private fun saveUserToDatabase(profileImageUrl:String){
        val id = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$id")
        val user = User(id,txtUserName.text.toString(),profileImageUrl,"false")
        ref.setValue(user)
            .addOnSuccessListener {
                println("finally created user")
                spinnerEnability(false)
                val intentMainMsg = Intent(this, LoginActivity::class.java)
                intentMainMsg.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentMainMsg)
            }
            .addOnFailureListener{
                Log.d("failed","could not created account ${it.localizedMessage}")
                spinnerEnability(false)
                Toast.makeText(this,"Error: ${it.localizedMessage}",Toast.LENGTH_LONG).show()
            }
    }

    fun spinnerEnability(enable:Boolean){

        if(enable){
            progressView.visibility = View.VISIBLE
            spinnerCreation.visibility = View.VISIBLE
        }
        else{
            progressView.visibility = View.VISIBLE
            spinnerCreation.visibility =View.INVISIBLE
        }
    }
    fun hideKeyBoard(){
        var inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }
}
