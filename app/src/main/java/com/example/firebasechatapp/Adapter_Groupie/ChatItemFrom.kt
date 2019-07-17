package com.example.firebasechatapp.Adapter_Groupie

import com.example.firebasechatapp.Model.User
import com.example.firebasechatapp.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.message_row_from.view.*
import kotlinx.android.synthetic.main.messages_row_to.view.*

class ChatItemFrom(val message:String,val user: User): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.message_row_from
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.msgTxtBodyFrom.text = message
        val uri =  user.profileImage
        val targetImgView = viewHolder.itemView.msgUserImageFrom
        Picasso.get().load(uri).into(targetImgView)
    }
}