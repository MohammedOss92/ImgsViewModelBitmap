package com.sarrawi.img.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sarrawi.img.R
import com.sarrawi.img.databinding.ImgDesignBinding
import com.sarrawi.img.databinding.ImgDesignfavBinding
import com.sarrawi.img.model.FavoriteImage
import com.sarrawi.img.model.ImgsModel

class Fav_Adapter(val con: Context): RecyclerView.Adapter<Fav_Adapter.ViewHolder>() {


    var onItemClick: ((Int,FavoriteImage, Int) -> Unit)? = null
    var onbtnclick: ((item:FavoriteImage) -> Unit)? = null
    val displayMetrics = con.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    // قم بتحديد القيم المطلوبة للصورة
    val targetWidth = screenWidth / 2 // على سبيل المثال، يمكنك تحديد العرض إلى نصف عرض الشاشة
    val targetHeight = screenHeight / 2 // على سبيل المثال، يمكنك تحديد الارتفاع إلى نصف ارتفاع الشاشة


    inner class ViewHolder(val binding: ImgDesignfavBinding):RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                //اذا كانت null سيتم استخدام 0؟
                onItemClick?.invoke(fav_img_list[layoutPosition].id ?: 0, fav_img_list[layoutPosition], layoutPosition)

            }
            binding.imgFave.setOnClickListener {
                onbtnclick?.invoke(fav_img_list[adapterPosition])
            }

        }

        fun bind(position: Int) {


                val current_imgModel = fav_img_list[position]
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_baseline_autorenew_24) // الصورة المؤقتة لحالة التحميل
                    .error(R.drawable.error_a) // الصورة المعروضة في حالة حدوث خطأ أثناء التحميل
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                Glide.with(con)
                    .asBitmap() // تحميل الصورة كـ Bitmap
                    .load(current_imgModel.image_url)
                    .apply(requestOptions)
                    .override(targetWidth, targetHeight)
                    .circleCrop()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgadapterImgViewContent)


        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<FavoriteImage>(){
        override fun areItemsTheSame(oldItem: FavoriteImage, newItem: FavoriteImage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteImage, newItem: FavoriteImage): Boolean {
            return newItem == oldItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var fav_img_list: List<FavoriteImage>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(ImgDesignfavBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return fav_img_list.size
    }

    fun updateInternetStatus(isConnected: Boolean) {
        notifyDataSetChanged()
    }


}