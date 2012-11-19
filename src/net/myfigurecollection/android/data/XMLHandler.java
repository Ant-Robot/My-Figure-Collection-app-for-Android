
package net.myfigurecollection.android.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;

import Utils.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import net.myfigurecollection.android.data.objects.Category;
import net.myfigurecollection.android.data.objects.Figure;
import net.myfigurecollection.android.webservices.MFCService;
import net.myfigurecollection.android.webservices.RequestListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An asyncTask to parse MyFigurCollection API XML
 * 
 * @author Climbatize
 * 
 */
public class XMLHandler extends AsyncTask<String, Integer, Void>
{

	RequestListener				listener;
	private static boolean		isRequesting	= false;
	Document					document;
	Element						root;
	private final Context		context;
	ProgressDialog				pd;
	public String				num_res			= "0";

	public static final String	CAT_OWNED		= "owned";
	public static final String	CAT_WISHED		= "wished";
	public static final String	CAT_ORDERED		= "ordered";
	private long				Insertdate		= 0;

	public static final String	folder			= Environment.getExternalStorageDirectory().getPath() + "/Android/data/net.myfigurecollection/Datas";

	public Element getRoot()
	{
		return root;
	}

	public void setRoot(final Element root)
	{
		this.root = root;
	}

	URL				flux;
	private boolean	refresh	= false;

	/*
	 * public XMLHandler(int tab, String filepath, URL url) {
	 * flux = url;
	 * //loadXML(filepath);
	 * }
	 */

	public XMLHandler(final Activity c, final boolean forceRefresh)
	{
		context = c;
		refresh = forceRefresh;
		if (c instanceof RequestListener)
		{
			listener = (RequestListener) c;
		}
	}

	public void setListener(final RequestListener listener)
	{
		this.listener = listener;
	}

	/**
	 * 
	 * @param flux
	 * @param status
	 * @param file
	 */
	public void loadXML(final URL flux, final int status, final File file)
	{
		try
		{

			try
			{

				if (file.exists())
				{
					// if ((Calendar.getInstance().getTimeInMillis() -
					// file.lastModified()) > 1000 * 60 * 30) {
					file.delete();
					// }
				}

				if (!file.exists())
				{
					final URLConnection conn = flux.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(10000);
					final InputStream is = conn.getInputStream();

					final OutputStream out = new FileOutputStream(file);
					final byte buf[] = new byte[1024];
					int len;
					while ((len = is.read(buf)) > 0)
					{
						out.write(buf, 0, len);
					}
					out.close();
					is.close();
				}

			}
			catch (final Exception e)
			{
				Log.e(Constants.PROJECT_TAG, "Exeption retrieving XML", e);
			}
			try
			{
				document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(file));
			}
			catch (final Exception e)
			{
				Log.e(Constants.PROJECT_TAG, "xml error ", e);
			}

		}
		catch (final Exception e)
		{
			Log.e(Constants.PROJECT_TAG, "TsukiQueryError", e);
		}

