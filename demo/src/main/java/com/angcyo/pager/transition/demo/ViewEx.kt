package com.angcyo.pager.transition.demo


import android.animation.ValueAnimator
import android.app.Activity
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ViewCompat
import android.text.InputFilter
import android.text.TextUtils
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.*

/**
 * Kotlin View的扩展
 * Created by angcyo on 2017-06-03.
 */

@Suppress("UNCHECKED_CAST")
public fun <V : View> View.v(id: Int): V? {
    val view: View? = findViewById(id)
    return view as V?
}

public val View.random: Random by lazy {
    Random(System.nanoTime())
}

public val View.scaledDensity: Float
    get() = resources.displayMetrics.scaledDensity

public val View.density: Float
    get() = resources.displayMetrics.density

public val <T> T.dp: Float by lazy {
    Resources.getSystem()?.displayMetrics?.density ?: 0f
}

public val <T> T.dpi: Int by lazy {
    Resources.getSystem()?.displayMetrics?.density?.toInt() ?: 0
}

public val View.viewDrawWith: Int
    get() = measuredWidth - paddingLeft - paddingRight

public val View.viewDrawHeight: Int
    get() = measuredHeight - paddingTop - paddingBottom

private val View.tempRect: Rect by lazy {
    Rect()
}

public fun View.getGlobalVisibleRect(): Rect {
    //top 永远都不会少于0  bottom 永远都不会大于屏幕高度, 可见的rect就是, 不可见的会被剃掉
    getGlobalVisibleRect(tempRect)
    return tempRect
}

/**返回居中绘制文本的y坐标*/
public fun View.getDrawCenterTextCy(paint: Paint): Float {
    val rawHeight = measuredHeight - paddingTop - paddingBottom
    return paddingTop + rawHeight / 2 + paint.textDrawCy()
}

public fun View.getDrawCenterTextCx(paint: Paint, text: String): Float {
    val rawWidth = measuredWidth - paddingLeft - paddingRight
    return paddingLeft + rawWidth / 2 - paint.textDrawCx(text)
}

public fun Paint.textDrawCx(text: String): Float {
    return measureText(text) / 2
}

/**文本绘制时候 的中点y坐标*/
public fun Paint.textDrawCy(): Float {
    return (descent() - ascent()) / 2 - descent()
}

public fun View.centerX(): Int {
    return (this.x + this.measuredWidth / 2).toInt()
}

public fun View.centerY(): Int {
    return (this.y + this.measuredHeight / 2).toInt()
}

public fun View.getDrawCenterCy(): Float {
    val rawHeight = measuredHeight - paddingTop - paddingBottom
    return (paddingTop + rawHeight / 2).toFloat()
}

public fun View.getDrawCenterCx(): Float {
    val rawWidth = measuredWidth - paddingLeft - paddingRight
    return (paddingLeft + rawWidth / 2).toFloat()
}

/**最小圆的半径*/
public fun View.getDrawCenterR(): Float {
    val rawHeight = measuredHeight - paddingTop - paddingBottom
    val rawWidth = measuredWidth - paddingLeft - paddingRight
    return (Math.min(rawWidth, rawHeight) / 2).toFloat()
}

public fun TextView.getDrawCenterTextCy(): Float {
    val rawHeight = measuredHeight - paddingTop - paddingBottom
    return paddingTop + rawHeight / 2 + (paint.descent() - paint.ascent()) / 2 - paint.descent()
}

/**文本的高度*/
public fun <T> T.textHeight(paint: Paint): Float = paint.descent() - paint.ascent()

public fun TextView.textHeight(): Float = paint.descent() - paint.ascent()

/**文本宽度*/
public fun View.textWidth(paint: Paint?, text: String?): Float = paint?.measureText(text ?: "")
    ?: 0F

public fun TextView.textWidth(text: String?): Float = paint.measureText(text ?: "")
public fun TextView.drawPadding(padding: Int) {
    compoundDrawablePadding = padding
}

public fun TextView.drawPadding(padding: Float) {
    drawPadding(padding.toInt())
}


public fun View.getColor(id: Int): Int = ContextCompat.getColor(context, id)

public fun View.getDimensionPixelOffset(id: Int): Int = resources.getDimensionPixelOffset(id)

/**Match_Parent*/
public fun View.exactlyMeasure(size: Int): Int = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)

public fun View.exactlyMeasure(size: Float): Int = this.exactlyMeasure(size.toInt())

/**Wrap_Content*/
public fun View.atmostMeasure(size: Int): Int = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.AT_MOST)

public fun View.atmostMeasure(size: Float): Int = this.atmostMeasure(size.toInt())


public fun View.longClick(listener: (View) -> Unit) {
    setOnLongClickListener {
        listener.invoke(it)
        true
    }
}

/**焦点变化改变监听*/
public fun EditText.onFocusChange(listener: (Boolean) -> Unit) {
    this.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus -> listener.invoke(hasFocus) }
    listener.invoke(this.isFocused)
}

