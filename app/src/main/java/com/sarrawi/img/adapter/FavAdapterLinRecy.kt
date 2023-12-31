package com.sarrawi.img.adapter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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
import com.sarrawi.img.databinding.ImgDesignfavBinding
import com.sarrawi.img.databinding.RowImagesBinding
import com.sarrawi.img.databinding.RowimagefavBinding
import com.sarrawi.img.model.FavoriteImage
import androidx.core.app.TaskStackBuilder

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

//            binding.root.setOnClickListener {
//                //اذا كانت null سيتم استخدام 0؟
////                onItemClick?.invoke(fav_img_list[layoutPosition].id ?: 0, layoutPosition ?: 0)
////                onItemClick?.invoke(fav_img_list.getOrNull(layoutPosition)?.id ?: 0, layoutPosition ?: 0)
//                onItemClick?.invoke(fav_img_list[layoutPosition].id ?: 0, fav_img_list[layoutPosition], layoutPosition)
//
//            }
            binding.imgFave.setOnClickListener {
//                onbtnclick?.invoke(fav_img_list[adapterPosition])
                onbtnclick?.invoke(fav_img_list[bindingAdapterPosition])

            }

            adView=itemView.findViewById(R.id.adViewfav)

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

                .into(binding.imageView)




            binding.saveImg.setOnClickListener {
//                onSaveImageClickListenerfav?.onSaveImageClick(adapterPosition)
                saveBitmapToExternalStorage((binding.imageView.drawable as BitmapDrawable).bitmap)
            }

            binding.whatsapp.setOnClickListener {
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

            binding.messenger.setOnClickListener {

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

            if (current_imgModel.new_img == 0) {
                binding.newImg.setVisibility(View.INVISIBLE)
            } else {
                binding.newImg.setVisibility(View.VISIBLE)
            }

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
//    private fun saveBitmapToExternalStorage(bitmap: Bitmap) {
//        val fileName = "image_${System.currentTimeMillis()}.jpg"
//
//        try {
//            // احصل على مسار التخزين الخارجي
//            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//
//            // تأكد من أن المجلد موجود، إذا لم يكن، قم بإنشاء المجلد
//            if (!imagesDir.exists()) {
//                imagesDir.mkdirs()
//            }
//
//            val imageFile = File(imagesDir, fileName)
//            val outputStream = FileOutputStream(imageFile)
//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//            outputStream.flush()
//            outputStream.close()
//
//            // إعلام المستخدم بأن الصورة تم حفظها
//            Toast.makeText(con, "تم حفظ الصورة", Toast.LENGTH_SHORT).show()
//        } catch (e: IOException) {
//            e.printStackTrace()
//            // يمكنك إدراج رسالة خطأ هنا إذا لزم الأمر
//        }
//    }

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            val packageManager = context.packageManager
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun saveBitmapToExternalStorage(bitmap: Bitmap) {
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
            showToast("تم حفظ الصورة")

            // إظهار إشعار في لوحة الإشعارات باستخدام الصورة المحفوظة
            showNotification("تم تنزيل الصورة", imageFile)

        } catch (e: IOException) {
            e.printStackTrace()
            // يمكنك إدراج رسالة خطأ هنا إذا لزم الأمر
        }
    }

    fun showToast(message: String) {
        Toast.makeText(con, message, Toast.LENGTH_SHORT).show()
    }

    fun showNotification(title: String, imageFile: File) {
        val channelId = "channel_id"
        val notificationId = 1

        // إنشاء قناة الإشعار إذا لم تكن موجودة (لإصدارات Android 8.0 فأعلى)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = con.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // إعداد محتوى الإشعار باستخدام الصورة المحفوظة
        val builder = NotificationCompat.Builder(con, channelId)
            .setSmallIcon(R.drawable.iconn)
            .setContentTitle(title)
            .setContentText("تم تحميل الصورة بنجاح")
            .setLargeIcon(BitmapFactory.decodeFile(imageFile.absolutePath))
            .setAutoCancel(true)

        // إرسال الإشعار
        val notificationManager = con.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

}