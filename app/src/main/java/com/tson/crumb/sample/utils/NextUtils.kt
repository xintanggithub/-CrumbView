package com.tson.crumb.sample.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * @author Tson
 */
class NextUtils {

    companion object {
        private const val TAG = "NextUtils"

        /**
         * 即将打开页面获取数据所用的ID
         */
        const val STEP_ID = "stepId"

        /**
         * 第几页
         */
        const val PAGE_COUNT = "page_count"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { NextUtils() }
    }

    fun nextFragment(act: FragmentActivity, bean: OrganizationPageBean, rt: (OrganizationPageBean) -> Fragment) {
        act.supportFragmentManager.beginTransaction().also {
            it.setBreadCrumbTitle(bean.title)
            it.replace(bean.id, rt(bean))
            it.addToBackStack(null)
            it.commitAllowingStateLoss()
        }
    }
}

data class OrganizationPageBean(var id: Int, var title: String, var map: HashMap<String, Any>) {
    constructor(id: Int, title: String) : this(id, title, hashMapOf())
}
