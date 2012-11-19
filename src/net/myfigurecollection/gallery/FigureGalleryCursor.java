/**
 * 
 */

package net.myfigurecollection.gallery;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import Utils.Constants;
import android.content.Context;
import android.util.Log;

/**
 * @author climbatize
 * 
 */
public class FigureGalleryCursor extends JsonCursor
{
	AQuery aq;

	/**
	 * @param jsonObjects
	 */
	public FigureGalleryCursor(ArrayList<JSONObject> jsonObjects)
	{
		super(jsonObjects);
	}

	public FigureGalleryCursor(Context context, String id_figure, JsonCursorListener listener)
	{
		super(new ArrayList<JSONObject>());
		
		mListener = listener;
		String url = Constants.API_MODE_FIGURE_GALLERY_JSON + id_figure;
		aq = new AQuery(context);
		aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
			@Override
			public void callback(String url, JSONObject object, AjaxStatus status)
			{
				try
				{
					FigureGalleryCursor.this.addPictures(object.getJSONObject("gallery").getJSONArray("picture"));
					if (mListener != null)
					{
						mListener.onJsonReady(FigureGalleryCursor.this);
					}
				}
				catch (JSONException e)
				{
					Log.e("", "callback", e);
				}
			}
		});
	}

	/**
	 * 
	 * @param _pictures
	 */
	public void addPictures(JSONArray _pictures)
	{
		for (int i = 0; i < _pictures.length(); i++)
		{
			try
			{
				JSONObject job = _pictures.getJSONObject(i);
				objects.add(job);
				Log.d("FigureGalleryCursor", "cache "+job.getString("src"));
				aq.cache(job.getString("src").replace("/thumbnails/", "/"), 1000*60*60*24*30);
				aq.cache(job.getString("src"), 0);
			}
			catch (JSONException e)
			{
				Log.e("JsonCursor", "JsonCursor", e);
			}
		}
	}

}
