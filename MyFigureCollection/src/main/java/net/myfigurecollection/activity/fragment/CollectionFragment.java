package net.myfigurecollection.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.gson.Gson;
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
import net.myfigurecollection.api.SearchMode;
import net.myfigurecollection.api.request.CollectionRequest;
import net.myfigurecollection.api.request.SearchRequest;
import net.myfigurecollection.widgets.SpiceFragment;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class CollectionFragment extends SpiceFragment implements RequestListener<CollectionMode>, AdapterView.OnItemClickListener {


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_ROOT_NUMBER = "root_number";
    private static final String ARG_SEARCH = "search";
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private final Type type_list_item = new TypeToken<List<Item>>() {
    }.getType();
    private StickyListHeadersListView mList;
    private OkHttpBitmapSpiceManager spiceManagerBinary = new OkHttpBitmapSpiceManager();
    private List<Item> items;
    private int currentPage = 1;
    private int currentRoot = 0;


    public CollectionFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CollectionFragment newInstance(int sectionNumber, int rootNumber) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putInt(ARG_ROOT_NUMBER, rootNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static CollectionFragment newInstance(String search) {
        CollectionFragment fragment = new CollectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, 9);
        args.putString(ARG_SEARCH, search);
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
        //super.onViewCreated(view, savedInstanceState);




        String stringitems = null;
        if (savedInstanceState != null) stringitems = savedInstanceState.getString("items");
        if (stringitems != null && !stringitems.equalsIgnoreCase("null")) {
            items = gson.fromJson(stringitems, type_list_item);
            mList.setAdapter(new MFCListAdapter(getActivity(), spiceManagerBinary, items, R.layout.header));
            mList.setOnItemClickListener(this);
        } else {
            currentPage = 1;
            getCollection();
        }
    }

    private void getCollection() {
        String user = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("user", null);

        if (user != null) {
            CollectionFragment.this.getActivity().setProgressBarIndeterminateVisibility(true);
            CollectionFragment.this.getActivity().setProgressBarIndeterminate(true);


            final int section = getArguments().getInt(ARG_SECTION_NUMBER);
            final int root = getArguments().getInt(ARG_ROOT_NUMBER);

            if (section == 9) {
                SearchRequest request = new SearchRequest(getArguments().getString(ARG_SEARCH));
                spiceManager.execute(request, request.createCacheKey(), DurationInMillis.ONE_HOUR, new RequestListener<SearchMode>() {
                    @Override
                    public void onRequestFailure(SpiceException e) {
                        CollectionFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
                        CollectionFragment.this.getActivity().setProgressBarIndeterminate(false);
                        Toast.makeText(getActivity(),getActivity().getString(R.string.search_failed),Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onRequestSuccess(SearchMode collectionMode) {


                        items = collectionMode.getItem();
                        Collections.sort(items);
                        CollectionFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
                        CollectionFragment.this.getActivity().setProgressBarIndeterminate(false);
                        mList.setAdapter(new MFCListAdapter(getActivity(), spiceManagerBinary, items, R.layout.header));
                        mList.setOnItemClickListener(CollectionFragment.this);


                        PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).edit().putString("status_search", gson.toJson(items, type_list_item)).commit();
                        PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).edit().putLong("status_search_date", new Date().getTime()).commit();
                    }

                }
                );
            } else {
                CollectionRequest request = new CollectionRequest(user, currentPage + "", section + "", root+"");
                spiceManager.execute(request, request.createCacheKey(), DurationInMillis.ONE_HOUR, this);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Bundle out = outState;
        if (out == null) {
            out = new Bundle();
        }

        out.putString("items", gson.toJson(items));
        super.onSaveInstanceState(out);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mList = (StickyListHeadersListView) rootView.findViewById(android.R.id.list);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        final int section = getArguments().getInt(ARG_SECTION_NUMBER);

        if (section < 9)
            ((MainActivity) activity).onSectionAttached(
                    section);
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.request_error, e.getMessage()), Toast.LENGTH_LONG).show();
        getActivity().setProgressBarIndeterminateVisibility(false);
        getActivity().setProgressBarIndeterminate(false);


        String status = "status_na";
        switch (getArguments().getInt(ARG_SECTION_NUMBER, 1)) {
            case 0:
                status = CollectionRequest.COLLECTION_STATUS_WISHED+currentRoot;
                break;
            case 1:
                status = CollectionRequest.COLLECTION_STATUS_ORDERED+currentRoot;
                break;
            case 2:
                status = CollectionRequest.COLLECTION_STATUS_OWNED+currentRoot;
                break;
            default:
                status = SearchRequest.MODE;
        }

        items = gson.fromJson(PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).getString(status, ""), type_list_item);

        mList.setAdapter(new MFCListAdapter(getActivity(), spiceManagerBinary, items, R.layout.header));
        mList.setOnItemClickListener(this);
    }

    @Override
    public void onRequestSuccess(CollectionMode collectionMode) {

        String max = "1";
        String status = "status_na", last = "status_na_date";

        if (items==null || currentPage == 1) switch (getArguments().getInt(ARG_SECTION_NUMBER, 1)) {
            case 0:
                items = collectionMode.getCollection().getWished().getItem();
                max = collectionMode.getCollection().getWished().getNum_pages();
                status = CollectionRequest.COLLECTION_STATUS_WISHED;
                last = CollectionRequest.COLLECTION_STATUS_LAST_WISHED;
                break;
            case 1:
                items = collectionMode.getCollection().getOrdered().getItem();
                max = collectionMode.getCollection().getOrdered().getNum_pages();
                status = CollectionRequest.COLLECTION_STATUS_ORDERED;
                last = CollectionRequest.COLLECTION_STATUS_LAST_ORDERED;
                break;
            case 2:
                items = collectionMode.getCollection().getOwned().getItem();
                max = collectionMode.getCollection().getOwned().getNum_pages();
                status = CollectionRequest.COLLECTION_STATUS_OWNED;
                last = CollectionRequest.COLLECTION_STATUS_LAST_OWNED;
                break;
        }
        else switch (getArguments().getInt(ARG_SECTION_NUMBER, 1)) {
            case 0:
                items.addAll(collectionMode.getCollection().getWished().getItem());
                max = collectionMode.getCollection().getWished().getNum_pages();
                status = CollectionRequest.COLLECTION_STATUS_WISHED;
                last = CollectionRequest.COLLECTION_STATUS_LAST_WISHED;
                break;
            case 1:
                items.addAll(collectionMode.getCollection().getOrdered().getItem());
                max = collectionMode.getCollection().getOrdered().getNum_pages();
                status = CollectionRequest.COLLECTION_STATUS_ORDERED;
                last = CollectionRequest.COLLECTION_STATUS_LAST_ORDERED;
                break;
            case 2:
                items.addAll(collectionMode.getCollection().getOwned().getItem());
                max = collectionMode.getCollection().getOwned().getNum_pages();
                status = CollectionRequest.COLLECTION_STATUS_OWNED;
                last = CollectionRequest.COLLECTION_STATUS_LAST_OWNED;
                break;
        }

        if (currentPage < Integer.parseInt(max)) {
            currentPage++;
            getCollection();

        } else {
            /*if (currentRoot<2)
            {
                currentPage = 1;
                currentRoot++;
                getCollection();
            }*/

            currentPage=1;
            Collections.sort(items);
            CollectionFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
            CollectionFragment.this.getActivity().setProgressBarIndeterminate(false);
            mList.setAdapter(new MFCListAdapter(getActivity(), spiceManagerBinary, items, R.layout.header));
            mList.setOnItemClickListener(this);


            PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).edit().putString(status+currentRoot, gson.toJson(items, type_list_item)).commit();
            PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()).edit().putLong(last+currentRoot, new Date().getTime()).commit();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Intent itemView = new Intent(getActivity().getBaseContext(), ItemActivity.class);
        Bundle b = new Bundle();
        Item src = items.get(position);
        src.setStatus(getArguments().getInt(ARG_SECTION_NUMBER));
        b.putString("item", gson.toJson(src));
        /*b.putString(AuthenticatorActivity.ARG_ACCOUNT_TYPE, AccountGeneral.ACCOUNT_TYPE);
        b.putBoolean(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);*/
        itemView.putExtras(b);
        startActivity(itemView);

    }

    public void update(int mStatus) {
        getArguments().putInt(ARG_SECTION_NUMBER, mStatus);
        getCollection();
    }
}
