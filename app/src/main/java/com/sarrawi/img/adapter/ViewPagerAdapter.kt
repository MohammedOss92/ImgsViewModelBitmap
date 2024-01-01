package com.sarrawi.img.adapter

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
import com.sarrawi.img.databinding.ImgPagerBinding
import com.sarrawi.img.databinding.RowImagesBinding
import com.sarrawi.img.model.ImgsModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ViewPagerAdapter (val con: Context):RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    private var isInternetConnected: Boolean = true
    var onbtnClick: ((item:ImgsModel,position:Int) -> Unit)? = null
    var onSaveImageClickListenerp: OnSaveImageClickListenerp? = null
    val displayMetrics = con.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels
    private var adCount = 4
    // قم بتحديد القيم المطلوبة للصورة
    val targetWidth = screenWidth / 2 // على سبيل المثال، يمكنك تحديد العرض إلى نصف عرض الشاشة
    val targetHeight = screenHeight / 2 // على سبيل المثال، يمكنك تحديد الارتفاع إلى نصف ارتفاع الشاشة


    inner class ViewHolder(val binding:ImgPagerBinding): RecyclerView.ViewHolder(binding.root){
        var adView: AdView?=null
        init {
            if(isInternetConnected) {
                binding.imgFave?.setOnClickListener {
                    onbtnClick?.invoke(img_list_Pager[position], position)
                }


            }
            else{



                binding.root.setOnClickListener{
//                        Toast.makeText(con,"ghghg",Toast.LENGTH_SHORT).show()
                    val snackbar = Snackbar.make(it,"لا يوجد اتصال بالإنترنت", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }



            }



        }

        fun bind(position: Int, isInternetConnected: Boolean) {
            if (isInternetConnected) {


                val current_imgModel = img_list_Pager[position]
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_baseline_autorenew_24)
                    .error(R.drawable.error_a)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)

                binding.imageView?.let {
                    Glide.with(con)
                        .asBitmap()
                        .load(current_imgModel.image_url)
                        .apply(requestOptions)

                        .into(it)
                }

                binding.lyNoInternet.visibility = View.GONE

                binding.lyNoInternet.visibility = View.GONE

                if (current_imgModel.new_img == 0) {
                    binding.newImg?.setVisibility(View.INVISIBLE)
                } else {
                    binding.newImg?.setVisibility(View.VISIBLE)
                }

                binding.apply {
                    if(current_imgModel.is_fav){
                        imgFave?.setImageResource(R.drawable.baseline_favorite_true)
                    }else{
                        imgFave?.setImageResource(R.drawable.baseline_favorite_border_false)
                    }

                }

                binding.saveImg?.setOnClickListener {
//                    onSaveImageClickListenerp?.onSaveImageClickp(adapterPosition)
                    saveBitmapToExternalStorage((binding.imageView?.drawable as BitmapDrawable).bitmap)
                }

                binding.whatsapp?.setOnClickListener {
                    val drawable: BitmapDrawable = binding.imageView?.getDrawable() as BitmapDrawable
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

                    val whatsappIntent = Intent(Intent.ACTION_SEND)
                    whatsappIntent.type = "text/plain"
                    whatsappIntent.setPackage("com.whatsapp")
                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share")
                    try {
                        con.startActivity(whatsappIntent)
                    } catch (ex: ActivityNotFoundException) {
                        Snackbar.make(binding.root, "Whatsapp have not been installed.", Snackbar.LENGTH_SHORT).show()

                    }

                }

                binding.messenger?.setOnClickListener {

                    val drawable: BitmapDrawable = binding.imageView?.getDrawable() as BitmapDrawable
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

                    val whatsappIntent = Intent(Intent.ACTION_SEND)
                    whatsappIntent.type = "text/plain"
                    whatsappIntent.setPackage("com.facebook.orca")
                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "The text you wanted to share")
                    try {
                        con.startActivity(whatsappIntent)
                    } catch (ex: ActivityNotFoundException) {
                        Snackbar.make(binding.root, "لم يتم تثبيت تطبيق Facebook Messenger.", Snackbar.LENGTH_SHORT).show()

                    }

//                    val drawable: BitmapDrawable = binding.imageView.drawable as BitmapDrawable
//                    val bitmap: Bitmap = drawable.bitmap
//                    val file = File(con.externalCacheDir, "image.png")
//                    val outputStream = FileOutputStream(file)
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//                    outputStream.flush()
//                    outputStream.close()
//                    val uri: Uri = Uri.fromFile(file)
//                    val facebookMessengerPackage = "com.facebook.orca"
//                    if (isAppInstalled(con, facebookMessengerPackage)) {
//                        val intent = Intent(Intent.ACTION_SEND)
//                        intent.type = "image/png"
//                        intent.putExtra(Intent.EXTRA_STREAM, uri)
//                        intent.putExtra(Intent.EXTRA_TEXT, "النص الذي ترغب في مشاركته")
//                        intent.setPackage(facebookMessengerPackage)
//                        try {
//                            con.startActivity(Intent.createChooser(intent, "مشاركة عبر"))
//                        } catch (ex: ActivityNotFoundException) {
//                            Snackbar.make(binding.root, "لم يتم تثبيت تطبيق Facebook Messenger.", Snackbar.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        Snackbar.make(binding.root, "يجب تثبيت تطبيق Facebook Messenger.", Snackbar.LENGTH_SHORT).show()
//                    }
                    /*
                        val drawable: BitmapDrawable = binding.imageView.drawable as BitmapDrawable
                        val bitmap: Bitmap = drawable.bitmap

                        // حفظ الصورة في التخزين الخارجي
                        val file = File(con.externalCacheDir, "image.png")
                        val outputStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()

                        // إنشاء Uri للصورة المحفوظة
                        val uri: Uri = Uri.fromFile(file)

                        // إنشاء Intent لمشاركة الصورة عبر Facebook Messenger
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/png"
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.putExtra(Intent.EXTRA_TEXT, "النص الذي ترغب في مشاركته")

                        // تحديد اسم الحزمة لتحديد تطبيق Facebook Messenger
                        intent.setPackage("com.facebook.orca")

                        try {
                            // بدء النشاط
                            con.startActivity(Intent.createChooser(intent, "مشاركة عبر"))
                        } catch (ex: ActivityNotFoundException) {
                            Snackbar.make(binding.root, "لم يتم تثبيت تطبيق Facebook Messenger.", Snackbar.LENGTH_SHORT).show()
                        }

     */
                }


                binding.imgShare?.setOnClickListener {
                    // يفترض أن هذا الكود داخل نشاط أو خدمة أو أي كلاس يمتلك الوصول إلى context



                    val drawable: BitmapDrawable = binding.imageView?.getDrawable() as BitmapDrawable
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

//                binding.share?.setOnClickListener {
//                    // يفترض أن هذا الكود داخل نشاط أو خدمة أو أي كلاس يمتلك الوصول إلى context
//
//                    val drawable: BitmapDrawable = binding.imageViewpager.getDrawable() as BitmapDrawable
//                    val bitmap: Bitmap = drawable.bitmap
//
//                    val bitmapPath: String = MediaStore.Images.Media.insertImage(
//                        con.contentResolver,
//                        bitmap,
//                        "title",
//                        null
//                    )
//
//                    val uri: Uri = Uri.parse(bitmapPath)
//
//                    val intent = Intent(Intent.ACTION_SEND)
//                    intent.type = "image/png"
//                    intent.putExtra(Intent.EXTRA_STREAM, uri)
//                    intent.putExtra(Intent.EXTRA_TEXT, "Playstore Link: https://play.google.com/store")
//
//                    con.startActivity(Intent.createChooser(intent, "Share"))
//
//                }
            } else {
                // عند عدم وجود اتصال بالإنترنت، قم بعرض الـ lyNoInternet بدلاً من الصورة
                binding.imageView?.let {
                    Glide.with(con)
                        .load(R.drawable.nonet) // تحميل صورة nonet.jpg
                        .into(it)
                }
                binding.imageView?.visibility = View.GONE
                binding.lyNoInternet.visibility = View.VISIBLE
                binding.imgShare?.visibility = View.GONE
                binding.imgFave?.visibility = View.GONE
                binding.saveImg?.visibility = View.GONE
                binding.messenger?.visibility = View.GONE
                binding.whatsapp?.visibility = View.GONE
            }


            adView=itemView.findViewById(R.id.adViewpa)


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
    var img_list_Pager: List<ImgsModel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerAdapter.ViewHolder {
        return  ViewHolder(ImgPagerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewPagerAdapter.ViewHolder, position: Int) {

        holder.bind(position,isInternetConnected)
        if (position % adCount == 0) {  // تحقق مما إذا كانت هذه العنصر هي عنصر الإعلان
            Log.d("AD_TAG", "Loading Ad at position $position")
            holder.adView?.loadAd(AdRequest.Builder().build())  // تحميل الإعلان

        }

    }

    override fun getItemCount(): Int {
        return img_list_Pager.size
    }

    fun updateInternetStatus(isConnected: Boolean) {
        isInternetConnected = isConnected
        notifyDataSetChanged()
    }

    interface OnSaveImageClickListenerp {
        fun onSaveImageClickp(position: Int)
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