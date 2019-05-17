package com.angcyo.pager.transition.demo

import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.support.design.animation.ArgbEvaluatorCompat
import android.support.transition.TransitionSet
import android.view.View

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/16
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class ViewTransitionFragment : BaseTransitionFragment() {

    /**
     * 支持任意view的过渡效果
     * */
    var previewView: View? = null

    /**
     * 相关配置操作类
     * */
    var transitionConfig = ViewTransitionConfig()

    override fun getContentLayoutId(): Int = R.layout.base_view_transition_layout

    override fun onInitBaseView(viewHolder: RBaseViewHolder, arguments: Bundle?, savedInstanceState: Bundle?) {
        super.onInitBaseView(viewHolder, arguments, savedInstanceState)

        previewView = viewHolder.v(R.id.base_preview_view)

        viewHolder.v<MatrixLayout>(R.id.base_matrix_layout)
            ?.setOnMatrixTouchListener(object : MatrixLayout.OnMatrixTouchListener {
                override fun checkTouchEvent(matrixLayout: MatrixLayout): Boolean {
                    return transitionConfig.checkMatrixTouchEvent(this@ViewTransitionFragment, matrixLayout)
                }

                override fun onMatrixChange(
                    matrixLayout: MatrixLayout,
                    matrix: Matrix,
                    fromRect: RectF,
                    toRect: RectF
                ) {
                    transitionConfig.onMatrixChange(
                        this@ViewTransitionFragment,
                        matrixLayout,
                        matrix,
                        fromRect,
                        toRect
                    )
                }

                override fun onTouchEnd(
                    matrixLayout: MatrixLayout,
                    matrix: Matrix,
                    fromRect: RectF,
                    toRect: RectF
                ): Boolean {
                    return transitionConfig.onMatrixTouchEnd(
                        this@ViewTransitionFragment,
                        matrixLayout,
                        matrix,
                        fromRect,
                        toRect
                    )
                }
            })

        transitionConfig.initFragmentView(this, viewHolder, arguments, savedInstanceState)
    }

    override fun doTransitionShow() {
        super.doTransitionShow()
    }

    override fun doTransitionHide() {
        super.doTransitionHide()
    }

    override fun onTransitionShowBeforeValues() {
        //super.onTransitionShowBeforeValues()
        transitionConfig.transitionShowBeforeSetValues(this)
    }

    override fun onTransitionShowAfterValues() {
        //super.onTransitionShowAfterValues()
        transitionConfig.transitionShowAfterSetValues(this)
    }

    override fun onTransitionHideBeforeValues() {
        // super.onTransitionHideBeforeValues()
        transitionConfig.transitionHideBeforeSetValues(this)
    }

    override fun onTransitionHideAfterValues() {
        // super.onTransitionHideAfterValues()
        transitionConfig.transitionHideAfterSetValues(this)
    }

    override fun createShowTransitionSet(): TransitionSet {
        return transitionConfig.createShowTransitionSet(this)
    }

    override fun createHideTransitionSet(): TransitionSet {
        return transitionConfig.createHideTransitionSet(this)
    }

    /**
     * 计算色值
     * */
    private var argbEvaluator: ArgbEvaluatorCompat? = null

    fun getEvaluatorColor(fraction: Float, startValue: Int, endValue: Int): Int {
        if (argbEvaluator == null) {
            argbEvaluator = ArgbEvaluatorCompat()
        }
        return argbEvaluator!!.evaluate(fraction, startValue, endValue)
    }

}