
package net.myfigurecollection.android.data;

import net.myfigurecollection.android.data.objects.Figure;
import android.content.SearchRecentSuggestionsProvider;
import android.database.CrossProcessCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.CursorWrapper;
import android.net.Uri;
import android.util.Log;

/**
 * 
 * @author Climbatize
 * 
 */
public class SearchFigureProvider extends SearchRecentSuggestionsProvider
{
	public class CrossProcessCursorWrapper extends CursorWrapper implements
			CrossProcessCursor
	{
		public CrossProcessCursorWrapper(Cursor cursor)
		{
			super(cursor);
		}

		@Override
		public void fillWindow(int position, CursorWindow window)
		{
			if (position < 0 || position > getCount()) { return; }
			window.acquireReference();
			try
			{
				moveToPosition(position - 1);
				window.clear();
				window.setStartPosition(position);
				int columnNum = getColumnCount();
				window.setNumColumns(columnNum);
				while (moveToNext() && window.allocRow())
				{
					for (int i = 0; i < columnNum; i++)
					{
						String field = getString(i);
						if (field != null)
						{
							if (!window.putString(field, getPosition(), i))
							{
								window.freeLastRow();
								break;
							}
						} else
						{
							if (!window.putNull(getPosition(), i))
							{
								window.freeLastRow();
								break;
							}
						}
					}
				}
			}
			catch (IllegalStateException e)
			{
				// simply ignore it
			}
			finally
			{
				window.releaseReference();
			}
		}

		@Override
		public CursorWindow getWindow()
		{
			return null;
		}

		@Override
		public boolean onMove(int oldPosition, int newPosition)
		{
			return true;
		}
	}

	public final static String	AUTHORITY	= "net.myfigurecollection.SearchFigureProvider";
	public final static int		MODE		= SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES | SearchRecentSuggestionsProvider.DATABASE_MODE_2LINES;

	Cursor						results;

	public SearchFigureProvider()
	{
		setupSuggestions(SearchFigureProvider.AUTHORITY, SearchFigureProvider.MODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.SearchRecentSuggestionsProvider#query(android.net.Uri,
	 * java.lang.String[], java.lang.String, java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		Log.d("SearchFigureProvider", "suggestion ->" + selectionArgs[0]);

		results = // super.query(uri, projection, selection, selectionArgs,
					// sortOrder);

		// final AQuery aq = new AQuery(getContext());
		// aq.ajax("http://myfigurecollection.net/ajax.php", JSONObject.class,
		// new AjaxCallback<JSONObject>() {
		// @Override
		// public void callback(String url, JSONObject object, AjaxStatus
		// status)
		// {
		//
		// try
		// {
		// String datas = object.getString("html");
		// results = new JSONCursor(datas.split("<\\/li><li>"),
		// results.getColumnNames());
		// }
		// catch (JSONException e)
		// {
		// Log.e("", "callback", e);
		// }
		//
		// }
		// });

		getContext().getContentResolver().query(Figure.SUGGEST_URI,
				projection, "name LIKE ?", new String[] { "%" + selectionArgs[0] + "%" },
				Figure.NAME + " ASC");

		return new CrossProcessCursorWrapper(results);
	}
}