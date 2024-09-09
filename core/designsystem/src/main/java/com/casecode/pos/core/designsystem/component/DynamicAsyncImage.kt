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
package com.casecode.pos.core.designsystem.component

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter.State.Error
import coil.compose.AsyncImagePainter.State.Loading
import coil.compose.AsyncImagePainter.State.Success
import coil.compose.rememberAsyncImagePainter
import com.casecode.pos.core.designsystem.R
import com.casecode.pos.core.designsystem.theme.LocalTintTheme

/**
 * A wrapper around [AsyncImage] which determines the colorFilter based on the theme
 */
@Composable
fun DynamicAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter = painterResource(R.drawable.core_designsystem_ic_placeholder),
) {
    val iconTint = LocalTintTheme.current.iconTint
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val imageLoader =
        rememberAsyncImagePainter(
            model = imageUrl,
            onState = { state ->
                isLoading = state is Loading
                isError = state is Error
            },
        )
    val isLocalInspection = LocalInspectionMode.current
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading && !isLocalInspection) {
            // Display a progress bar while loading
            CircularProgressIndicator(
                modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(64.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        Image(
            contentScale = ContentScale.Crop,
            painter = if (isError.not() && !isLocalInspection) imageLoader else placeholder,
            contentDescription = contentDescription,
            colorFilter = if (iconTint != Unspecified) ColorFilter.tint(iconTint) else null,
        )
    }
}

@Composable
fun DynamicAsyncQrCodeImage(
    data: Bitmap?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter = painterResource(R.drawable.core_designsystem_ic_placeholder),
) {
    val iconTint = LocalTintTheme.current.iconTint
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val imageLoader =
        rememberAsyncImagePainter(
            model = data,
            onState = { state ->
                isLoading = state is Loading
                isError = state is Error
            },
        )
    val isLocalInspection = LocalInspectionMode.current
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading && !isLocalInspection) {
            // Display a progress bar while loading
            CircularProgressIndicator(
                modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(64.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        Image(
            contentScale = ContentScale.Crop,
            painter = if (isError.not() && !isLocalInspection) imageLoader else placeholder,
            contentDescription = contentDescription,
            colorFilter = if (iconTint != Unspecified) ColorFilter.tint(iconTint) else null,
        )
    }
}

/**
 * A composable function that displays an image asynchronously with a placeholder and a success callback.
 *
 * @param imageUrl The URI of the image to load.
 * @param contentDescription The content description of the image.
 * @param modifier The modifier to be applied to the composable.
 * @param placeholder The painter to display while the image is loading.
 * @param onSuccess A callback function that is invoked when the image is loaded successfully.
 *                 The callback receives the loaded Bitmap.
 */
@Composable
fun DynamicAsyncImage(
    imageUrl: Uri,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter = painterResource(R.drawable.core_designsystem_ic_placeholder),
    onSuccess: (Bitmap?) -> Unit,
) {
    val iconTint = LocalTintTheme.current.iconTint
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val imageLoader =
        rememberAsyncImagePainter(
            model = imageUrl,
            onState = { state ->
                isLoading = state is Loading
                isError = state is Error
                if (state is Success) {
                    onSuccess(state.result.drawable.toBitmap())
                }
            },
        )

    val isLocalInspection = LocalInspectionMode.current
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading && !isLocalInspection) {
            // Display a progress bar while loading
            CircularProgressIndicator(
                modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(64.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        Image(
            contentScale = ContentScale.Crop,
            painter = if (isError.not() && !isLocalInspection) imageLoader else placeholder,
            contentDescription = contentDescription,
            colorFilter = if (iconTint != Unspecified) ColorFilter.tint(iconTint) else null,
        )
    }
}

@Composable
fun DynamicAsyncImage(
    imageUrl: Uri?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: Painter = painterResource(R.drawable.core_designsystem_ic_placeholder),
) {
    val iconTint = LocalTintTheme.current.iconTint
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val imageLoader =
        rememberAsyncImagePainter(
            model = imageUrl,
            onState = { state ->
                isLoading = state is Loading
                isError = state is Error
            },
        )

    val isLocalInspection = LocalInspectionMode.current
    Box(
        modifier = modifier,
    ) {
        if (isLoading && !isLocalInspection) {
            // Display a progress bar while loading
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        Image(
            contentScale = ContentScale.Crop,
            painter = if (isError.not() && !isLocalInspection) imageLoader else placeholder,
            contentDescription = contentDescription,
            modifier = modifier,
            colorFilter = if (iconTint != Unspecified) ColorFilter.tint(iconTint) else null,
        )
    }
}