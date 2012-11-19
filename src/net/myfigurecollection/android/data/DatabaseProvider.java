
package net.myfigurecollection.android.data;

import java.util.HashMap;

import net.myfigurecollection.android.data.objects.Category;
import net.myfigurecollection.android.data.objects.Figure;

import org.json.JSONArray;

import Utils.Constants;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author Climbatize
 * 
 */
public class DatabaseProvider extends ContentProvider
{

	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		private static final String	DB_TABLE_FIGURES_CREATE	= "CREATE TABLE " + Constants.DATABASE_TABLE_FIGURES + " (" + Figure._ID + " INTEGER PRIMARY KEY,"
																	+ Figure.ID + " INTEGER, " + Figure.NAME + " TEXT," + Figure.MANUFACTURER + " TEXT, "
																	+ Figure.DATE + " INTEGER, " + Figure.PRICE + " INTEGER, " + Figure.NUMBER_OWNED
																	+ " INTEGER, "
																	+ Figure.SCORE + " INTEGER, " + Figure.WISHABILITY + " INTEGER, " + Figure.CATEGORY
																	+ " INTEGER, " + Figure.JAN + " TEXT, " + Figure.ISBN + " TEXT," + Category.COLOR
																	+ " Text,"
																	+ Figure.STATUS + " Text," + "InsertDate LONG"

																	+ ");";

		DatabaseHelper(Context context)
		{
			super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			if (Constants.DEBUGMODE) Log.d(Constants.PROJECT_TAG, DatabaseHelper.DB_TABLE_FIGURES_CREATE);
			db.execSQL(DatabaseHelper.DB_TABLE_FIGURES_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			if (Constants.DEBUGMODE) Log.w(Constants.PROJECT_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_FIGURES);

			onCreate(db);
		}

	}

	public static final String				AUTHORITY			= "net.myfigurecollection.dataprovider.db";

	private static final int				FIGURE				= 0;

	private static final int				FIGURE_ID			= 1;

	private static final int				FIGURE_SUGGESTION	= 2;

	public static JSONArray					figures;

	private static HashMap<String, String>	newsProjectionMap;

	private static HashMap<String, String>	suggestProjectionMap;

	private static UriMatcher				uriMatcher;

