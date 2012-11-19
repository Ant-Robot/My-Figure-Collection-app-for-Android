
package net.myfigurecollection.android.data.json;

import org.json.JSONArray;
import org.json.JSONException;

import android.database.AbstractCursor;

public class JSONCursor extends AbstractCursor
{
	private final String[]	columns;
	private final JSONArray	dataArray;

	public JSONCursor(JSONArray data, String[] cols)
	{
		dataArray = data;
		columns = cols;
	}

	public JSONCursor(String[] data, String[] cols)
	{
		JSONArray ja = new JSONArray();
		for (String string : data)
		{
			ja.put(string);
		}
		dataArray = ja;
		columns = cols;
	}

	@Override
	public String[] getColumnNames()
	{
		// Log.d("TNT", "return col names : "+columns);
		return columns;
	}

	@Override
	public int getCount()
	{
		return dataArray.length();
	}

	@Override
	public double getDouble(int column)
	{
		try
		{
			return dataArray.getJSONObject(mPos).getDouble(getColumnName(column));
		}
		catch (JSONException e)
		{
			return 0;
		}
	}

	@Override
	public float getFloat(int column)
	{
		return (float) getDouble(column);
	}

	@Override
	public int getInt(int column)
	{
		try
		{
			return dataArray.getJSONObject(mPos).getInt(getColumnName(column));
		}
		catch (JSONException e)
		{
			return 0;
		}
	}

	@Override
	public long getLong(int column)
	{
		try
		{
			return dataArray.getJSONObject(mPos).getLong(getColumnName(column));
		}
		catch (JSONException e)
		{
			return 0;
		}
	}

	@Override
	public short getShort(int column)
	{
		return (short) getInt(column);
	}

	@Override
	public String getString(int column)
	{
		try
		{
			return dataArray.getJSONObject(mPos).getString(getColumnName(column));
		}
		catch (JSONException e)
		{
			return null;
		}
	}

	@Override
	public boolean isNull(int column)
	{
		try
		{
			return dataArray.getJSONObject(mPos).isNull(getColumnName(column));
		}
		catch (JSONException e)
		{
			return true;
		}
	}
}