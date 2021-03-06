package net.myfigurecollection.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.octo.android.robospice.spicelist.SpiceListItemView;

import net.myfigurecollection.R;
import net.myfigurecollection.api.Picture;

public class GalleryView extends RelativeLayout implements SpiceListItemView<Picture> {

    private ImageView thumbImageView;
    private Picture item;

    public GalleryView(Context context) {
        super(context);
        inflateView(context);
    }

    private void inflateView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_cell_gallery, this);
        this.thumbImageView = (ImageView) this.findViewById(R.id.octo_thumbnail_imageview);

    }

    @Override
    public void update(Picture item) {
        this.item = item;
        if (thumbImageView!=null)
            this.thumbImageView.setBackgroundColor(Color.parseColor(item.getCategory().getColor()));
        this.setBackgroundColor(Color.parseColor(item.getCategory().getColor().replace("#","#22")));
        if ("0".equals(item.getNsfw()))
            this.findViewById(R.id.imageViewNsfw).setVisibility(GONE);
        else
            this.findViewById(R.id.imageViewNsfw).setVisibility(VISIBLE);
    }

    @Override
    public Picture getData() {
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


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}