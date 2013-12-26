package net.myfigurecollection.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.octo.android.robospice.request.okhttp.simple.OkHttpBitmapRequest;
import com.octo.android.robospice.spicelist.SpiceListItemView;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;
import com.octo.android.robospice.spicelist.okhttp.OkHttpSpiceArrayAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import net.myfigurecollection.R;
import net.myfigurecollection.api.Picture;
import net.myfigurecollection.view.GalleryView;

import java.io.File;
import java.util.List;

public class MFCGalleryAdapter extends OkHttpSpiceArrayAdapter<Picture> implements StickyGridHeadersSimpleAdapter {
    private final int mHeaderResId;
    private LayoutInflater mInflater;

    // --------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public MFCGalleryAdapter(Context context, OkHttpBitmapSpiceManager spiceManagerBitmap, List<Picture> items, int headerResId) {
        super(context, spiceManagerBitmap, items);
        this.mHeaderResId = headerResId;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public OkHttpBitmapRequest createRequest(Picture picture, int imageIndex, int requestImageWidth, int requestImageHeight) {
        File tempFile = new File(getContext().getExternalCacheDir(), getContext().getString(R.string.mfc_cache_thumbs_gallery, picture.getId()));
        return new OkHttpBitmapRequest(picture.getSrc(), Integer.parseInt(picture.getResolution().getWidth()),
                Integer.parseInt(picture.getResolution().getHeight()), tempFile);
    }

    @Override
    public SpiceListItemView<Picture> createView(Context context, ViewGroup parent) {
        return new GalleryView(getContext());
    }

    @Override
    public long getHeaderId(int i) {
        Picture item = getItem(i);
        /*CharSequence value;
        if (item instanceof CharSequence) {
            value = (CharSequence)item.getCategory().getName();
        } else {
            value = item.toString();
        }

        return value.subSequence(0, 1).charAt(0);*/
        return Long.parseLong(item.getCategory().getId());
    }

    @Override
    public View getHeaderView(int i, View convertView, ViewGroup viewGroup) {
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mHeaderResId, viewGroup, false);
            holder = new HeaderViewHolder();
            if (convertView != null) {
                holder.textView = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            }
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }


        // set header text as first char in string
        holder.textView.setText(getItem(i).getCategory().getName());
        holder.textView.setBackgroundColor(Color.parseColor(getItem(i).getCategory().getColor()));

        return convertView;
    }

    protected class HeaderViewHolder {
        public TextView textView;
    }


}
