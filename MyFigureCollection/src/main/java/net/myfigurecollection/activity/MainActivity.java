package net.myfigurecollection.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import net.myfigurecollection.R;
import net.myfigurecollection.activity.fragment.CollectionFragment;
import net.myfigurecollection.activity.fragment.GalleryFragment;
import net.myfigurecollection.activity.fragment.NavigationDrawerFragment;
import net.myfigurecollection.activity.fragment.RootFragment;
import net.myfigurecollection.adapter.CollectionSectionsPagerAdapter;
import net.myfigurecollection.api.Root;
import net.myfigurecollection.authentication.AccountGeneral;
import net.myfigurecollection.widgets.SpiceActionBarActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends SpiceActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, GalleryFragment.OnFragmentInteractionListener, SearchView.OnQueryTextListener, ViewPager.OnPageChangeListener {

    final MainActivity cbt = this;
    private final Handler handler = new Handler();
    AccountManagerFuture<Bundle> amf;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public int currentStatus;
    private ActionBar actionBar;
    private CollectionSectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        //supportRequestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);




        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        String user = PreferenceManager.getDefaultSharedPreferences(this).getString("user", null);

        /*if (user != null && getSupportFragmentManager().getFragments().size() <= 1) {
            getGallery(user);
        }*/


        checkCookie();

        mSectionsPagerAdapter = new CollectionSectionsPagerAdapter(this,0, getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(this);


        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < 3; i++) {

            String title = null;

            Locale l = Locale.getDefault();
            switch (i) {
                case 0:
                    title = getString(R.string.title_root_section0).toUpperCase(l);
                    break;
                case 1:
                    title = getString(R.string.title_root_section1).toUpperCase(l);
                    break;
                case 2:
                    title = getString(R.string.title_root_section2).toUpperCase(l);
                    break;
            }
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(title)
                            .setTabListener(new ActionBar.TabListener() {
                                @Override
                                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                                    if (mViewPager!=null)
                                        mViewPager.setCurrentItem(tab.getPosition());
                                }

                                @Override
                                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                                }

                                @Override
                                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                                }
                            }));
        }



    }

    @SuppressWarnings("deprecation")
    private void getCookie(AccountManager am) {


        Account[] accounts;


        if (am != null) {
            accounts = am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

            if (accounts.length > 0) {
                amf = am.getAuthToken(accounts[0], AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY, true, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle result;
                            Intent i;

                            result = future.getResult();
                            if (result.containsKey(AccountManager.KEY_INTENT)) {
                                i = (Intent) result.get(AccountManager.KEY_INTENT);
                                if (i.toString().contains("GrantCredentialsPermissionActivity")) {
                                    // Will have to wait for the user to accept
                                    // the request therefore this will have to
                                    // run in a foreground application
                                    cbt.startActivity(i);
                                } else {
                                    cbt.startActivity(i);
                                }

                            } else {
                                String token = (String) result.get(AccountManager.KEY_AUTHTOKEN);
                                if (token != null)
                                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("cookie", token).commit();

                                /*
                                 * work with token
                                 */

                                // Remember to invalidate the token if the web service rejects it
                                // if(response.isTokenInvalid()){
                                //    accMgr.invalidateAuthToken(authTokenType, token);
                                // }
                                //am.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, token);

                            }
                        } catch (OperationCanceledException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (AuthenticatorException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, handler);

            }
        }
    }

    private void checkCookie() {
        final AccountManager am = AccountManager.get(getBaseContext());
        String cookie = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("cookie", null);
        if (cookie != null) {
            String[] params = cookie.split(";");

            Date d = new Date();
            for (String param : params) {
                String[] ckie = param.split("=");
                if ("expires".equalsIgnoreCase(ckie[0].trim())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss Z", Locale.ENGLISH);

                    try {
                        d = sdf.parse(ckie[1]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }

            if ((new Date()).after(d)) {
                if (am != null) {
                    am.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, cookie);
                    getCookie(am);
                }
            }
        }

    }

    private void getGallery(String user) {

        actionBar.removeAllTabs();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, GalleryFragment.newInstance(user))
                .commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // update the main content by replacing fragments

        mSectionsPagerAdapter.setStatus(position);




    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
        }
        currentStatus = number+1;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        checkCookie();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);

            SearchView searchView = null;
            // Associate searchable configuration with the SearchView
            MenuItem menuItem = menu.findItem(R.id.search);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                SearchManager searchManager =
                        (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                if (menuItem != null) {
                    searchView = (SearchView) menuItem.getActionView();
                    if (searchView != null)
                        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                }
            } else {
                searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
            }

            if (searchView != null) {
                searchView.setIconifiedByDefault(true);
                searchView.setOnQueryTextListener(this);
            }

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
        int id = item.getItemId();

        switch (id) {
            case R.id.search:
                onSearchRequested();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Intent i = new Intent(this, SearchActivity.class);
        i.setAction(Intent.ACTION_SEARCH);
        i.putExtra(SearchManager.QUERY, s);
        startActivity(i);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
        public void onPageSelected(int position) {
            actionBar.setSelectedNavigationItem(position);
        }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

}
