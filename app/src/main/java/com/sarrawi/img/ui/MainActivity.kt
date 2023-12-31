package com.sarrawi.img.ui

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.appsearch.SetSchemaRequest.READ_EXTERNAL_STORAGE
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sarrawi.img.R
import com.sarrawi.img.databinding.ActivityMainBinding
import com.sarrawi.img.db.viewModel.ImgTypes_ViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var navController: NavController
    var fragment = 1
    private val imgtypesViewmodel:ImgTypes_ViewModel by lazy {
        ViewModelProvider(this,ImgTypes_ViewModel.ImgTypesViewModelFactory(this.application))[imgtypesViewmodel::class.java]
    }
    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1 // تعريف الثابت هنا
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 2 // تعريف الثابت هنا


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.title = ""
        bottomNav = findViewById(R.id.bottomNav)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration =
            AppBarConfiguration(setOf(
                R.id.SecondFragment,
                R.id.favoriteFragmentRecy,
                R.id.thirdFragment,
                R.id.fourFragment,
                R.id.splashScreenFragment,
                R.id.favFragmentLinRecy,
                R.id.pagerFragmentImg,
                R.id.FirstFragment,
                R.id.newImgFragment,
                R.id.favoritePagerFrag
            ))
        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.splashScreenFragment ||destination.id == R.id.FirstFragment || destination.id == R.id.thirdFragment ||
                destination.id == R.id.fourFragment || destination.id == R.id.favFragmentLinRecy
                || destination.id == R.id.pagerFragmentImg || destination.id == R.id.favoritePagerFrag||destination.id == R.id.newImgFragment) {

                bottomNav.visibility = View.GONE
            } else {

                bottomNav.visibility = View.VISIBLE
            }


        }









    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return true
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // تم منح الإذن
//                val dir = File(Environment.getExternalStorageDirectory(), "MyPics")
//                if (!dir.exists()) {
//                    dir.mkdirs()
//                }
//            } else {
//                // تم رفض الإذن، يمكنك اتخاذ إجراء مناسب هنا
//            }
//        }
//
//    }

    private fun createDirectory() {
        val dir = File(Environment.getExternalStorageDirectory(), "MyPics")
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // تم منح الإذن، قم بإنشاء المجلد
                    createDirectory()
                }
            }
        }
    }

}