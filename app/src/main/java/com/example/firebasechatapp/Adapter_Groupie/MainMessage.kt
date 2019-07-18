package com.example.firebasechatapp.Adapter_Groupie

import com.example.firebasechatapp.Model.User
import com.example.firebasechatapp.R
import com.example.firebasechatapp.Model.ChatMessage
import com.example.firebasechatapp.Utility.UserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_item.view.*

class MainMessage(val chatMessage: ChatMessage): Item<ViewHolder>() {
    var ChatToUser :User? = null
    override fun getLayout(): Int {
        return R.layout.latest_message_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtLatestMessage.text = chatMessage.text



        val chatToId : String
        if(chatMessage.fromId == UserData.currentUser?.uid){
            chatToId = chatMessage.toId
        }
        else{
            chatToId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatToId")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                ChatToUser = p0.getValue(User::class.java)
                viewHolder.itemView.txtLatestuserName.text = ChatToUser?.userName
                if(ChatToUser?.loginStatus == "true"){
                    viewHolder.itemView.activeStatus.alpha = 1.toFloat()
                }
                else{
                    viewHolder.itemView.activeStatus.alpha = 0.toFloat()
                }

                val targetImgView = viewHolder.itemView.latestUserImage
                Picasso.get().load(ChatToUser?.profileImage).into(targetImgView)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })



    }
}