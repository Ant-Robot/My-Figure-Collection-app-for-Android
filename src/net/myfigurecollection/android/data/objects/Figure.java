
package net.myfigurecollection.android.data.objects;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import net.myfigurecollection.android.data.DatabaseProvider;
import android.net.Uri;

public class Figure implements Comparable<Figure>
{

	// Database datas
	public static final String	_ID										= "_id";
	public static final String	ARTIST									= "artist";
	public static final String	CATALOG									= "catalog";
	public static final String	CATEGORY								= "category";
	public static final Uri		CONTENT_URI								= Uri.parse("content://" + DatabaseProvider.AUTHORITY + "/figures");
	public static final String	DATA									= "data";
	public static final String	DATE									= "releaseDate";
	public static final String	DATE_OWNED								= "date_owned";
	public static final String	DEFAULT_SORT_ORDER						= Figure.NAME;
	public static final String	DRAFT									= "draft";
	public static final String	ID										= "id";
	public static final String	ISBN									= "isbn";
	public static final String	JAN										= "jan";
	public static final String	LINK									= "link";
	public static final String	MANUFACTURER							= "manufacturer";
	public static final String	NAME									= "name";
	public static final String	NUMBER_OWNED							= "number";
	public static final String	ORIGIN									= "origin";
	public static final String	PRICE									= "price";
	public static final String	SCALE									= "scale";
	public static final String	SCORE									= "score";
	public static final String	SCULPTOR								= "sculptor";
	public static final String	STATUS									= "status";
	public static final Uri		SUGGEST_URI								= Uri.parse("content://" + DatabaseProvider.AUTHORITY + "/suggest");
	public static final String	THUMBNAIL								= "thumbnail";
	public static final String	VERSION									= "version";
	public static final String	WISHABILITY								= "wishability";
	public static final int		XML_CATEGORY_POSITION					= 1;
	public static final int		XML_CATEGORY_POSITION_COLOR				= 2;
	public static final int		XML_CATEGORY_POSITION_ID				= 0;
	public static final int		XML_CATEGORY_POSITION_NAME				= 1;
	public static final int		XML_DATA_POSITION						= 2;
	public static final int		XML_DATA_POSITION_CATALOG				= 3;
	public static final int		XML_DATA_POSITION_DATE					= 5;
	public static final int		XML_DATA_POSITION_ID					= 0;
	public static final int		XML_DATA_POSITION_ISBN					= 2;
	public static final int		XML_DATA_POSITION_JAN					= 1;
	public static final int		XML_DATA_POSITION_NAME					= 4;
	public static final int		XML_DATA_POSITION_PRICE					= 6;
	public static final int		XML_MYCOLLECTION_POSITION				= 3;
	public static final int		XML_MYCOLLECTION_POSITION_NUMBER		= 0;
	public static final int		XML_MYCOLLECTION_POSITION_SCORE			= 1;
	public static final int		XML_MYCOLLECTION_POSITION_WISHABILITY	= 2;
	public static final int		XML_ROOT_POSITION						= 0;
	public static final int		XML_ROOT_POSITION_ID					= 0;
	public static final int		XML_ROOT_POSITION_NAME					= 1;

	public static String[] getColumns()
	{
		return new String[] { Figure._ID, Figure.ID, Figure.CATEGORY, Figure.DATE, Figure.DATE_OWNED, Figure.DRAFT, Figure.LINK, Figure.MANUFACTURER,
				Figure.NAME, Figure.NUMBER_OWNED, Figure.ORIGIN, Figure.PRICE, Figure.SCALE, Figure.SCORE, Figure.SCULPTOR,
				Figure.THUMBNAIL, Figure.VERSION, Figure.WISHABILITY // ,TYPE
		};
	}

	// End of database datas

	private Category	category;
	private Date		date;
	private boolean		draft;

	private int			id;
	private String		link;
	private String		manufacturer;
	private String		month;
	private String		name;
	private int			number_owned;

