/**
 * 
 */

package net.myfigurecollection.gallery;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.AbstractCursor;
import android.util.Log;

/**
 * @author Climbatize
 * @since 2012-04-15
 * 
 *        {
 *        id: "408598",
 *        src:
 *        "http://s1.tsuki-board.net/image/thumbnails/Shirinights1334477498.jpeg"
 *        ,
 *        author: "Shirinights",
 *        date: "Sun, 15 Apr 2012 08:11:38 +0000",
 *        category: {
 *        id: "1",
 *        name: "Figures",
 *        color: "#008000"
 *        },
 *        resolution: {
 *        width: "2048",
 *        height: "1365"
 *        },
 *        size: "190836",
 *        title: { },
 *        nsfw: "0"
 *        }
 * 
 */
public class JsonCursor extends AbstractCursor
{
	ArrayList<JSONObject>	objects;
	String[]				columns;
	JsonCursorListener		mListener;

	/**
	 * 
	 */
	public JsonCursor(String[] _columns)
	{
		objects = new ArrayList<JSONObject>();
		columns = _columns;
	}

	/**
	 * 
	 * @param jsonObjects
	 */
	public JsonCursor(JSONArray jsonObjects)
	{
		objects = new ArrayList<JSONObject>();
		columns = new String[0];
		for (int i = 0; i < jsonObjects.length(); i++)
		{
			try
			{
				objects.add(jsonObjects.getJSONObject(i));
			}
			catch (JSONException e)
			{
				Log.e("JsonCursor", "JsonCursor", e);
			}
		}

	}

	public JsonCursor(ArrayList<JSONObject> jsonObjects)
	{
		objects = jsonObjects;
		columns = new String[0];
	}

	@Override
	public String[] getColumnNames()
	{
		if (columns == null || columns.length == 0)
		{
			JSONObject first = objects.get(0);

			Iterator<?> keys = first.keys();
			columns = new String[first.length() + 1];
			int i = 0;

			columns[i++] = "_id";

			while (keys.hasNext())
			{
				columns[i++] = (String) keys.next();
			}
		}

		return columns;
	}

	@Override
	public int getCount()
	{
		return objects.size();
	}

	@Override
	public double getDouble(int arg0)
	{
		JSONObject current = objects.get(mPos);
		double value = -1;
		try
		{
			if (arg0 == 0) value = mPos;
			else value = current.getDouble(getColumnNames()[arg0]);
		}
		catch (JSONException e)
		{
			Log.e("JsonCursor", "getDouble", e);
		}

		return value;
	}

	@Override
	public float getFloat(int column)
	{
		JSONObject current = objects.get(mPos);
		float value = -1;
		try
		{
			if (column == 0) value = mPos;
			else value = Float.parseFloat(current.getString(getColumnNames()[column]));
		}
		catch (JSONException e)
		{
			Log.e("JsonCursor", "getDouble", e);
		}

		return value;
	}

	@Override
	public int getInt(int column)
	{
		JSONObject current = objects.get(mPos);
		int value = -1;
		try
		{
			if (column == 0) value = mPos;
			else value = current.getInt(getColumnNames()[column]);
		}
		catch (JSONException e)
		{
			Log.e("JsonCursor", "getDouble", e);
		}

		return value;
	}

	@Override
	public long getLong(int column)
	{
		JSONObject current = objects.get(mPos);
		long value = -1;
		try
		{
			if (column == 0) value = mPos;
			else value = current.getLong(getColumnNames()[column]);
		}
		catch (JSONException e)
		{
			Log.e("JsonCursor", "getDouble", e);
		}

		return value;
	}

	@Override
	public short getShort(int column)
	{
		JSONObject current = objects.get(mPos);
		short value = -1;
		try
		{
			if (column == 0) value = (short) mPos;
			else value = Short.parseShort(current.getString(getColumnNames()[column]));
		}
		catch (JSONException e)
		{
			Log.e("JsonCursor", "getDouble", e);
		}

		return value;
	}

	@Override
	public String getString(int column)
	{
		JSONObject current = objects.get(mPos);
		String value = null;
		try
		{
			if (column == 0) value = "" + mPos;
			else value = current.getString(getColumnNames()[column]);
		}
		catch (JSONException e)
		{
			Log.e("JsonCursor", "getDouble", e);
		}

		return value;
	}

	@Override
	public boolean isNull(int column)
	{
		boolean isnull = true;
		JSONObject current = objects.get(mPos);
		try
		{
			isnull = column != 0;
			if (isnull)
			{
				String value = current.getString(getColumnNames()[column]);
				isnull = value == null || value.isEmpty();
			}
		}
		catch (JSONException e)
		{
			Log.e("JsonCursor", "getDouble", e);
		}
		return isnull;
	}

	@Override
	public int getColumnCount()
	{
		String[] col = getColumnNames();
		if (col == null) return 0;
		return col.length;
	}

	@Override
	public int getColumnIndex(String columnName)
	{
		String[] col = getColumnNames();
		for (int i = 0; i < getColumnCount(); i++)
		{
			String column = col[i];
			// Some adapter require a "_id" field, most json object have a "id"
			// field
			if (column.equals(columnName)) return i;
		}
		return -1;
	}

	@Override
	public int getColumnIndexOrThrow(String columnName)
	{
		int index = getColumnIndex(columnName);

		if (index < 0) throw new IllegalArgumentException(columnName + " unknown");
		return index;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		String[] col = getColumnNames();
		if (columnIndex > getColumnCount()) return null;
		return col[columnIndex];
	}

	/**
	 * @return the columns
	 */
	public String[] getColumns()
	{
		String[] col = getColumnNames();
		return col;
	}

	/**
	 * @return the objects
	 */
	public ArrayList<JSONObject> getObjects()
	{
		return objects;
	}

	/**
	 * @param mListener
	 *            the mListener to set
	 */
	public void setListener(JsonCursorListener mListener)
	{
		this.mListener = mListener;
	}

}
