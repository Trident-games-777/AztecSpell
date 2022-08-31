package jp.co.ta.loaders

import android.content.Context
import android.provider.Settings
import java.io.File

class InitialLoader(
    private val context: Context
) {
    fun isStubNeeded(): Boolean {
        val isADB =
            Settings.Global.getString(context.contentResolver, Settings.Global.ADB_ENABLED) == "1"

        val isRoot = try {
            val seq = sequenceOf(
                "/sbin/su", "/system/bin/su",
                "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su",
                "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"
            ).map { File(it).exists() }
            seq.any() { it }
        } catch (e: SecurityException) {
            false
        }

        return isADB || isRoot
    }
}