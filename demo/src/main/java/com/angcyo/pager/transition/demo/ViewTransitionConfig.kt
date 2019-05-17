package com.angcyo.pager.transition.demo

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.transition.*
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.github.chrisbanes.photoview.PhotoView
import java.lang.ref.WeakReference

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/16
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class ViewTransitionConfig {

    /**
     * 背景动画开始的颜色
     * */
    var backgroundStartColor = Color.TRANSPARENT
    /**
     * 背景动画结束的颜色
     * */
    var backgroundEndColor = Color.BLACK

    /**
     * 背景当前的颜色, 拖拽时 改变的颜色
     * */
    var backgroundCurrentColor: Int? = null

    /**
     * fragment 中 onInitBaseView 触发回调
     * */
    var initFragmentView: (
        fragment: ViewTransitionFragment,
        viewHolder: RBaseViewHolder,
        arguments: Bundle?,
        savedInstanceState: Bundle?
    ) -> Unit =
        { fragment, _, _, _ ->
            val targetView = onGetTargetView(startPagerIndex)
            if (targetView == null) {
                fromViewWeak?.clear()
                fromViewWeak = null
            } else {
                fromViewWeak = WeakReference(targetView)
            }

            fromViewWeak?.get()?.let { fView ->
                if (fView is ImageView && fragment.previewView is ImageView) {
                    (fragment.previewView as? ImageView)?.let { pView ->
                        pView.setImageDrawable(fView.drawable?.mutate()?.constantState?.newDrawable())
                    }
                }
            }
        }

    //<editor-fold desc="转场动画相关处理">

    //当参数配置, 无法执行转场动画时, 使用默认的动画矩形转场
    val defaultFromRect = Rect(
        RUtils.getScreenWidth() / 2 - 10 * dpi, RUtils.getScreenHeight() / 2 - 10 * dpi,
        RUtils.getScreenWidth() / 2 + 10 * dpi, RUtils.getScreenHeight() / 2 + 10 * dpi
    )

    //默认的最终 坐标/宽高
    val defaultToRect = Rect(0, 0, -1, -1)

    /**
     * 动画结束时的坐标和宽高
     * */
    val toRect = Rect(defaultToRect)

    var fromViewWeak: WeakReference<View>? = null

    val getImagePlaceholder: (position: Int) -> Drawable? = {
        var targetView: View?

        if (it == currentPagerIndex) {
            targetView = fromViewWeak?.get()

        } else {
            targetView = onGetTargetView(it)
        }

        if (targetView is ImageView) {
            targetView.drawable?.mutate()?.constantState?.newDrawable()
        } else {
            null
        }
    }

    val getTargetViewRect: () -> Rect? = {
        val view = fromViewWeak?.get()
        view?.getViewRect()
    }

    /**
     * 背景动画作用的View
     * */
    var backgroundColorAnimView: (fragment: ViewTransitionFragment) -> View? = {
        it.rootLayout
    }

    /**
     * 转场动画作用的view
     * */
    var transitionAnimView: (fragment: ViewTransitionFragment) -> View? = { fragment ->
        if (fragment is PagerTransitionFragment && pagerCount > 0) {
            if (fromViewWeak?.get() == null) {
                fragment.viewPager
            } else {
                fragment.previewView
            }
        } else {
            fragment.previewView
        }
    }

    /**
     * 转场动画 界面显示时, 需要捕捉的值.
     * */
    var transitionShowBeforeSetValues: (
        fragment: ViewTransitionFragment
    ) -> Unit = { fragment ->

        backgroundColorAnimView(fragment)?.setBackgroundColor(backgroundStartColor)

        val animView = transitionAnimView(fragment)

        if (animView == fragment.previewView) {
            fromViewWeak?.get()?.let { fView ->
                if (fView is ImageView && animView is ImageView) {
                    (animView as? ImageView)?.let { pView ->
                        pView.scaleType = fView.scaleType
                    }
                }
            }
        }

        animView?.apply {
            val fromRect = getTargetViewRect() ?: defaultFromRect

            translationX = fromRect.left.toFloat()
            translationY = fromRect.top.toFloat()
            setWidthHeight(fromRect.width(), fromRect.height())

            if (fromViewWeak == null && animView != fragment.previewView) {
                //预览图的转场动画, 不需要透明度动画的支持
                alpha = 0.1f
            }
        }
    }

    var transitionShowAfterSetValues: (
        fragment: ViewTransitionFragment
    ) -> Unit = { fragment ->
        transitionHideBeforeSetValues(fragment)
    }

    /**
     * 转场动画 界面移除时, 需要捕捉的值
     * */
    var transitionHideBeforeSetValues: (
        fragment: ViewTransitionFragment
    ) -> Unit = { fragment ->

        backgroundColorAnimView(fragment)?.setBackgroundColor(backgroundCurrentColor ?: backgroundEndColor)

        val animView = transitionAnimView(fragment)

        animView?.apply {
            (this as? ImageView)?.apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            alpha = 1f

            translationX = toRect.left.toFloat()
            translationY = toRect.top.toFloat()
            setWidthHeight(toRect.width(), toRect.height())
        }
    }

    var transitionHideAfterSetValues: (
        fragment: ViewTransitionFragment
    ) -> Unit = { fragment ->
        transitionShowBeforeSetValues(fragment)
    }

    var createShowTransitionSet: (
        fragment: ViewTransitionFragment
    ) -> TransitionSet = { fragment ->

        val transitionSet = TransitionSet()
        transitionSet.addTransition(ChangeBounds())
        transitionSet.addTransition(ChangeTransform())
        //transitionSet.addTransition(ChangeScroll()) //图片过渡效果, 请勿设置此项
        transitionSet.addTransition(ChangeClipBounds())
        transitionSet.addTransition(ChangeImageTransform())

        //背景颜色过渡动画
        backgroundColorAnimView(fragment)?.let {
            transitionSet.addTransition(ColorTransition().addTarget(it))
        }

        //透明度动画
        if (fromViewWeak == null) {
            transitionAnimView(fragment)?.let {
                transitionSet.addTransition(AlphaTransition().addTarget(it))
            }
        }

        transitionSet.duration = BaseTransitionFragment.ANIM_DURATION
        transitionSet.interpolator = FastOutSlowInInterpolator()
        transitionSet
    }

    var createHideTransitionSet: (
        fragment: ViewTransitionFragment
    ) -> TransitionSet = { fragment ->
        createShowTransitionSet(fragment)
    }

    //</editor-fold desc="转场动画相关处理">

    //<editor-fold desc="Matrix拖拽返回处理">

    /**
     * 返回true, 激活 拖拽返回
     * */
    var checkMatrixTouchEvent: (fragment: ViewTransitionFragment, matrixLayout: MatrixLayout) -> Boolean =
        { fragment, _ ->
            var result = true
            if (fragment.isTransitionAnimEnd) {
                if (fragment is PagerTransitionFragment) {
                    result = pagerCount <= 0

                    for (i in 0 until fragment.viewPager.childCount) {
                        val childView = fragment.viewPager.getChildAt(i)

                        if (childView != null) {
                            if (fragment.viewPager.isViewIn(childView)) {
                                val photoView: PhotoView? = childView.v(R.id.base_photo_view)!!

                                result = (photoView != null && photoView.scale <= 1)

                                break
                            }
                        }
                    }
                }
            } else {
                result = false
            }
            result
        }

    /**
     * 拖拽中
     * */
    var onMatrixChange: (fragment: ViewTransitionFragment, matrixLayout: MatrixLayout, matrix: Matrix, fromRect: RectF, toRect: RectF) -> Unit =
        { fragment, _, _, fromRect, toRect ->
            backgroundCurrentColor =
                fragment.getEvaluatorColor(toRect.top / fromRect.bottom, backgroundEndColor, backgroundStartColor)
            backgroundColorAnimView(fragment)?.setBackgroundColor(backgroundCurrentColor!!)
        }

    /**
     * 拖拽结束, 返回true, 自行处理. 否则会 回滚到默认位置
     * */
    var onMatrixTouchEnd: (fragment: ViewTransitionFragment, matrixLayout: MatrixLayout, matrix: Matrix, fromRect: RectF, toRect: RectF) -> Boolean =
        { fragment, matrixLayout, _, fromRect, toRect ->
            if (toRect.top / fromRect.bottom > 0.3f) {
                this.toRect.set(toRect)

                matrixLayout.resetMatrix()
                fragment.doTransitionHide()
                true
            } else {
                false
            }
        }

    //</editor-fold desc="Matrix拖拽返回处理">

    //<editor-fold desc="ViewPager相关处理">

    /**
     * 默认显示第几页
     * viewPager.setCurrentItem()
     * */
    var startPagerIndex = 0
        set(value) {
            field = value
            currentPagerIndex = value
        }

    /**
     * 当前第几页
     * */
    var currentPagerIndex = startPagerIndex

    var pagerCount = 0
    var pagerLayoutId = R.layout.base_item_single_photo_pager_layout

    /**
     * 获取指定位置的图片url地址
     * */
    var onGetPagerImageUrl: (position: Int) -> String? = { null }

    /**
     * 获取指定位置对应的View, 用于设置 ImagePlaceholder , 和 fromRect
     * */
    var onGetTargetView: (position: Int) -> View? = {
        if (pagerCount > 0) {
            if (currentPagerIndex == startPagerIndex) {
                fromViewWeak?.get()
            } else {
                null
            }
        } else {
            fromViewWeak?.get()
        }
    }

    /**
     * 图片点击回调
     * */
    var onItemPhotoClickListener: (
        fragment: PagerTransitionFragment, adapter: RPagerAdapter,
        viewHolder: RBaseViewHolder, itemView: PhotoView, position: Int
    ) -> Unit =
        { fragment, _, _, _, _ -> fragment.doTransitionHide() }

    /**
     * 图片长安回调
     * */
    var onItemPhotoLongClickListener: (
        fragment: PagerTransitionFragment, adapter: RPagerAdapter,
        viewHolder: RBaseViewHolder, itemView: PhotoView, position: Int
    ) -> Boolean =
        { _, _, _, _, _ -> true }

    /**
     * 页面切换回调
     * */
    var onPageSelected: (fragment: PagerTransitionFragment, adapter: RPagerAdapter, position: Int) -> Unit =
        { fragment, _, position ->
            currentPagerIndex = position

            if (fragment.isTransitionAnimEnd) {
                val targetView = onGetTargetView(position)
                if (targetView == null) {
                    fromViewWeak?.clear()
                    fromViewWeak = null
                } else {
                    fromViewWeak = WeakReference(targetView)
                }
            }

            (fragment.previewView as? ImageView)?.setImageDrawable(getImagePlaceholder(position))
        }

    var getPagerCount: (fragment: PagerTransitionFragment, adapter: RPagerAdapter) -> Int = { _, _ ->
        pagerCount
    }

    var getPagerItemType: (fragment: PagerTransitionFragment, adapter: RPagerAdapter, position: Int) -> Int =
        { _, _, _ -> 1 }

    var getPagerLayoutId: (fragment: PagerTransitionFragment, adapter: RPagerAdapter, position: Int, itemType: Int) -> Int =
        { _, _, _, _ ->
            pagerLayoutId
        }

    /**
     * 绑定页面
     * */
    var bindPagerItemView: (
        fragment: PagerTransitionFragment, adapter: RPagerAdapter,
        viewHolder: RBaseViewHolder, position: Int, itemType: Int
    ) -> Unit = { fragment, adapter, viewHolder, position, itemType ->
        //图片事件处理
        val photoView: PhotoView = viewHolder.v(R.id.base_photo_view)!!
        photoView.apply {
            setOnPhotoTapListener { view, x, y ->
                //L.i("点击Photo")
            }

            setOnViewTapListener { view, x, y ->
                //L.i("点击View")
                onItemPhotoClickListener(fragment, adapter, viewHolder, photoView, position)
            }

            setOnLongClickListener {
                //L.i("长按1")
                onItemPhotoLongClickListener(fragment, adapter, viewHolder, photoView, position)
            }
        }

        //加载图片
        val data = onGetPagerImageUrl(position)
        if (data is String) {
            photoView.load(data) {
                dontAnimate()
                autoClone()
                diskCacheStrategy(DiskCacheStrategy.ALL)

                getImagePlaceholder(position)?.let {
                    placeholder(it.mutate().constantState?.newDrawable())
                }

                addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //L.i("加载失败")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //L.i("加载成功 $model $resource $target $isFirstResource")
                        return false
                    }

                })
            }
        }
    }

    //</editor-fold desc="ViewPager相关处理">
}