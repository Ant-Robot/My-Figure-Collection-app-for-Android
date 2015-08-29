package com.ant_robot.myfigurecollection;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.ant_robot.mfc.api.pojo.Picture;
import com.ant_robot.mfc.api.pojo.PictureGallery;
import com.ant_robot.mfc.api.request.MFCRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements GalleryFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    List<Picture> pictures;
    int currentIndex;
    int currentSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState!=null)
        currentSection = savedInstanceState.getInt("lastSection",-1);

        setContentView(R.layout.activity_main);

       
        mTitle = getTitle();

        // Set up the drawer.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Snackbar.make(findViewById(R.id.container), menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                currentSection = menuItem.getItemId();
                executeRequest();



                return true;
            }
        });



        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                null,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {

                ActionBar actionBar = getSupportActionBar();

                if (actionBar!=null) {
                    actionBar.setTitle(mTitle);
                }

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                ActionBar actionBar = getSupportActionBar();

                if (actionBar!=null) {
                    actionBar.setTitle("");
                }

            }



        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(mDrawerToggle);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        if(currentSection<=0)
        {
            currentSection = R.id.drawer_gallery;
            currentIndex = 0;
            executeRequest();
        }
        else
        {
            findViewById(R.id.loading).setVisibility(View.GONE);
        }

        view.setCheckedItem(currentSection);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState==null)
            outState = new Bundle();
        outState.putInt("lastSection", currentSection);
        outState.putCharSequence("mTitle", mTitle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentSection = savedInstanceState.getInt("lastSection",-1);
        mTitle = savedInstanceState.getCharSequence("mTitle");
    }

    private void executeRequest() {
        switch (currentSection)
        {
            case R.id.drawer_gallery:
            {
                currentIndex = 0;
                retrievePictures();
                return;
            }

            default:
                // update the main content by replacing fragments
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(currentSection))
                        .commit();
                break;
        }
    }

    private void retrievePictures()
    {
        if (currentIndex==0) {
            pictures = new ArrayList<>();
        }

        currentIndex++;
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        SharedPreferences settings = getSharedPreferences(getString(R.string.app_name), 0);
        MFCRequest.INSTANCE.getGalleryService().getGalleryForUser(settings.getString(getString(R.string.prompt_email), ""), currentIndex, new Callback<PictureGallery>() {
            @Override
            public void success(PictureGallery pictureGallery, Response response) {
                Resources res = getResources();
                int num = Integer.parseInt(pictureGallery.getGallery().getNumPictures());
                mTitle = res.getQuantityString(R.plurals.pictures, num, num);

                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(mTitle);
                }

                pictures.addAll(pictureGallery.getGallery().getPicture());

                if (currentIndex==Integer.parseInt(pictureGallery.getGallery().getNumPages())){

                    // update the main content by replacing fragments
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    pictureGallery.getGallery().setPicture(pictures);
                    GalleryFragment fragment = GalleryFragment.newInstance(pictureGallery.getGallery());
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment)
                            .commitAllowingStateLoss();

                    findViewById(R.id.loading).setVisibility(View.GONE);

                }else
                {
                    retrievePictures();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case R.id.drawer_home:
                mTitle = getString(R.string.title_section1);
                break;
            case R.id.drawer_favourite:
                mTitle = getString(R.string.title_section2);
                break;
            case R.id.drawer_settings:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;

            case R.id.action_settings:
                retrievePictures();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Context activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