	private String		origin;
	private int			price;
	private String		scale;
	private int			score;

	private String		sculptor;
	private String		thumbnail;
	private String		version;
	private int			wishability;
	private String		year;

	public Figure()
	{

		name = "";
		version = "";
		origin = "";
		manufacturer = "";
		sculptor = "";

		year = "";
		month = "";
		scale = "";
	}

	@Override
	public int compareTo(Figure another)
	{
		return getName().compareTo(another.getName());
	}

	public Object fetch(String address) throws MalformedURLException, IOException
	{
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

	/**
	 * @return the category
	 */
	public Category getCategory()
	{
		return category;
	}

	/**
	 * @return the date
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @return the link
	 */
	public String getLink()
	{
		return link != null ? link : "http://myfigurecollection.net/pics/figure/" + getId() + ".jpg";
	}

	/**
	 * @return the manufacturer
	 */
	public String getManufacturer()
	{
		return manufacturer;
	}

	/**
	 * @return the month
	 */
	public String getMonth()
	{
		return month;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the number_owned
	 */
	public int getNumber_owned()
	{
		return number_owned;
	}

	/**
	 * @return the origin
	 */
	public String getOrigin()
	{
		return origin;
	}

	/**
	 * @return the price
	 */
	public int getPrice()
	{
		return price;
	}

	/**
	 * @return the scale
	 */
	public String getScale()
	{
		return scale;
	}

	/**
	 * @return the score
	 */
	public int getScore()
	{
		return score;
	}

	/**
	 * @return the sculptor
	 */
	public String getSculptor()
	{
		return sculptor;
	}

	/**
	 * @return the thumbnail
	 */
	public String getThumbnail()
	{
		return thumbnail;
	}

	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * @return the wishability
	 */
	public int getWishability()
	{
		return wishability;
	}

	/**
	 * @return the year
	 */
	public String getYear()
	{
		return year;
	}

	/**
	 * @return the draft
	 */
	public boolean isDraft()
	{
		return draft;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(Category category)
	{
		this.category = category;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date)
	{
		this.date = date;
	}

	/**
	 * @param draft
	 *            the draft to set
	 */
	public void setDraft(boolean draft)
	{
		this.draft = draft;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * @param link
	 *            the link to set
	 */
	public void setLink(String link)
	{
		this.link = link;
	}

	/**
	 * @param manufacturer
	 *            the manufacturer to set
	 */
	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(String month)
	{
		this.month = month;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @param numberOwned
	 *            the number_owned to set
	 */
	public void setNumber_owned(int numberOwned)
	{
		number_owned = numberOwned;
	}

	/**
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(String origin)
	{
		this.origin = origin;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(int price)
	{
		this.price = price;
	}

	/**
	 * @param scale
	 *            the scale to set
	 */
	public void setScale(String scale)
	{
		this.scale = scale;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(int score)
	{
		this.score = score;
	}

	/**
	 * @param sculptor
	 *            the sculptor to set
	 */
	public void setSculptor(String sculptor)
	{
		this.sculptor = sculptor;
	}

	/**
	 * @param thumbnail
	 *            the thumbnail to set
	 */
	public void setThumbnail(String thumbnail)
	{
		this.thumbnail = thumbnail;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	/*
	 * private Drawable ImageOperations(Context ctx, String url, String
	 * saveFilename) { try { InputStream is = (InputStream) this.fetch(url);
	 * Drawable d = Drawable.createFromStream(is, "src"); return d; } catch
	 * (MalformedURLException e) { e.printStackTrace(); return null; } catch
	 * (IOException e) { e.printStackTrace(); return null; } }
	 */

	/**
	 * @param wishability
	 *            the wishability to set
	 */
	public void setWishability(int wishability)
	{
		this.wishability = wishability;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(String year)
	{
		this.year = year;
	}

	@Override
	public String toString()
	{
		return name + (version.equals("") ? "" : " (" + version + ")");
	}

}
