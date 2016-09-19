package com.steven.bk30_recyclerviewtablayoutpullrefresh.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> list = null;
    private String[] arrTabTitles = null;

    public MyPagerAdapter(FragmentManager fm, List<Fragment> list, String[] arrTabTitles) {
        super(fm);
        this.list = list;
        this.arrTabTitles = arrTabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return arrTabTitles[position];
    }
}
