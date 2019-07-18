package com.example.firebasechatapp.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid:String,val userName:String,val profileImage:String,val loginStatus:String):Parcelable {
    constructor():this("","","","")
}