package com.casecode.pos.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.casecode.pos.R
import com.casecode.pos.base.PositiveDialogListener
import com.casecode.pos.databinding.ActivityMainBinding
import com.casecode.pos.ui.signIn.SignInActivity
import com.casecode.pos.ui.signout.SignOutDialog
import com.casecode.pos.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PositiveDialogListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val mainViewModel: MainViewModel by viewModels()


    @Inject
    lateinit var auth: FirebaseAuth

    // Keep track of the visibility of the menu item
   // private var isMenuItemVisible = true
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        setupNavigationDrawer()
    }

    private fun setupNavigationDrawer() {

        setupToolbar()
        setupMenu()
        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.nav_statistics,
                R.id.nav_pos,
                R.id.nav_invoices,
                R.id.nav_items,
              //  R.id.nav_code_scanner,
                R.id.nav_users,
                R.id.nav_setting

            ), binding.drawerLayout
        )

        binding.appBarMain.toolbar.setupWithNavController(
            navController, appBarConfiguration
        )

        binding.navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_sign_out -> {

                    // Show the dialog when needed, passing the text you want to display in the dialog
                    val dialog = SignOutDialog()
                    dialog.show(supportFragmentManager, "SignOut")

                    false
                }

                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    binding.drawerLayout.closeDrawers()
                    true
                }

            }
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
    }
    override fun onSupportNavigateUp() = navController.navigateUp(appBarConfiguration)

    private fun setupMenu() {
        val menuProvider = object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                val menuItem: MenuItem? = menu.findItem(R.id.action_main_profile)

                val actionView = menuItem?.actionView
                val photoImageView = actionView?.findViewById<ImageView>(R.id.menu_item_photo)
                val photoUrl = auth.currentUser?.photoUrl.toString()
                photoImageView?.load(photoUrl)
                photoImageView?.setOnClickListener {
                    navController.navigate(R.id.nav_profile)
                }

            }

            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater,
            ) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.nav_sign_out -> {

                        // Show the dialog when needed, passing the text you want to display in the dialog
                        val dialog = SignOutDialog()
                        dialog.show(supportFragmentManager, "SignOut")
                        false
                    }
                    else -> {
                        NavigationUI.onNavDestinationSelected(menuItem, navController)
                        binding.drawerLayout.closeDrawers()
                        true
                    }

                }
            }

        }
            addMenuProvider(menuProvider, this, Lifecycle.State.RESUMED)
    }

    override fun onDialogPositiveClick() {
        mainViewModel.signOut()
        // Redirect the user to the login screen or perform any other necessary actions
        val intent = Intent(this@MainActivity, SignInActivity::class.java)
        startActivity(intent)
        // Close the current activity
        finish()
    }
}