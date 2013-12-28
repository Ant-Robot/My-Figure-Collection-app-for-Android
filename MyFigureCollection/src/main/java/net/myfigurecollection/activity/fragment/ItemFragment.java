package net.myfigurecollection.activity.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.okhttp.simple.OkHttpBitmapRequest;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;

import net.myfigurecollection.R;
import net.myfigurecollection.api.Item;
import net.myfigurecollection.api.StatusAnswer;
import net.myfigurecollection.api.request.ItemRequest;
import net.myfigurecollection.view.ItemView;
import net.myfigurecollection.widgets.SpiceFragment;

import java.io.File;
import java.lang.reflect.Type;

/**
 * A placeholder fragment containing a simple view.
 */
public class ItemFragment extends SpiceFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "item";
    private String stringItem;
    private Item item;
    private OkHttpBitmapSpiceManager spiceManagerBinary = new OkHttpBitmapSpiceManager();

    public ItemFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ItemFragment newInstance(String item) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NUMBER, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManagerBinary.start(getActivity());
    }

    @Override
    public void onStop() {
        spiceManagerBinary.shouldStop();
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        stringItem = getArguments().getString(ARG_SECTION_NUMBER);
        if (stringItem != null && !stringItem.equalsIgnoreCase("null")) {
            Type type = new TypeToken<Item>() {
            }.getType();
            item = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(stringItem, type);
            getActivity().supportInvalidateOptionsMenu();
        }

        View rootView = inflater.inflate(R.layout.fragment_item, container, false);

        if (rootView != null) {
            ItemView itemView = (ItemView) rootView.findViewById(R.id.infos);
            itemView.update(item);
            final ImageView iv = (ImageView) itemView.findViewById(R.id.octo_thumbnail_imageview);
            final RatingBar stars = (RatingBar) rootView.findViewById(R.id.ratingBar);
            if (item.getMycollection()!=null){
                if ("-1".equalsIgnoreCase(item.getMycollection().getScore())){
                    stars.setRating(Float.parseFloat(item.getMycollection().getWishability()) / 2.0f);
                    ((TextView)rootView.findViewById(R.id.layout_rating).findViewById(R.id.title)).setText(getString(R.string.Wishability));
                }
                else stars.setRating(Float.parseFloat(item.getMycollection().getScore()) / 2.0f);
            }

            ViewTreeObserver vto = iv.getViewTreeObserver();
            if (vto != null) {
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {


                        int finalHeight = iv.getMeasuredHeight();
                        int finalWidth = iv.getMeasuredWidth();

                        File tempFile = new File(getActivity().getExternalCacheDir(), getActivity().getString(R.string.mfc_cache_thumbs_item, item.getData().getId()));

                        if (!tempFile.exists()) {
                            OkHttpBitmapRequest req = new OkHttpBitmapRequest(getActivity().getString(R.string.mfc_figure_pics_thumb_root, item.getData().getId()), finalWidth, finalHeight, tempFile);
                            spiceManagerBinary.execute(req, tempFile.getName(), DurationInMillis.ONE_HOUR, new RequestListener<Bitmap>() {
                                @Override
                                public void onRequestFailure(SpiceException e) {
                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.request_error, e.getMessage()), Toast.LENGTH_LONG).show();
                                    getActivity().setProgressBarIndeterminateVisibility(false);
                                }

                                @Override
                                public void onRequestSuccess(Bitmap bitmap) {
                                    iv.setImageBitmap(bitmap);
                                }
                            });
                        } else {
                            Bitmap bitmap;
                            bitmap = BitmapFactory.decodeFile(tempFile.getPath());
                            iv.setImageBitmap(bitmap);
                        }


                        return true;
                    }

                });
            }

            final ImageView iv2 = (ImageView) rootView.findViewById(R.id.imageView);

            ViewTreeObserver vto2 = iv2.getViewTreeObserver();
            if (vto2 != null) {
                vto2.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {


                        File tempFilebig = new File(getActivity().getExternalCacheDir(), getActivity().getString(R.string.mfc_cache_pic_item, item.getData().getId()));

                        if (!tempFilebig.exists()) {

                            OkHttpBitmapRequest req = new OkHttpBitmapRequest(getActivity().getString(R.string.mfc_figure_pics_big_root, item.getData().getId()), tempFilebig);
                            spiceManagerBinary.execute(req, tempFilebig.getName(), DurationInMillis.ONE_HOUR, new RequestListener<Bitmap>() {
                                @Override
                                public void onRequestFailure(SpiceException e) {
                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.request_error, e.getMessage()), Toast.LENGTH_LONG).show();
                                    getActivity().setProgressBarIndeterminateVisibility(false);
                                    iv2.setImageResource(R.drawable.no250);
                                }

                                @Override
                                public void onRequestSuccess(Bitmap bitmap) {
                                    iv2.setImageBitmap(bitmap);
                                }
                            });
                        } else {
                            Bitmap bitmap;
                            bitmap = BitmapFactory.decodeFile(tempFilebig.getPath());
                            iv2.setImageBitmap(bitmap);
                        }

                        return true;

                    }

                });
            }

        }


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu; this adds items to the action bar if it is present.

        if (item != null)
            switch (item.getStatus()) {
                case 0:
                    inflater.inflate(R.menu.item_wished, menu);
                    break;
                case 1:
                    inflater.inflate(R.menu.item_ordered, menu);
                    break;
                case 2:
                    inflater.inflate(R.menu.item_owned, menu);
                    break;
            }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean ret = false;
        ItemRequest request = new ItemRequest(this.item, getActivity());

        switch (id) {
            case R.id.action_delete:
                request.remove(this.item.getMycollection().getNumber());
                ret = true;
                break;
            case R.id.action_own:
                ret = true;
                break;
            case R.id.action_order:
                ret = true;
                break;
            case R.id.action_wished:
                ret = true;
                break;
        }

        spiceManager.execute(request, new RequestListener<StatusAnswer>() {
            @Override
            public void onRequestFailure(SpiceException e) {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.request_error, e.getMessage()), Toast.LENGTH_LONG).show();
                getActivity().setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void onRequestSuccess(StatusAnswer statusAnswer) {
                Toast.makeText(getActivity(), statusAnswer.toString(), Toast.LENGTH_LONG).show();
                getActivity().setProgressBarIndeterminateVisibility(false);
            }
        });

        return ret || super.onOptionsItemSelected(item);
    }
}

