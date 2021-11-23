
### 1.示例
<img  src="https://img-blog.csdnimg.cn/679fccef25c4424aa16a8cf54228240f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAVHNvblRhbmc=,size_20,color_FFFFFF,t_70,g_se,x_16"  width=450/>

### 2.CrumbView
#### 2.1自定义CrumbView
```java

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.model.view.R;

public class CrumbView extends HorizontalScrollView {

    private static final String LAB_TAG = "LAB";
    private int LIGHT_COLOR, DARK_COLOR;
    private Resources mRes;
    private LinearLayout mContainer, rootView;
    private FragmentManager mFragmentManager;

    public CrumbView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRes = context.getResources();
        TypedArray typedArray = mRes.obtainAttributes(attrs, R.styleable.CrumbViewAttrs);
        try {
            LIGHT_COLOR = typedArray.getColor(R.styleable.CrumbViewAttrs_light_color, mRes.getColor(R.color.light_color));
            DARK_COLOR = typedArray.getColor(R.styleable.CrumbViewAttrs_dark_color, mRes.getColor(R.color.dark_color));
        } finally {
            typedArray.recycle();
        }
        rootView = new LinearLayout(context);
        rootView.setOrientation(LinearLayout.HORIZONTAL);
        rootView.setGravity(Gravity.CENTER_VERTICAL);
        addView(rootView);
    }

    private void initView(Context context, boolean useLab) {
        mContainer = new LinearLayout(context);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);
        int left = 0;
        if (!useLab) {
            left = mRes.getDimensionPixelOffset(R.dimen.crumb_view_padding);
        }
        mContainer.setPadding(left, 0,
                mRes.getDimensionPixelOffset(R.dimen.crumb_view_padding), 0);
        mContainer.setGravity(Gravity.CENTER_VERTICAL);
        rootView.addView(mContainer);
    }

    public void setActivity(FragmentActivity activity) {
        setActivity(activity, null);
    }

    public void setActivity(FragmentActivity activity, String lab) {
        mFragmentManager = activity.getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                updateCrumbs();
            }
        });
        boolean useLab = false;
        if (null != lab) {
            useLab = true;
            setLab(activity, lab);
        }
        initView(activity, useLab);
        updateCrumbs();
    }

    private void setLab(FragmentActivity activity, String lab) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.crumb_item_header, null);
        TextView tv = itemView.findViewById(R.id.crumb_name);
        tv.setText(lab);
        tv.setTextColor(DARK_COLOR);
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        rootView.addView(itemView);
    }

    public void clearFragment() {
        // 嵌套的fragment数量
        int numFrags = mFragmentManager.getBackStackEntryCount();
        try {
            for (int i = 0; i < numFrags; i++) {
                mFragmentManager.popBackStack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCrumbs() {
        // 嵌套的fragment数量
        int numFrags = mFragmentManager.getBackStackEntryCount();
        // 面包屑的数量
        int numCrumbs = mContainer.getChildCount();

        for (int i = 0; i < numFrags; i++) {
            final FragmentManager.BackStackEntry backStackEntry = mFragmentManager.getBackStackEntryAt(i);
            if (i < numCrumbs) {
                View view = mContainer.getChildAt(i);
                Object tag = view.getTag();
                if (tag != backStackEntry) {
                    for (int j = i; j < numCrumbs; j++) {
                        mContainer.removeViewAt(i);
                    }
                    numCrumbs = i;
                }
            }
            if (i >= numCrumbs) {
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.crumb_item_layout, null);
                TextView tv = itemView.findViewById(R.id.crumb_name);
                tv.setText(backStackEntry.getBreadCrumbTitle());
                itemView.setTag(backStackEntry);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FragmentManager.BackStackEntry bse;
                        if (v.getTag() instanceof FragmentManager.BackStackEntry) {
                            bse = (FragmentManager.BackStackEntry) v.getTag();
                            mFragmentManager.popBackStack(bse.getId(), 0);
                        } else {
                            //全部回退
                            int count = mFragmentManager.getBackStackEntryCount();
                            if (count > 0) {
                                bse = mFragmentManager.getBackStackEntryAt(0);
                                mFragmentManager.popBackStack(bse.getId(), 0);
                            }
                        }

                    }
                });
                mContainer.addView(itemView);
            }
        }
        numCrumbs = mContainer.getChildCount();
        while (numCrumbs > numFrags) {
            mContainer.removeViewAt(numCrumbs - 1);
            numCrumbs--;
        }

        //调整可见性
        for (int i = 0; i < numCrumbs; i++) {
            final View child = mContainer.getChildAt(i);
            // 高亮
            highLightIndex(child, !(i < numCrumbs - 1));
        }

        // 滑动到最后一个
        post(new Runnable() {
            @Override
            public void run() {
                fullScroll(ScrollView.FOCUS_RIGHT);
            }
        });
    }

    public void highLightIndex(View view, boolean highLight) {
        TextView text = view.findViewById(R.id.crumb_name);
        ImageView image = view.findViewById(R.id.crumb_icon);
        if (highLight) {
            text.setTextColor(LIGHT_COLOR);
            image.setVisibility(View.GONE);
        } else {
            text.setTextColor(DARK_COLOR);
            image.setVisibility(View.VISIBLE);
        }
    }
}

```
#### 2.2 自定义参数
```xml
    <declare-styleable name="CrumbViewAttrs">
        <attr name="light_color" format="color" />
        <attr name="dark_color" format="color" />
        <attr name="padding" format="dimension" />
    </declare-styleable>
```

