package com.sarrawi.img.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sarrawi.img.R
import com.sarrawi.img.databinding.TypesDesign2Binding
import com.sarrawi.img.databinding.TypesDesignBinding
import com.sarrawi.img.model.Img_Types_model

class TypesAdapter_T (val con: Context): RecyclerView.Adapter<TypesAdapter_T.ViewHolder>() {

    var onItemClick: ((Int) -> Unit)? = null
    val displayMetrics = con.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    // قم بتحديد القيم المطلوبة للصورة
    val targetWidth = screenWidth / 2 // على سبيل المثال، يمكنك تحديد العرض إلى نصف عرض الشاشة
    val targetHeight = screenHeight / 2 // على سبيل المثال، يمكنك تحديد الارتفاع إلى نصف ارتفاع الشاشة

    inner class ViewHolder(val binding: TypesDesign2Binding):RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                //اذا كانت null سيتم استخدام 0؟
                //onItemClick?.invoke(msgsTypesModel[layoutPosition].id,msgsTypesModel[layoutPosition].MsgTypes!!)
                onItemClick?.invoke(imgTypes_list[layoutPosition].id?:0)
            }
        }

        fun bind(position: Int) {
            val current_imgTypes=imgTypes_list[position]
            binding.apply {
                TextViewCateg.text=current_imgTypes.ImgTypes
//                val drawable = ContextCompat.getDrawable(requireContext(), current_imgTypes.Pic)
//                ImgViewCateg.setImageDrawable(drawable)
//                ImgViewCateg.setImageResource(current_imgTypes.Pic)

            }

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_autorenew_24)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
            Glide.with(con)
                .asBitmap()
                .load(current_imgTypes.Pic)
                .apply(requestOptions)

                .into(binding.ImgViewCateg)
        }
    }
    private val diffCallback = object : DiffUtil.ItemCallback<Img_Types_model>(){
        override fun areItemsTheSame(oldItem: Img_Types_model, newItem: Img_Types_model): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Img_Types_model, newItem: Img_Types_model): Boolean {
            return newItem == oldItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var imgTypes_list: List<Img_Types_model>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(TypesDesign2Binding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return imgTypes_list.size
    }


}