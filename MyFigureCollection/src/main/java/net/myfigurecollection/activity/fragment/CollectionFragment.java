package net.myfigurecollection.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;

import net.myfigurecollection.R;
import net.myfigurecollection.activity.ItemActivity;
import net.myfigurecollection.activity.MainActivity;
import net.myfigurecollection.adapter.MFCListAdapter;
import net.myfigurecollection.api.CollectionMode;
import net.myfigurecollection.api.Item;
import net.myfigurecollection.api.request.CollectionRequest;
import net.myfigurecollection.authentication.AccountGeneral;
import net.myfigurecollection.authentication.AuthenticatorActivity;
import net.myfigurecollection.widgets.SpiceListFragment;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class CollectionFragment extends SpiceListFragment implements RequestListener<CollectionMode> {


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private OkHttpBitmapSpiceManager spiceManagerBinary = new OkHttpBitmapSpiceManager();
    private List<Item> items;
    private int currentPage = 1;


    public CollectionFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CollectionFragment newInstance(int sectionNumber) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String stringitems = null;
        if (savedInstanceState != null) stringitems = savedInstanceState.getString("items");
        if (stringitems != null && !stringitems.equalsIgnoreCase("null")) {
            Type type = new TypeToken<List<Item>>() {
            }.getType();
            items = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(stringitems, type);
            setListAdapter(new MFCListAdapter(getActivity(), spiceManagerBinary, items));
        } else {
            currentPage = 1;
            getCollection();
        }
    }

    private void getCollection() {
        String user = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("user", null);

        if (user != null) {
            CollectionFragment.this.getActivity().setProgressBarIndeterminateVisibility(true);


            CollectionRequest request = new CollectionRequest(user, currentPage + "", (getArguments().getInt(ARG_SECTION_NUMBER)) + "", "0");
            spiceManager.execute(request, request.createCacheKey(), DurationInMillis.ONE_HOUR, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Bundle out = outState;
        if (out == null) {
            out = new Bundle();
        }

        out.putString("items", new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(items));
        super.onSaveInstanceState(out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        Toast.makeText(this.getActivity(), "Error during request: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(CollectionMode collectionMode) {

        String max = "1";

        if (currentPage == 1) switch (getArguments().getInt(ARG_SECTION_NUMBER, 1)) {
            case 0:
                items = collectionMode.getCollection().getWished().getItem();
                max = collectionMode.getCollection().getWished().getNum_pages();
                break;
            case 1:
                items = collectionMode.getCollection().getOrdered().getItem();
                max = collectionMode.getCollection().getOrdered().getNum_pages();
                break;
            case 2:
                items = collectionMode.getCollection().getOwned().getItem();
                max = collectionMode.getCollection().getOwned().getNum_pages();
                break;
        }
        else switch (getArguments().getInt(ARG_SECTION_NUMBER, 1)) {
            case 0:
                items.addAll(collectionMode.getCollection().getWished().getItem());
                max = collectionMode.getCollection().getWished().getNum_pages();
                break;
            case 1:
                items.addAll(collectionMode.getCollection().getOrdered().getItem());
                max = collectionMode.getCollection().getOrdered().getNum_pages();
                break;
            case 2:
                items.addAll(collectionMode.getCollection().getOwned().getItem());
                max = collectionMode.getCollection().getOwned().getNum_pages();
                break;
        }

        if (currentPage < Integer.parseInt(max)) {
            currentPage++;
            getCollection();

        } else {
            Collections.sort(items);
            CollectionFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
            setListAdapter(new MFCListAdapter(getActivity(), spiceManagerBinary, items));
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent itemView = new Intent(getActivity().getBaseContext(), ItemActivity.class);
        Bundle b = new Bundle();
        b.putString("item", new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(items.get(position)));
        /*b.putString(AuthenticatorActivity.ARG_ACCOUNT_TYPE, AccountGeneral.ACCOUNT_TYPE);
        b.putBoolean(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);*/
        itemView.putExtras(b);
        startActivity(itemView);
    }
}
