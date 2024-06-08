package com.casecode.pos.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import com.casecode.data.utils.NetworkMonitor
import com.casecode.pos.design.theme.POSTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor
    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val darkTheme = isSystemInDarkTheme()

            // Update the edge to edge configuration to match the theme
            // This is the same parameters as the default enableEdgeToEdge call, but we manually
            // resolve whether or not to show dark theme using uiState, since it can be different
            // than the configuration's dark theme value based on the user preference.
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim,
                        darkScrim,
                    ) { darkTheme },
                )
                onDispose {}
            }
            val appState = rememberMainAppState(
                networkMonitor = networkMonitor,
            )

            CompositionLocalProvider {
                POSTheme {
                    MainScreen(appState = appState)
                }
            }

        }
    }


    /*   private fun setupNavigationDrawer() {

           setupToolbar()
           setupMenu()

           appBarConfiguration = AppBarConfiguration(
               topLevelDestinationIds = setOf(
                   R.id.nav_reports,
                   R.id.nav_pos,
                   R.id.nav_invoices,
                   R.id.nav_items,
                   //  R.id.nav_code_scanner,
                   R.id.nav_users,
                   R.id.nav_setting,

                   ),
               binding.drawerLayout,
           )

           binding.appBarMain.toolbar.setupWithNavController(
               navController, appBarConfiguration,
           )

           binding.navView.setupWithNavController(navController)

           binding.navView.setNavigationItemSelectedListener { menuItem ->
               when (menuItem.itemId) {
                   R.id.nav_sign_out -> {
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

       override fun onDestroy() {
           super.onDestroy()
           _binding = null
       }*/

    override fun onStop() {
        super.onStop()
        Timber.e("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("onDestroy")
    }


    /**
     * The default light scrim, as defined by androidx and the platform:
     */
    private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

    /**
     * The default dark scrim, as defined by androidx and the platform:
     */
    private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)


}