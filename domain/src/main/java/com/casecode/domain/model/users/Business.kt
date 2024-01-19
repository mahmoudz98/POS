package com.casecode.domain.model.users

import timber.log.Timber


data class Business(
     val storeType: StoreType? = null,
     val email: String? = null,
     val phone: String? = null,
     
     val branches: List<Branch> = listOf(),
                   )

enum class StoreType(
     val englishName: String,
     val arabicName: String,
                    )
{
   Clothes("Clothes", "ملابس"),
   Coffee("Coffee", "قهوة"),
   Hyper("Hyper", "هايبر");
   
   
   companion object
   {
      fun toStoreType(storeType: String): StoreType?
      {
         return entries.find{ type ->
            type.arabicName == storeType || type.englishName.lowercase() == storeType.lowercase()
         }
      }
 
   }
}