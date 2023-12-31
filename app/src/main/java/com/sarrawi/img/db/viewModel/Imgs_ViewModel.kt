package com.sarrawi.img.db.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sarrawi.img.Api.ApiService
import com.sarrawi.img.db.repository.ImgRepository
import com.sarrawi.img.model.ImgsModel
import com.sarrawi.img.model.ImgsRespone
import com.sarrawi.img.model.results
import com.sarrawi.img.utils.DataStatus
import com.sarrawi.img.utils.NetworkConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class Imgs_ViewModel(private val context: Context,  private val imgsRepo:ImgRepository ): ViewModel() {


    private val retrofitService = ApiService.provideRetrofitInstance()

    private val _response = MutableLiveData<List<ImgsModel>>()
    private val _responseWithTitle= MutableLiveData<List<ImgsModel>>()
    private val _isLoading = MutableLiveData<Boolean>()

////
    private val _images = MutableLiveData<List<ImgsModel>>()
    var ID_Type_id: Int=-1


    val images: LiveData<List<ImgsModel>>
        get() = _images
    ///////


    val isLoading: LiveData<Boolean>
        get() = _isLoading


////////////////////////////////////
   // check Network with Live data
private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean>
        get() = _isConnected


    fun checkNetworkConnection(applicationContext: Context) {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observeForever { isConnected ->
            _isConnected.value = isConnected
        }
    }





    fun getAllImgsNewViewModel():LiveData<List<ImgsModel>> {
        _isLoading.postValue(true) // عرض ProgressBar قبل بدء التحميل

        val _response = MutableLiveData<List<ImgsModel>>()

        viewModelScope.launch {
            try{
                val response = imgsRepo.getImgs_Repo_New()

                if (response.isSuccessful) {
                    val results = response.body()?.results
                    _response.postValue(results)
                    Log.i("TestRoom", "getAllImgs: posts $results")
//                    imgsRepo.insert_imgs_repo(response.body()?.results)
                } else {
                    Log.i("TestRoom", "getAllImgs: data corrupted")
                    Log.d("tag", "getAll Error: ${response.code()}")
                    Log.d("tag", "getAll: ${response.body()}")
                }
            }
            catch (e: Exception)
            {
                Log.e("TestRoom", "getAllImgs: Error: ${e.message}")
            }
            finally {
                _isLoading.postValue(false) // إخفاء ProgressBar بعد انتهاء التحميل
            }
        }
        return _response
    }


    fun getAllImgsViewModel(ID_Type_id: Int): LiveData<List<ImgsModel>> {
        _isLoading.postValue(true) // عرض ProgressBar قبل بدء التحميل

        val _response = MutableLiveData<List<ImgsModel>>()

        viewModelScope.launch {
            try {
                val response = imgsRepo.getImgs_Repo(ID_Type_id)

                if (response.isSuccessful) {
                    val results = response.body()?.results
                    _response.postValue(results)
                    Log.i("TestRoom", "getAllImgs: posts $results")
//                    imgsRepo.insert_imgs_repo(response.body()?.results)
                } else {
                    Log.i("TestRoom", "getAllImgs: data corrupted")
                    Log.d("tag", "getAll Error: ${response.code()}")
                    Log.d("tag", "getAll: ${response.body()}")
                }
            } catch (e: Exception) {
                Log.e("TestRoom", "getAllImgs: Error: ${e.message}")
            } finally {
                _isLoading.postValue(false) // إخفاء ProgressBar بعد انتهاء التحميل
            }
        }

        return _response
    }

//    fun getImgsData(ID_Type_id: Int, page: Int): LiveData<List<ImgsModel>> {
    fun getImgsData(ID_Type_id: Int): LiveData<PagingData<ImgsModel>> {
        //val _response = MutableLiveData<List<ImgsModel>>()
        var _response = MutableLiveData<PagingData<ImgsModel>>()

        // قم بإجراء الاستدعاء إلى Retrofit باستخدام viewModelScope.launch
        viewModelScope.launch {
            try {
                val response = imgsRepo.getImgsData(ID_Type_id)

//                if (response.isSuccessful) {
//                    val results = response.body()?.results
//                    _response.postValue(results)
//                    Log.i("TestRoom", "getAllImgs: posts $results")
//                    //                    imgsRepo.insert_imgs_repo(response.body()?.results)
//
//                } else {
//                    Log.i("TestRoom", "getAllImgs: data corrupted")
//                    Log.d("tag", "getAll Error: ${response.code()}")
//                    Log.d("tag", "getAll: ${response.body()}")
//                }

                //_response.postValue(response.value)
                _response = response as MutableLiveData<PagingData<ImgsModel>>
            } catch (e: Exception) {
                Log.e("TestRoom", "getAllImgs: Error: ${e.message}")
            }
        }

        return _response
    }

    fun getsnippets(ID_Type_id: Int): LiveData<PagingData<results>> {
        //val _response = MutableLiveData<List<ImgsModel>>()
        var _response = MutableLiveData<PagingData<results>>()

        // قم بإجراء الاستدعاء إلى Retrofit باستخدام viewModelScope.launch
        viewModelScope.launch {
            try {
                val response = imgsRepo.getsnippets(ID_Type_id)


//                if (response.isSuccessful) {
//                    val results = response.body()?.results
//                    _response.postValue(results)
//                    Log.i("TestRoom", "getAllImgs: posts $results")
//                    //                    imgsRepo.insert_imgs_repo(response.body()?.results)
//
//                } else {
//                    Log.i("TestRoom", "getAllImgs: data corrupted")
//                    Log.d("tag", "getAll Error: ${response.code()}")
//                    Log.d("tag", "getAll: ${response.body()}")
//                }

                //_response.postValue(response.value)
                _response = response as MutableLiveData<PagingData<results>>
                Log.e("Test", "getAll")
            } catch (e: Exception) {
                Log.e("TestRoom", "getAllImgs: Error: ${e.message}")
            }
        }

        return _response
    }
    fun getsnippetss(ID_Type_id: Int): LiveData<PagingData<results>> {
        return imgsRepo.getsnippetss(ID_Type_id)
    }


//    fun getImgsData(ID_Type_id: Int, page: Int): LiveData<List<ImgsModel>> {
//        val _response = MutableLiveData<List<ImgsModel>>()
//        viewModelScope.launch {
//            try {
//                val response = imgsRepo.getImgsData(ID_Type_id, page)
//                if (response.isSuccessful) {
//                    val results = response.body()?.results
//                    _response.postValue(results)
//                    Log.i("TestRoom", "getAllImgs: posts $results")
////                    imgsRepo.insert_imgs_repo(response.body()?.results)
//                } else {
//                    Log.i("TestRoom", "getAllImgs: data corrupted")
//                    Log.d("tag", "getAll Error: ${response.code()}")
//                    Log.d("tag", "getAll: ${response.body()}")
//                }
//            } catch (e: Exception) {
//                Log.e("TestRoom", "getAllImgs: Error: ${e.message}")
//            }
//        }
//        return _response
//    }


    private val _imgList = MutableLiveData<DataStatus<List<ImgsModel>>>()
    val imgList : LiveData<DataStatus<List<ImgsModel>>>
    get() = _imgList

    fun getImgs_viewmodel(ID_Type_id: Int) = viewModelScope.launch {
        imgsRepo.getImgs_Repo2(ID_Type_id).collect{
            _imgList.value=it
        }
    }

}