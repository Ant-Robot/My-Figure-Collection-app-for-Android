package net.myfigurecollection.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.myfigurecollection.R;
import net.myfigurecollection.activity.ItemActivity;
import net.myfigurecollection.activity.fragment.GalleryFragment;
import net.myfigurecollection.activity.fragment.ItemFragment;
import net.myfigurecollection.activity.fragment.WebFragment;

import java.util.Locale;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ItemSectionsPagerAdapter extends FragmentPagerAdapter {

    private ItemActivity itemActivity;

    public ItemSectionsPagerAdapter(ItemActivity itemActivity, FragmentManager fm) {
        super(fm);
        this.itemActivity = itemActivity;
    }

    @Override
    public Fragment getItem(int position) {
        if (itemActivity != null && itemActivity.item != null)
            switch (position) {
                case 0:
                    itemActivity.supportInvalidateOptionsMenu();
                    return ItemFragment.newInstance(itemActivity.stringItem);
                case 1:
                    return GalleryFragment.newInstance(itemActivity.item.getData().getId(), true);
                default:
                    return WebFragment.newInstance("http://myfigurecollection.net/item/" + itemActivity.item.getData().getId());
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
                return itemActivity.getString(R.string.title_item_section0).toUpperCase(l);
            case 1:
                return itemActivity.getString(R.string.title_item_section1).toUpperCase(l);
            case 2:
                return itemActivity.getString(R.string.title_item_section2).toUpperCase(l);
        }
        return null;
    }
}

