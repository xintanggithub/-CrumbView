package com.tson.crumb.sample

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.tson.crumb.sample.fragment.TestFragment
import com.tson.crumb.sample.utils.NextUtils
import com.tson.crumb.sample.utils.NextUtils.Companion.PAGE_COUNT
import com.tson.crumb.sample.utils.NextUtils.Companion.STEP_ID
import com.tson.crumb.sample.utils.OrganizationPageBean
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Tson
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        crumbView.setActivity(this)
        // 加载第一页
        NextUtils.instance.nextFragment(this, OrganizationPageBean(R.id.frameFrag, "标题1").also {
            it.map = HashMap<String, Any>().also { m ->
                m[STEP_ID] = "100001"
                m[PAGE_COUNT] = 1
            }
        }) {
            return@nextFragment TestFragment.getInstance(it)
        }
        // 清除
        clear.setOnClickListener { crumbView.clearFragment() }
    }
}
