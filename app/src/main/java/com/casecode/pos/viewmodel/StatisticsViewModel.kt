package com.casecode.pos.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.domain.model.stores.Store
import com.casecode.domain.usecase.AddDocuments
import com.casecode.domain.usecase.DeleteDocument
import com.casecode.domain.usecase.GetDocuments
import com.casecode.domain.usecase.GetStoreUseCase
import com.casecode.domain.usecase.UpdateDocument
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
     private val application: Application,
     private val getDocumentsUseCase: GetDocuments,
     private val getStoreUseCase: GetStoreUseCase,
     private val addDocumentsUseCase: AddDocuments,
     private val updateDocumentUseCase: UpdateDocument,
     private val deleteDocumentUseCase: DeleteDocument
) : ViewModel() {
    init {
        getStores()
    }

    private val _stores: MutableLiveData<List<Store>> = MutableLiveData()
    val stores: LiveData<List<Store>>
        get() = _stores

    private fun getStores() = viewModelScope.launch {
        getStoreUseCase().collect {
            when (it) {
                is Resource.Success -> {
                    _stores.value = it.data
                    Timber.e("Success")
                }

                is Resource.Error -> {
                    Timber.e("ERROR")

                }

                else -> {
                    Timber.e("ELSE")

                }
            }
        }
    }

    fun getDocuments(collection: String): LiveData<Resource<List<DocumentSnapshot>>> {
        val resultLiveData = MutableLiveData<Resource<List<DocumentSnapshot>>>()

        viewModelScope.launch {
            getDocumentsUseCase(collection).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result!!.documents

                    resultLiveData.value = Resource.Success(documents)
                } else {
                    Timber.e("task.exception: ${task.exception}")
                    resultLiveData.value = Resource.Error(task.exception!!)
                }
            }
        }

        return resultLiveData
    }


    fun getDocuments(
        collection: String,
        documentId: String,
        subCollection: String
    ): LiveData<Resource<List<DocumentSnapshot>>> {
        val resultLiveData = MutableLiveData<Resource<List<DocumentSnapshot>>>()

        viewModelScope.launch {
            getDocumentsUseCase(
                collection,
                documentId,
                subCollection
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result!!.documents
                    resultLiveData.value = Resource.Success(documents)
                } else {
                    Timber.e(task.exception)
                    resultLiveData.value = Resource.Error(task.exception!!)
                }
            }
        }

        return resultLiveData
    }

    fun getDocuments(
        collection: String,
        documentId: String,
        subCollection: String
    ): LiveData<FirebaseResult<List<DocumentSnapshot>>> {
        val resultLiveData = MutableLiveData<FirebaseResult<List<DocumentSnapshot>>>()

        viewModelScope.launch {
            getDocumentsUseCase(
                collection,
                documentId,
                subCollection
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result!!.documents
                    resultLiveData.value = FirebaseResult.Success(documents)
                } else {
                    Timber.e(task.exception)
                    resultLiveData.value = FirebaseResult.Failure(task.exception!!)
                }
            }
        }

        return resultLiveData
    }

    fun addDocuments(
        collection: String,
        date: Any
    ): LiveData<Resource<DocumentReference>> {
        val resultLiveData = MutableLiveData<Resource<DocumentReference>>()

        viewModelScope.launch {
            addDocumentsUseCase(collection, date).addOnSuccessListener { documentReference ->
                // Document was successfully added
                Timber.d("Document added with ID: ${documentReference.id}")
                resultLiveData.value = Resource.Success(documentReference)
            }.addOnFailureListener { e ->
                // Error occurred while adding the document
                Timber.e("Error adding document", e)
                resultLiveData.value = Resource.Error(e)
            }
        }

        return resultLiveData
    }

    fun setDocument(
        collectionPath: String,
        documentId: String,
        updates: Any
    ): LiveData<Resource<Void>> {
        val resultLiveData = MutableLiveData<Resource<Void>>()

        viewModelScope.launch {
            updateDocumentUseCase(collectionPath, documentId, updates)
                .addOnSuccessListener {
                    // Update successful
                    Timber.i("Update successful, $it")
//                    resultLiveData.value = FirebaseResult.Success("Update successful")
                }.addOnFailureListener { error ->
                    // Handle error
                    Timber.e("Error updating document", error)
                    resultLiveData.value = Resource.Error(error)
                }
        }

        return resultLiveData
    }

    fun updateDocument(
        collectionPath: String,
        documentId: String,
        updates: HashMap<String, Any>
    ): LiveData<Resource<Void>> {
        val resultLiveData = MutableLiveData<Resource<Void>>()

        viewModelScope.launch {

            updateDocumentUseCase.updateDocument(collectionPath, documentId, updates)
                .addOnSuccessListener {
                    // Update successful
                    Timber.i("Update successful, $it")
                    resultLiveData.value = Resource.Success(it)
                }.addOnFailureListener { error ->
                    // Handle error
                    Timber.e("Error updating document", error)
                    resultLiveData.value = Resource.Error(error)
                }


        }

        return resultLiveData
    }

    fun deleteDocument(
        collectionPath: String,
        documentId: String,
    ): LiveData<Resource<String>> {
        val resultLiveData = MutableLiveData<Resource<String>>()

        viewModelScope.launch {
            deleteDocumentUseCase(collectionPath, documentId)
                .addOnSuccessListener {
                    // Document successfully deleted
                    // Handle any additional logic here
                    resultLiveData.value = Resource.Success(
                        application.getString(
                            R.string.document_successfully_deleted,
                            documentId
                        )
                    )
                }.addOnFailureListener { error ->
                    // An error occurred while deleting the document
                    // Handle the error appropriately
                    Timber.e("Error deleting document", error)
                    resultLiveData.value = Resource.Error(error)
                }
        }

        return resultLiveData
    }

    companion object {
        private const val TAG = "StatisticsViewModel"
    }
}