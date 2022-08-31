package jp.co.ta.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import jp.co.ta.R

class AztecFinishFragment : Fragment(R.layout.fragment_aztec_finish) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.exit).setOnClickListener {
            requireActivity().finish()
        }
        view.findViewById<Button>(R.id.newGame).setOnClickListener {
            findNavController().navigate(
                AztecFinishFragmentDirections.actionAztecFinishFragmentToAztecGreetingFragment()
            )
        }
    }
}