package com.example.firebasechatapp.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.Adapter_Groupie.MainMessage
import com.example.firebasechatapp.Adapter_Groupie.UserItem
import com.example.firebasechatapp.Model.User
import com.example.firebasechatapp.R
import com.example.firebasechatapp.Utility.ChatMessage
import com.example.firebasechatapp.Utility.USER_KEY
import com.example.firebasechatapp.Utility.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messages_main.*
import kotlinx.android.synthetic.main.latest_message_item.*

class MessagesMain : AppCompatActivity() {
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_main)

        mainMessageList.adapter = adapter
        mainMessageList.layoutManager = LinearLayoutManager(this)
        mainMessageList.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener{item,view ->
            val intent = Intent(this,ChatRoomActivity::class.java)
            val Item = item as MainMessage
            intent.putExtra(USER_KEY,Item.ChatToUser)
            startActivity(intent)
        }

        listenForLatestMessage()
        fetchCurrentUser()
        varifyUserIsLoggedIn()

    }

    private fun fetchCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        println("uid: $uid")
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")


        val use = FirebaseAuth.getInstance()


        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                UserData.currentUser =  p0.getValue(User::class.java)?:return
                println("current User:  ${UserData.currentUser?.uid}")

            }

        })

//        ref.addListenerForSingleValueEvent(object :ValueEventListener{
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                UserData.currentUser = p0.getValue(User::class.java)
//                println("current User:  ${UserData.currentUser?.uid}")
//            }
//
//        })
    }

    private fun varifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid==null){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.new_message ->{
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val latestMessagesMap = HashMap<String,ChatMessage>()
    private fun refreshRecyclerViewLatestMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(MainMessage(it))
        }
    }
    private fun listenForLatestMessage(){
        val fromid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromid")

        ref.addChildEventListener(object:ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)?:return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewLatestMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)?:return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewLatestMessages()
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)

    }
}
