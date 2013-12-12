package net.myfigurecollection.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.octo.android.robospice.request.okhttp.simple.OkHttpBitmapRequest;
import com.octo.android.robospice.spicelist.SpiceListItemView;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;
import com.octo.android.robospice.spicelist.okhttp.OkHttpSpiceArrayAdapter;

import net.myfigurecollection.api.Item;
import net.myfigurecollection.view.ItemView;

import java.io.File;
import java.util.List;

public class MFCListAdapter extends OkHttpSpiceArrayAdapter<Item> {

    // --------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public MFCListAdapter(Context context, OkHttpBitmapSpiceManager spiceManagerBitmap, List<Item> items) {
        super(context, spiceManagerBitmap, items);
    }

    @Override
    public OkHttpBitmapRequest createRequest(Item gitHubUser, int imageIndex, int requestImageWidth, int requestImageHeight) {
        File tempFile = new File(getContext().getCacheDir(), "THUMB_IMAGE_TEMP_" + gitHubUser.getData().getId());
        return new OkHttpBitmapRequest("http://myfigurecollection.net/pics/figure/" + gitHubUser.getData().getId()+".jpg", requestImageWidth,
                requestImageHeight, tempFile);
    }

    @Override
    public SpiceListItemView<Item> createView(Context context, ViewGroup parent) {
        return new ItemView(getContext());
    }
}