#### 2.3 面包屑字体颜色色
```xml
    <color name="light_color">#7F7F7F</color>
    <color name="dark_color">#2F73F6</color>
```
#### 2.4 面包屑布局crumb_item_header
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:gravity="center_vertical"
    android:orientation="horizontal"
    android:paddingStart="8dp">

    <TextView
        android:id="@+id/crumb_name"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="4dp"
        android:textColor="@color/light_color"
        android:textSize="17sp" />

    <ImageView
        android:id="@+id/crumb_icon"
        android:layout_width="15dp"
        android:layout_height="15dp"
        android:scaleType="fitXY"
        android:layout_marginEnd="4dp"
        android:src="@drawable/right_arrow" />

</LinearLayout>
```
#### 2.5 面包屑布局crumb_item_layout
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:gravity="center_vertical"
    android:orientation="horizontal">

    <TextView
        android:id="@+id/crumb_name"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textColor="@color/light_color"
        android:layout_marginEnd="4dp"
        android:textSize="17sp" />

    <ImageView
        android:id="@+id/crumb_icon"
        android:layout_width="15dp"
        android:layout_height="15dp"
        android:layout_marginEnd="4dp"
        android:scaleType="fitXY"
        android:src="@drawable/right_arrow" />
</LinearLayout>
```
#### 2.6 面包屑翻页工具类
```java
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.model.view.easy.fargment.BaseFragment

/**
 *  Date 2020-10-20 13:59
 *
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

    fun nextFragment(
        act: FragmentActivity,
        bean: OrganizationPageBean,
        rt: (OrganizationPageBean) -> Fragment
    ) {
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
```

### 3.测试代码

#### 3.1 测试Activity布局
```xml
<LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <Button
            android:id="@+id/clear"
            android:layout_width="wrap_content"
            android:text="清除"
            android:layout_height="wrap_content"/>

        <com.model.view.crumb.CrumbView
            android:id="@+id/crumbView"
            android:layout_width="match_parent"
            android:layout_height="48dp" />

        <FrameLayout
            android:id="@+id/frameFrag"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">

        </FrameLayout>

    </LinearLayout>
```
#### 3.2 测试fragment

##### 3.2.1 测试fragment布局(fragment就自己创建吧)
```xml
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <Button
            android:id="@+id/button13"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="加一个" />

        <TextView
            android:id="@+id/textView3"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:padding="10dp"
            android:text="TextView"
            android:textSize="22sp" />

        <Button
            android:id="@+id/button14"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="加多个" />
    </LinearLayout>
```

##### 3.2.2 fragment测试代码
```java
    companion object {

        private const val TAG = "OrganizationFragment"
	//实例化fragment
        fun getInstance(bean: OrganizationPageBean): OrganizationFragment {
            return OrganizationFragment().also {
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


	// 下一页（添加一层面包屑和fragment），如果加多个，执行多次即可
	button13.setOnClickListener {
            NextUtils.instance.nextFragment(
                activity!!,
                OrganizationPageBean(R.id.frameFrag, "标题${++pageCount}").also {
                    it.map = HashMap<String, Any>().also { m ->
                        m[STEP_ID] = "${100000 + pageCount}"
                        m[PAGE_COUNT] = pageCount
                    }
                }) {
                return@nextFragment getInstance(it)
            }
        }



```

#### 3.3 测试Activity
- 在onCreate中执行
```java
	// 初始加载第一页
	NextUtils.instance.nextFragment(this, OrganizationPageBean(R.id.frameFrag, "标题1").also {
            it.map = HashMap<String, Any>().also { m ->
                m[STEP_ID] = "100001"
                m[PAGE_COUNT] = 1
            }
        }) {
            return@nextFragment OrganizationFragment.getInstance(it)
        }
```
- 清除面包屑和所有fragment页
```java
	// 清除所有的fragment和面包屑
	clear.setOnClickListener {
            crumbView.clearFragment()
        }
```

以上代码，直接copy即可使用。
