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

import net.myfigurecollection.R;
import net.myfigurecollection.api.Item;
import net.myfigurecollection.view.ItemView;

import java.io.File;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MFCListAdapter extends OkHttpSpiceArrayAdapter<Item> implements StickyListHeadersAdapter {
    private final int mHeaderResId;
    private LayoutInflater mInflater;

    // --------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public MFCListAdapter(Context context, OkHttpBitmapSpiceManager spiceManagerBitmap, List<Item> items, int headerResId) {
        super(context, spiceManagerBitmap, items);
        this.mHeaderResId = headerResId;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public OkHttpBitmapRequest createRequest(Item item, int imageIndex, int requestImageWidth, int requestImageHeight) {
        File tempFile = new File(getContext().getExternalCacheDir(), getContext().getString(R.string.mfc_cache_thumbs_item, item.getData().getId()));

        return new OkHttpBitmapRequest(getContext().getString(R.string.mfc_figure_pics_thumb_root, item.getData().getId()), requestImageWidth,
                requestImageHeight, tempFile);
    }

    @Override
    public SpiceListItemView<Item> createView(Context context, ViewGroup parent) {
        return new ItemView(getContext());
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

        Item item = getItem(i);
        CharSequence string;
        if (item instanceof CharSequence) {
            string = (CharSequence) item;
        } else {
            string = item.getCategory().getName();
        }

        // set header text as first char in string
        holder.textView.setText(string);
        holder.textView.setBackgroundColor(Color.parseColor(item.getCategory().getColor()));

        return convertView;
    }

    @Override
    public long getHeaderId(int i) {
        return Long.parseLong(getItem(i).getCategory().getId());
    }

    protected class HeaderViewHolder {
        public TextView textView;
    }


}
