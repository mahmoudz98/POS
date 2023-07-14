package com.casecode.pos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.domain.usecase.GetDocuments
import com.casecode.domain.usecase.SetDocuments
import com.casecode.pos.utils.FirebaseResult
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getDocumentsUseCase: GetDocuments,
    private val setDocumentsUseCase: SetDocuments
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is statistics Fragment"
    }
    val text: LiveData<String> = _text

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

    fun setDocuments(
        collection: String,
        date: Any
    ): LiveData<FirebaseResult<DocumentReference>> {
        val resultLiveData = MutableLiveData<FirebaseResult<DocumentReference>>()

        viewModelScope.launch {
            setDocumentsUseCase(collection, date).addOnSuccessListener { documentReference ->
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
}