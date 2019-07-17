package com.example.firebasechatapp.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.firebasechatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun btnLoginClicked(view: View){

        if(txtEmailLogin.text.isNotEmpty() && txtPasswordLogin.text.isNotEmpty()){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(txtEmailLogin.text.toString(),txtPasswordLogin.text.toString())
                .addOnSuccessListener {
                    val intent = Intent(this,MessagesMain::class.java)
                    startActivity(intent)
                }
        }
    }

    fun forgotPassClick(view: View){
        Toast.makeText(this,"You have Forgot Password!",Toast.LENGTH_SHORT).show()
    }

    fun needHelp(view: View){
        Toast.makeText(this,"You need Help!",Toast.LENGTH_SHORT).show()
    }
    fun createAccountClick(view:View){
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}
