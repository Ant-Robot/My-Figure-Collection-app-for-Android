package com.ant_robot.myfigurecollection;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ant_robot.mfc.api.pojo.Picture;

import java.util.List;

public class MasonryAdapter extends RecyclerView.Adapter<MasonryAdapter.MasonryView> {
    private Context context;
    private List<Picture> mGallery;

    public MasonryAdapter(Context context, List<Picture> gallery) {
        this.context = context;
        this.mGallery = gallery;
    }

    @Override
    public MasonryView onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView layoutView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_picture, parent, false);
        MasonryView masonryView = new MasonryView(layoutView);
        return masonryView;
    }

    @Override
    public void onBindViewHolder(MasonryView holder, int position) {
        Picture picture = mGallery.get(position);
        holder.setItem(picture);
    }


    @Override
    public int getItemCount() {
        return mGallery.size();
    }


    public class MasonryView extends RecyclerView.ViewHolder {
        CardView holder;
        ImageView imageView;
        TextView textView;


        public MasonryView(CardView itemView) {
            super(itemView);
            holder = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.img);
            textView = (TextView) itemView.findViewById(R.id.img_name);
        }

        public void setItem(Picture picture) {
            String color = picture.getCategory().getColor();
            Drawable d = getPicture(picture.getSrc());
            String name = picture.getTitle();
            imageView.setImageDrawable(d);
            textView.setText(name);
            textView.setVisibility((name == null || name.length() == 0) ? View.GONE : View.VISIBLE);
            holder.setCardBackgroundColor((color != null && color.length() > 0) ? Color.parseColor(color) : Color.WHITE);

        }

        private Drawable getPicture(String src) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return MasonryAdapter.this.context.getDrawable(R.drawable.mfclogo);
            }

            return MasonryAdapter.this.context.getResources().getDrawable(R.drawable.mfclogo);

        }
    }
}