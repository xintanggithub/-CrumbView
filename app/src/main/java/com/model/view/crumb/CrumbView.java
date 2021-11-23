package com.model.view.crumb;

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

import com.tson.crumb.sample.R;

/**
 * @author Tson
 */
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

    private void setLab(final FragmentActivity activity, String lab) {
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

