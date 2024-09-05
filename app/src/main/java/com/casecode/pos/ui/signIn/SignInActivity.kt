/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.ui.signIn

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.model.data.LoginStateResult.Loading
import com.casecode.pos.core.ui.moveToMainActivity
import com.casecode.pos.core.ui.moveToStepperActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInActivity : ComponentActivity() {
    private val authViewModel: SignInActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        var authUiState: LoginStateResult by mutableStateOf(Loading)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { authViewModel.checkIfRegistrationAndBusinessCompleted() }
                launch { authViewModel.loginStateResult.collect { authUiState = it } }
            }
        }
        splashScreen.setKeepOnScreenCondition {
            authUiState == Loading
        }
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val zoomX =
                ObjectAnimator.ofFloat(
                    splashScreenView.iconView,
                    View.SCALE_X,
                    0.4f,
                    0.0f,
                )
            zoomX.interpolator = OvershootInterpolator()
            zoomX.duration = 400L
            zoomX.doOnEnd { splashScreenView.remove() }

            val zoomY =
                ObjectAnimator.ofFloat(
                    splashScreenView.iconView,
                    View.SCALE_Y,
                    0.4f,
                    0.0f,
                )
            zoomY.interpolator = OvershootInterpolator()
            zoomY.duration = 400L
            zoomY.doOnEnd { splashScreenView.remove() }
            zoomX.start()
            zoomY.start()
        }

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            DisposableEffect(authUiState) {
                when (authUiState) {
                    is LoginStateResult.NotCompleteBusiness -> {
                        moveToStepperActivity(this@SignInActivity)
                    }

                    is LoginStateResult.EmployeeLogin, is LoginStateResult.SuccessLoginAdmin -> {
                        moveToMainActivity(this@SignInActivity)
                    }

                    else -> {}
                }
                onDispose {}
            }

            POSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    SignInScreen(
                        authViewModel,
                    )
                }
            }
        }
    }
}