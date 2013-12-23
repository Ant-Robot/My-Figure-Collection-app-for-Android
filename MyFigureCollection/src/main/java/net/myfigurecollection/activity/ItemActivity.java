package net.myfigurecollection.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.okhttp.simple.OkHttpBitmapRequest;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;

import net.myfigurecollection.R;
import net.myfigurecollection.activity.fragment.GalleryFragment;
import net.myfigurecollection.activity.fragment.WebFragment;
import net.myfigurecollection.api.Item;
import net.myfigurecollection.view.ItemView;
import net.myfigurecollection.widgets.SpiceFragment;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Locale;

public class ItemActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private String stringItem;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_item);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(false);


        if (savedInstanceState != null) stringItem = savedInstanceState.getString("item");
        else stringItem = getIntent().getStringExtra("item");
        if (stringItem != null && !stringItem.equalsIgnoreCase("null")) {
            Type type = new TypeToken<Item>() {
            }.getType();
            item = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(stringItem, type);
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        if (item != null)
            switch (item.getStatus()) {
                case 0:
                    getMenuInflater().inflate(R.menu.item_wished, menu);
                    break;
                case 1:
                    getMenuInflater().inflate(R.menu.item_ordered, menu);
                    break;
                case 2:
                    getMenuInflater().inflate(R.menu.item_owned, menu);
                    break;
            }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends SpiceFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "item";
        private String stringItem;
        private Item item;
        private OkHttpBitmapSpiceManager spiceManagerBinary = new OkHttpBitmapSpiceManager();

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String item) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, item);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onStart() {
            super.onStart();
            spiceManagerBinary.start(getActivity());
        }

        @Override
        public void onStop() {
            spiceManagerBinary.shouldStop();
            super.onStop();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            stringItem = getArguments().getString(ARG_SECTION_NUMBER);
            if (stringItem != null && !stringItem.equalsIgnoreCase("null")) {
                Type type = new TypeToken<Item>() {
                }.getType();
                item = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(stringItem, type);
                getActivity().supportInvalidateOptionsMenu();
            }

            View rootView = inflater.inflate(R.layout.fragment_item, container, false);

            if (rootView != null) {
                ItemView itemView = (ItemView) rootView.findViewById(R.id.infos);
                itemView.update(item);
                final ImageView iv = (ImageView) itemView.findViewById(R.id.octo_thumbnail_imageview);
                final RatingBar stars = (RatingBar) rootView.findViewById(R.id.ratingBar);
                stars.setRating(Float.parseFloat(item.getMycollection().getScore().equalsIgnoreCase("-1")?item.getMycollection().getWishability():item.getMycollection().getScore())/2.0f);

                ViewTreeObserver vto = iv.getViewTreeObserver();
                if (vto != null) {
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {


                            int finalHeight = iv.getMeasuredHeight();
                            int finalWidth = iv.getMeasuredWidth();

                            File tempFile = new File(getActivity().getExternalCacheDir(), getActivity().getString(R.string.mfc_cache_thumbs_item, item.getData().getId()));

                            if (!tempFile.exists()) {
                                OkHttpBitmapRequest req = new OkHttpBitmapRequest(getActivity().getString(R.string.mfc_figure_pics_thumb_root, item.getData().getId()), finalWidth, finalHeight, tempFile);
                                spiceManagerBinary.execute(req, tempFile.getName(), DurationInMillis.ONE_HOUR, new RequestListener<Bitmap>() {
                                    @Override
                                    public void onRequestFailure(SpiceException e) {

                                    }

                                    @Override
                                    public void onRequestSuccess(Bitmap bitmap) {
                                        iv.setImageBitmap(bitmap);
                                    }
                                });
                            } else {
                                Bitmap bitmap;
                                bitmap = BitmapFactory.decodeFile(tempFile.getPath());
                                iv.setImageBitmap(bitmap);
                            }


                            return true;
                        }

                    });
                }

                final ImageView iv2 = (ImageView) rootView.findViewById(R.id.imageView);

                ViewTreeObserver vto2 = iv2.getViewTreeObserver();
                if (vto2 != null) {
                    vto2.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {


                            File tempFilebig = new File(getActivity().getExternalCacheDir(), getActivity().getString(R.string.mfc_cache_pic_item, item.getData().getId()));

                            if (!tempFilebig.exists()) {
                                OkHttpBitmapRequest req = new OkHttpBitmapRequest(getActivity().getString(R.string.mfc_figure_pics_big_root, item.getData().getId()), tempFilebig);
                                spiceManagerBinary.execute(req, tempFilebig.getName(), DurationInMillis.ONE_HOUR, new RequestListener<Bitmap>() {
                                    @Override
                                    public void onRequestFailure(SpiceException e) {

                                    }

                                    @Override
                                    public void onRequestSuccess(Bitmap bitmap) {
                                        iv2.setImageBitmap(bitmap);
                                    }
                                });
                            } else {
                                Bitmap bitmap;
                                bitmap = BitmapFactory.decodeFile(tempFilebig.getPath());
                                iv2.setImageBitmap(bitmap);
                            }

                            return true;

                        }

                    });
                }

            }


            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(ItemActivity.this.stringItem);
                case 1:
                    return GalleryFragment.newInstance(ItemActivity.this.item.getData().getId(), true);
                default:
                    return WebFragment.newInstance("http://myfigurecollection.net/item/" + ItemActivity.this.item.getData().getId());
            }

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
                    return getString(R.string.title_item_section0).toUpperCase(l);
                case 1:
                    return getString(R.string.title_item_section1).toUpperCase(l);
                case 2:
                    return getString(R.string.title_item_section2).toUpperCase(l);
            }
            return null;
        }
    }

}
