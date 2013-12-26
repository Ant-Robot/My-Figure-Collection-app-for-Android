package net.myfigurecollection.activity.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.simple.SmallBinaryRequest;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;

import net.myfigurecollection.R;
import net.myfigurecollection.adapter.MenuDrawerAdapter;
import net.myfigurecollection.api.User;
import net.myfigurecollection.api.UserMode;
import net.myfigurecollection.api.request.UserRequest;
import net.myfigurecollection.authentication.AccountGeneral;
import net.myfigurecollection.authentication.AuthenticatorActivity;
import net.myfigurecollection.widgets.SpiceFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import hugo.weaving.DebugLog;


/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends SpiceFragment implements RequestListener<UserMode> {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final Object AVATAR_ROOT = "http://s1.tsuki-board.net/pics/avatar/200/";
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;
    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private View header;
    private LayoutInflater inflater;
    private ViewGroup container;
    private OkHttpBitmapSpiceManager spiceManagerBinary = new OkHttpBitmapSpiceManager();


    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        //selectItem(mCurrentSelectedPosition);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);


    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManagerBinary.start(this.getActivity());
    }

    @Override
    public void onStop() {
        spiceManagerBinary.shouldStop();
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        AccountManager am
                = AccountManager.get(getActivity().getBaseContext());

        Account[] accounts = new Account[0];
        if (am != null) {
            accounts = am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        }
        if (accounts.length > 0) {
            refreshAccount(accounts[0]);
        } else {
            if (header != null)
                mDrawerListView.removeHeaderView(header);
            header = null;
        }

    }

    private void refreshAccount(Account account) {
        if (header == null) {
            header = inflater.inflate(R.layout.header_navigation_drawer, container, false);
            mDrawerListView.addHeaderView(header);

            mDrawerListView.setAdapter(new MenuDrawerAdapter(
                    getActionBar().getThemedContext(),
                    R.layout.view_cell_drawer));
            //mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        }

        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("user", account.name).commit();

        ((Button) header.findViewById(R.id.button)).setText(account.name);


        UserRequest request3 = new UserRequest(account.name);
        spiceManager.execute(request3, request3.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AccountManager am
                = AccountManager.get(getActivity().getBaseContext());

        Account[] accounts = new Account[0];

        if (am != null) {
            accounts = am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        }

        this.container = container;
        this.inflater = inflater;

        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        if (accounts.length > 0) {
            refreshAccount(accounts[0]);
        } else {
            Intent signin = new Intent(getActivity().getBaseContext(), AuthenticatorActivity.class);
            Bundle b = savedInstanceState == null ? new Bundle() : savedInstanceState;
            b.putString(AuthenticatorActivity.ARG_ACCOUNT_TYPE, AccountGeneral.ACCOUNT_TYPE);
            b.putBoolean(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
            signin.putExtras(b);
            startActivityForResult(signin, AuthenticatorActivity.REQ_SIGN_IN);
        }

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        /*if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.request_error, e.getMessage()), Toast.LENGTH_LONG).show();
        getActivity().setProgressBarIndeterminateVisibility(false);

        UserMode userMode = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("userMode", ""), UserMode.class);

        if (userMode != null) fillUserInfos(userMode.getUser());
    }

    @Override
    @DebugLog
    public void onRequestSuccess(UserMode userMode) {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("userMode", new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(userMode)).commit();
        fillUserInfos(userMode.getUser());
    }

    @DebugLog
    private boolean fillUserInfos(final User user) {
        SmallBinaryRequest req = new SmallBinaryRequest(AVATAR_ROOT + user.getPicture());


        File tempFile = new File(getActivity().getExternalCacheDir(), "AVATAR_" + user.getPicture());
        if (tempFile.exists()) {
            Bitmap bitmap;


            bitmap = BitmapFactory.decodeFile(tempFile.getPath());
            ((ImageView) header.findViewById(R.id.imageAvatar)).setImageBitmap(bitmap);
        } else
            spiceManager.execute(req, AVATAR_ROOT + user.getPicture(), DurationInMillis.ONE_DAY, new RequestListener<InputStream>() {
                @Override
                public void onRequestFailure(SpiceException e) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.request_error, e.getMessage()), Toast.LENGTH_LONG).show();
                    getActivity().setProgressBarIndeterminateVisibility(false);
                }

                @Override
                public void onRequestSuccess(InputStream file) {
                    File tempFile = new File(getActivity().getExternalCacheDir(), "AVATAR_" + user.getPicture());

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

                    ((ImageView) header.findViewById(R.id.imageAvatar)).setImageBitmap(bitmap);

                }
            });

        return false;
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
