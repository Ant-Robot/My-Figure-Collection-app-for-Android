package net.myfigurecollection.api.request.listener;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.octo.android.robospice.request.listener.RequestListener;

import net.myfigurecollection.api.CollectionMode;

/**
 * Created by Climbatize on 21/11/13.
 */
public class MFCRequestListener<T> implements RequestListener<T> {
    private Fragment fragment;

    public MFCRequestListener(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onRequestFailure(com.octo.android.robospice.persistence.exception.SpiceException spiceException) {
        Toast.makeText(fragment.getActivity(), "Error during request: " + spiceException.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(T collectionMode) {
        fragment.getActivity().setProgressBarIndeterminateVisibility(false);
        if (collectionMode == null) {
            return;
        }

        Log.d("MFC", collectionMode.toString());
    }
}
