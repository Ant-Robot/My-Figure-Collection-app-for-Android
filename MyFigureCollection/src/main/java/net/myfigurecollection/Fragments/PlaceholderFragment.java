package net.myfigurecollection.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;

import net.myfigurecollection.MainActivity;
import net.myfigurecollection.R;
import net.myfigurecollection.adapter.MFCListAdapter;
import net.myfigurecollection.api.CollectionMode;
import net.myfigurecollection.api.request.CollectionRequest;
import net.myfigurecollection.widgets.SpiceListFragment;

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
    private View rootView;


    public PlaceholderFragment() {
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PlaceholderFragment.this.getActivity().setProgressBarIndeterminateVisibility(true);


        CollectionRequest request = new CollectionRequest("Climbatize", "0", "0", "0");
        spiceManager.execute(request, request.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, this);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
       /* TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));*/

        return rootView;
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
        setListAdapter(new MFCListAdapter(getActivity(),spiceManagerBinary,collectionMode.getCollection().getWished().getItem()));

    }
}
