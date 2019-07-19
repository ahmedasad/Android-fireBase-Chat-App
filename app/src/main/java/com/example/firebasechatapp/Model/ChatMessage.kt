package com.example.firebasechatapp.Model

class ChatMessage(val id:String?,val text:String,val fromId:String,val toId:String,val timeStampt:String) {
    constructor():this("","","","","")
}