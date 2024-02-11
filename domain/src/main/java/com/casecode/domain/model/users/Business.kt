package com.casecode.domain.model.users


data class Business(
     val storeType: StoreType? = null,val email: String? = null,
     val phone: String? = null,
     @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
     val isCompletedStep: Boolean? = false,
     val branches: List<Branch> = listOf(),
                   )

enum class StoreType(
     val englishName: String,
     val arabicName: String,
                    )
{
   Clothes("Clothes","ملابس"), Coffee("Coffee","قهوة"), Hyper("Hyper","هايبر")
   
}