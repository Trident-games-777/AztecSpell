package jp.co.ta.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import jp.co.ta.R
import jp.co.ta.anim.AztecAnimations
import jp.co.ta.databinding.FragmentAztecCasinoBinding
import kotlin.random.Random

class AztecCasinoFragment : Fragment(R.layout.fragment_aztec_casino) {
    private lateinit var binding: FragmentAztecCasinoBinding
    private val res = listOf(
        R.drawable.item1, R.drawable.item2,
        R.drawable.item3, R.drawable.item4, R.drawable.item5
    )
    private val lineTop = mutableListOf<Int>()
    private val lineMiddle = mutableListOf<Int>()
    private val lineBottom = mutableListOf<Int>()

    private lateinit var topImages: List<View>
    private lateinit var middleImages: List<View>
    private lateinit var bottomImages: List<View>

    private var points = START_POINTS

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAztecCasinoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateCoinsText()
        val images = listOf(
            binding.img1, binding.img2, binding.img3, binding.img4,
            binding.img5, binding.img6, binding.img7, binding.img8, binding.img9
        )
        topImages = images.take(ROW_LENGTH)
        middleImages = images.drop(ROW_LENGTH).take(ROW_LENGTH)
        bottomImages = images.takeLast(ROW_LENGTH)

        binding.btnGo.setOnClickListener {
            binding.btnGo.isEnabled = false
            points -= POINTS_PER_MOVE
            updateCoinsText()
            AztecAnimations.hide(images) {
                setResources()
                AztecAnimations.fallDown(images) {
                    processWinLines {
                        if (points >= POINTS_PER_MOVE) {
                            binding.btnGo.isEnabled = true
                        } else {
                            findNavController().navigate(
                                AztecCasinoFragmentDirections.actionAztecCasinoFragmentToAztecFinishFragment()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setResources() {
        lineTop.clear()
        lineMiddle.clear()
        lineBottom.clear()

        repeat(ROW_LENGTH) { lineTop.add(res[Random.nextInt(res.size)]) }
        repeat(ROW_LENGTH) { lineMiddle.add(res[Random.nextInt(res.size)]) }
        repeat(ROW_LENGTH) { lineBottom.add(res[Random.nextInt(res.size)]) }

        binding.img1.setImageResource(lineTop[0])
        binding.img2.setImageResource(lineTop[1])
        binding.img3.setImageResource(lineTop[2])

        binding.img4.setImageResource(lineMiddle[0])
        binding.img5.setImageResource(lineMiddle[1])
        binding.img6.setImageResource(lineMiddle[2])

        binding.img7.setImageResource(lineBottom[0])
        binding.img8.setImageResource(lineBottom[1])
        binding.img9.setImageResource(lineBottom[2])
    }

    private fun updateCoinsText(value: Int = points) {
        binding.tvCoins.text = resources.getString(R.string.coins, value.toString())
    }

    private fun processWinLines(onProcess: () -> Unit) {
        val winLines = buildList {
            if (lineTop.all { it == lineTop.first() }) add(topImages)
            if (lineMiddle.all { it == lineMiddle.first() }) add(middleImages)
            if (lineBottom.all { it == lineBottom.first() }) add(bottomImages)
        }

        val animations = buildList {
            winLines.forEach { winLine ->
                add(AztecAnimations.animateWinLineAndPoints(
                    fromPoints = points,
                    toPoints = points + POINTS_PER_WIN_LINE,
                    actionWithPoint = { updateCoinsText(it) },
                    winViews = winLine
                ).also { points += POINTS_PER_WIN_LINE })
            }
        }

        AztecAnimations.startSequentialAnimator(animations) { onProcess() }
    }

    companion object {
        private const val ROW_LENGTH = 3
        private const val POINTS_PER_MOVE = 20
        private const val START_POINTS = 1000
        private const val POINTS_PER_WIN_LINE = 100
    }
}