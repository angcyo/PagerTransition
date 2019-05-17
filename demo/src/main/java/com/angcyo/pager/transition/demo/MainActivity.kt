package com.angcyo.pager.transition.demo

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //激活布局到 状态栏下, 否则transition的时候, 需要减去 状态栏的高度
        enableLayoutFullScreen(this, true)

        supportFragmentManager.beginTransaction().add(R.id.frame_layout, MainActivityFragment()).commitNow()
    }

    fun enableLayoutFullScreen(activity: Activity?, enable: Boolean) {
        if (activity == null) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //window.setStatusBarColor(Color.TRANSPARENT);

            val decorView = window.decorView
            if (enable) {
                //https://blog.csdn.net/xiaonaihe/article/details/54929504
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE /*沉浸式, 用户显示状态, 不会清楚原来的状态*/
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            }
        }
    }

    override fun onBackPressed() {

        val fragments = supportFragmentManager.fragments

        var back = true
        for (i in fragments.size - 1 downTo 0) {
            if (fragments[i].view == null) {
                continue
            }

            if (fragments[i] is BaseTransitionFragment) {
                back = (fragments[i] as BaseTransitionFragment).onBackPressed(this)
            }
            break
        }

        if (back) {
            super.onBackPressed()
        }
    }
}
