/**
 * 
 */

package Utils;

import android.content.Context;
import android.database.StaleDataException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.myfigurecollection.R;
import net.myfigurecollection.android.data.objects.Category;
import net.myfigurecollection.android.data.objects.Figure;

import com.androidquery.AQuery;

/**
 * @author Climbatize
 * 
 */
public class FigureAdapter extends SimpleCursorAdapter
{

	private final AQuery	listAq;

	public FigureAdapter(final Context context, final int layout, final String[] from, final int[] to, final AQuery aquery, final int flags)
	{
		super(context, layout, null, from, to, flags);
		listAq = aquery;
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#getDropDownView(int,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(final int arg0, final View arg1, final ViewGroup arg2)
	{
		// TODO Auto-generated method stub

		final View v = getView(arg0, arg1, arg2);

		final TextView subtitle = (TextView) v.findViewById(R.id.TextView_subtitle);
		final TextView title = (TextView) v.findViewById(R.id.TextView_title);
		final TextView version = (TextView) v.findViewById(R.id.TextView_version);

		subtitle.setVisibility(View.VISIBLE);
		title.setVisibility(View.VISIBLE);
		version.setVisibility(View.VISIBLE);

		return v;
	}

	@Override
	public long getItemId(final int position)
	{

		if ((getCursor() != null) && getCursor().moveToPosition(position))
		{
			Long id = 0l;
			try
			{
				id = (long) getCursor().getInt(getCursor().getColumnIndex(Figure.ID));

			}
			catch (final StaleDataException e)
			{
				Log.e(Constants.PROJECT_TAG, "StaleDataException", e);
			}
			return id;
		}
		return 0;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent)
	{
		final View v = super.getView(position, convertView, parent);

		final TextView subtitle = (TextView) v.findViewById(R.id.TextView_subtitle);
		final TextView title = (TextView) v.findViewById(R.id.TextView_title);
		final TextView version = (TextView) v.findViewById(R.id.TextView_version);

		subtitle.setVisibility(View.GONE);
		version.setVisibility(View.GONE);

		try
		{

			final int date = getCursor().getInt(getCursor().getColumnIndex(Figure.DATE));

			final StringBuilder sb = new StringBuilder("" + (date > 0 ? date : "")); //$NON-NLS-2$

			final String name = getCursor().getString(getCursor().getColumnIndex(Figure.NAME));

			final int manuStart = name.lastIndexOf("(", name.length() - 1);
			sb.append(" ").append(getCursor().getString(getCursor().getColumnIndex(Figure.MANUFACTURER)));

			if (getCursor().getInt(getCursor().getColumnIndex(Figure.PRICE)) > 0)
			{
				sb.append(" - ")
						.append("" + getCursor().getInt(getCursor().getColumnIndex(Figure.PRICE))).append(" JPY"); //$NON-NLS-2$
			}

			subtitle.setText(sb.toString());
			if (manuStart > 0)
			{
				title.setText(name.subSequence(0, manuStart));
			} else
			{
				title.setText(name);
			}
			version.setText(" ");

			final ImageView iv = (ImageView) v.findViewById(R.id.ImageView_icon);

			v.setId((int) getItemId(position));

			subtitle.setTextColor(Color.parseColor("#FF33b5e5"));
			version.setTextColor(Color.parseColor("#FF33b5e5"));
			v.setBackgroundColor(Color.TRANSPARENT);
			final int c = Color.parseColor(getCursor().getString(getCursor().getColumnIndex(Category.COLOR)).replace("#", "#FF")); //$NON-NLS-2$
			iv.setBackgroundColor(c);
			v.findViewById(R.id.viewCategory).setBackgroundColor(c);

			final AQuery aq = listAq.recycle(v);
			// Bitmap placeholder = aq.getCachedImage(R.drawable.tsuko);

			final String url = Constants.THUMBROOT + v.getId() + ".jpg";
			final Bitmap placeholder = aq.getCachedImage(url);
			// if (aq.shouldDelay(position, v, parent, url))
			// {

			// aq.id(R.id.ImageView_icon).image(placeholder,
			// com.androidquery.util.Constants.RATIO_PRESERVE);

			// } else
			{
				if (placeholder != null)
				{
					aq.id(R.id.ImageView_icon).progress(R.id.progressBar1).image(placeholder, com.androidquery.util.Constants.RATIO_PRESERVE);
				} else
				{
					aq.id(R.id.ImageView_icon).progress(R.id.progressBar1)
							.image(url, true, true, 0, 0, placeholder, 0, com.androidquery.util.Constants.RATIO_PRESERVE);
				}
			}

		}
		catch (final NullPointerException npe)
		{
			if (Constants.DEBUGMODE)
			{
				Log.w(Constants.PROJECT_TAG, "Not a figure");
			}
		}

		return v;
	}

}