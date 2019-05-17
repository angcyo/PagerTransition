package com.angcyo.pager.transition.demo

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.support.transition.Transition
import android.support.transition.TransitionValues
import android.view.ViewGroup

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/04/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class ColorTransition : Transition() {
    companion object {
        private const val KEY = "android:ColorTransition:color"
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
            (view.background as? ColorDrawable)?.let {
                values.values[KEY] = it.color
            }
        }
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues != null && endValues != null) {
            val startColor = startValues.values[KEY]
            val endColor = endValues.values[KEY]

            if (startColor != endColor) {
                val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
                colorAnimator.addUpdateListener { animation ->
                    val color = animation.animatedValue as Int//之后就可以得到动画的颜色了.
                    startValues.view.setBackgroundColor(color)//设置一下, 就可以看到效果..
                }
                colorAnimator.duration = duration
                colorAnimator.interpolator = interpolator
                return colorAnimator
            }
        }
        return null
    }
}