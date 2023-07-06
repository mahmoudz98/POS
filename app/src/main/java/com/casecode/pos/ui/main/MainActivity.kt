package com.casecode.pos.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.casecode.pos.R
import com.casecode.pos.databinding.ActivityMainBinding
import com.casecode.pos.ui.signout.SignOutDialog

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isMenuItemVisible = true // Keep track of the visibility of the menu item

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment).navController

    }
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        setupNavigationDrawer()
    }

    private fun setupNavigationDrawer() {

        setupToolbar()

        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.nav_statistics,
                R.id.nav_pos,
                R.id.nav_invoices,
                R.id.nav_products,
                R.id.nav_code_scanner,
                R.id.nav_users,
                R.id.nav_setting
                /*
                                R.id.nav_sign_out
                */
            ), binding.drawerLayout
        )


        binding.appBarMain.toolbar.setupWithNavController(
            navController, appBarConfiguration
        )
        binding.navView.setupWithNavController(navController)


        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_sign_out -> {
                    val dialog = SignOutDialog()
                    dialog.show(supportFragmentManager, "saf")

                }

                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    binding.drawerLayout.closeDrawers()


                }

            }
            true
        }

        // Set the checked item in the navigation drawer based on the current screen
        val currentDestination = navController.currentDestination?.id
        if (currentDestination != null) {
            binding.navView.setCheckedItem(currentDestination)
        }
    }

    private fun setupToolbar() {
        binding.appBarMain.toolbar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_main_profile -> {

                    navController.navigate(R.id.nav_profile)
                    true
                }

                else -> super.onOptionsItemSelected(menuItem)

            }
        }
        setupOnBackDestination()

    }

    private fun setupOnBackDestination() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            isMenuItemVisible = when (destination.id) {
                R.id.nav_profile -> {
                    false
                }

                R.id.nav_sign_out -> {

                    true
                }
                // }
                else -> {
                    true
                }
            }
            invalidateOptionsMenu() // Update the menu to reflect the new state

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem: MenuItem? = menu?.findItem(R.id.action_main_profile)
        menuItem?.isVisible = isMenuItemVisible
        return super.onPrepareOptionsMenu(menu)

    }


}