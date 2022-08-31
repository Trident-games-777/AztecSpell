package jp.co.ta.loaders

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import jp.co.ta.model.FirebaseData
import kotlin.coroutines.suspendCoroutine

object FirebaseDataLoader {
    private const val PATH = "firebase_data.json"
    private const val MAX_DOWNLOAD_SIZE_BYTES = 1024L * 1024L

    suspend fun loadFirebaseData(): FirebaseData = suspendCoroutine { continuation ->
        val fileRef = Firebase.storage.reference.child(PATH)
        fileRef.getBytes(MAX_DOWNLOAD_SIZE_BYTES).addOnSuccessListener { byteArray ->
            val data = Gson().fromJson(String(byteArray), FirebaseData::class.java)
            continuation.resumeWith(Result.success(data))
        }.addOnFailureListener { exception ->
            continuation.resumeWith(Result.failure(exception))
        }
    }
}