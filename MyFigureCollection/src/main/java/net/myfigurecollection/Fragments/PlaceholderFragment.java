package net.myfigurecollection.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;

import net.myfigurecollection.MainActivity;
import net.myfigurecollection.R;
import net.myfigurecollection.adapter.MFCListAdapter;
import net.myfigurecollection.api.CollectionMode;
import net.myfigurecollection.api.Item;
import net.myfigurecollection.api.request.CollectionRequest;
import net.myfigurecollection.widgets.SpiceListFragment;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends SpiceListFragment implements RequestListener<CollectionMode> {


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private OkHttpBitmapSpiceManager spiceManagerBinary = new OkHttpBitmapSpiceManager();
    private List<Item> items;
    private int currentPage = 1;


    public PlaceholderFragment() {
    }

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

        /*SearchRequest request1 = new SearchRequest("Saber");
        GalleryRequest request2 = new GalleryRequest("Climbatize", "0");
        UserRequest request3 = new UserRequest("Climbatize");
        ConnectionRequest request4 = new ConnectionRequest("Climbatize","160184");
        spiceManager.execute(request, request.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new MFCRequestListener<CollectionMode>(this));
        spiceManager.execute(request1, request1.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new MFCRequestListener<SearchMode>(this));
        spiceManager.execute(request2, request2.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new MFCRequestListener<GalleryMode>(this));
        spiceManager.execute(request3, request3.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new MFCRequestListener<UserMode>(this));
        spiceManager.execute(request4, request4.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, new MFCRequestListener<String>(this));
    */


    }

    private void getCollection() {
        String user = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("user", null);

        if (user != null) {
            PlaceholderFragment.this.getActivity().setProgressBarIndeterminateVisibility(true);


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
       /* TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));*/

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

        } else
        {
            Collections.sort(items);
            PlaceholderFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
            setListAdapter(new MFCListAdapter(getActivity(), spiceManagerBinary, items));
        }

    }
}
