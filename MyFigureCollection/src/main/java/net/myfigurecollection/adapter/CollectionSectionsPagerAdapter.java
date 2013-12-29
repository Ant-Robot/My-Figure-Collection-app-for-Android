package net.myfigurecollection.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.myfigurecollection.R;
import net.myfigurecollection.activity.ItemActivity;
import net.myfigurecollection.activity.MainActivity;
import net.myfigurecollection.activity.fragment.CollectionFragment;
import net.myfigurecollection.activity.fragment.GalleryFragment;
import net.myfigurecollection.activity.fragment.ItemFragment;
import net.myfigurecollection.activity.fragment.WebFragment;

import java.util.Locale;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class CollectionSectionsPagerAdapter extends FragmentPagerAdapter {

    private MainActivity itemActivity;

    public CollectionSectionsPagerAdapter(MainActivity itemActivity, FragmentManager fm) {
        super(fm);
        this.itemActivity = itemActivity;
    }

    @Override
    public Fragment getItem(int position) {
        if (itemActivity != null && itemActivity.getSupportFragmentManager().getFragments().size() <= 1) {

            return CollectionFragment.newInstance(itemActivity.currentStatus,position);

        }

        return null;

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

