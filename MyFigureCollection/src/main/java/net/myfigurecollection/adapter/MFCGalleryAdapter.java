package net.myfigurecollection.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.octo.android.robospice.request.okhttp.simple.OkHttpBitmapRequest;
import com.octo.android.robospice.spicelist.SpiceListItemView;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;
import com.octo.android.robospice.spicelist.okhttp.OkHttpSpiceArrayAdapter;

import net.myfigurecollection.api.Item;
import net.myfigurecollection.api.Picture;
import net.myfigurecollection.view.GalleryView;
import net.myfigurecollection.view.ItemView;

import java.io.File;
import java.util.List;

public class MFCGalleryAdapter extends OkHttpSpiceArrayAdapter<Picture> {

    // --------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public MFCGalleryAdapter(Context context, OkHttpBitmapSpiceManager spiceManagerBitmap, List<Picture> items) {
        super(context, spiceManagerBitmap, items);
    }

    @Override
    public OkHttpBitmapRequest createRequest(Picture gitHubUser, int imageIndex, int requestImageWidth, int requestImageHeight) {
        File tempFile = new File(getContext().getCacheDir(), "THUMB_IMAGE_TEMP_" + gitHubUser.getId());
        return new OkHttpBitmapRequest(gitHubUser.getSrc(), requestImageWidth,
                requestImageHeight, tempFile);
    }

    @Override
    public SpiceListItemView<Picture> createView(Context context, ViewGroup parent) {
        return new GalleryView(getContext());
    }
}
