package com.casecode.pos.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.casecode.pos.R
import com.casecode.pos.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null
    private var isMenuItemVisible = true // Keep track of the visibility of the menu item


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        setSupportActionBar(binding.appBarMain.toolbar)
        setupToolbar()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_statistics,
                R.id.nav_pos,
                R.id.nav_invoices,
                R.id.nav_products,
                R.id.nav_code_scanner,
                R.id.nav_users,
                R.id.nav_setting
            ), drawerLayout
        )
        binding.appBarMain.toolbar.setupWithNavController(navController!!, appBarConfiguration)
        navView.setupWithNavController(navController!!)


        // Set the checked item in the navigation drawer based on the current screen
        val currentDestination = navController?.currentDestination?.id
        if (currentDestination != null) {
            navView.setCheckedItem(currentDestination)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val  menuItem: MenuItem? = menu?.findItem(R.id.action_main_profile)
        menuItem?.isVisible  = isMenuItemVisible
        return super.onPrepareOptionsMenu(menu)

    }

    private fun setupToolbar() {
        binding.appBarMain.toolbar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_main_profile -> {

                    navController?.navigate(R.id.nav_profile)
                    true
                }

                else -> super.onOptionsItemSelected(menuItem)

            }
        }
        setupOnBackDestination()

    }

    private fun setupOnBackDestination(){
        navController?.addOnDestinationChangedListener{_, destination, _ ->
            isMenuItemVisible = when(destination.id){
                R.id.nav_profile -> {
                    false
                }

                else -> {
                    true
                }
            }
            invalidateOptionsMenu() // Update the menu to reflect the new state

        }
    }
    private fun setupOnBackPressedCallback(){
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isMenuItemVisible = true // Hide the menu item
                // Set isMenuItemVisible to true to show the menu item again
                invalidateOptionsMenu()
                isEnabled = false // Disable this callback to allow the default back button behavior
                // onBackPressed() // Call the default back button behavior

            }
        }

        // Add the OnBackPressedCallback to the OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, callback)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}