/**输入框, 按下删除键*/
public fun EditText.onBackPress(listener: (EditText) -> Unit) {
    setOnKeyListener { v, keyCode, keyEvent ->
        return@setOnKeyListener if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_UP) {
            listener.invoke(v as EditText)
            true
        } else {
            false
        }
    }
}

/**发送删除键*/
public fun EditText.sendDelKey() {
    this.del()
}

/**
 * 错误提示
 */
public fun View.error() {
    //Anim.band(this)
}

public fun View.visible() {
    visibility = View.VISIBLE
}

public fun View.gone() {
    visibility = View.GONE
}

public fun View.invisible() {
    visibility = View.INVISIBLE
}

public fun TextView.isEmpty(): Boolean {
    return TextUtils.isEmpty(string())
}

public fun TextView.string(trim: Boolean = true): String {
    var rawText = if (TextUtils.isEmpty(text)) {
        ""
    } else {
        text.toString()
    }
    if (trim) {
        rawText = rawText.trim({ it <= ' ' })
    }
    return rawText
}

/**
 * 返回结果表示是否为空
 */
public fun EditText.checkEmpty(checkPhone: Boolean = false): Boolean {
    if (isEmpty()) {
        error()
        requestFocus()
        return true
    }
    if (checkPhone) {
//        if (isPhone()) {
//
//        } else {
//            error()
//            requestFocus()
//            return true
//        }
    }
    return false
}

public fun EditText.setInputText(text: String?) {
    this.setText(text)
    setSelection(text?.length ?: 0)
}

/**触发删除或回退键*/
public fun EditText.del() {
    this.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
}

/**取消增益滑动效果*/
public fun View.setNoOverScroll() {
    overScrollMode = View.OVER_SCROLL_NEVER
}

/**设置阴影背景*/
public fun View.showShadowViewDrawable(shadowRadius: Int = 6) {
//    val sp = ShadowProperty()
//            .setShadowColor(0x77000000)
//            .setShadowDy((1f * density()).toInt())//y轴偏移
//            .setShadowRadius((shadowRadius * density()).toInt())//阴影半径
//            .setShadowSide(ShadowProperty.ALL)
//    val sd = ShadowViewDrawable(sp, Color.RED, 0f, 0f)
//    ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, null)
//    ViewCompat.setBackground(this, sd)
}

/**自己监听控件的单击事件, 防止系统的不回调*/
public fun View.onSingleTapConfirmed(listener: () -> Boolean) {
    val gestureDetectorCompat = GestureDetectorCompat(context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                return listener.invoke()
            }
        })
    setOnTouchListener { _, event ->
        gestureDetectorCompat.onTouchEvent(event)
        false
    }
}

/**无限循环, 每秒60帧的速度*/
public val View.valueAnimator: ValueAnimator by lazy {
    ValueAnimator.ofInt(0, 100).apply {
        interpolator = LinearInterpolator()
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
        duration = 1000
    }
}

public fun CompoundButton.onChecked(listener: (Boolean) -> Unit) {
    this.setOnCheckedChangeListener { _, isChecked ->
        listener.invoke(isChecked)
    }
    listener.invoke(isChecked)
}

public fun TextView.addPaintFlags(flag: Int, add: Boolean = true, invalidate: Boolean = true) {
    if (add) {
        paint.flags = paint.flags or flag
    } else {
        paint.flags = paint.flags and flag.inv()
    }
    if (invalidate) {
        postInvalidate()
    }
}

public fun TextView.setTextBold(bold: Boolean) {
    addPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG, bold, true)
}

public fun Paint.setPaintFlags(flag: Int, add: Boolean = true) {
    if (add) {
        this.flags = this.flags or flag
    } else {
        this.flags = this.flags and flag.inv()
    }
}

public fun View.hideFromBottom(anim: Boolean = true) {
    if (this.translationY == 0f) {
        //是显示状态
        if (anim) {
            this.animate().setDuration(300)
                .translationY((this.measuredHeight).toFloat())
                .start()
        } else {
            ViewCompat.setTranslationY(this, (this.measuredHeight).toFloat())
        }
    }
}

public fun View.showFromBottom(anim: Boolean = true) {
    if (this.translationY == (this.measuredHeight).toFloat()) {
        //是隐藏状态
        if (anim) {
            this.animate().setDuration(300)
                .translationY(0f)
                .start()
        } else {
            ViewCompat.setTranslationY(this, 0f)
        }
    }
}

public fun View.hideFromTop(anim: Boolean = true) {
    if (this.translationY == 0f) {
        //是显示状态
        if (anim) {
            this.animate().setDuration(300)
                .translationY((-this.measuredHeight).toFloat())
                .start()
        } else {
            ViewCompat.setTranslationY(this, (-this.measuredHeight).toFloat())
        }
    }
}

public fun View.showFromTop(anim: Boolean = true) {
    if (this.translationY == (-this.measuredHeight).toFloat()) {
        //是隐藏状态
        if (anim) {
            this.animate().setDuration(300)
                .translationY(0f)
                .start()
        } else {
            ViewCompat.setTranslationY(this, 0f)
        }
    }
}

/**布局中心的坐标*/
public fun View.layoutCenterX(): Int {
    return left + measuredWidth / 2
}

