package net.myfigurecollection.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import net.myfigurecollection.Fragments.CollectionFragment;
import net.myfigurecollection.R;

/**
 * Created by Climbatize on 13/12/13.
 */
public class CollectionPagerAdapter extends FragmentPagerAdapter {
    int status
            ;
    ViewPager pager;

    public CollectionPagerAdapter(FragmentManager fm, ViewPager pager) {
        super(fm);
        this.status = -1;
        this.pager = pager;
    }

    @Override
    public Fragment getItem(int root) {

        return CollectionFragment.newInstance(status,root);
    }


    @Override
    public int getCount() {
        if (status<0)return 0;
        return 3 ;
    }

    public void setStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return pager.getContext().getString(R.string.cat_figures);
            case 1:
                return pager.getContext().getString(R.string.cat_goods);
            case 2:
                return pager.getContext().getString(R.string.cat_media);

        }
        return super.getPageTitle(position);
    }
}
