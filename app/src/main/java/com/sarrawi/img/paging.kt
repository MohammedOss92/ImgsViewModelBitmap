package com.sarrawi.img

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.img.model.FavoriteImage
import com.sarrawi.img.model.ImgsModel

//private fun setUpRvth() {
//    if (isAdded) {
//        // تعيين المدير التخطيط (GridLayout) لـ RecyclerView أولاً
//        binding.rvImgCont.layoutManager = GridLayoutManager(requireContext(), 2)
//
//        // تعيين المحمل للـ RecyclerView بعد تعيين المدير التخطيط
//        binding.rvImgCont.adapter = imgAdaptert
//
//        // بعد ذلك، قم بتعيين البيانات باستخدام ViewModel و LiveData
////
////            imgsViewModel.getImgsData(ID, startIndex).observe(viewLifecycleOwner) {
//        imgsViewModel.getImgsData(ID).observe(viewLifecycleOwner) {
//
//            imgAdaptert.submitData(viewLifecycleOwner.lifecycle, it)
//
//
////                favoriteImagesViewModel.getAllFav()
////                    .observe(viewLifecycleOwner) { favoriteImages ->
////                        val allImages: List<ImgsModel> = newImgs
////                        for (image in allImages) {
////                            val isFavorite =
////                                favoriteImages.any { it.id == image.id } // تحقق مما إذا كانت الصورة مفضلة
////                            image.is_fav = isFavorite // قم بتحديث حالة الصورة
////                        }
//        }
//        // اختيار دالة التعيين وضبط السياسة لـ RecyclerView
//        imgAdaptert.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
//// بعد تحديث البيانات
//        binding.rvImgCont.scrollToPosition(0)
//
//    }
//}

//four
//fun adapterOnClick2() {
//    pagingadapterLinRecy.onbtnClick = { it: ImgsModel, i: Int ->
//        val fav = FavoriteImage(it.id!!, it.ID_Type_id, it.new_img, it.image_url)
//
//        println("it.is_fav: ${it.is_fav}")
//        if (it.is_fav) {
//            it.is_fav = false
//            imgsffav.removeFavoriteImage(fav)
//
//            imgsffav.updateImages()
//            val snackbar = Snackbar.make(view!!, "تم الحذف", Snackbar.LENGTH_SHORT)
//            snackbar.show()
////                setUpViewPager()
//
//            pagingadapterLinRecy.notifyDataSetChanged()
//            println("it.is_fav: ${it.is_fav}")
//            currentItemId = i
//            if (currentItemId != -1) {
//                binding.rvImgCont.scrollToPosition(currentItemId)
//            }
//        } else {
//            it.is_fav = true
//            imgsffav.addFavoriteImage(fav)
//
//            imgsffav.updateImages()
//            val snackbar = Snackbar.make(view!!, "تم الاضافة", Snackbar.LENGTH_SHORT)
//            snackbar.show()
////                setUpViewPager()
//
//            pagingadapterLinRecy.notifyDataSetChanged()
//            println("it.is_fav: ${it.is_fav}")
//            currentItemId = i
//            if (currentItemId != -1) {
//                binding.rvImgCont.scrollToPosition(currentItemId)
//            }
//        }
//        // تحقق من قيمة it.is_fav
//        println("it.is_fav: ${it.is_fav}")
////            setUpViewPager()
//
//        pagingadapterLinRecy.notifyDataSetChanged()
//        println("it.is_fav: ${it.is_fav}")
//        if (currentItemId != -1) {
//            binding.rvImgCont.scrollToPosition(currentItemId)
//        }
//    }
//
//    pagingadapterLinRecy.onItemClick = { _, imgModel: ImgsModel, currentItemId->
//
////            if (imgsViewmodel.isConnected.value == true) {
////                val directions = FourFragmentDirections.actionFourFragmentToPagerFragmentImg(ID,currentItemId,imgModel.image_url)
////                findNavController().navigate(directions)
////            } else {
////                val snackbar = Snackbar.make(
////                    requireView(),
////                    "لا يوجد اتصال بالإنترنت",
////                    Snackbar.LENGTH_SHORT
////                )
////                snackbar.show()
////            }
//
//        val directions = FourFragmentDirections.actionFourFragmentToPagerFragmentImg(ID,currentItemId,imgModel.image_url)
//        findNavController().navigate(directions)
//
//    }
//
//}
//private fun setUpRvFo(){
//    if (isAdded) {
//        // تعيين المدير التخطيط (GridLayout) لـ RecyclerView أولاً
//        binding.rvImgCont.layoutManager = LinearLayoutManager(requireContext())
//
//        // تعيين المحمل للـ RecyclerView بعد تعيين المدير التخطيط
//        binding.rvImgCont.adapter = pagingadapterLinRecy
//
//        // بعد ذلك، قم بتعيين البيانات باستخدام ViewModel و LiveData
////
////            imgsViewModel.getImgsData(ID, startIndex).observe(viewLifecycleOwner) {
//        imgsViewmodel.getImgsData(ID).observe(viewLifecycleOwner) {
//
//            pagingadapterLinRecy.submitData(viewLifecycleOwner.lifecycle, it)
//
//            binding.rvImgCont.scrollToPosition(currentItemId)
//
//        }
//        // اختيار دالة التعيين وضبط السياسة لـ RecyclerView
//        pagingadapterLinRecy.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
//
//
//    }
//}

//favo
/*
*  private fun setUpRv() {
        if (isAdded) {
            lifecycleScope.launch(Dispatchers.Main) { // استخدام Dispatchers.Main لتحديث UI
                binding.recyclerFav.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.recyclerFav.adapter = pagingfavAdapter
                favoriteImagesViewModel.getAllFav().collect {
                    // تم استدعاء الدالة على الخيط الرئيسي
                    pagingfavAdapter.submitData(it)

                }
                pagingfavAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW

            }
        }
    }*/

/*fav lin
*
*  private fun setUpRv() {
        if (isAdded) {
            lifecycleScope.launch(Dispatchers.Main) { // استخدام Dispatchers.Main لتحديث UI
                binding.recyclerFav.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerFav.adapter = favPagingAdapterImageLinear
                favoriteImagesViewModel.getAllFav().collect {
                    // تم استدعاء الدالة على الخيط الرئيسي
                    favPagingAdapterImageLinear.submitData(it)
                    if (currentItemId != -1) {
                        binding.recyclerFav.scrollToPosition(currentItemId)
                    }
                }
                favPagingAdapterImageLinear.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
                if (currentItemId != -1) {
                    binding.recyclerFav.scrollToPosition(currentItemId)
                }
            }
        }
    }



    private fun adapterOnClick() {

        favPagingAdapterImageLinear.onbtnclick = {
            it.is_fav = false
            imgsffav.updateImages()
            imgsffav.removeFavoriteImage(FavoriteImage(it.id!!, it.ID_Type_id, it.new_img, it.image_url))

            val snackbar = Snackbar.make(view!!, "تم الحذف", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }

        favPagingAdapterImageLinear.onItemClick={_, favimage: FavoriteImage, currentItemId ->
            val directions = FavFragmentLinRecyDirections.actionFavFragmentLinRecyToFavoritePagerFrag(ID, currentItemId,favimage.image_url)
            findNavController().navigate(directions)
        }

    }
*/