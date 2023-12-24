package com.casecode.data.repository

import com.casecode.testing.CoroutinesTestExtension
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.util.Util.voidErrorTransformer
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, CoroutinesTestExtension::class)
class FirestoreRepositoryImplTest
{
   @MockK
   lateinit var firestore: FirebaseFirestore
   
   @InjectMockKs
   lateinit var firestoreRepository: FirestoreRepositoryImpl
   
   @BeforeEach
   fun setup()
   {
      MockKAnnotations.init(this)
      
   }
   
   @Test
   fun test()
   {
      val taskCompletionSource = TaskCompletionSource<Void>()
      val collection = "user"
      val document = "doc"
      val updates = hashMapOf("mah" to "sa")
      every {
         firestore.collection(collection).document(document)
            .update(updates as Map<String, Any>).addOnSuccessListener {  }
            
      } answers {
         // Simulate success
         val onSuccessListener: OnSuccessListener<Void> = arg(3)
         onSuccessListener.onSuccess(null)
         taskCompletionSource.task.continueWith(com.google.firebase.firestore.util.Executors.DIRECT_EXECUTOR,
            voidErrorTransformer())
      }
      runTest {
         firestoreRepository.updateDocument(collection, document, updates as HashMap<String, Any>)
         coVerify {
            firestoreRepository.updateDocument(collection,
               document,
               updates as HashMap<String, Any>).addOnSuccessListener {  }
         }
//         confirmVerified(firestoreRepository)
      }
   }
}
