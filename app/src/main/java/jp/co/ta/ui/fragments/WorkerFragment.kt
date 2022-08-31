package jp.co.ta.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import jp.co.ta.R
import jp.co.ta.loaders.AppsDataLoader
import jp.co.ta.loaders.FileLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkerFragment : Fragment(R.layout.fragment_worker) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: WorkerFragmentArgs by navArgs()

        lifecycleScope.launch(Dispatchers.IO) {
            val link =
                FileLoader.loadFromFile(requireContext()) ?: AppsDataLoader(
                    context = requireContext(),
                    appsFlyerId = args.firebaseData.appsFlyerId,
                    oneSignalId = args.firebaseData.oneSignalId,
                    initialLink = args.firebaseData.initialLink,
                    googleId = args.googleId
                ).loadAppsData()
            withContext(Dispatchers.Main.immediate) {
                findNavController().navigate(
                    WorkerFragmentDirections.actionWorkerFragmentToAztecWebFragment(
                        link,
                        args.firebaseData.domainLink
                    )
                )
            }
        }
    }
}