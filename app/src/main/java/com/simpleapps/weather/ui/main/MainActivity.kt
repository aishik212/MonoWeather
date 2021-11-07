package com.simpleapps.weather.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.navigation.NavigationView
import com.simpleapps.cacheutils.CacheUtils
import com.simpleapps.weather.R
import com.simpleapps.weather.core.BaseActivity
import com.simpleapps.weather.databinding.ActivityMainBinding
import com.simpleapps.weather.utils.extensions.hide
import com.simpleapps.weather.utils.extensions.show
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class MainActivity :
    BaseActivity<MainActivityViewModel, ActivityMainBinding>(MainActivityViewModel::class.java),
    HasAndroidInjector, NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun initViewModel(viewModel: MainActivityViewModel) {
        binding.viewModel = viewModel
    }

    override fun getLayoutRes() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setTransparentStatusBar()
        setupNavigation()
        val cache = CacheUtils.initCache(applicationContext)
        val isNight = cache.getBoolean("night_mode", false)
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        binding.darkImv.setOnClickListener {
            val cache = CacheUtils.initCache(applicationContext)
            val isNight = cache.getBoolean("night_mode", false)
            if (isNight) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                cache.edit().putBoolean("night_mode", false).apply()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                cache.edit().putBoolean("night_mode", true).apply()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun setTransparentStatusBar() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemSearch -> {
                findNavController(R.id.container_fragment).navigate(R.id.searchFragment)
                true
            }
            else -> false
        }
    }

    private fun setupNavigation() {
/*        val appBarConfig = AppBarConfiguration(
                setOf(R.id.dashboardFragment),
                binding.drawerLayout
        )

        binding.toolbar.overflowIcon = getDrawable(R.drawable.ic_menu)
        binding.toolbar.navigationIcon?.setTint(Color.parseColor("#130e51"))
        setupWithNavController(binding.toolbar, navController, appBarConfig)
        setupWithNavController(binding.navigationView, navController)
        binding.navigationView.setNavigationItemSelectedListener(this)*/
        val navController = findNavController(R.id.container_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment -> {
                    binding.toolbar.hide()
                }
                R.id.dashboardFragment -> {
                    binding.toolbar.show()
                    binding.toolbar.navigationIcon = null
                }
                R.id.searchFragment -> {
                    binding.toolbar.hide()
                }
                else -> {
                    binding.toolbar.show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            findNavController(R.id.container_fragment),
            binding.drawerLayout
        )
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                binding.drawerLayout.openDrawer(GravityCompat.START)
            R.id.aboutApp -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                findNavController(R.id.container_fragment).navigate(R.id.githubDialog)
            }
        }
        return item.onNavDestinationSelected(findNavController(R.id.container_fragment)) || super.onOptionsItemSelected(
            item
        )
    }
}
