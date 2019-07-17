package com.example.firebasechatapp.Adapter_Groupie

import com.example.firebasechatapp.Model.User
import com.example.firebasechatapp.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.new_user_message.view.*

class UserItem(val user: User): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.new_user_message

    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.UserName.text = user.userName
        Picasso.get().load(user.profileImage).into(viewHolder.itemView.UserImg)

    }
}