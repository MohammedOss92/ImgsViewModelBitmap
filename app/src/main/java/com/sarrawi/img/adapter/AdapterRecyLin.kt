package com.sarrawi.img.adapter

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.img.R
import com.sarrawi.img.databinding.RowImagesBinding
import com.sarrawi.img.model.ImgsModel
import java.io.*


class AdapterRecyLin(val con: Context):
    RecyclerView.Adapter<AdapterRecyLin.ViewHolder>() {
    var onItemClick: ((Int,ImgsModel, Int) -> Unit)? = null
    var onbtnClick: ((item:ImgsModel,position:Int) -> Unit)? = null
    var onSaveImageClickListener: OnSaveImageClickListener? = null
    private var isInternetConnected: Boolean = true

    private var isToolbarVisible = true
    val displayMetrics = con.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    // قم بتحديد القيم المطلوبة للصورة
    val targetWidth = screenWidth / 2 // على سبيل المثال، يمكنك تحديد العرض إلى نصف عرض الشاشة
    val targetHeight = screenHeight / 2 // على سبيل المثال، يمكنك تحديد الارتفاع إلى نصف ارتفاع الشاشة


    inner class ViewHolder(val binding:RowImagesBinding):RecyclerView.ViewHolder(binding.root) {

        init {
            if(isInternetConnected) {
                 binding.root.setOnClickListener {
                    onItemClick?.invoke(img_list[layoutPosition].id ?: 0, img_list[layoutPosition], layoutPosition)
            }

                binding.imgFave.setOnClickListener {
                    onbtnClick?.invoke(img_list[position],position)
                }

                binding.saveImg.setOnClickListener {
                    onSaveImageClickListener?.onSaveImageClick(adapterPosition)
                }



            }
        else{
            binding.root.setOnClickListener{
//                        Toast.makeText(con,"ghghg",Toast.LENGTH_SHORT).show()
                val snackbar = Snackbar.make(it,"لا يوجد اتصال بالإنترنت", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }

            binding.imgFave.setOnClickListener {
                val snackbar = Snackbar.make(it,"لا يوجد اتصال بالإنترنت", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }

            binding.saveImg.setOnClickListener {
                val snackbar = Snackbar.make(it,"لا يوجد اتصال بالإنترنت", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }

        }



        }


        fun bind(position: Int,isInternetConnected: Boolean) {

            val current_imgModel = img_list[position]
            if (isInternetConnected) {

                val current_imgModel = img_list[position]
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
                    .into(binding.imageView)
                binding.lyNoInternet.visibility = View.GONE

//            Glide.with(con)
//                .load(current_imgModel.image_url)
//                .into(binding.imageView)





            binding.apply {
             if(current_imgModel.is_fav){
                imgFave.setImageResource(R.drawable.baseline_favorite_true)
             }else{
                 imgFave.setImageResource(R.drawable.baseline_favorite_border_false)
             }

            }

                binding.share?.setOnClickListener {
                    // يفترض أن هذا الكود داخل نشاط أو خدمة أو أي كلاس يمتلك الوصول إلى context

                    val drawable: BitmapDrawable = binding.imageView.getDrawable() as BitmapDrawable
                    val bitmap: Bitmap = drawable.bitmap

                    val bitmapPath: String = MediaStore.Images.Media.insertImage(
                        con.contentResolver,
                        bitmap,
                        "title",
                        null
                    )

                    val uri: Uri = Uri.parse(bitmapPath)

                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/png"
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    intent.putExtra(Intent.EXTRA_TEXT, "Playstore Link: https://play.google.com/store")

                    con.startActivity(Intent.createChooser(intent, "Share"))

                }

            } else {
                // عند عدم وجود اتصال بالإنترنت، قم بعرض الـ lyNoInternet بدلاً من الصورة
                Glide.with(con)
                    .load(R.drawable.nonet) // تحميل صورة nonet.jpg
                    .into(binding.imageView)
                binding.imageView.visibility = View.GONE
                binding.lyNoInternet.visibility = View.VISIBLE
            }
//            binding.imgFave.setOnClickListener {
//                onbtnClick?.invoke(img_list[position],position)
//            }
//
//            binding.saveImg.setOnClickListener {
//                onSaveImageClickListener?.onSaveImageClick(adapterPosition)
//            }


            binding.share?.setOnClickListener {
                // يفترض أن هذا الكود داخل نشاط أو خدمة أو أي كلاس يمتلك الوصول إلى context

                val drawable: BitmapDrawable = binding.imageView.getDrawable() as BitmapDrawable
                val bitmap: Bitmap = drawable.bitmap

                val bitmapPath: String = MediaStore.Images.Media.insertImage(
                    con.contentResolver,
                    bitmap,
                    "title",
                    null
                )

                val uri: Uri = Uri.parse(bitmapPath)

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/png"
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.putExtra(Intent.EXTRA_TEXT, "Playstore Link: https://play.google.com/store")

                con.startActivity(Intent.createChooser(intent, "Share"))

            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ImgsModel>(){
        override fun areItemsTheSame(oldItem: ImgsModel, newItem: ImgsModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImgsModel, newItem: ImgsModel): Boolean {
            return newItem == oldItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var img_list: List<ImgsModel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(RowImagesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(position,isInternetConnected)

    }

    override fun getItemCount(): Int {
        return img_list.size
    }

    fun updateInternetStatus(isConnected: Boolean) {
        isInternetConnected = isConnected
        notifyDataSetChanged()
    }

    interface OnSaveImageClickListener {
        fun onSaveImageClick(position: Int)
    }

    private fun saveImageLocally(bitmap: Bitmap): File {
        val imagesFolder = File(Environment.getExternalStorageDirectory(), "تطبيق الصور")
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs()
        }

        val file = File(imagesFolder, "shared_image.png")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }


    }