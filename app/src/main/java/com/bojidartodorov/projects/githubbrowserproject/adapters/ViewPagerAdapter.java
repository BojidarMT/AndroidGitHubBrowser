package com.bojidartodorov.projects.githubbrowserproject.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bojidartodorov.projects.githubbrowserproject.fragments.AllIssuesFragment;
import com.bojidartodorov.projects.githubbrowserproject.fragments.ClosedIssuesFragment;
import com.bojidartodorov.projects.githubbrowserproject.fragments.OpenedIssuesFragment;


/**
 * Created by Bojidar on 28.11.2015 г..
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"Opened", "Closed", "All"};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return OpenedIssuesFragment.newInstance(position + 1);
            case 1:
                return ClosedIssuesFragment.newInstance(position + 1);
            case 2:
                return AllIssuesFragment.newInstance(position + 1);
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
