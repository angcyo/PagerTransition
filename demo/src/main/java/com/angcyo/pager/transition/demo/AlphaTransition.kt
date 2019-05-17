package com.angcyo.pager.transition.demo

import android.animation.Animator
import android.animation.ValueAnimator
import android.support.transition.Transition
import android.support.transition.TransitionValues
import android.view.ViewGroup

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/16
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class AlphaTransition : Transition() {
    companion object {
        private const val KEY = "android:AlphaTransition:alpha"
    }

    override fun captureStartValues(values: TransitionValues) {
        captureValues(values)
    }

    override fun captureEndValues(values: TransitionValues) {
        captureValues(values)
    }

    private fun captureValues(values: TransitionValues) {
        val view = values.view
        if (targets.contains(view)) {
            values.values[KEY] = view.alpha
        }
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues != null && endValues != null) {
            val startAlpha: Float = startValues.values[KEY] as Float
            val endAlpha: Float = endValues.values[KEY] as Float

            if (startAlpha != endAlpha) {
                val animator = ValueAnimator.ofFloat(startAlpha, endAlpha)
                animator.addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    startValues.view.alpha = value
                }
                animator.duration = duration
                animator.interpolator = interpolator
                return animator
            }
        }
        return null
    }
}