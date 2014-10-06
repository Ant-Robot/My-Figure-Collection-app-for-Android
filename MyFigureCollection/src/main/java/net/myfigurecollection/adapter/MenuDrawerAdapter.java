package net.myfigurecollection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.myfigurecollection.R;

/**
 * @author pierre-michel.villa@infostrates.com
 * @version 1.0
 * @since 21/12/2013
 */
public class MenuDrawerAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final int mResource;

    public MenuDrawerAdapter(Context context, int resource) {
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        if (convertView == null) {
            view = mInflater.inflate(mResource, parent, false);
        } else {
            view = convertView;
        }

        switch (position)
        {
            case 0:
                ((TextView) view.findViewById(R.id.title)).setText(R.string.title_section1);
                ((ImageView) view.findViewById(R.id.picto)).setImageResource(R.drawable.ic_action_wished);
                break;
            case 1:
                ((TextView) view.findViewById(R.id.title)).setText(R.string.title_section2);
                ((ImageView) view.findViewById(R.id.picto)).setImageResource(R.drawable.ic_action_ordered);
                break;
            case 2:
                ((TextView) view.findViewById(R.id.title)).setText(R.string.title_section3);
                ((ImageView) view.findViewById(R.id.picto)).setImageResource(R.drawable.ic_action_owned);
                break;
        }
        return view;
    }
}

