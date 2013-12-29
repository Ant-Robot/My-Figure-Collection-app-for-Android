package net.myfigurecollection.activity.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.simple.SmallBinaryRequest;
import com.octo.android.robospice.spicelist.okhttp.OkHttpBitmapSpiceManager;

import net.myfigurecollection.R;
import net.myfigurecollection.adapter.MFCGalleryAdapter;
import net.myfigurecollection.api.GalleryMode;
import net.myfigurecollection.api.Picture;
import net.myfigurecollection.api.request.GalleryRequest;
import net.myfigurecollection.utils.Utils;
import net.myfigurecollection.widgets.SpiceFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends SpiceFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "current gallery";
    private static final String ARG_PARAM2 = "type gallery";
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private boolean mParam2;
    private OkHttpBitmapSpiceManager spiceManagerBinary = new OkHttpBitmapSpiceManager();
    private List<Picture> items;
    private int currentPage;

    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(String param1) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static GalleryFragment newInstance(String param1, boolean param2) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putBoolean(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getBoolean(ARG_PARAM2, false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_PARAM1, mParam1);
        outState.putBoolean(ARG_PARAM2, mParam2);
        outState.putString("items", new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(items));
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String stringitems = null;
        if (savedInstanceState != null) stringitems = savedInstanceState.getString("items");
        if (stringitems != null && !stringitems.equalsIgnoreCase("null")) {
            Type type = new TypeToken<List<Picture>>() {
            }.getType();
            items = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(stringitems, type);
            Collections.sort(items);
            ((GridView) view.findViewById(R.id.gridView)).setAdapter(new MFCGalleryAdapter(getActivity(), spiceManagerBinary, items,R.layout.header));


        } else {
            currentPage = 1;
            getGallery((GridView) view.findViewById(R.id.gridView));
        }

        final View viewf = view;

        ((GridView) view.findViewById(R.id.gridView)).setOnItemClickListener(new AdapterView
                .OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View tview, int position, long id) {
                final Picture pic = items.get(position);

                SmallBinaryRequest req = new SmallBinaryRequest(pic.getSrc().replace("/thumbnails", ""));

                getActivity().setProgressBarIndeterminateVisibility(true);

                File tempFile = new File(getActivity().getExternalCacheDir(), getActivity().getString(R.string.mfc_cache_pic_gallery,pic.getId()));
                final ImageView expandedImageView = (ImageView) viewf.findViewById(R.id.expanded_image);

                if (tempFile.length()>0) {
                    Bitmap bitmap = Utils.getBitmapFromFile(tempFile.getPath(),expandedImageView.getMeasuredHeight(),expandedImageView.getMeasuredWidth());
                            //BitmapFactory.decodeFile(tempFile.getPath());
                    getActivity().setProgressBarIndeterminateVisibility(false);
                    zoomImageFromThumb(tview, expandedImageView, viewf, bitmap);
                } else
                    spiceManager.execute(req, pic.getSrc().replace("/thumbnails", ""), DurationInMillis.ONE_WEEK * 52, new RequestListener<InputStream>() {
                        @Override
                        public void onRequestFailure(SpiceException e) {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.request_error, e.getMessage()), Toast.LENGTH_LONG).show();
                            getActivity().setProgressBarIndeterminateVisibility(false);
                        }

                        @Override
                        public void onRequestSuccess(InputStream file) {
                            File tempFile = new File(getActivity().getExternalCacheDir(), getActivity().getString(R.string.mfc_cache_pic_gallery,pic.getId()));

                            OutputStream out = null;
                            try {
                                out = new FileOutputStream(tempFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            byte buf[] = new byte[1024];
                            int len;
                            try {
                                while ((len = file.read(buf)) > 0)
                                    out.write(buf, 0, len);
                                out.close();
                                file.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            Bitmap bitmap;


                            bitmap = Utils.getBitmapFromFile(tempFile.getPath(), expandedImageView.getMeasuredHeight(), expandedImageView.getMeasuredWidth());
                            getActivity().setProgressBarIndeterminateVisibility(false);
                            zoomImageFromThumb(tview, expandedImageView, viewf, bitmap);

                        }
                    });

            }
        });

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    private void getGallery(final GridView view) {
        String user = mParam1;
        Boolean isItem = mParam2;

        if (user != null) {
            GalleryFragment.this.getActivity().setProgressBarIndeterminateVisibility(true);


            GalleryRequest request;

            if (!isItem)request = new GalleryRequest(user, currentPage + "");
            else request = new GalleryRequest(Integer.parseInt(user),currentPage+"");

            spiceManager.execute(request, request.createCacheKey(), DurationInMillis.ONE_HOUR, new RequestListener<GalleryMode>() {
                @Override
                public void onRequestFailure(SpiceException e) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.request_error, e.getMessage()), Toast.LENGTH_LONG).show();
                    getActivity().setProgressBarIndeterminateVisibility(false);
                }

                @Override
                public void onRequestSuccess(GalleryMode galleryMode) {
                    int max = Integer.parseInt(galleryMode.getGallery().getNum_pages());

                    if (currentPage == 1) {
                        items = galleryMode.getGallery().getPicture();
                        Collections.sort(items);
                    } else {
                        items.addAll(galleryMode.getGallery().getPicture());
                        Collections.sort(items);
                    }

                    if (currentPage++ >= max) {
                        view.setAdapter(new MFCGalleryAdapter(getActivity(), spiceManagerBinary, items, R.layout.header));
                        getActivity().setProgressBarIndeterminateVisibility(false);
                    } else {
                        getGallery(view);
                    }


                }
            });
        }
    }

    /**
     * "Zooms" in a thumbnail view by assigning the high resolution image to a hidden "zoomed-in"
     * image view and animating its bounds to fit the entire activity content area. More
     * specifically:
     * <p/>
     * <ol>
     * <li>Assign the high-res image to the hidden "zoomed-in" (expanded) image view.</li>
     * <li>Calculate the starting and ending bounds for the expanded view.</li>
     * <li>Animate each of four positioning/sizing properties (X, Y, SCALE_X, SCALE_Y)
     * simultaneously, from the starting bounds to the ending bounds.</li>
     * <li>Zoom back out by running the reverse animation on click.</li>
     * </ol>
     */
    private void zoomImageFromThumb(final View thumbView, final ImageView expandedImageView, final View container, Bitmap file) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            zoomImageThumbAnimated(thumbView, expandedImageView, container, file);
        }else
        {
            // Load the high-resolution "zoomed-in" image.
            expandedImageView.setImageBitmap(file);

            // Calculate the starting and ending bounds for the zoomed-in image. This step
            // involves lots of math. Yay, math.
            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();
            final Point globalOffset = new Point();

            // The start bounds are the global visible rectangle of the thumbnail, and the
            // final bounds are the global visible rectangle of the container view. Also
            // set the container view's offset as the origin for the bounds, since that's
            // the origin for the positioning animation properties (X, Y).
            thumbView.getGlobalVisibleRect(startBounds);
            container.getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);

            // Adjust the start bounds to be the same aspect ratio as the final bounds using the
            // "center crop" technique. This prevents undesirable stretching during the animation.
            // Also calculate the start scaling factor (the end scaling factor is always 1.0).
            float startScale;
            if ((float) finalBounds.width() / finalBounds.height()
                    > (float) startBounds.width() / startBounds.height()) {
                // Extend start bounds horizontally
                startScale = (float) startBounds.height() / finalBounds.height();
                float startWidth = startScale * finalBounds.width();
                float deltaWidth = (startWidth - startBounds.width()) / 2;
                startBounds.left -= deltaWidth;
                startBounds.right += deltaWidth;
            } else {
                // Extend start bounds vertically
                startScale = (float) startBounds.width() / finalBounds.width();
                float startHeight = startScale * finalBounds.height();
                float deltaHeight = (startHeight - startBounds.height()) / 2;
                startBounds.top -= deltaHeight;
                startBounds.bottom += deltaHeight;
            }

            // Hide the thumbnail and show the zoomed-in view. When the animation begins,
            // it will position the zoomed-in view in the place of the thumbnail.
            thumbView.setVisibility(View.INVISIBLE);
            expandedImageView.setVisibility(View.VISIBLE);


            // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
            // and show the thumbnail instead of the expanded image.
            final float startScaleFinal = startScale;
            expandedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    thumbView.setVisibility(View.VISIBLE);
                    expandedImageView.setVisibility(View.GONE);
                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void zoomImageThumbAnimated(final View thumbView, final ImageView expandedImageView, View container, Bitmap file) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        expandedImageView.setImageBitmap(file);

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        container.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
