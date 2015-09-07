package com.ant_robot.myfigurecollection;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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

import com.ant_robot.mfc.api.pojo.Picture;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
        ProgressBar spinner;
        Button button;

        public MasonryView(CardView itemView) {
            super(itemView);
            holder = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.img);
            textView = (TextView) itemView.findViewById(R.id.img_name);
            spinner = (ProgressBar) itemView.findViewById(R.id.progress);
            button = (Button) itemView.findViewById(R.id.button);
        }

        public void setItem(final Picture picture) {
            String color = picture.getCategory().getColor();
            //Drawable d = getPicture(picture.getSrc());
            String name = picture.getTitle();
            button.setTag(picture.getFull());
            //imageView.setImageDrawable(d);
            textView.setText(name);
            textView.setVisibility((name == null || name.length() == 0) ? View.GONE : View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            holder.setCardBackgroundColor((color != null && color.length() > 0) ? Color.parseColor(color) : Color.WHITE);
            Picasso.with(MasonryAdapter.this.context).load(picture.getMedium()).placeholder(getPicture(picture)).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    spinner.setVisibility(View.GONE);

                }

                @Override
                public void onError() {
                    Log.d("MFC", picture.getMedium());
                    Picasso.with(MasonryAdapter.this.context).load(picture.getSrc()).placeholder(R.drawable.mfclogo).into(imageView);
                    spinner.setVisibility(View.GONE);

                }
            });

        }

        private Drawable getPicture(Picture picture) {
            int width = Integer.parseInt(picture.getResolution().getWidth());
            int height = Integer.parseInt(picture.getResolution().getHeight());

            int ratio = 1;

            while (width/ratio>10 && height/ratio>10){
                ratio*=2;
            }

            Bitmap bitmap = Bitmap.createBitmap(width/ ratio, height / ratio, Bitmap.Config.ALPHA_8);
            Canvas canvas = new Canvas(bitmap);

            canvas.drawColor(Color.TRANSPARENT);
            Drawable transparentDrawable = new BitmapDrawable(Resources.getSystem(), bitmap);
            transparentDrawable.setBounds(0, 0, Integer.parseInt(picture.getResolution().getWidth()), Integer.parseInt(picture.getResolution().getHeight()));

            return transparentDrawable;

        }
    }
}