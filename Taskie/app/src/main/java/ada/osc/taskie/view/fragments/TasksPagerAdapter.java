package ada.osc.taskie.view.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnPageChange;

public class TasksPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();

    public TasksPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setItems(List<Fragment> fragments) {
        fragmentList.clear();
        fragmentList.addAll(fragments);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == (getCount() - 1)){
            return "Favorite tasks";
        }
        return "Page " + Integer.toString( position + 1);
    }



}
