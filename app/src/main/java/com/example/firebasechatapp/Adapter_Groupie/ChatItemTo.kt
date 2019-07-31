package com.example.firebasechatapp.Adapter_Groupie

import com.example.firebasechatapp.Model.User
import com.example.firebasechatapp.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.messages_row_to.view.*

class ChatItemTo(val message:String,val user: User,val time:String): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.messages_row_to
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.msgtxtBodyTo.text = message
        viewHolder.itemView.msgtxtTimeTo.text = time
        val uri =  user.profileImage
        val targetImgView = viewHolder.itemView.msgUserImageTo
        Picasso.get().load(uri).into(targetImgView)
    }
}