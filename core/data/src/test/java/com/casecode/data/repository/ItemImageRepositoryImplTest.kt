package com.casecode.data.repository

import com.casecode.pos.core.data.repository.ItemImageRepositoryImpl
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.After
import org.junit.Before


class ItemImageRepositoryImplTest {
    private val auth: FirebaseAuth = mockk<FirebaseAuth>()
    private val firebaseStorage: FirebaseStorage = mockk<FirebaseStorage>()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope: TestScope = TestScope(testDispatcher)

    // subject under test
    private lateinit var itemImageRepositoryImpl: ItemImageRepositoryImpl

    private val uid = "test"

    // Capture the success and failure listeners
    private val successListenerSlot = slot<OnSuccessListener<Void>>()
    private val failureListenerSlot = slot<OnFailureListener>()

    private lateinit var mockStorageReference: StorageReference


    @Before
    fun setup() {
        mockkConstructor(ItemImageRepositoryImpl::class)

        mockStorageReference = mockk()

        every { auth.currentUser?.uid } returns uid
/*
        itemImageRepositoryImpl = ItemImageRepositoryImpl(auth, firebaseStorage, testDispatcher)
*/

    }

    @After
    fun tearDown() {clearAllMocks() }



}