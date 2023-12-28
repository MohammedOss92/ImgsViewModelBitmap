package com.sarrawi.img.db.Dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.sarrawi.img.model.FavoriteImage

@Dao
interface FavoriteImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteImage(favoriteImage: FavoriteImage)

    @Delete
    suspend fun deleteFavoriteImage(favoriteImage: FavoriteImage)

    @Query("SELECT * FROM favorite_images order by id desc ")
    suspend fun getAllFavoriteImages(): List<FavoriteImage>

    @Query("Update favorite_images SET is_fav = :state where id =:ID")
    suspend fun update_fav(ID:Int,state:Boolean)

    @Query("SELECT * FROM favorite_images order by id desc ")
    fun getAllFavoritea(): LiveData<List<FavoriteImage>>

//    @Query("SELECT * FROM favorite_images order by id desc ")
    @Query("SELECT * FROM favorite_images ORDER BY timestamp DESC ")
    fun getAllFavorite(): PagingSource<Int,FavoriteImage>

    @Query("SELECT * FROM favorite_images where id =:ID order by id desc ")
    fun getFavByID(ID:Int): LiveData<List<FavoriteImage>>
}