public fun View.layoutCenterY(): Int {
    return top + measuredHeight / 2
}

public fun EditText.addFilter(filter: InputFilter) {
    val oldFilters = filters
    val newFilters = arrayOfNulls<InputFilter>(oldFilters.size + 1)
    System.arraycopy(oldFilters, 0, newFilters, 0, oldFilters.size)
    newFilters[oldFilters.size] = filter
    filters = newFilters
}

public fun EditText.setFilter(filter: InputFilter) {
    val newFilters = arrayOfNulls<InputFilter>(1)
    newFilters[0] = filter
    filters = newFilters
}

/**
 * 竖直方向上的padding
 */
public fun View.getPaddingVertical(): Int {
    return paddingTop + paddingBottom
}

/**
 * 水平方向上的padding
 */
public fun View.getPaddingHorizontal(): Int {
    return paddingLeft + paddingRight
}

/**
 * 设置视图的宽高
 * */
public fun View.setWidthHeight(width: Int, height: Int) {
    val params = layoutParams
    params.width = width
    params.height = height
    layoutParams = params
}

public fun View.setWidth(width: Int) {
    val params = layoutParams
    params.width = width
    layoutParams = params
}

public fun View.setHeight(height: Int) {
    val params = layoutParams
    params.height = height
    layoutParams = params
}

/**
 * 加载网络图片或者地址
 * */
public fun ImageView.load(url: String?, option: (RequestBuilder<Drawable>.() -> Unit)? = null) {
    if (TextUtils.isEmpty(url)) {
    } else {
        Glide.with(this)
            .load(url)
            .apply {
                //dontAnimate()
                //autoClone()
                diskCacheStrategy(DiskCacheStrategy.ALL)
                override(Target.SIZE_ORIGINAL)

                addListener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                })

                option?.let {
                    it()
                }
            }
            .into(this)
    }
}

/**
 * 获取View, 相对于手机屏幕的矩形
 * */
public fun View.getViewRect(): Rect {
    var offsetX = 0
    var offsetY = 0

    //横屏, 并且显示了虚拟导航栏的时候. 需要左边偏移
    //只计算一次
    (context as? Activity)?.let {
        val decorRect = Rect()
        it.window.decorView.getGlobalVisibleRect(decorRect)
        if (decorRect.width() > decorRect.height()) {
            //横屏了
            //offsetX = -RUtils.navBarHeight(it)
        }
    }

    return getViewRect(offsetX, offsetY)
}

/**
 * 获取View, 相对于手机屏幕的矩形, 带皮阿尼一
 * */
public fun View.getViewRect(offsetX: Int, offsetY: Int): Rect {
    val r = Rect()
    //可见位置的坐标, 超出屏幕的距离会被剃掉
    //image.getGlobalVisibleRect(r)
    val r2 = IntArray(2)
    //val r3 = IntArray(2)
    //相对于屏幕的坐标
    getLocationOnScreen(r2)
    //相对于窗口的坐标
    //image.getLocationInWindow(r3)

    val left = r2[0] + offsetX
    val top = r2[1] + offsetY

    r.set(left, top, left + measuredWidth, top + measuredHeight)
    return r
}

public fun View.marginLayoutParams(config: ViewGroup.MarginLayoutParams.() -> Unit) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.let {
        it.config()
        layoutParams = it
    }
}

public fun View.layoutParams(config: ViewGroup.LayoutParams.() -> Unit) {
    layoutParams.let {
        it.config()
        layoutParams = it
    }
}

public fun <T : View> View.find(id: Int): T? {
    return findViewById<T>(id)
}

/**
 * 旋转到多少度
 * */
public fun View.rotation(rotation: Float, duration: Long = 300, config: ViewPropertyAnimator.() -> Unit = {}) {
    animate().apply {
        rotation(rotation)
        setDuration(duration)
        config()
        start()
    }
}

/**
 * 旋转多少度
 * */
public fun View.rotationBy(rotation: Float, duration: Long = 300, config: ViewPropertyAnimator.() -> Unit = {}) {
    animate().apply {
        rotationBy(rotation)
        setDuration(duration)
        config()
        start()
    }
}

public fun View.setPadding(padding: Int) {
    setPadding(padding, padding, padding, padding)
}

public fun View.setPaddingVertical(padding: Int) {
    setPadding(left, padding, right, padding)
}

public fun View.setPaddingHorizontal(padding: Int) {
    setPadding(padding, top, padding, bottom)
}

/**
 * 判断v, 是否在 view 内
 * */
public fun View.isViewIn(v: View): Boolean {
    if (v.left - scrollX >= 0 &&
        v.right - scrollX <= measuredWidth &&
        v.top - scrollY >= 0 &&
        v.bottom - scrollY == measuredHeight
    ) {
        return true
    }
    return false
}

public fun ViewGroup.inflate(@LayoutRes id: Int, attachToRoot: Boolean = true): View {
    return LayoutInflater.from(context).inflate(id, this, attachToRoot)
}

public fun Rect.set(rectF: RectF) {
    set(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
}