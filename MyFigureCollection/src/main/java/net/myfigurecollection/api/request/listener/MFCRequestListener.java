package net.myfigurecollection.api.request.listener;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.octo.android.robospice.request.listener.RequestListener;

import net.myfigurecollection.R;

import hugo.weaving.DebugLog;

/**
 * Created by Climbatize on 21/11/13.
 */
public class MFCRequestListener<T> implements RequestListener<T> {
    private Activity activity;

    public MFCRequestListener(Fragment fragment) {
        this.activity = fragment.getActivity();
    }

    public MFCRequestListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onRequestFailure(com.octo.android.robospice.persistence.exception.SpiceException spiceException) {
        Toast.makeText(activity, activity.getResources().getString(R.string.request_error, spiceException.getMessage()), Toast.LENGTH_LONG).show();
        activity.setProgressBarIndeterminateVisibility(false);
    }

    @Override
    @DebugLog
    public void onRequestSuccess(T response) {
        activity.setProgressBarIndeterminateVisibility(false);
        if (response == null) {
            return;
        }
    }


}
