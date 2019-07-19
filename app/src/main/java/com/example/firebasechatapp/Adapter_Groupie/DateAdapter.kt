package com.example.firebasechatapp.Adapter_Groupie


import android.widget.TextView
import com.example.firebasechatapp.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.date_layout.view.*

class DateAdapter(val date:String): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.date_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.itemView.txtDate.text = date
    }
}