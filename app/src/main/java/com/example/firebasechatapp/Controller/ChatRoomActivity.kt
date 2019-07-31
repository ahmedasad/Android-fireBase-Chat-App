package com.example.firebasechatapp.Controller

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
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
import com.google.firebase.database.*
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
        println("tiem ::: " + returnDateString(Calendar.getInstance().time.toString()))

        FirebaseDatabase.getInstance().getReference("/current-chat/${FirebaseAuth.getInstance().uid}/focus").push()
        checkIfFocus()
        checkIfUserIsTyping()


    }

    override fun onDestroy() {
        val ref = FirebaseDatabase.getInstance().getReference("/current-chat/${FirebaseAuth.getInstance().uid}")
        ref.removeValue()
        super.onDestroy()
    }

    override fun onPause() {
        val ref = FirebaseDatabase.getInstance().getReference("/current-chat/${FirebaseAuth.getInstance().uid}")
        ref.removeValue()
        super.onPause()
    }


    private fun checkIfUserIsTyping() {
        val status = findViewById<TextView>(R.id.txtstatus)
        val user = intent.getParcelableExtra<User>(USER_KEY)
        val toId = user.uid
        val ref = FirebaseDatabase.getInstance().getReference("/current-chat/$toId/focus")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
//                val status = p0.getValue()


                if(p0.getValue()!=null)
                {
                    var state = p0.getValue()
                    if(p0.getValue().toString() == "true"){
                        println("status:: true "+p0.getValue() )
                        status.text = "typing..."
                        hideKeyBoard()
                    }
                    else if(p0.getValue().toString() != "true"){
                        println("status:: false "+p0.getValue() )
                        status.text = ""
                        hideKeyBoard()
                    }
                }



            }
            //
            override fun onCancelled(p0: DatabaseError) {}
        })

    }

    private fun focusStatus(focus: Boolean) {

        val fromId = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/current-chat/$fromId/focus")

        if (!focus) {
            ref.setValue("false")
                .addOnSuccessListener {
                    println("focused")
                }
        } else {
            ref.setValue("true")
                .addOnSuccessListener {
                    println("not focused")
                }
        }

    }


    fun sendMsgBtnClicked(view: View) {
        performSendMessage()
    }


    lateinit var currentDate: String
    var date = ""
    lateinit var currentday: String
    var day = ""
    private fun listenForMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid.toString()

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        println("Key: ${fromId.toString()}")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val chatMessage = p0.getValue(ChatMessage::class.java)

                currentDate = chatMessage!!.timeStampt
                currentday = currentDate.split(",")[0]
                if (date.isEmpty()) {
                    date = currentDate
                    day = currentday
                    adapter.add(DateAdapter(day))
                } else if (day != currentday) {
                    date = currentDate
                    day = currentday
                    adapter.add(DateAdapter(day))
                }


                if (chatMessage?.text != null) {

                    if (chatMessage.fromId == fromId && chatMessage.text.isNotEmpty()) {

                        val currentUser = UserData.currentUser ?: return
                        adapter.add(ChatItemTo(chatMessage.text, currentUser, currentDate.split(",")[1]))
                    } else {
                        //                        println("chat message: ${chatMessage.text}")
                        adapter.add(ChatItemFrom(chatMessage.text, toUser!!, currentDate.split(",")[1]))
                    }
                    messagesListView.scrollToPosition(adapter.itemCount - 1)
                } else {
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

        val sDF = SimpleDateFormat("E,h:mm a")
        val date = sDF.format(Calendar.getInstance().time)


        if (fromId!!.isNotEmpty() && toId.isNotEmpty() && message.isNotEmpty()) {
            val chatMessage = ChatMessage(
                ref.key!!,
                message,
                fromId!!,
                toId,
                date
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
            val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
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

    fun returnDateString(isoString: String): String {
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
        var convertDate = Date()
        try {
            convertDate = isoFormatter.parse(isoString)
        } catch (e: Exception) {
            Log.d("parse", "can not parse Date")
        }
        val outDateString = SimpleDateFormat("E,h:mm a", Locale.getDefault())
        return outDateString.format(convertDate)
    }

    private fun checkIfFocus() {
        sendtxtMessage.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(view: View, hasFocus: Boolean) {
                if (!hasFocus) {
                    focusStatus(false)
                } else {
                    focusStatus(true)
                }

            }
        })

    }
}
