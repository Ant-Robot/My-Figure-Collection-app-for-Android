package com.ant_robot.myfigurecollection;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ant_robot.mfc.api.pojo.Item;
import com.ant_robot.mfc.api.pojo.Picture;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemView> {
    private Context context;
    private List<Item> mGallery;

    public ItemAdapter(Context context, List<Item> gallery) {
        this.context = context;
        this.mGallery = gallery;
    }

    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView layoutView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_picture, parent, false);
        ItemView itemView = new ItemView(layoutView);
        return itemView;
    }

    @Override
    public void onBindViewHolder(ItemView holder, int position) {
        Item picture = mGallery.get(position);
        holder.setItem(picture);
    }


    @Override
    public int getItemCount() {
        return mGallery.size();
    }


    public class ItemView extends RecyclerView.ViewHolder {
        CardView holder;
        ImageView imageView;
        TextView textView;
        ProgressBar spinner;
        Button button;

        public ItemView(CardView itemView) {
            super(itemView);
            holder = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.img);
            textView = (TextView) itemView.findViewById(R.id.item_name);
            spinner = (ProgressBar) itemView.findViewById(R.id.progress);
            button = (Button) itemView.findViewById(R.id.button);
        }

        public void setItem(final Item item) {
            String id = item.getData().getId();
            String color = item.getCategory().getColor();
            //Drawable d = getPicture(item.getSrc());
            String name = item.getData().getName();
            button.setTag(id);
            //imageView.setImageDrawable(d);
            textView.setText(name);
            textView.setVisibility((name == null || name.length() == 0) ? View.GONE : View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            holder.setCardBackgroundColor((color != null && color.length() > 0) ? Color.parseColor(color) : Color.WHITE);
            Picasso.with(ItemAdapter.this.context).load("http://myfigurecollection.net/pics/figure/" + id).placeholder(R.drawable.mfclogo).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    spinner.setVisibility(View.GONE);
                }
            });

        }
    }
}