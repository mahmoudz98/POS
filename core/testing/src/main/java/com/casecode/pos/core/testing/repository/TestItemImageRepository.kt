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
package com.casecode.pos.core.testing.repository

import android.graphics.Bitmap
import com.casecode.pos.core.data.R
import com.casecode.pos.core.domain.repository.DeleteImage
import com.casecode.pos.core.domain.repository.ItemImageRepository
import com.casecode.pos.core.domain.repository.ReplaceImage
import com.casecode.pos.core.domain.repository.UploadImage
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.base.BaseTestRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestItemImageRepository
@Inject
constructor() :
    BaseTestRepository(),
    ItemImageRepository {
    override suspend fun uploadImage(bitmap: Bitmap, imageName: String): UploadImage = if (shouldReturnError) {
        Resource.Companion.error(R.string.core_data_download_url_failure)
    } else {
        Resource.Companion.success("imageTest.com")
    }

    override suspend fun replaceImage(bitmap: Bitmap, imageUrl: String): ReplaceImage = if (shouldReturnError) {
        Resource.Companion.error(R.string.core_data_replace_image_failure)
    } else {
        Resource.Companion.success("imageTest.com")
    }

    override suspend fun deleteImage(imageUrl: String): DeleteImage = if (shouldReturnError) {
        Resource.Companion.error(R.string.core_data_delete_image_failure_generic)
    } else {
        Resource.Companion.success(true)
    }

    override fun init() {}
}