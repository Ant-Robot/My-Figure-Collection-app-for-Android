package net.myfigurecollection.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
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

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
        itemNameTextView.setText(item.getName());
        itemContentTextView.setText(item.getCopyright());
        colorView.setBackgroundColor(Color.parseColor(item.getCategory().getColor()));
        this.setBackgroundColor(Color.parseColor(item.getCategory().getColor().replace("#","#22")));
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