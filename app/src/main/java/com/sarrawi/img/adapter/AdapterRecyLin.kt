package com.sarrawi.img.adapter

import android.Manifest.permission.*
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
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
    private var adCount = 4
    private var isToolbarVisible = true
    val displayMetrics = con.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    // قم بتحديد القيم المطلوبة للصورة
    val targetWidth = screenWidth / 2 // على سبيل المثال، يمكنك تحديد العرض إلى نصف عرض الشاشة
    val targetHeight = screenHeight / 2 // على سبيل المثال، يمكنك تحديد الارتفاع إلى نصف ارتفاع الشاشة


    inner class ViewHolder(val binding: RowImagesBinding):RecyclerView.ViewHolder(binding.root) {
        var adView: AdView?=null
        init {

        }


        fun bind(position: Int,isInternetConnected: Boolean) {

            val current_imgModel = img_list[position]
            if (isInternetConnected) {

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

                    .into(binding.imageView)


//            Glide.with(con)
//                .load(current_imgModel.image_url)
//                .into(binding.imageView)


                binding.root.setOnClickListener {
                    onItemClick?.invoke(img_list[layoutPosition].id ?: 0, img_list[layoutPosition], layoutPosition)
                }

                binding.imgFave.setOnClickListener {
                    onbtnClick?.invoke(img_list[position],position)
                }
//        onSaveImageClickListener?.onSaveImageClick(layoutPosition)
                binding.saveImg.setOnClickListener {
                    saveBitmapToExternalStorage((binding.imageView.drawable as BitmapDrawable).bitmap)
                }


            binding.apply {
             if(current_imgModel.is_fav){
                imgFave.setImageResource(R.drawable.baseline_favorite_true)
             }

             else{
                 imgFave.setImageResource(R.drawable.baseline_favorite_border_false)
             }

            }

                binding.whatsapp.setOnClickListener {
                    val whatsappPackage = "com.whatsapp"

// التحقق مما إذا كان تطبيق WhatsApp مثبتًا
                    if (isAppInstalled(con, whatsappPackage)) {
                        // تمثيل الصورة
                        val drawable: BitmapDrawable = binding.imageView.drawable as BitmapDrawable
                        val bitmap: Bitmap = drawable.bitmap

                        // حفظ الصورة في التخزين الخارجي
                        val file = File(con.externalCacheDir, "image.png")
                        val outputStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()

                        // إنشاء Uri للصورة المحفوظة
                        val uri: Uri = FileProvider.getUriForFile(con, con.packageName + ".provider", file)

                        // إنشاء Intent لمشاركة الصورة عبر WhatsApp
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.putExtra(Intent.EXTRA_TEXT, "Playstore Link: https://play.google.com/store")

                        // تحديد اسم الحزمة لتحديد تطبيق WhatsApp
                        intent.setPackage(whatsappPackage)

                        // ضبط العلامات لمنح أذونات القراءة لتطبيق WhatsApp
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        // بدء النشاط
                        con.startActivity(intent)
                    } else {
                        // إذا لم يكن WhatsApp مثبتًا، عرض Snackbar
                        Snackbar.make(binding.root, "يجب تثبيت تطبيق WhatsApp", Snackbar.LENGTH_SHORT).show()
                    }



                }

                binding.messenger.setOnClickListener {
                    val messengerPackage = "com.facebook.orca" // حزمة تطبيق Facebook Messenger

// التحقق مما إذا كان Facebook Messenger مثبت
                    if (isAppInstalled(con, messengerPackage)) {
                        // تمثيل الصورة
                        val drawable: BitmapDrawable = binding.imageView.drawable as BitmapDrawable
                        val bitmap: Bitmap = drawable.bitmap

                        // حفظ الصورة في التخزين الخارجي
                        val file = File(con.externalCacheDir, "image.png")
                        val outputStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()

                        // إنشاء Uri للصورة المحفوظة
                        val uri: Uri = FileProvider.getUriForFile(con, con.packageName + ".provider", file)

                        // إنشاء Intent لمشاركة الصورة عبر Facebook Messenger
                        val messengerIntent = Intent(Intent.ACTION_SEND)
                        messengerIntent.type = "image/*"
                        messengerIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        messengerIntent.putExtra(Intent.EXTRA_TEXT, "Playstore Link: https://play.google.com/store")
                        messengerIntent.setPackage(messengerPackage)
                        messengerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                        // بدء النشاط
                        con.startActivity(messengerIntent)
                    } else {
                        // إذا لم يكن Facebook Messenger مثبتًا، عرض Snackbar
                        Snackbar.make(binding.root, "يجب تثبيت تطبيق Facebook Messenger", Snackbar.LENGTH_SHORT).show()
                    }

                }


                binding.imgShare?.setOnClickListener {
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

                binding.imgShare.visibility= View.GONE

                binding.root.setOnClickListener{
//                        Toast.makeText(con,"ghghg",Toast.LENGTH_SHORT).show()
                    val snackbar = Snackbar.make(it,"لا يوجد اتصال بالإنترنت", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }

                binding.imgFave.visibility= View.GONE
                binding.whatsapp.visibility= View.GONE
                binding.messenger.visibility= View.GONE
                binding.saveImg.visibility= View.GONE
            }


            adView=itemView.findViewById(R.id.adViewlin)

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
        if (position % adCount == 0) {  // تحقق مما إذا كانت هذه العنصر هي عنصر الإعلان
            Log.d("AD_TAG", "Loading Ad at position $position")
            holder.adView?.loadAd(AdRequest.Builder().build())  // تحميل الإعلان

        }

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

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            val packageManager = context.packageManager
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


}