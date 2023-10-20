package com.sarrawi.img

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sarrawi.img.databinding.ActivityMainBinding
import com.sarrawi.img.db.viewModel.ImgTypes_ViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var navController: NavController
    var fragment = 1
    private val imgtypesViewmodel:ImgTypes_ViewModel by lazy {
        ViewModelProvider(this,ImgTypes_ViewModel.ImgTypesViewModelFactory(this.application))[imgtypesViewmodel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        bottomNav = findViewById(R.id.bottomNav)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.SecondFragment, R.id.favoriteFragmentRecy,R.id.thirdFragment,R.id.fourFragment,R.id.splashScreenFragment,R.id.favFragmentLinRecy))
//        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.splashScreenFragment || destination.id == R.id.thirdFragment || destination.id == R.id.fourFragment|| destination.id == R.id.favFragmentLinRecy) {

                bottomNav.visibility = View.GONE
            } else {

                bottomNav.visibility = View.VISIBLE
            }

        }


//s

//        setupActionBarWithNavController(navController,appBarConfiguration)



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
}