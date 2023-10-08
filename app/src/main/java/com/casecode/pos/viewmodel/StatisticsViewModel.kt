package com.casecode.pos.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.domain.usecase.AddDocuments
import com.casecode.domain.usecase.DeleteDocument
import com.casecode.domain.usecase.GetDocuments
import com.casecode.domain.usecase.UpdateDocument
import com.casecode.pos.R
import com.casecode.pos.utils.FirebaseResult
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
    private val addDocumentsUseCase: AddDocuments,
    private val updateDocumentUseCase: UpdateDocument,
    private val deleteDocumentUseCase: DeleteDocument
) : ViewModel() {

    fun getDocuments(collection: String): LiveData<FirebaseResult<List<DocumentSnapshot>>> {
        val resultLiveData = MutableLiveData<FirebaseResult<List<DocumentSnapshot>>>()

        viewModelScope.launch {
            getDocumentsUseCase(collection).addOnCompleteListener { task ->
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
    ): LiveData<FirebaseResult<DocumentReference>> {
        val resultLiveData = MutableLiveData<FirebaseResult<DocumentReference>>()

        viewModelScope.launch {
            addDocumentsUseCase(collection, date).addOnSuccessListener { documentReference ->
                // Document was successfully added
                Timber.d("Document added with ID: ${documentReference.id}")
                resultLiveData.value = FirebaseResult.Success(documentReference)
            }.addOnFailureListener { e ->
                // Error occurred while adding the document
                Timber.e("Error adding document", e)
                resultLiveData.value = FirebaseResult.Failure(e)
            }
        }

        return resultLiveData
    }

    fun setDocument(
        collectionPath: String,
        documentId: String,
        updates: Any
    ): LiveData<FirebaseResult<Void>> {
        val resultLiveData = MutableLiveData<FirebaseResult<Void>>()

        viewModelScope.launch {
            updateDocumentUseCase(collectionPath, documentId, updates)
                .addOnSuccessListener {
                    // Update successful
                    Timber.i("Update successful, $it")
//                    resultLiveData.value = FirebaseResult.Success("Update successful")
                }.addOnFailureListener { error ->
                    // Handle error
                    Timber.e("Error updating document", error)
                    resultLiveData.value = FirebaseResult.Failure(error)
                }
        }

        return resultLiveData
    }

    fun updateDocument(
        collectionPath: String,
        documentId: String,
        updates: HashMap<String, Any>
    ): LiveData<FirebaseResult<Void>> {
        val resultLiveData = MutableLiveData<FirebaseResult<Void>>()

        viewModelScope.launch {

            updateDocumentUseCase.updateDocument(collectionPath, documentId, updates)
                .addOnSuccessListener {
                    // Update successful
                    Timber.i("Update successful, $it")
                    resultLiveData.value = FirebaseResult.Success(it)
                }.addOnFailureListener { error ->
                    // Handle error
                    Timber.e("Error updating document", error)
                    resultLiveData.value = FirebaseResult.Failure(error)
                }


        }

        return resultLiveData
    }

    fun deleteDocument(
        collectionPath: String,
        documentId: String,
    ): LiveData<FirebaseResult<String>> {
        val resultLiveData = MutableLiveData<FirebaseResult<String>>()

        viewModelScope.launch {
            deleteDocumentUseCase(collectionPath, documentId)
                .addOnSuccessListener {
                    // Document successfully deleted
                    // Handle any additional logic here
                    resultLiveData.value = FirebaseResult.Success(
                        application.getString(
                            R.string.document_successfully_deleted,
                            documentId
                        ))
                }.addOnFailureListener { error ->
                    // An error occurred while deleting the document
                    // Handle the error appropriately
                    Timber.e("Error deleting document", error)
                    resultLiveData.value = FirebaseResult.Failure(error)
                }
        }

        return resultLiveData
    }
}