package com.casecode.pos.ui.stepper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.casecode.pos.core.designsystem.theme.POSTheme
import com.casecode.pos.feature.stepper.StepperScreen
import com.casecode.pos.ui.signIn.SignInActivity
import com.casecode.pos.utils.moveToMainActivity
import com.casecode.pos.utils.moveToSignInActivity
import com.casecode.pos.utils.moveToStepperActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StepperActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            POSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                )
                {
                    StepperScreen(
                        onMoveToMainActivity = {
                            moveToMainActivity(this@StepperActivity)
                        },
                        onMoveToSignInActivity = {
                            moveToSignInActivity(this@StepperActivity)
                        },
                    )
                }
            }
        }
    }
}