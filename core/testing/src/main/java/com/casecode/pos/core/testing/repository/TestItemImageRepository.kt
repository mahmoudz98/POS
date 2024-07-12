package com.casecode.pos.core.testing.repository

import android.graphics.Bitmap
import com.casecode.pos.core.data.R
import com.casecode.pos.core.domain.repository.DeleteImage
import com.casecode.pos.core.domain.repository.ItemImageRepository
import com.casecode.pos.core.domain.repository.ReplaceImage
import com.casecode.pos.core.domain.repository.UploadImage
import com.casecode.pos.core.testing.base.BaseTestRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestItemImageRepository @Inject constructor() : ItemImageRepository, BaseTestRepository() {
    override suspend fun uploadImage(bitmap: Bitmap, imageName: String): UploadImage {
        return if (shouldReturnError) UploadImage.error(R.string.download_url_failure)
        else UploadImage.success("imageTest.com")

    }

    override suspend fun replaceImage(bitmap: Bitmap, imageUrl: String): ReplaceImage {
        return if (shouldReturnError) ReplaceImage.error(R.string.replace_image_failure)
        else ReplaceImage.success("imageTest.com")
    }

    override suspend fun deleteImage(imageUrl: String): DeleteImage {
        return if (shouldReturnError) UploadImage.error(R.string.delete_image_failure_generic)
        else DeleteImage.success(true)
    }

    override fun init() {}
}