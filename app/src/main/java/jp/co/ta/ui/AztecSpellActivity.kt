package jp.co.ta.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import jp.co.ta.R
import jp.co.ta.loaders.FirebaseDataLoader
import jp.co.ta.loaders.InitialLoader
import jp.co.ta.ui.fragments.SplashFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AztecSpellActivity : AppCompatActivity() {
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { keepSplashScreen }
        }
        setContentView(R.layout.activity_aztec_spell)

        val initialLoader = InitialLoader(this)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment

        lifecycleScope.launch(Dispatchers.IO) {
            if (initialLoader.isStubNeeded()) {
                withContext(Dispatchers.Main.immediate) {
                    navHost.navController.navigate(
                        SplashFragmentDirections.actionSplashFragmentToAztecGreetingFragment()
                    )
                }
            } else {
                val data = FirebaseDataLoader.loadFirebaseData()
                val id =
                    AdvertisingIdClient.getAdvertisingIdInfo(this@AztecSpellActivity).id.toString()
                withContext(Dispatchers.Main.immediate) {
                    navHost.navController.navigate(
                        SplashFragmentDirections.actionSplashFragmentToWorkerFragment(
                            firebaseData = data,
                            googleId = id
                        )
                    )
                }
            }
            keepSplashScreen = false
        }
    }
}