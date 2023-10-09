package com.casecode.data.repository


import com.casecode.domain.model.users.Business
import com.casecode.domain.model.users.StoreType
import com.casecode.domain.utils.Resource
import com.casecode.testing.repository.TestBusinessRepository
import com.casecode.testing.util.CoroutinesTestExtension
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class BusinessRepositoryTest
{
   
   
   private val repository = TestBusinessRepository()
   
   @org.junit.jupiter.api.Test
   fun getBusiness_shouldReturnBusiness_whenUidIsValid() = runTest {
      // Given
      val uid = "1234567890"
      val bClothes = Business(StoreType.Clothes, "mahmoud9@gmail.com", "123")
      
      
      // When send business and get data business
      repository.sendAddBusiness(bClothes)
      val result = repository.getBusiness(uid)
      
      // Then
      assertThat(result, equalTo(bClothes))
   }
   
   @Test
   fun setBusiness_shouldReturnSuccess_whenBusinessIsValid() = runTest {
      // Given a new business
      val uid = "1234567890"
      val business = Business(
         storeType = StoreType.Clothes,
         email = "johndoe@example.com",
         phone = "12345678"
                             )
      
      // When a result is requested from business repository
      val result = repository.setBusiness(business, uid)
      
      // Then result is true
      assertThat(result, equalTo(Resource.Success(true)))
   }
}