	static
	{
		DatabaseProvider.uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		DatabaseProvider.uriMatcher.addURI(DatabaseProvider.AUTHORITY, Constants.DATABASE_TABLE_FIGURES, DatabaseProvider.FIGURE);
		DatabaseProvider.uriMatcher.addURI(DatabaseProvider.AUTHORITY, Constants.DATABASE_TABLE_FIGURES + "/*", DatabaseProvider.FIGURE_ID);
		DatabaseProvider.uriMatcher.addURI(DatabaseProvider.AUTHORITY, Constants.DATABASE_SUGGEST, DatabaseProvider.FIGURE_SUGGESTION);

		DatabaseProvider.newsProjectionMap = new HashMap<String, String>();
		DatabaseProvider.newsProjectionMap.put(Figure._ID, Figure._ID);
		DatabaseProvider.newsProjectionMap.put(Figure.CATEGORY, Figure.CATEGORY);
		DatabaseProvider.newsProjectionMap.put(Figure.DATE, Figure.DATE);
		DatabaseProvider.newsProjectionMap.put(Figure.ID, Figure.ID);
		DatabaseProvider.newsProjectionMap.put(Figure.MANUFACTURER, Figure.MANUFACTURER);
		DatabaseProvider.newsProjectionMap.put(Figure.NAME, Figure.NAME);
		DatabaseProvider.newsProjectionMap.put(Figure.NUMBER_OWNED, Figure.NUMBER_OWNED);
		DatabaseProvider.newsProjectionMap.put(Figure.PRICE, Figure.PRICE);
		DatabaseProvider.newsProjectionMap.put(Figure.SCORE, Figure.SCORE);
		DatabaseProvider.newsProjectionMap.put(Figure.JAN, Figure.JAN);
		DatabaseProvider.newsProjectionMap.put(Figure.ISBN, Figure.ISBN);
		DatabaseProvider.newsProjectionMap.put(Figure.WISHABILITY, Figure.WISHABILITY);
		DatabaseProvider.newsProjectionMap.put(Figure.STATUS, Figure.STATUS);
		DatabaseProvider.newsProjectionMap.put(Category.COLOR, Category.COLOR);

		DatabaseProvider.suggestProjectionMap = new HashMap<String, String>();
		DatabaseProvider.suggestProjectionMap.put(Figure._ID, Figure._ID);
		DatabaseProvider.suggestProjectionMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, Figure.NAME + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1);
		DatabaseProvider.suggestProjectionMap.put(SearchManager.SUGGEST_COLUMN_QUERY, Figure.NAME + " as " + SearchManager.SUGGEST_COLUMN_QUERY);
		DatabaseProvider.suggestProjectionMap.put(SearchManager.SUGGEST_COLUMN_TEXT_2, Figure.MANUFACTURER + " as " + SearchManager.SUGGEST_COLUMN_TEXT_2);

	}

	private DatabaseHelper					openHelper;

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values)
	{

		SQLiteDatabase db = openHelper.getWritableDatabase();

		db.beginTransaction();
		switch (DatabaseProvider.uriMatcher.match(uri)) {
			case FIGURE:
				for (ContentValues value : values)
				{
					db.insert(Constants.DATABASE_TABLE_FIGURES, Figure.ISBN, value);
				}
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		db.setTransactionSuccessful();
		db.endTransaction();

		getContext().getContentResolver().notifyChange(uri, null);

		return 1;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs)
	{
		SQLiteDatabase db = openHelper.getWritableDatabase();

		switch (DatabaseProvider.uriMatcher.match(uri)) {
			case FIGURE:
				return db.delete(Constants.DATABASE_TABLE_FIGURES, where, whereArgs);
			case FIGURE_ID:
				return db.delete(Constants.DATABASE_TABLE_FIGURES, Figure._ID + "=" + uri.getPathSegments().get(1)
						+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public String getType(Uri uri)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		ContentValues values;
		if (initialValues != null)
		{
			values = new ContentValues(initialValues);
		} else
		{
			values = new ContentValues();
		}

		SQLiteDatabase db = openHelper.getWritableDatabase();

		long rowId;

		switch (DatabaseProvider.uriMatcher.match(uri)) {
			case FIGURE:
				rowId = db.insert(Constants.DATABASE_TABLE_FIGURES, Figure.ID, values);
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		if (rowId > 0)
		{
			Uri u = ContentUris.withAppendedId(uri, rowId);
			getContext().getContentResolver().notifyChange(u, null);
			return u;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate()
	{
		openHelper = new DatabaseHelper(getContext());
		return false;
	}

	@SuppressWarnings("fallthrough")
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String defaultSortOrder;
		switch (DatabaseProvider.uriMatcher.match(uri)) {
			case FIGURE_ID:
				qb.appendWhere(Figure.ID + "=" + uri.getPathSegments().get(1));
			case FIGURE:
				qb.setTables(Constants.DATABASE_TABLE_FIGURES);
				qb.setProjectionMap(DatabaseProvider.newsProjectionMap);
				defaultSortOrder = Figure.DEFAULT_SORT_ORDER;
				break;
			case FIGURE_SUGGESTION:
				qb.setTables(Constants.DATABASE_TABLE_FIGURES);
				qb.setProjectionMap(DatabaseProvider.suggestProjectionMap);
				defaultSortOrder = Figure.DEFAULT_SORT_ORDER;
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder))
		{
			orderBy = defaultSortOrder;
		} else
		{
			orderBy = sortOrder;
		}

		// if(Constants.DEBUGMODE) Log.d("RATP", "query" +
		// qb.buildQuery(projection, selection,
		// selectionArgs, null, null, orderBy,null));

		// Get the database and run the query
		SQLiteDatabase db = openHelper.getReadableDatabase();

		// if(Constants.DEBUGMODE)
		// Log.d(Constants.PROJECT_TAG,qb.buildQuery(projection, selection,
		// selectionArgs, null, null, orderBy,null));
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		SQLiteDatabase db = openHelper.getWritableDatabase();

		switch (DatabaseProvider.uriMatcher.match(uri)) {
			case FIGURE:
				return db.update(Constants.DATABASE_TABLE_FIGURES, values, selection, selectionArgs);
			case FIGURE_ID:
				return db.update(Constants.DATABASE_TABLE_FIGURES, values, Figure._ID + "=" + uri.getPathSegments().get(1)
						+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

	}

}
