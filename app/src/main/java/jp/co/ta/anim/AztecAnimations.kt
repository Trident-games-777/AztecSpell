package jp.co.ta.anim

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.BounceInterpolator
import androidx.core.animation.doOnEnd
import kotlin.random.Random

object AztecAnimations {
    fun fallDown(views: List<View>, onAnimationEnd: () -> Unit = {}) {
        val animations = buildList {
            views.forEach { view ->
                view.alpha = 1f
                add(
                    ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -800f, 0f).apply {
                        duration = Random.nextLong(300, 700)
                        interpolator = BounceInterpolator()
                    }
                )
            }
        }
        val animator = AnimatorSet()
        animator.playTogether(animations)
        animator.doOnEnd { onAnimationEnd() }
        animator.start()
    }

    fun hide(views: List<View>, onAnimationEnd: () -> Unit = {}) {
        val animations = buildList {
            views.forEach { view ->
                add(
                    ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f).apply {
                        duration = Random.nextLong(300, 700)
                    }
                )
            }
        }
        val animator = AnimatorSet()
        animator.playTogether(animations)
        animator.doOnEnd { onAnimationEnd() }
        animator.start()
    }

    fun animateWinLineAndPoints(
        fromPoints: Int,
        toPoints: Int,
        actionWithPoint: (Int) -> Unit,
        winViews: List<View>,
    ): Animator {
        val animations: List<Animator> = buildList {
            winViews.forEach { view ->
                add(
                    ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 360f).apply {
                        duration = 500
                    }
                )
            }
            add(ValueAnimator.ofInt(fromPoints, toPoints).apply {
                addUpdateListener { actionWithPoint(it?.animatedValue as Int) }
                duration = 500
            })
        }

        val animator = AnimatorSet()
        animator.playTogether(animations)
        return animator
    }

    fun startSequentialAnimator(animations: List<Animator>, onAnimationEnd: () -> Unit = {}) {
        AnimatorSet().apply {
            playSequentially(animations)
            doOnEnd { onAnimationEnd() }
            start()
        }
    }

}