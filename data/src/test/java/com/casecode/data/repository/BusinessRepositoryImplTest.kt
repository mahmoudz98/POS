package com.casecode.data.repository


import com.casecode.data.mapper.toBusinessRequest
import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Business
import com.casecode.domain.model.users.StoreType
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import io.mockk.coVerify
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test


/**
 * A JUnit test class for the [BusinessRepositoryImpl] class.
 * This class use [MockKExtension] that ensure mockk work with junit 5.
 *
 * Created by Mahmoud Abdalhafeez on 12/13/2023
 */
class BusinessRepositoryImplTest
{
   
   private var firestore: FirebaseFirestore = mockk<FirebaseFirestore>()
   
   private val testDispatcher = StandardTestDispatcher()
   private val testScope: TestScope = TestScope(testDispatcher)
   
   // subject under test
   private lateinit var businessRepository: BusinessRepositoryImpl
   
   private val uid = "test"
   
   // Capture the success and failure listeners
   private val successListenerSlot = slot<OnSuccessListener<Void>>()
   private val failureListenerSlot = slot<OnFailureListener>()
   
   @Before
   fun setup()
   {
      businessRepository =
         BusinessRepositoryImpl(firestore, testDispatcher)
      
   }
   
   @After
   fun tearDown()
   {
      unmockkAll()
   }
   @Test
   fun setBusiness_withCompleteData_shouldReturnSuccess() = testScope.runTest {
      
      
      // Arrange
      val businessComplete = createValidBusiness()
      
     
      // Mock firestore behavior
      mockFirestoreUpdateSuccess()
      
      // Act
      val result = businessRepository.setBusiness(businessComplete, uid)
      
      // verify
      verifyFirestoreUpdateCalled()
      
      // Assert
      val expectedResult = Resource.success(true)
      assertThat(result, `is`(expectedResult))
      
      
   }
   
   @Test
   fun `setBusiness should handle when fail`() = testScope.runTest {
      // Mock Firestore behavior for network error
      mockFirestoreUpdateFailure()
      
      val result = businessRepository.setBusiness(createValidBusiness(), "test_network_error")
      
      assertThat(result, `is`(Resource.error("business failed")))
   }
   
   @Test
   fun `setBusiness should handle when Firestore exception`() = testScope.runTest {
      // Mock Firestore behavior for network error
      mockFirestoreException()
      
      // Call the method you want to test
      val result = businessRepository.setBusiness(createValidBusiness(), uid)
      
      
      assertThat(result, `is`(Resource.error("Failed to update business. Please try again later.")))
      
   }
   
   
   private fun mockFirestoreUpdateSuccess()
   {
      every {
         firestore.collection(USERS_COLLECTION_PATH).document(uid)
            .set(createValidBusiness().toBusinessRequest() as Map<String, Any>)
            .addOnSuccessListener(capture(successListenerSlot))
            .addOnFailureListener(capture(failureListenerSlot))
      } answers {
         // Simulate a successful operation by invoking the success listener with null
         successListenerSlot.captured.onSuccess(null)
         // Return a mock Task
         mockk<Task<Void>>()
      }
   }
   
   private fun mockFirestoreUpdateFailure()
   {
      every {
         firestore.collection(any()).document(any())
            .set(any() as Map<String, Any>)
            .addOnSuccessListener(capture(successListenerSlot))
            .addOnFailureListener(capture(failureListenerSlot))
      } answers {
         //failureListenerSlot.captured.onFailure(Exception("business failed"))
         throw Exception("business failed")
      }
   }
   
   private fun mockFirestoreException()
   {
      every {
         firestore.collection(USERS_COLLECTION_PATH).document(uid)
            .set(createValidBusiness().toBusinessRequest()  as Map<String, Any>)
            .addOnSuccessListener(capture(successListenerSlot))
            .addOnFailureListener(capture(failureListenerSlot))
      } answers {
         failureListenerSlot.captured.onFailure(Exception("business failed"))
         throw  FirebaseFirestoreException("Failed to update business. Please try again later.",
            FirebaseFirestoreException.Code.INTERNAL)
      }
   }
   
   private fun verifyFirestoreUpdateCalled()
   {
      coVerify {
         firestore.collection(USERS_COLLECTION_PATH).document(uid)
            .set(createValidBusiness().toBusinessRequest() as Map<String, Any>)
            .addOnSuccessListener(capture(successListenerSlot))
            .addOnFailureListener(capture(failureListenerSlot))
      }
   }
   
   private fun createValidBusiness(): Business
   {
      // Create a valid Business object for testing
      // Replace with your actual business data
      return Business(
         StoreType.Clothes,
         "My Business",
         "info@mybusiness.com",
         listOf(Branch()),
         
         )
   }
   
  
   
   
}
