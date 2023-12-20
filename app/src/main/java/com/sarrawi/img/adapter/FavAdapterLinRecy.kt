package com.sarrawi.img.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.sarrawi.img.R
import com.sarrawi.img.databinding.ImgDesignfavBinding
import com.sarrawi.img.databinding.RowImagesBinding
import com.sarrawi.img.databinding.RowimagefavBinding
import com.sarrawi.img.model.FavoriteImage

import com.sarrawi.img.model.ImgsModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FavAdapterLinRecy(val con: Context):
    RecyclerView.Adapter<FavAdapterLinRecy.ViewHolder>() {

//    var onItemClick: ((Int, Int) -> Unit)? = null
    var onItemClick: ((Int,FavoriteImage, Int) -> Unit)? = null
    var onbtnclick: ((item:FavoriteImage) -> Unit)? = null
    var onSaveImageClickListenerfav: OnSaveImageClickListenerfav? = null

    val displayMetrics = con.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels
    private var adCount = 4
    // قم بتحديد القيم المطلوبة للصورة
    val targetWidth = screenWidth / 2 // على سبيل المثال، يمكنك تحديد العرض إلى نصف عرض الشاشة
    val targetHeight = screenHeight / 2 // على سبيل المثال، يمكنك تحديد الارتفاع إلى نصف ارتفاع الشاشة

    inner class ViewHolder(val binding: RowimagefavBinding):RecyclerView.ViewHolder(binding.root) {
        var adView: AdView?=null

        init {

            binding.root.setOnClickListener {
                //اذا كانت null سيتم استخدام 0؟
//                onItemClick?.invoke(fav_img_list[layoutPosition].id ?: 0, layoutPosition ?: 0)
//                onItemClick?.invoke(fav_img_list.getOrNull(layoutPosition)?.id ?: 0, layoutPosition ?: 0)
                onItemClick?.invoke(fav_img_list[layoutPosition].id ?: 0, fav_img_list[layoutPosition], layoutPosition)

            }
            binding.imgFave.setOnClickListener {
//                onbtnclick?.invoke(fav_img_list[adapterPosition])
                onbtnclick?.invoke(fav_img_list[bindingAdapterPosition])

            }

        }

        fun bind(position: Int) {

            val current_imgModel = fav_img_list[position]
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_autorenew_24)
                .error(R.drawable.error_a)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)

            Glide.with(con)
                .asBitmap()
                .load(current_imgModel.image_url)
                .apply(requestOptions)
                .centerCrop()
                .into(binding.imageView)




            binding.saveImg.setOnClickListener {
//                onSaveImageClickListenerfav?.onSaveImageClick(adapterPosition)
                saveBitmapToExternalStorage((binding.imageView.drawable as BitmapDrawable).bitmap)
            }

//            binding.share?.setOnClickListener {
//                // يفترض أن هذا الكود داخل نشاط أو خدمة أو أي كلاس يمتلك الوصول إلى context
//
//                val drawable: BitmapDrawable = binding.imageView.getDrawable() as BitmapDrawable
//                val bitmap: Bitmap = drawable.bitmap
//
//                val bitmapPath: String = MediaStore.Images.Media.insertImage(
//                    con.contentResolver,
//                    bitmap,
//                    "title",
//                    null
//                )
//
//                val uri: Uri = Uri.parse(bitmapPath)
//
//                val intent = Intent(Intent.ACTION_SEND)
//                intent.type = "image/png"
//                intent.putExtra(Intent.EXTRA_STREAM, uri)
//                intent.putExtra(Intent.EXTRA_TEXT, "Playstore Link: https://play.google.com/store")
//
//                con.startActivity(Intent.createChooser(intent, "Share"))
//
//            }

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
        return  ViewHolder(RowimagefavBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        if (position % adCount == 0) {  // تحقق مما إذا كانت هذه العنصر هي عنصر الإعلان
            Log.d("AD_TAG", "Loading Ad at position $position")
            holder.adView?.loadAd(AdRequest.Builder().build())  // تحميل الإعلان

        }
    }

    override fun getItemCount(): Int {
        return fav_img_list.size
    }

    fun updateInternetStatus(isConnected: Boolean) {

        notifyDataSetChanged()
    }
    interface OnSaveImageClickListenerfav {
        fun onSaveImageClick(position: Int)
    }

    // دالة لحفظ الصورة كملف في التخزين الخارجي
    private fun saveBitmapToExternalStorage(bitmap: Bitmap) {
        val fileName = "image_${System.currentTimeMillis()}.jpg"

        try {
            // احصل على مسار التخزين الخارجي
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

            // تأكد من أن المجلد موجود، إذا لم يكن، قم بإنشاء المجلد
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            val imageFile = File(imagesDir, fileName)
            val outputStream = FileOutputStream(imageFile)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // إعلام المستخدم بأن الصورة تم حفظها
            Toast.makeText(con, "تم حفظ الصورة", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            // يمكنك إدراج رسالة خطأ هنا إذا لزم الأمر
        }
    }


}