package com.example.firebasechatapp.Controller

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechatapp.Adapter_Groupie.ChatItemFrom
import com.example.firebasechatapp.Adapter_Groupie.ChatItemTo
import com.example.firebasechatapp.Model.User
import com.example.firebasechatapp.R
import com.example.firebasechatapp.Utility.ChatMessage
import com.example.firebasechatapp.Utility.USER_KEY
import com.example.firebasechatapp.Utility.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.activity_register.*

class ChatRoomActivity : AppCompatActivity() {
    val adapter = GroupAdapter<ViewHolder>()
    var toUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        supportActionBar?.title = "Chat Log"

        toUser = intent.getParcelableExtra<User>(USER_KEY)
        supportActionBar?.title = toUser?.userName

        messagesListView.adapter = adapter
        listenForMessages()
        messagesListView.layoutManager = LinearLayoutManager(this)
        hideKeyBoard()
    }

    fun sendMsgBtnClicked(view: View) {
        performSendMessage()
    }

    private fun listenForMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid.toString()

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        println("Key: ${fromId.toString()}")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                println("chat is start reading")
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage?.text != null) {
                    if (chatMessage.fromId == fromId && chatMessage.text.isNotEmpty()) {
                        println("chat ::: ${chatMessage?.fromId}")

                        val currentUser = UserData.currentUser?:return
                        println("chat user  $currentUser")
                        adapter.add(ChatItemTo(chatMessage.text, currentUser))
                    } else {
//                        println("chat message: ${chatMessage.text}")
                        adapter.add(ChatItemFrom(chatMessage.text, toUser!!))
                    }
                }
                else{
                    println("chat is null")
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                println("could not run")
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                println("could not move")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                println("could not Change")
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                println("could not Remove")
            }

        })
    }

    private fun performSendMessage() {
        val fromId = FirebaseAuth.getInstance().uid
        val message = sendtxtMessage.text.toString()
        val user = intent.getParcelableExtra<User>(USER_KEY)
        val toId = user.uid

//        val ref = FirebaseDatabase.getInstance().getReference("/messages").push()

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        if (fromId!!.isNotEmpty() && toId.isNotEmpty() && message.isNotEmpty()) {
            val chatMessage = ChatMessage(ref.key!!, message, fromId!!, toId, System.currentTimeMillis() / 1000)

            ref.setValue(chatMessage)
                .addOnSuccessListener {
                    sendtxtMessage.text.clear()
                    messagesListView.scrollToPosition(adapter.itemCount - 1)
                    Log.d("chat", "save message ${ref.key}")
                }

            toRef.setValue(chatMessage)

            val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
            latestMessageRef.setValue(chatMessage)
            val latestMessageToRef= FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
            latestMessageToRef.setValue(chatMessage)
        }


    }


    fun hideKeyBoard() {
        var inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            sendtxtMessage.text.clear()
        }
    }
}
