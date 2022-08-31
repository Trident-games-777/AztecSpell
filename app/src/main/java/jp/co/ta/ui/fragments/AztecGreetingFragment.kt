package jp.co.ta.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import jp.co.ta.R

class AztecGreetingFragment : Fragment(R.layout.fragment_aztec_greeting) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.startPlay).setOnClickListener {
            findNavController().navigate(
                AztecGreetingFragmentDirections.actionAztecGreetingFragmentToAztecCasinoFragment()
            )
        }
    }
}