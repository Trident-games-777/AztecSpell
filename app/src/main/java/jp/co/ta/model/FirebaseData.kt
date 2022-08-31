package jp.co.ta.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FirebaseData(
    val oneSignalId: String,
    val appsFlyerId: String,
    val initialLink: String,
    val domainLink: String
) : Parcelable
