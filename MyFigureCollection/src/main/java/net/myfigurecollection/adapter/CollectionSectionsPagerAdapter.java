package net.myfigurecollection.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import net.myfigurecollection.R;
import net.myfigurecollection.activity.ItemActivity;
import net.myfigurecollection.activity.MainActivity;
import net.myfigurecollection.activity.fragment.CollectionFragment;
import net.myfigurecollection.activity.fragment.GalleryFragment;
import net.myfigurecollection.activity.fragment.ItemFragment;
import net.myfigurecollection.activity.fragment.RootFragment;
import net.myfigurecollection.activity.fragment.WebFragment;

import java.util.Locale;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class CollectionSectionsPagerAdapter extends FragmentStatePagerAdapter {

    private MainActivity itemActivity;
    private int mStatus;

    public CollectionSectionsPagerAdapter(MainActivity activity, int status, FragmentManager fm) {
        super(fm);
        this.mStatus = status;
        this.itemActivity = activity;
    }

    public void setStatus(int mStatus) {
        this.mStatus = mStatus;
        this.notifyDataSetChanged();

    }

    @Override
    public Fragment getItem(int position) {
        if (itemActivity != null) {

            return CollectionFragment.newInstance(mStatus,position);

        }

        return null;

    }



    public int getItemPosition(Object item) {

        if (item instanceof CollectionFragment) {
            ((CollectionFragment) item).update(mStatus);
        }
        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(item);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return itemActivity.getString(R.string.title_root_section0).toUpperCase(l);
            case 1:
                return itemActivity.getString(R.string.title_root_section1).toUpperCase(l);
            case 2:
                return itemActivity.getString(R.string.title_root_section2).toUpperCase(l);
        }
        return null;
    }
}

