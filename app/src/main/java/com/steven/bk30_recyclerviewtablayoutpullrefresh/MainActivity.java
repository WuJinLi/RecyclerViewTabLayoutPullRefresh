package com.steven.bk30_recyclerviewtablayoutpullrefresh;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.steven.bk30_recyclerviewtablayoutpullrefresh.adapter.MyPagerAdapter;
import com.steven.bk30_recyclerviewtablayoutpullrefresh.fragment.DummyFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Context mContext = this;
    private ViewPager viewPager_main;
    private List<Fragment> totalList = new ArrayList<>();
    private TabLayout tabLayout_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化TAB导航条及ViewPager
        initTabsAndViewPager();
    }

    private void initTabsAndViewPager() {
        // 隐藏状态栏，全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tabLayout_main = (TabLayout) findViewById(R.id.tabLayout_main);
        String[] arrTabTitles = getResources().getStringArray(R.array.arrTitles);
        viewPager_main = (ViewPager) findViewById(R.id.viewPager_main);

        for (int i = 0; i < arrTabTitles.length; i++) {
            DummyFragment fragment = DummyFragment.newInstance(i);
            totalList.add(fragment);
        }
        PagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), totalList,
                arrTabTitles);
        viewPager_main.setAdapter(adapter);

        tabLayout_main.setupWithViewPager(viewPager_main);
    }
}
