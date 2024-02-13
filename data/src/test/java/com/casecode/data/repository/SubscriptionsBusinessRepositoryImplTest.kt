package com.casecode.data.repository

import com.casecode.data.mapper.asSubscriptionBusiness
import com.casecode.data.mapper.asSubscriptionRequest
import com.casecode.domain.model.users.SubscriptionBusiness
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.casecode.pos.data.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class SubscriptionsBusinessRepositoryImplTest {

    private var firestore: FirebaseFirestore = mockk<FirebaseFirestore>()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope: TestScope = TestScope(testDispatcher)

    /**
     * A test  SubscriptionsRepository implementation that can be used for testing.
     */
    private lateinit var subscriptionRepository: SubscriptionsBusinessRepositoryImpl
    private val uid = "test"

    // Capture the success and failure listeners
    private val successListenerSlot = slot<OnSuccessListener<Void>>()
    private val failureListenerSlot = slot<OnFailureListener>()
    private val onSuccessListenerSlot = slot<OnSuccessListener<QuerySnapshot>>()

    @Before
    fun setup() {
        subscriptionRepository =
            SubscriptionsBusinessRepositoryImpl(firestore, testDispatcher)

    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun setSubscriptionBusiness_shouldReturnAddBusinessSuccess() = testScope.runTest {
        val subscription = SubscriptionBusiness(
            "basic",
            200, 30, listOf("admin", "sales")
        )

        every {
            firestore.collection(USERS_COLLECTION_PATH).document(uid)
                .update(subscription.asSubscriptionRequest())
                .addOnSuccessListener(capture(successListenerSlot))
                .addOnFailureListener(capture(failureListenerSlot))

        } answers {
            successListenerSlot.captured.onSuccess(null)
            mockk<Task<Void>>()
        }
        // Act
        val resultAddSubscription =
            subscriptionRepository.setSubscriptionBusiness(subscription, uid)

        // Assert
        val expectedResult = Resource.success(true)
        assertThat(resultAddSubscription, `is`(expectedResult))
    }

    @Test
    fun setSubscriptionBusiness_shouldReturnErrorUnKnownHostException() = testScope.runTest {
        val subscription = SubscriptionBusiness(
            "basic",
            200, 30, listOf("admin", "sales")
        )
        every {
            firestore.collection(USERS_COLLECTION_PATH).document(uid)
                .update(subscription.asSubscriptionRequest())
                .addOnSuccessListener(capture(successListenerSlot))
                .addOnFailureListener(capture(failureListenerSlot))

        } answers {
            throw UnknownHostException()
        }
        // Act
        val resultAddSubscription =
            subscriptionRepository.setSubscriptionBusiness(subscription, uid)

        // Assert
        val expectedResult = Resource.error<Boolean>(R.string.add_subscription_business_network)
        assertThat(resultAddSubscription, `is`(expectedResult))
    }
    @Test
    fun setSubscriptionBusiness_shouldReturnException() = testScope.runTest {
        val subscription = SubscriptionBusiness(
            "basic",
            200, 30, listOf("admin", "sales")
        )
        every {
            firestore.collection(USERS_COLLECTION_PATH).document(uid)
                .update(subscription.asSubscriptionRequest())
                .addOnSuccessListener(capture(successListenerSlot))
                .addOnFailureListener(capture(failureListenerSlot))

        } answers {
            throw Exception()
        }
        // Act
        val resultAddSubscription =
            subscriptionRepository.setSubscriptionBusiness(subscription, uid)

        // Assert
        val expectedResult = Resource.error<Boolean>(R.string.add_subscription_business_failure)
        assertThat(resultAddSubscription, `is`(expectedResult))
    }
}