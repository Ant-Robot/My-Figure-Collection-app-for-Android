package net.myfigurecollection.activity.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;

import net.myfigurecollection.R;
import net.myfigurecollection.adapter.MFCGalleryAdapter;
import net.myfigurecollection.adapter.MFCListAdapter;
import net.myfigurecollection.api.GalleryMode;
import net.myfigurecollection.api.Item;
import net.myfigurecollection.api.Picture;
import net.myfigurecollection.api.request.CollectionRequest;
import net.myfigurecollection.api.request.GalleryRequest;
import net.myfigurecollection.widgets.SpiceFragment;

import java.lang.reflect.Type;
import java.util.List;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class GalleryFragment extends SpiceFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "current gallery";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private OkHttpBitmapSpiceManager spiceManagerBinary = new OkHttpBitmapSpiceManager();

    private List<Picture> items;
    private int currentPage;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(String param1) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_PARAM1,mParam1);
        outState.putString("items", new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(items));
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String stringitems = null;
        if (savedInstanceState != null) stringitems = savedInstanceState.getString("items");
        if (stringitems != null && !stringitems.equalsIgnoreCase("null")) {
            Type type = new TypeToken<List<Item>>() {
            }.getType();
            items = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(stringitems, type);
            ((GridView)view.findViewById(R.id.gridView)).setAdapter(new MFCGalleryAdapter(getActivity(), spiceManagerBinary, items));
        } else {
            currentPage = 1;
            getGallery((GridView)view.findViewById(R.id.gridView));
        }
    }
    private void getGallery(final GridView view) {
        String user = mParam1;

        if (user != null) {
            GalleryFragment.this.getActivity().setProgressBarIndeterminateVisibility(true);


            GalleryRequest request = new GalleryRequest(user,"0");
            spiceManager.execute(request, request.createCacheKey(), DurationInMillis.ONE_HOUR, new RequestListener<GalleryMode>() {
                @Override
                public void onRequestFailure(SpiceException e) {

                }

                @Override
                public void onRequestSuccess(GalleryMode galleryMode) {
                    items = galleryMode.getGallery().getPicture();
                    view.setAdapter(new MFCGalleryAdapter(getActivity(), spiceManagerBinary, items));
                    GalleryFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);

                }
            });
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
