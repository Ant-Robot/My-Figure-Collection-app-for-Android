package net.myfigurecollection.view;

/**
 * Created by Climbatize on 11/12/13.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.octo.android.robospice.spicelist.SpiceListItemView;

import net.myfigurecollection.R;
import net.myfigurecollection.api.Item;

public class ItemView extends RelativeLayout implements SpiceListItemView<Item> {

    private TextView itemNameTextView;
    private TextView itemContentTextView;
    private ImageView thumbImageView;
    private View colorView;
    private Item item;

    public ItemView(Context context) {
        super(context);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_cell_item, this);
        this.itemNameTextView = (TextView) this.findViewById(R.id.item_name_textview);
        this.itemContentTextView = (TextView) this.findViewById(R.id.item_content_textview);
        this.thumbImageView = (ImageView) this.findViewById(R.id.octo_thumbnail_imageview);
        this.colorView = this.findViewById(R.id.view_category);
    }

    @Override
    public void update(Item item) {
        this.item = item;
        itemNameTextView.setText(item.getData().getName());
        itemContentTextView.setText(String.valueOf(item.getData().getPrice()));
        colorView.setBackgroundColor(Color.parseColor(item.getCategory().getColor()));
    }

    @Override
    public Item getData() {
        return item;
    }

    @Override
    public ImageView getImageView(int imageIndex) {
        return thumbImageView;
    }

    @Override
    public int getImageViewCount() {
        return 1;
    }
}