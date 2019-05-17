package com.angcyo.pager.transition.demo

import android.support.v4.app.FragmentManager

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/16
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

object RPager {
    fun pager(fragmentManager: FragmentManager?, id: Int, init: (ViewTransitionConfig.() -> Unit) = {}) {
        fragmentManager?.beginTransaction()
            ?.add(id, PagerTransitionFragment().apply {
                transitionConfig.init()
            })
            ?.commitNow()
    }
}