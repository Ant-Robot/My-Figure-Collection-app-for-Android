package net.myfigurecollection.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.okhttp.simple.OkHttpBitmapRequest;
import com.octo.android.robospice.request.simple.SmallBinaryRequest;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;

import net.myfigurecollection.R;
import net.myfigurecollection.activity.fragment.WebFragment;
import net.myfigurecollection.adapter.MFCGalleryAdapter;
import net.myfigurecollection.api.GalleryMode;
import net.myfigurecollection.api.Item;
import net.myfigurecollection.api.Picture;
import net.myfigurecollection.api.request.GalleryRequest;
import net.myfigurecollection.view.ItemView;
import net.myfigurecollection.widgets.SpiceFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
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
        getMenuInflater().inflate(R.menu.item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        private int currentPage;
        private List<Picture> items;

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
            }
            View rootView = inflater.inflate(R.layout.fragment_item, container, false);
            ItemView itemView = (ItemView) rootView.findViewById(R.id.infos);
            itemView.update(item);

            final ImageView iv = (ImageView) itemView.findViewById(R.id.octo_thumbnail_imageview);

            ViewTreeObserver vto = iv.getViewTreeObserver();
            if (vto != null) {
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        int finalHeight = iv.getMeasuredHeight();
                        int finalWidth = iv.getMeasuredWidth();

                        File tempFile = new File(getActivity().getCacheDir(), "THUMB_IMAGE_TEMP_" + item.getData().getId());
                        OkHttpBitmapRequest req = new OkHttpBitmapRequest("http://myfigurecollection.net/pics/figure/" + item.getData().getId() + ".jpg", finalWidth, finalHeight, tempFile);
                        spiceManagerBinary.execute(req, tempFile.getName(), DurationInMillis.ONE_HOUR, new RequestListener<Bitmap>() {
                            @Override
                            public void onRequestFailure(SpiceException e) {

                            }

                            @Override
                            public void onRequestSuccess(Bitmap bitmap) {
                                iv.setImageBitmap(bitmap);
                            }
                        });
                        return true;
                    }
                });
            }

            currentPage = 1;
            final View viewf = rootView.findViewById(R.id.gallery);

            getGallery((GridView) rootView.findViewById(R.id.gridView));

            ((GridView) rootView.findViewById(R.id.gridView)).setOnItemClickListener(new AdapterView
                    .OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View tview, int position, long id) {
                    final Picture pic = items.get(position);
                    final View tviewf = tview;

                    SmallBinaryRequest req = new SmallBinaryRequest(pic.getSrc().replace("/thumbnails", ""));

                    getActivity().setProgressBarIndeterminateVisibility(true);

                    File tempFile = new File(getActivity().getCacheDir(), "IMAGE_TEMP_" + pic.getId());
                    if (tempFile.exists()) {
                        Bitmap bitmap;


                        bitmap = BitmapFactory.decodeFile(tempFile.getPath());
                        getActivity().setProgressBarIndeterminateVisibility(false);
                        zoomImageFromThumb(tviewf, (ImageView) viewf.findViewById(R.id.expanded_image), viewf, bitmap);
                    } else
                        spiceManager.execute(req, pic.getSrc().replace("/thumbnails", ""), DurationInMillis.ONE_WEEK * 52, new RequestListener<InputStream>() {
                            @Override
                            public void onRequestFailure(SpiceException e) {
                                Toast.makeText(PlaceholderFragment.this.getActivity(), "Error during request: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onRequestSuccess(InputStream file) {
                                File tempFile = new File(getActivity().getCacheDir(), "IMAGE_TEMP_" + pic.getId());

                                OutputStream out = null;
                                try {
                                    out = new FileOutputStream(tempFile);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                                byte buf[] = new byte[1024];
                                int len;
                                try {
                                    while ((len = file.read(buf)) > 0)
                                        out.write(buf, 0, len);
                                    out.close();
                                    file.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                Bitmap bitmap;


                                bitmap = BitmapFactory.decodeFile(tempFile.getPath());
                                getActivity().setProgressBarIndeterminateVisibility(false);
                                zoomImageFromThumb(tviewf, (ImageView) viewf.findViewById(R.id.expanded_image), viewf, bitmap);

                            }
                        });

                }
            });

            mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);


            return rootView;
        }

        private void getGallery(final GridView view) {

            getActivity().setProgressBarIndeterminateVisibility(true);


            GalleryRequest request = new GalleryRequest(Integer.parseInt(item.getData().getId()), ""+currentPage);
            spiceManager.execute(request, request.createCacheKey(), DurationInMillis.ONE_HOUR, new RequestListener<GalleryMode>() {
                @Override
                public void onRequestFailure(SpiceException e) {

                }

                @Override
                public void onRequestSuccess(GalleryMode galleryMode) {
                    int max = Integer.parseInt(galleryMode.getGallery().getNum_pages());

                    if (currentPage == 1){
                        items = galleryMode.getGallery().getPicture();
                        view.setAdapter(new MFCGalleryAdapter(getActivity(), spiceManagerBinary, items));
                    }
                    else
                    {
                        items.addAll(galleryMode.getGallery().getPicture());
                        ((MFCGalleryAdapter)view.getAdapter()).notifyDataSetChanged();
                    }

                    if (currentPage++ >= max) {
                        getActivity().setProgressBarIndeterminateVisibility(false);
                        ((MFCGalleryAdapter)view.getAdapter()).notifyDataSetChanged();
                    }else
                    {
                        getGallery(view);
                    }

                }
            });
        }

        /**
         * "Zooms" in a thumbnail view by assigning the high resolution image to a hidden "zoomed-in"
         * image view and animating its bounds to fit the entire activity content area. More
         * specifically:
         * <p/>
         * <ol>
         * <li>Assign the high-res image to the hidden "zoomed-in" (expanded) image view.</li>
         * <li>Calculate the starting and ending bounds for the expanded view.</li>
         * <li>Animate each of four positioning/sizing properties (X, Y, SCALE_X, SCALE_Y)
         * simultaneously, from the starting bounds to the ending bounds.</li>
         * <li>Zoom back out by running the reverse animation on click.</li>
         * </ol>
         */
        private void zoomImageFromThumb(final View thumbView, final ImageView expandedImageView, final View container, Bitmap file) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                zoomImageThumbAnimated(thumbView, expandedImageView, container, file);
            }else
            {
                // Load the high-resolution "zoomed-in" image.
                expandedImageView.setImageBitmap(file);

                // Calculate the starting and ending bounds for the zoomed-in image. This step
                // involves lots of math. Yay, math.
                final Rect startBounds = new Rect();
                final Rect finalBounds = new Rect();
                final Point globalOffset = new Point();

                // The start bounds are the global visible rectangle of the thumbnail, and the
                // final bounds are the global visible rectangle of the container view. Also
                // set the container view's offset as the origin for the bounds, since that's
                // the origin for the positioning animation properties (X, Y).
                thumbView.getGlobalVisibleRect(startBounds);
                container.getGlobalVisibleRect(finalBounds, globalOffset);
                startBounds.offset(-globalOffset.x, -globalOffset.y);
                finalBounds.offset(-globalOffset.x, -globalOffset.y);

                // Adjust the start bounds to be the same aspect ratio as the final bounds using the
                // "center crop" technique. This prevents undesirable stretching during the animation.
                // Also calculate the start scaling factor (the end scaling factor is always 1.0).
                float startScale;
                if ((float) finalBounds.width() / finalBounds.height()
                        > (float) startBounds.width() / startBounds.height()) {
                    // Extend start bounds horizontally
                    startScale = (float) startBounds.height() / finalBounds.height();
                    float startWidth = startScale * finalBounds.width();
                    float deltaWidth = (startWidth - startBounds.width()) / 2;
                    startBounds.left -= deltaWidth;
                    startBounds.right += deltaWidth;
                } else {
                    // Extend start bounds vertically
                    startScale = (float) startBounds.width() / finalBounds.width();
                    float startHeight = startScale * finalBounds.height();
                    float deltaHeight = (startHeight - startBounds.height()) / 2;
                    startBounds.top -= deltaHeight;
                    startBounds.bottom += deltaHeight;
                }

                // Hide the thumbnail and show the zoomed-in view. When the animation begins,
                // it will position the zoomed-in view in the place of the thumbnail.
                thumbView.setVisibility(View.INVISIBLE);
                expandedImageView.setVisibility(View.VISIBLE);


                // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
                // and show the thumbnail instead of the expanded image.
                final float startScaleFinal = startScale;
                expandedImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        thumbView.setVisibility(View.VISIBLE);
                        expandedImageView.setVisibility(View.GONE);
                    }
                });
            }
        }

        private Animator mCurrentAnimator;
        private int mShortAnimationDuration;

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        private void zoomImageThumbAnimated(final View thumbView, final ImageView expandedImageView, View container, Bitmap file) {
            // If there's an animation in progress, cancel it immediately and proceed with this one.
            if (mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }

            // Load the high-resolution "zoomed-in" image.
            expandedImageView.setImageBitmap(file);

            // Calculate the starting and ending bounds for the zoomed-in image. This step
            // involves lots of math. Yay, math.
            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();
            final Point globalOffset = new Point();

            // The start bounds are the global visible rectangle of the thumbnail, and the
            // final bounds are the global visible rectangle of the container view. Also
            // set the container view's offset as the origin for the bounds, since that's
            // the origin for the positioning animation properties (X, Y).
            thumbView.getGlobalVisibleRect(startBounds);
            container.getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);

            // Adjust the start bounds to be the same aspect ratio as the final bounds using the
            // "center crop" technique. This prevents undesirable stretching during the animation.
            // Also calculate the start scaling factor (the end scaling factor is always 1.0).
            float startScale;
            if ((float) finalBounds.width() / finalBounds.height()
                    > (float) startBounds.width() / startBounds.height()) {
                // Extend start bounds horizontally
                startScale = (float) startBounds.height() / finalBounds.height();
                float startWidth = startScale * finalBounds.width();
                float deltaWidth = (startWidth - startBounds.width()) / 2;
                startBounds.left -= deltaWidth;
                startBounds.right += deltaWidth;
            } else {
                // Extend start bounds vertically
                startScale = (float) startBounds.width() / finalBounds.width();
                float startHeight = startScale * finalBounds.height();
                float deltaHeight = (startHeight - startBounds.height()) / 2;
                startBounds.top -= deltaHeight;
                startBounds.bottom += deltaHeight;
            }

            // Hide the thumbnail and show the zoomed-in view. When the animation begins,
            // it will position the zoomed-in view in the place of the thumbnail.
            thumbView.setAlpha(0f);
            expandedImageView.setVisibility(View.VISIBLE);

            // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
            // the zoomed-in view (the default is the center of the view).
            expandedImageView.setPivotX(0f);
            expandedImageView.setPivotY(0f);

            // Construct and run the parallel animation of the four translation and scale properties
            // (X, Y, SCALE_X, and SCALE_Y).
            AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                            finalBounds.left))
                    .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                            finalBounds.top))
                    .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                    .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
            set.setDuration(mShortAnimationDuration);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCurrentAnimator = null;
                }
            });
            set.start();
            mCurrentAnimator = set;

            // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
            // and show the thumbnail instead of the expanded image.
            final float startScaleFinal = startScale;
            expandedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCurrentAnimator != null) {
                        mCurrentAnimator.cancel();
                    }

                    // Animate the four positioning/sizing properties in parallel, back to their
                    // original values.
                    AnimatorSet set = new AnimatorSet();
                    set
                            .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                            .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                            .with(ObjectAnimator
                                    .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                            .with(ObjectAnimator
                                    .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                    set.setDuration(mShortAnimationDuration);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            thumbView.setAlpha(1f);
                            expandedImageView.setVisibility(View.GONE);
                            mCurrentAnimator = null;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            thumbView.setAlpha(1f);
                            expandedImageView.setVisibility(View.GONE);
                            mCurrentAnimator = null;
                        }
                    });
                    set.start();
                    mCurrentAnimator = set;
                }
            });
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
                default:
                    return WebFragment.newInstance("http://myfigurecollection.net/item/" + ItemActivity.this.item.getData().getId());
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_item_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_item_section2).toUpperCase(l);
            }
            return null;
        }
    }

}