		if (document != null)
		{
			root = document.getDocumentElement();

			PopulateDatabase(root, status);
		}

	}

	/**
	 * 
	 * @param root2
	 * @param status
	 */
	private void PopulateDatabase(final Element root2, final int status)
	{

		Element elment = root2;
		if (status > -1)
		{
			elment = (Element) root2.getElementsByTagName("collection").item(0);

		}
		// final String[] projection = new String[] { Figure._ID, Figure.ID,
		// Figure.NAME, Figure.DATE, Figure.MANUFACTURER, Figure.PRICE,
		// Category.COLOR };

		/*
		 * Cursor cr = context.getContentResolver().query(Figure.CONTENT_URI,
		 * projection, "status=?", new String[] { Integer.toString(status) },
		 * Figure.NAME + " ASC");
		 * for (int i = 0; i < cr.getCount(); i++) {
		 * cr.moveToPosition(i);
		 * }
		 */

		// SearchRecentSuggestions suggestions = new
		// SearchRecentSuggestions(context, SearchFigureProvider.AUTHORITY,
		// SearchFigureProvider.MODE);

		final int size = elment.getElementsByTagName("item").getLength();
		final ContentValues[] bulkValues = new ContentValues[size];
		int i = 0;

		final NodeList elements = elment.getElementsByTagName("item");

		for (int e = 0; e < size; e++)
		{
			final Node figure = elements.item(e);

			final ContentValues values = new ContentValues();
			final Node figure_data = figure.getChildNodes().item(Figure.XML_DATA_POSITION);
			final Node figure_category = figure.getChildNodes().item(Figure.XML_CATEGORY_POSITION);

			if (figure_category != null)
			{
				values.put(Figure.CATEGORY, parseIntinString(figure_category.getChildNodes().item(Figure.XML_CATEGORY_POSITION_ID)
						.getFirstChild().getNodeValue()));
			}
			if (figure_data != null)
			{
				final Node color = figure_category.getChildNodes().item(Figure.XML_CATEGORY_POSITION_COLOR).getFirstChild();
				final Node id = figure_data.getChildNodes().item(Figure.XML_DATA_POSITION_ID).getFirstChild();
				final Node jan = figure_data.getChildNodes().item(Figure.XML_DATA_POSITION_JAN).getFirstChild();
				final Node date = figure_data.getChildNodes().item(Figure.XML_DATA_POSITION_DATE).getFirstChild();
				final Node isbn = figure_data.getChildNodes().item(Figure.XML_DATA_POSITION_ISBN).getFirstChild();
				final Node name = figure_data.getChildNodes().item(Figure.XML_DATA_POSITION_NAME).getFirstChild();
				final Node price = figure_data.getChildNodes().item(Figure.XML_DATA_POSITION_PRICE).getFirstChild();

				if ((figure_category != null) && (color != null))
				{
					values.put(Category.COLOR, color.getNodeValue());
				}
				if (id != null)
				{
					values.put(Figure.ID, parseIntinString(id.getNodeValue()));
				}
				if (jan != null)
				{
					values.put(Figure.JAN, jan.getNodeValue());
				}
				if (isbn != null)
				{
					values.put(Figure.ISBN, isbn.getNodeValue());
				}
				if (name != null)
				{
					values.put(Figure.NAME, name.getNodeValue());
					final String finalname = name.getNodeValue();
					final int manuStart = finalname.lastIndexOf("(", finalname.length() - 1);
					final String manu = finalname.substring(manuStart + 1, finalname.length() - 1);
					values.put(Figure.MANUFACTURER, manu);
				}
				if (price != null)
				{
					values.put(Figure.PRICE, parseIntinString(price.getNodeValue()));
				}
				if ((date != null) && (date.getNodeValue() != null))
				{
					final String[] split = date.getNodeValue().split("-");
					if ((split != null) && !"0000".equals(split[0]))
					{
						values.put(Figure.DATE, parseIntinString(split[0]));
					}
				}
			}
			if (status != -1)
			{
				final Node figure_collection = figure.getChildNodes().item(Figure.XML_MYCOLLECTION_POSITION);
				if (figure_collection != null)
				{
					final Node score = figure_collection.getChildNodes().item(Figure.XML_MYCOLLECTION_POSITION_SCORE).getFirstChild();
					final Node owned = figure_collection.getChildNodes().item(Figure.XML_MYCOLLECTION_POSITION_NUMBER).getFirstChild();
					final Node wishability = figure_collection.getChildNodes().item(Figure.XML_MYCOLLECTION_POSITION_WISHABILITY).getFirstChild();

					if (score != null)
					{
						values.put(Figure.SCORE, parseIntinString(score.getNodeValue()));
					}
					if (owned != null)
					{
						values.put(Figure.NUMBER_OWNED, parseIntinString(owned.getNodeValue()));
					}
					if (wishability != null)
					{
						values.put(Figure.WISHABILITY, parseIntinString(wishability.getNodeValue()));
					}
				}
			} else
			{
				// TODO:Search
				// values.put(Figure.SCORE,
				// parseIntinString(root.getChildText("num_results")));
			}
			values.put(Figure.STATUS, status);
			values.put("InsertDate", Insertdate);

			bulkValues[i++] = values;

			// suggestions.saveRecentQuery((((Element)
			// figure).getChild(Figure.DATA).getChildText(Figure.NAME)).split(" - ")[1],
			// null);

			// if(status<0)
			// context.getContentResolver().insert(Figure.CONTENT_URI, values);
			// else if (context.getContentResolver().update(Figure.CONTENT_URI,
			// values, Figure.ID + "=" + values.getAsString(Figure.ID), null) ==
			// 0)
			// context.getContentResolver().insert(Figure.CONTENT_URI, values);

		}

		context.getContentResolver().delete(Figure.CONTENT_URI, "InsertDate" + "<" + Insertdate + " AND " + Figure.STATUS + "=" + status, null);
		context.getContentResolver().bulkInsert(Figure.CONTENT_URI, bulkValues);

	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	int parseIntinString(final String s)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch (final Exception e)
		{
			return 0;
		}
	}

	@Override
	protected Void doInBackground(final String... params)
	{
		if (!XMLHandler.isRequesting)
		{
			XMLHandler.isRequesting = true;

			try
			{
				String f;
				String nFolder = MFCService.APP_FOLDER;
				final File dir = (new File(XMLHandler.folder));
				dir.mkdirs();

				if (dir.isDirectory())
				{
					nFolder = XMLHandler.folder + "/";
				}

				f = (nFolder + "retrieved.mfc");

				final File file = new File(f);

				if ((params.length > 1) && "refresh".equals(params[1]))
				{
					file.delete();
				}

				if (!refresh)
				{
					refresh = (!file.exists() || ((Calendar.getInstance().getTimeInMillis() - file.lastModified()) > (1000 * 60 * 60 * 4)));
				}

				if (params[0].contains(Constants.API_MODE_SEARCH))
				{
					URL url;

					url = new URL(params[0]);

					loadXML(url, -1, file);
					publishProgress(100, -1);

				} else if (refresh)
				{
					for (int i = 0; i < 3; i++)
					{

						URL url;

						url = new URL(params[0] + "&status=" + i);

						loadXML(url, i, file);

						if (file.exists())
						{

							int num_pages = 1;

							try
							{
								final int nodeState = 1;
								final int nodeNumPages = 2;

								num_pages = Integer.parseInt(root.getElementsByTagName("collection").item(0).getChildNodes().item(nodeState).getChildNodes()
										.item(nodeNumPages).getFirstChild().getNodeValue());
							}
							catch (final NullPointerException e)
							{
								Log.e("XMLHandler", "NullPointerException", e);
							}

							for (int j = 2; j <= num_pages; j++)
							{
								loadXML(new URL(url + "&page=" + j), i, file);
								publishProgress((int) ((j / (float) num_pages) * 100), i);

							}
						}
						publishProgress(100, i);
					}
				}

			}
			catch (final MalformedURLException e)
			{
				Log.e(Constants.PROJECT_TAG, "MalformedURLException in doInBackground", e);
			}

			XMLHandler.isRequesting = false;
		}
		return null;
	}

	@Override
	protected void onPostExecute(final Void voi)
	{

		/* if (!lock) */{
			try
			{
				if ((pd != null) && pd.isShowing())
				{
					pd.dismiss();
				}

			}
			catch (final IllegalArgumentException e)
			{
				Log.e(Constants.PROJECT_TAG, "IllegalArgumentException", e);
			}

			if (listener != null)
			{
				listener.onRequestcompleted(0, voi);
			}
		}
	}

	@Override
	protected void onProgressUpdate(final Integer... values)
	{
		if (pd != null)
		{
			switch (values[1]) {
				case 0:
					pd.setTitle(XMLHandler.CAT_WISHED);
					break;
				case 1:
					pd.setTitle(XMLHandler.CAT_ORDERED);
					break;
				case 2:
					pd.setTitle(XMLHandler.CAT_OWNED);
					break;

				default:
					pd.setTitle("");
					break;
			}
			pd.setProgress(values[0]);
		}
	}

	@Override
	protected void onPreExecute()
	{
		if (!XMLHandler.isRequesting)
		{
			Insertdate = (new Date()).getTime();
			pd = new ProgressDialog(context);
			pd.setCancelable(false);
			pd.setMessage("Downloading data");
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.show();
		}
		super.onPreExecute();
	}
}