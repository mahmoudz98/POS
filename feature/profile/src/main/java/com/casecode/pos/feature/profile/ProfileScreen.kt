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
package com.casecode.pos.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.casecode.pos.core.designsystem.component.DynamicAsyncImage
import com.casecode.pos.core.designsystem.component.PosLoadingWheel
import com.casecode.pos.core.designsystem.component.PosTopAppBar
import com.casecode.pos.core.designsystem.icon.PosIcons
import com.casecode.pos.core.model.data.users.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun ProfileRoute(profileViewModel: ProfileViewModel = hiltViewModel(), onBackClick: () -> Unit) {
    ProfileScreen(profileViewModel = profileViewModel, onBackClick = onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val snackState = remember { SnackbarHostState() }

    SnackbarHost(
        hostState = snackState,
        Modifier
            .padding(8.dp)
            .zIndex(1f),
    )
    uiState.userMessage?.let { message ->
        val snackbarText = stringResource(message)
        LaunchedEffect(snackState, uiState, message, snackbarText) {
            snackState.showSnackbar(snackbarText)
            profileViewModel.onSnackbarMessageShown()
        }
    }
    val pagerState =
        rememberPagerState(
            pageCount = {
                3
            },
        )
    Column(
        modifier =
        modifier
            .padding(8.dp)
            .fillMaxSize(),
    ) {
        PosTopAppBar(
            modifier = Modifier,
            titleRes = R.string.feature_profile_title,
            navigationIcon = PosIcons.ArrowBack,
            navigationIconContentDescription =
            stringResource(
                id = com.casecode.pos.core.ui.R.string.core_ui_dialog_cancel_button_text,
            ),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),

            onNavigationClick = { onBackClick() },
        )
        if (uiState.isLoading) {
            PosLoadingWheel(
                modifier =
                modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                contentDesc = "LoadingProfile",
            )
        } else {
            ProfileHeader(firebaseUser = uiState.currentUser)
            Spacer(modifier = Modifier.height(8.dp))
            val coroutineScope = rememberCoroutineScope()

            SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
                ProfileTab(
                    title = stringResource(
                        com.casecode.pos.core.ui.R.string.core_ui_menu_business_info_title,
                    ),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                )
                ProfileTab(
                    title = stringResource(
                        com.casecode.pos.core.ui.R.string.core_ui_menu_branches_title,
                    ),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                )
                ProfileTab(
                    title = stringResource(
                        com.casecode.pos.core.ui.R.string.core_ui_subscription_plan_title,
                    ),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    },
                )
            }
        }
        // Pager
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
        ) { page ->
            when (page) {
                0 -> BusinessInfoTab(uiState.business)
                1 -> BranchesTab()
                /*
                TODO:
                                2 -> SubscriptionTab()
                 */
            }
        }
    }
}

@Composable
fun ProfileHeader(firebaseUser: FirebaseUser?) {
    Column(
        modifier =
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // User image/avatar can be added here
        Spacer(modifier = Modifier.height(8.dp))
        DynamicAsyncImage(
            imageUrl = firebaseUser?.photoUrl,
            // placeholder = painterResource(id = R.drawable.ic_google),
            contentDescription = null,
            modifier =
            Modifier
                .size(64.dp)
                .clip(CircleShape),
        )
        Text(text = firebaseUser?.displayName ?: "")
        Text(text = firebaseUser?.email ?: "")
    }
}

@Composable
fun ProfileTab(title: String, onClick: () -> Unit) {
    Tab(
        selected = false,
        onClick = onClick,
        text = { Text(text = title) },
    )
}