package com.sarrawi.img.adapter



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.img.R
import com.sarrawi.img.databinding.ImgDesignBinding
import com.sarrawi.img.model.ImgsModel

class ImageAdapterTwo(val con:Context) : ListAdapter<ImgsModel, ImageAdapterTwo.ItemViewHolder>(DiffCallback()) {

    var onItemClick: ((Int,ImgsModel, Int) -> Unit)? = null

    private var isInternetConnected: Boolean = true

    var onbtnClick: ((item:ImgsModel,position:Int) -> Unit)? = null
    val displayMetrics = con.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    // قم بتحديد القيم المطلوبة للصورة
    val targetWidth = screenWidth / 2 // على سبيل المثال، يمكنك تحديد العرض إلى نصف عرض الشاشة
    val targetHeight = screenHeight / 2 // على سبيل المثال، يمكنك تحديد الارتفاع إلى نصف ارتفاع الشاشة


    inner class ItemViewHolder(private val binding: ImgDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        // You can use binding to access views and bind data

        init {
            if(isInternetConnected) {
                binding.root.setOnClickListener {
                    //اذا كانت null سيتم استخدام 0؟
//                    onItemClick?.invoke(img_list[layoutPosition].id ?: 0,img_list[layoutPosition].image_url, layoutPosition ?: 0)
                    onItemClick?.invoke(imgList[layoutPosition].id ?: 0, imgList[layoutPosition], layoutPosition)

                }

//                binding.imgFave.setOnClickListener {
//                    onbtnClick?.invoke(imgList[position], position)
//                }

            }
            else{
                binding.root.setOnClickListener{
//                        Toast.makeText(con,"ghghg",Toast.LENGTH_SHORT).show()
                    val snackbar = Snackbar.make(it,"لا يوجد اتصال بالإنترنت", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }

//                binding.imgFave.setOnClickListener {
//                    val snackbar = Snackbar.make(it,"لا يوجد اتصال بالإنترنت", Snackbar.LENGTH_SHORT)
//                    snackbar.show()
//                }

            }
        }



        fun bind(position: Int, isInternetConnected: Boolean) {
            if (isInternetConnected) {

                val current_imgModel = imgList[position]
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_baseline_autorenew_24)
                    .error(R.drawable.error_a)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)

                Glide.with(con)
                    .asBitmap() // تحميل الصورة كـ Bitmap
                    .load(current_imgModel.image_url)
                    .apply(requestOptions)
                    .override(targetWidth, targetHeight)
                    .circleCrop()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgadapterImgViewContent)



                binding.apply {
//                    if(current_imgModel.is_fav){
//                        imgFave.setImageResource(R.drawable.baseline_favorite_true)
//                    }else{
//                        imgFave.setImageResource(R.drawable.baseline_favorite_border_false)
//                    }

                }
            } else {
                // عند عدم وجود اتصال بالإنترنت، قم بعرض الـ lyNoInternet بدلاً من الصورة
                Glide.with(con)
                    .load(R.drawable.nonet) // تحميل صورة nonet.jpg
                    .into(binding.imgadapterImgViewContent)
                binding.imgadapterImgViewContent.visibility = View.GONE
                binding.lyNoInternet.visibility = View.VISIBLE
            }



        }

        fun bind(imgsModel: ImgsModel) {
            // Example: binding.textView.text = imgsModel.someText
        }
    }

    private val differ = AsyncListDiffer(this, DiffCallback())

    var imgList: List<ImgsModel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ImgDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
}

class DiffCallback : DiffUtil.ItemCallback<ImgsModel>() {
    override fun areItemsTheSame(oldItem: ImgsModel, newItem: ImgsModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ImgsModel, newItem: ImgsModel): Boolean {
        return oldItem == newItem
    }

}
