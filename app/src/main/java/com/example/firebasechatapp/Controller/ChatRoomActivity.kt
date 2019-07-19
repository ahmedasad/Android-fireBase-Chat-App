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
import com.example.firebasechatapp.Adapter_Groupie.DateAdapter
import com.example.firebasechatapp.Model.User
import com.example.firebasechatapp.R
import com.example.firebasechatapp.Model.ChatMessage
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
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

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
        messagesListView.scrollToPosition(adapter.itemCount - 1)
        hideKeyBoard()
        println("tiem ::: "+returnDateString( Calendar.getInstance().time.toString()))
    }

    fun sendMsgBtnClicked(view: View) {
        performSendMessage()
    }

    lateinit var currentDate:String
    var date = ""
     private fun listenForMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid.toString()

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        println("Key: ${fromId.toString()}")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatMessage = p0.getValue(ChatMessage::class.java)

                currentDate = returnDateString(chatMessage!!.timeStampt)
                if(date.isEmpty()){
                    date = currentDate
                    adapter.add(DateAdapter(date))
                }

                else if(date != currentDate){
                    date = currentDate
                    adapter.add(DateAdapter(date))
                }


                if (chatMessage?.text != null) {

                        if (chatMessage.fromId == fromId && chatMessage.text.isNotEmpty()) {

                            val currentUser = UserData.currentUser ?: return
                            adapter.add(ChatItemTo(chatMessage.text, currentUser))
                        } else {
                            //                        println("chat message: ${chatMessage.text}")
                            adapter.add(ChatItemFrom(chatMessage.text, toUser!!))
                        }
                        messagesListView.scrollToPosition(adapter.itemCount - 1)
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
            val chatMessage = ChatMessage(
                ref.key!!,
                message,
                fromId!!,
                toId,
                Calendar.getInstance().time.toString()
            )

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

    fun returnDateString(isoString:String):String{
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
        var convertDate = Date()
        try {
            convertDate = isoFormatter.parse(isoString)
        }catch (e: Exception){
            Log.d("parse","can not parse Date")
        }
        val outDateString = SimpleDateFormat("E,h:mm a",Locale.getDefault())
        return outDateString.format(convertDate)
    }
}
