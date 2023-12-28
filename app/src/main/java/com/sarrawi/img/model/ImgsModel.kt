package com.sarrawi.img.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ImgsModel(


    @SerializedName("id")
    var id:Int?=0,

    @SerializedName("ID_Type_id")
    var ID_Type_id : Int,

    @SerializedName("new_img")
    var new_img :Int,

    @SerializedName("image_url")
    var image_url :String,


    var is_fav:Boolean = false

//    var timestamp: Long? = System.currentTimeMillis()


)

