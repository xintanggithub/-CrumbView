package com.tson.crumb.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tson.crumb.sample.R
import com.tson.crumb.sample.utils.NextUtils
import com.tson.crumb.sample.utils.NextUtils.Companion.PAGE_COUNT
import com.tson.crumb.sample.utils.NextUtils.Companion.STEP_ID
import com.tson.crumb.sample.utils.OrganizationPageBean
import kotlinx.android.synthetic.main.fragment_layout.*

class TestFragment : Fragment() {

    companion object {

        private const val TAG = "OrganizationFragment"

        //实例化fragment
        fun getInstance(bean: OrganizationPageBean): TestFragment {
            return TestFragment().also {
                val bd = Bundle()
                for (mutableEntry in bean.map) {
                    when (val value = mutableEntry.value) {
                        is String -> bd.putString(mutableEntry.key, value)
                        is Int -> bd.putInt(mutableEntry.key, value)
                        is Boolean -> bd.putBoolean(mutableEntry.key, value)
                    }
                }
                it.arguments = bd
            }
        }
    }

    private var pageCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_layout, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 取值，显示到view上
        pageCount = arguments?.getInt(PAGE_COUNT) ?: 0
        textView3.text = "pageCount=$pageCount"

        // 添加一个
        button13.setOnClickListener { addNextPages() }

        // 添加多个
        button14.setOnClickListener { addNextPages(3) }
    }

    private fun addNextPages(page: Int = 0) {
        for (i in 0..page) {
            val count = pageCount + i + 1
            NextUtils.instance.nextFragment(
                requireActivity(),
                OrganizationPageBean(R.id.frameFrag, "标题$count").also {
                    it.map = HashMap<String, Any>().also { m ->
                        m[STEP_ID] = "${100000 + count}"
                        m[PAGE_COUNT] = count
                    }
                }) {
                return@nextFragment getInstance(it)
            }
        }

    }

}