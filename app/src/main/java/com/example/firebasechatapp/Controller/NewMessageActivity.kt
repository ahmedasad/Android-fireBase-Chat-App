package com.example.firebasechatapp.Controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.Model.User
import com.example.firebasechatapp.Adapter_Groupie.UserItem
import com.example.firebasechatapp.R
import com.example.firebasechatapp.Utility.USER_KEY

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"

        fetchUsers(this)

    }

    private fun fetchUsers(context: Context){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {

                    val user = it.getValue(User::class.java)
                    if(user!=null ){adapter.add(UserItem(user))}
                }


                list.adapter = adapter
                list.layoutManager = LinearLayoutManager(context!!)

                adapter.setOnItemClickListener{ item,view ->

                    val user = item as UserItem

                    val intent = Intent(context,ChatRoomActivity::class.java)
                    intent.putExtra(USER_KEY,user.user)
                    startActivity(intent)
                }
            }



            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

}
