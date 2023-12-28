package com.sarrawi.img.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_images")
data class FavoriteImage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "ID_Type_id")
    var ID_Type_id: Int,

    @ColumnInfo(name = "new_img")
    var new_img: Int,

    @ColumnInfo(name = "image_url")
    var image_url: String,

    @ColumnInfo(name = "timestamp")
    var timestamp: Long? = System.currentTimeMillis(),


    var is_fav:Boolean = true
)
