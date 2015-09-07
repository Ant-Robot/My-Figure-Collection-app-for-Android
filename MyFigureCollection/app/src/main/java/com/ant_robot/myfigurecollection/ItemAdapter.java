package com.ant_robot.myfigurecollection;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
import com.squareup.picasso.Transformation;

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
        CardView layoutView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
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
        ImageView imageCircle;
        TextView textView;
        TextView textViewCat;
        ProgressBar spinner;
        Button button;

        public ItemView(CardView itemView) {
            super(itemView);
            holder = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.img);
            imageCircle = (ImageView) itemView.findViewById(R.id.img_bg);
            textView = (TextView) itemView.findViewById(R.id.item_name);
            textViewCat = (TextView) itemView.findViewById(R.id.textView_category);
            spinner = (ProgressBar) itemView.findViewById(R.id.progress);
            button = (Button) itemView.findViewById(R.id.button);
        }

        public void setItem(final Item item) {
            String id = item.getData().getId();
            String color = item.getCategory().getColor();
            //Drawable d = getPicture(item.getSrc());
            String name = item.getData().getName();
            button.setTag(R.id.item_name,item);
            button.setTag(R.id.img_container,imageView);
            //imageView.setImageDrawable(d);
            textView.setText(name);
            textView.setVisibility((name == null || name.length() == 0) ? View.GONE : View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);

            final int colorCat = (color != null && color.length() > 0) ? Color.parseColor(color) : Color.GRAY;
            imageCircle.getBackground().setColorFilter(colorCat, PorterDuff.Mode.MULTIPLY);
            textViewCat.setText(item.getCategory().getName());
            textViewCat.setTextColor(colorCat);

            Picasso.with(ItemAdapter.this.context).load("http://myfigurecollection.net/pics/figure/" + id+".jpg").placeholder(R.drawable.mfclogo).transform(new CircleTransform()).into(imageView, new Callback() {
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

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}