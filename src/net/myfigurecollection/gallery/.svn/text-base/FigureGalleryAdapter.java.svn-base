/**
 * 
 */

package net.myfigurecollection.gallery;

import net.myfigurecollection.R;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Gallery;

import com.androidquery.AQuery;
import com.androidquery.util.Constants;

/**
 * @author Climbatize
 * 
 */
public class FigureGalleryAdapter extends SimpleCursorAdapter
{
	private final AQuery				aq;
	private final FigureGalleryCursor	fgc;
	private final Context				mContext;
	private final LayoutInflater		mInflater;

	/**
	 * @param context
	 * @param layout
	 * @param c
	 * @param from
	 * @param to
	 */
	public FigureGalleryAdapter(Context context, int layout, Cursor c)
	{
		super(context, layout, c, new String[] {/*
												 * "id","src","author","date",
												 * "category"
												 * ,"resolution","size"
												 * ,"title","nsfw"
												 */}, new int[0], 0);
		aq = new AQuery(context);
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE);
		fgc = ((FigureGalleryCursor) getCursor());

	}

	@Override
	public JSONObject getItem(int position)
	{
		if (getCursor().moveToPosition(position))
		{
			FigureGalleryCursor fgc = ((FigureGalleryCursor) getCursor());
			return fgc.getObjects().get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		if (getCursor().moveToPosition(position))
		{
			FigureGalleryCursor fgc = ((FigureGalleryCursor) getCursor());
			return fgc.getLong(fgc.getColumnIndex("id"));
		}
		return -1;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2)
	{
		View v = mInflater.inflate(R.layout.cell_gallery, null);
		v.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		/*
		 * ImageView iv = (ImageView) v.findViewById(R.id.imageViewPicture);
		 * iv.setId(fgc.getInt(fgc.getColumnIndex("id")));
		 * iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
		 * iv.setBackgroundColor(Color.BLACK);
		 */

		AQuery a = aq.recycle(v);

		a.id(R.id.textViewAuthor).text(fgc.getString(fgc.getColumnIndex("author")));
		String url = fgc.getString(fgc.getColumnIndex("src"));

		Bitmap placeholder = a.getCachedImage(url);
		if (!a.shouldDelay(arg0, v, arg2, url))
		{
			a.id(R.id.imageViewPicture)
					.image(url.replace("/thumbnails/", "/"), false, true, 320, 480, placeholder, Constants.FADE_IN, Constants.RATIO_PRESERVE);
		} else
		{
			a.id(R.id.imageViewPicture).image(placeholder, Constants.RATIO_PRESERVE);
		}
		return v;
	}

}
