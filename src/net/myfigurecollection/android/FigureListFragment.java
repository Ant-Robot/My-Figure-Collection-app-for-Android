
package net.myfigurecollection.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.myfigurecollection.R;
import net.myfigurecollection.android.data.SearchFigureProvider;
import net.myfigurecollection.android.data.XMLHandler;
import net.myfigurecollection.android.data.objects.Category;
import net.myfigurecollection.android.data.objects.Figure;
import net.myfigurecollection.android.webservices.MFCService;
import net.myfigurecollection.android.webservices.RequestListener;
import Utils.Constants;
import Utils.Flip3dAnimation;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.StaleDataException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.google.zxing.integration.android.IntentIntegrator;

public class FigureListFragment extends SherlockListFragment implements RequestListener, LoaderCallbacks<Cursor>
{
	public final class FigureAcionMode implements
			ActionMode.Callback
	{

		public FigureAcionMode(long id)
		{
			curfigure = id;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem aItem)
		{
			try
			{

				// curfigure = menuInfo.id;
				if (Constants.DEBUGMODE) Log.d(Constants.PROJECT_TAG, "Item " + curfigure);
				/* Switch on the ID of the item, to get what the user selected. */

				switch (aItem.getItemId()) {
					case Constants.CONTEXTMENU_DELETEITEM:
						MFCService.getInstance(getActivity(), UserSettings).remove(Long.toString(curfigure), curcat, FigureListFragment.this);
						delete = true;
						mode.finish();
						return true; /* true means: "we handled the event". */
					case Constants.CONTEXTMENU_OWNITEM:
						MFCService.getInstance(getActivity(), UserSettings).own(Long.toString(curfigure), curcat, "", "", "1", "0000-00-00",
								FigureListFragment.this);
						own = true;
						mode.finish();
						return true;
					case Constants.CONTEXTMENU_WISHITEM:
						MFCService.getInstance(getActivity(), UserSettings).wish(Long.toString(curfigure), curcat, "", FigureListFragment.this);
						wish = true;
						refreshTab();
						mode.finish();
						return true;
					case Constants.CONTEXTMENU_ORDERITEM:
						MFCService.getInstance(getActivity(), UserSettings).order(Long.toString(curfigure), curcat, "", "", FigureListFragment.this);
						buy = true;
						mode.finish();
						return true;
					case Constants.CONTEXTMENU_GOTOMFC:
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(Constants.FIGUREURL + Long.toString(curfigure)));
						mode.finish();
						startActivity(i);
						return false;

					case Constants.CONTEXTMENU_SCANITEM:

						IntentIntegrator.initiateScan(getActivity());

						return true; /* true means: "we handled the event". */

				}

			}
			catch (NullPointerException e)
			{
				Log.e(Constants.PROJECT_TAG, "NullPointerException", e);
			}
			return false;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu)
		{
			if (getArguments().getInt(FigureListFragment.TAB, -1) > -1) menu.add(Constants.CONTEXTMENU_DELETEITEM, Constants.CONTEXTMENU_DELETEITEM,
					Constants.CONTEXTMENU_DELETEITEM,
					getString(R.string.menu_figure_delete));
			if (getArguments().getInt(FigureListFragment.TAB, -1) < 0) menu.add(Constants.CONTEXTMENU_WISHITEM, Constants.CONTEXTMENU_WISHITEM,
					Constants.CONTEXTMENU_WISHITEM,
					getString(R.string.menu_figure_wish));
			if (getArguments().getInt(FigureListFragment.TAB, -1) < 1) menu.add(Constants.CONTEXTMENU_ORDERITEM, Constants.CONTEXTMENU_ORDERITEM,
					Constants.CONTEXTMENU_ORDERITEM,
					getString(R.string.menu_figure_order));
			if (getArguments().getInt(FigureListFragment.TAB, -1) < 2) menu.add(Constants.CONTEXTMENU_OWNITEM, Constants.CONTEXTMENU_OWNITEM,
					Constants.CONTEXTMENU_OWNITEM,
					getString(R.string.menu_figure_own));
			menu.add(0, Constants.CONTEXTMENU_GOTOMFC, 99, getString(R.string.menu_figure_MFC));
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode)
		{

		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu)
		{
			return false;
		}

	}

	public class FigureDatabaseAdapter extends SimpleCursorAdapter
	{

		public FigureDatabaseAdapter(Context context, int layout, String[] from, int[] to, int flags)
		{
			super(context, layout, null, from, to, flags);
		}

		@Override
		public long getItemId(int position)
		{

			if (figureDatabaseAdapter != null && figureDatabaseAdapter.getCursor() != null && figureDatabaseAdapter.getCursor().moveToPosition(position))
			{
				Long id = 0l;
				try
				{
					id = (long) getCursor().getInt(getCursor().getColumnIndex(Figure.ID));

				}
				catch (StaleDataException e)
				{
					Log.e(Constants.PROJECT_TAG, "StaleDataException", e);
				}
				return id;
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View v = super.getView(position, convertView, parent);

			getListView()
					.setFastScrollEnabled(FigureListFragment.isLarge && FigureListFragment.isLandscape || FigureListFragment.EXPANDED_MODE == View.VISIBLE);

			final TextView subtitle = (TextView) v.findViewById(R.id.TextView_subtitle);
			final TextView title = (TextView) v.findViewById(R.id.TextView_title);
			final TextView version = (TextView) v.findViewById(R.id.TextView_version);

			title.setVisibility(FigureListFragment.isLarge && FigureListFragment.isLandscape ? View.VISIBLE : FigureListFragment.EXPANDED_MODE);
			subtitle.setVisibility(FigureListFragment.isLarge && FigureListFragment.isLandscape ? View.VISIBLE : FigureListFragment.EXPANDED_MODE);
			version.setVisibility(FigureListFragment.isLarge && FigureListFragment.isLandscape ? View.VISIBLE : FigureListFragment.EXPANDED_MODE);

			try
			{

				int date = getCursor().getInt(getCursor().getColumnIndex(Figure.DATE));

				StringBuilder sb = new StringBuilder("" + (date > 0 ? date : ""));

				String name = getCursor().getString(getCursor().getColumnIndex(Figure.NAME));

				int manuStart = name.lastIndexOf("(", name.length() - 1);
				sb.append(" ").append(getCursor().getString(getCursor().getColumnIndex(Figure.MANUFACTURER)));

				if (getCursor().getInt(getCursor().getColumnIndex(Figure.PRICE)) > 0) sb.append(" - ")
						.append("" + getCursor().getInt(getCursor().getColumnIndex(Figure.PRICE))).append(" JPY");

				subtitle.setText(sb.toString());
				if (manuStart > 0)
				{
					title.setText(name.subSequence(0, manuStart));
				} else
				{
					title.setText(name);
				}
				version.setText(" ");

				ImageView iv = (ImageView) v.findViewById(R.id.ImageView_icon);

				v.setId((int) getItemId(position));
				if (curfigure == v.getId())
				{
					// title.setTextColor(Color.BLACK);
					subtitle.setTextColor(Color.WHITE);
					version.setTextColor(Color.WHITE);
					v.setBackgroundColor(Color.parseColor("#FF33b5e5"));
					v.findViewById(R.id.viewCategory).setBackgroundColor(
							Color.parseColor(getCursor().getString(getCursor().getColumnIndex(Category.COLOR)).replace("#", "#88")));
				} else
				{
					// title.setTextColor(Color.WHITE);
					subtitle.setTextColor(Color.parseColor("#FF33b5e5"));
					version.setTextColor(Color.parseColor("#FF33b5e5"));
					v.setBackgroundColor(Color.TRANSPARENT);
					int c = Color.parseColor(getCursor().getString(getCursor().getColumnIndex(Category.COLOR)).replace("#", "#FF"));
					iv.setBackgroundColor(c);
					v.findViewById(R.id.viewCategory).setBackgroundColor(c);
				}

				AQuery aq = listAq.recycle(v);
				// Bitmap placeholder = aq.getCachedImage(R.drawable.tsuko);

				final String url = Constants.THUMBROOT + v.getId() + ".jpg";
				Bitmap placeholder = aq.getCachedImage(url);
				if (aq.shouldDelay(position, v, parent, url))
				{

					aq.id(R.id.ImageView_icon).image(placeholder, com.androidquery.util.Constants.RATIO_PRESERVE);

				} else
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
			catch (NullPointerException npe)
			{
				if (Constants.DEBUGMODE) Log.w(Constants.PROJECT_TAG, "Not a figure");
			}

			return v;
		}

	}

	public interface OnFigureSelectedListener
	{
		public void changeValue(int id, String value);

		public void changeVisibility(int id, int value);

		public void onContextMenu(int id);

		public void onFigureSelected(String tutUrl);
	}

	public static String		ADDITIONAL_SORTER	= "";
	private static final String	COLOR				= "color";
	public static int			EXPANDED_MODE		= 0;
	public static final String	FIGURE_TAB			= "net.myfigurecollection.FigureTab";
	public static boolean		isLandscape;
	private static boolean		isLarge				= false;
	private static final String	MARGINBOTTOM		= "marginb";
	private static final String	MARGINLEFT			= "marginr";
	private static final String	MARGINRIGHT			= "marginl";
	private static final String	MARGINTOP			= "margint";
	public static ActionMode	mMode;
	private static boolean		refresh				= false;

	private static final String	TAB					= "tab";
	private static final String	WEIGHT				= "weight";

	public static FigureListFragment newInstance(int color, float weight, int margin_left, int margin_right, int margin_top, int margin_bottom, int tab,
			OnFigureSelectedListener listener)
	{
		FigureListFragment f = new FigureListFragment(color, weight, margin_left, margin_right, margin_top, margin_bottom, tab, listener);
		return f;
	}

	// private Cursor newsCursor;
	public String										curcat;

	long												curfigure;
	protected ProgressDialog							dialog;
	private FigureDatabaseAdapter						figureDatabaseAdapter;
	boolean												lastlogin	= false, own = false, buy = false, wish = false, delete = false, noeffect = false;
	private AQuery										listAq;
	private android.widget.LinearLayout.LayoutParams	lp;

	FrameLayout											mView		= null;

	float												scale		= 1;

	private OnFigureSelectedListener					tutSelectedListener;

	private SharedPreferences							UserSettings;

	public FigureListFragment()
	{}

	public FigureListFragment(int color, float weight, int margin_left, int margin_right, int margin_top, int margin_bottom, int tab,
			OnFigureSelectedListener listener)
	{
		Bundle args = new Bundle();
		args.putInt(FigureListFragment.COLOR, color);
		args.putFloat(FigureListFragment.WEIGHT, weight);
		args.putInt(FigureListFragment.MARGINLEFT, margin_left);
		args.putInt(FigureListFragment.MARGINRIGHT, margin_right);
		args.putInt(FigureListFragment.MARGINBOTTOM, margin_bottom);
		args.putInt(FigureListFragment.MARGINTOP, margin_top);
		args.putInt(FigureListFragment.TAB, tab);
		setArguments(args);

		tutSelectedListener = listener;
	}

	void closePopup()
	{
		if (dialog != null) dialog.dismiss();
	}

	private void createTabs()
	{

		final String[] projection = getProjection();

		curcat = Integer.toString(getArguments().getInt(FigureListFragment.TAB, -1));

		// newsCursor =
		// getActivity().getContentResolver().query(Figure.CONTENT_URI,
		// projection, "status=?", new String[] { curcat },ADDITIONAL_SORTER +
		// Figure.NAME + " ASC");
		// newsCursor.registerDataSetObserver(new DataSetObserver() {
		// @Override
		// public void onInvalidated()
		// {
		// for (StackTraceElement e : new Throwable().getStackTrace())
		// Log.d(Constants.PROJECT_TAG, "onInvalidated : " + e);
		// super.onInvalidated();
		// }
		// });

		listAq = new AQuery(getActivity());

		// listAq.id(R.id.gallery).gone();
		figureDatabaseAdapter = new FigureDatabaseAdapter(getActivity(), R.layout.cell_figure, projection, new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }, 0);

		setListAdapter(figureDatabaseAdapter);

		getLoaderManager().initLoader(0, null, this);

		try
		{
			getListView().invalidate();
		}
		catch (IllegalStateException e)
		{
			Log.e(Constants.PROJECT_TAG, "IllegalStateException", e);
		}

		try
		{
			closePopup();
		}
		catch (IllegalArgumentException e)
		{
			Log.e(Constants.PROJECT_TAG, "Dialog Error", e);
		}

	}

	public Callback getActionMode()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int getContainer()
	{
		// TODO Auto-generated method stub
		return ((ViewGroup) getView().getParent()).getId();
	}

	public String[] getProjection()
	{
		return new String[] { Figure._ID, Figure.ID, Figure.NAME, Figure.DATE, Figure.MANUFACTURER, Figure.PRICE, Category.COLOR,
				Figure.SCORE };
	}

	public void load(boolean forceRefresh)
	{
		try
		{
			getListView().scrollTo(0, 0);
			Log.d("DEBUG", " list = " + getListView().getCount());
		}
		catch (IllegalStateException e)
		{
			Log.e(Constants.PROJECT_TAG, "IllegalStateException", e);
		}
		// tab = 0;
		// if (getActivity().getIntent().getExtras() != null)
		// {
		// tab =
		// getActivity().getIntent().getExtras().getInt(FigureListFragment.FIGURE_TAB);
		// }

		// if (!creating)
		{
			if (forceRefresh) AQUtility.cleanCacheAsync(getActivity(), 0, 0);
			XMLHandler xmlh = new XMLHandler(getActivity(), forceRefresh);
			FigureListFragment.refresh = false;
			xmlh.setListener(this);
			xmlh.execute((Constants.API_MODE_COLLECTION + UserSettings.getString("Login", "Kumasanmk")));
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(getListView());

		/* Add Context-Menu listener to the ListView. */
		// getListView().setOnCreateContextMenuListener(l);
		getListView().setLongClickable(true);

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				Log.d("test", "menuContext");
				FigureListFragment.mMode = getSherlockActivity().startActionMode(new FigureAcionMode(arg1.getId()));
				tutSelectedListener.onContextMenu((int) getListView().getSelectedItemId());
				figureDatabaseAdapter.notifyDataSetChanged();
				return true;
			}
		});

		getListView().setVisibility(View.VISIBLE);

	}

	public boolean onContextItemSelected(MenuItem aItem)
	{
		try
		{

			AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();

			if (Constants.DEBUGMODE) Log.d(Constants.PROJECT_TAG, "Item " + menuInfo.id);
			/* Switch on the ID of the item, to get what the user selected. */

			// curfigure = menuInfo.id;
			figureDatabaseAdapter.notifyDataSetChanged();
			switch (aItem.getItemId()) {
				case Constants.CONTEXTMENU_DELETEITEM:
					MFCService.getInstance(getActivity(), UserSettings).remove(Long.toString(curfigure), curcat, this);
					delete = true;
					return true; /* true means: "we handled the event". */
				case Constants.CONTEXTMENU_OWNITEM:
					MFCService.getInstance(getActivity(), UserSettings).own(Long.toString(curfigure), curcat, "", "", "1", "0000-00-00", this);
					own = true;
					return true;
				case Constants.CONTEXTMENU_WISHITEM:
					MFCService.getInstance(getActivity(), UserSettings).wish(Long.toString(curfigure), curcat, "", this);
					wish = true;
					refreshTab();
					return true;
				case Constants.CONTEXTMENU_ORDERITEM:
					MFCService.getInstance(getActivity(), UserSettings).order(Long.toString(curfigure), curcat, "", "", this);
					buy = true;
					return true;
				case Constants.CONTEXTMENU_GOTOMFC:
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(Constants.FIGUREURL + Long.toString(curfigure)));
					startActivity(i);
					return false;

				case Constants.CONTEXTMENU_SCANITEM:

					IntentIntegrator.initiateScan(getActivity());

					return true; /* true means: "we handled the event". */

			}

		}
		catch (NullPointerException e)
		{
			Log.e(Constants.PROJECT_TAG, "NullPointerException", e);
		}

		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// setListAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
		// R.array.tut_titles, R.layout.list_item));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
	 * android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1)
	{
		if (curcat == null) curcat = "-1";
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(getActivity(), Figure.CONTENT_URI,
				getProjection(), "status=?",
				new String[] { curcat },
				FigureListFragment.ADDITIONAL_SORTER + Figure.NAME + " ASC");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		mView = new FrameLayout(getActivity());

		// GradientDrawable background = (GradientDrawable)
		// this.getResources().getDrawable(R.drawable.bg_rect);

		// this.mView.setBackgroundDrawable(background);
		lp = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT, getArguments()
				.getInt(FigureListFragment.MARGINRIGHT));
		lp.setMargins(getArguments().getInt(FigureListFragment.MARGINLEFT), getArguments().getInt(FigureListFragment.MARGINTOP),
				getArguments().getInt(FigureListFragment.MARGINRIGHT),
				getArguments().getInt(FigureListFragment.MARGINBOTTOM));
		mView.setLayoutParams(lp);

		FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		ListView lv = new ListView(getActivity());
		lv.setLayoutParams(flp);
		lv.setId(android.R.id.list);
		lv.setScrollingCacheEnabled(false);
		lv.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
		lv.setCacheColorHint(getArguments().getInt(FigureListFragment.COLOR));
		lv.setFastScrollEnabled(true);
		lv.setSmoothScrollbarEnabled(true);
		lv.setStackFromBottom(false);
		// lv.setDivider(getResources().getDrawable(R.drawable.dividerblue));
		// lv.setDividerHeight(2);
		lv.setDivider(null);
		mView.addView(lv);
		// final int padding = (int)
		// getResources().getDimension(R.dimen.list_padding);
		// mView.setPadding(padding, padding, padding, padding);

		UserSettings = getActivity().getSharedPreferences(MFCService.PREFERENCES, Context.MODE_WORLD_WRITEABLE);
		if (!FigureListFragment.isLarge) FigureListFragment.isLarge = getResources().getBoolean(R.bool.isLarge);

		return mView;

	}

	@Override
	public void onDestroy()
	{
		// if (newsCursor != null) newsCursor.close();
		// if(mMode!=null) mMode.finish();
		super.onDestroy();
	}

	public void onImageDownloaded(ImageView arg0)
	{
		final float centerX = arg0.getWidth() / 2;
		final float centerY = arg0.getHeight() / 2;
		final Flip3dAnimation animation = new Flip3dAnimation(0, 180, centerX, centerY);
		animation.setDuration(200);
		animation.setInterpolator(new AccelerateInterpolator());
		arg0.startAnimation(animation);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		if (tutSelectedListener == null && (getActivity() instanceof OnFigureSelectedListener))
		{
			tutSelectedListener = (OnFigureSelectedListener) getActivity();
		}

		curfigure = v.getId();
		String content = Constants.FIGUREURL + curfigure + "#idx_tabs";
		FigureListFragment.EXPANDED_MODE = View.GONE;
		tutSelectedListener.onFigureSelected(content);
		figureDatabaseAdapter.notifyDataSetChanged();
		if (FigureListFragment.mMode != null) FigureListFragment.mMode.finish();
		// l.setSelection(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android
	 * .support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0)
	{
		if (figureDatabaseAdapter != null) figureDatabaseAdapter.swapCursor(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android
	 * .support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1)
	{
		if (figureDatabaseAdapter != null) figureDatabaseAdapter.swapCursor(arg1);

	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onRequestcompleted(int requestCode, Object result)
	{
		if (requestCode > 0)
		{
			lastlogin = result != null;
		} else
		{

			createTabs();

			noeffect = false;

			ContentValues values = new ContentValues();
			values.put(Figure.ID, curfigure);

			if (wish)
			{
				values.put(Figure.STATUS, 0);
				getActivity().getContentResolver().update(Figure.CONTENT_URI, values, Figure.ID + "=" + values.getAsString(Figure.ID), null);
				FigureListFragment.refresh = true;
			} else if (buy)
			{
				values.put(Figure.STATUS, 1);
				getActivity().getContentResolver().update(Figure.CONTENT_URI, values, Figure.ID + "=" + values.getAsString(Figure.ID), null);
				FigureListFragment.refresh = true;
			} else if (own)
			{
				values.put(Figure.STATUS, 2);
				getActivity().getContentResolver().update(Figure.CONTENT_URI, values, Figure.ID + "=" + values.getAsString(Figure.ID), null);
				FigureListFragment.refresh = true;
			} else if (delete)
			{
				getActivity().getContentResolver().delete(Figure.CONTENT_URI, Figure.ID + "=" + curfigure, null);
			}

			wish = buy = own = delete = false;
			refreshTab();

		}
	}

	@Override
	public void onResume()
	{
		if (getActivity().getIntent().getExtras() != null)
		{
			if (tutSelectedListener == null && (getActivity() instanceof OnFigureSelectedListener))
			{
				tutSelectedListener = (OnFigureSelectedListener) getActivity();
			}

			scale = getResources().getDisplayMetrics().density;

			if (Constants.DEBUGMODE) Log.d(Constants.PROJECT_TAG, "onCreate : intent action: " + getActivity().getIntent().getAction());
			if (Intent.ACTION_SEARCH.equals(getActivity().getIntent().getAction()))
			{
				String query = getActivity().getIntent().getStringExtra(SearchManager.QUERY);

				SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(), SearchFigureProvider.AUTHORITY, SearchFigureProvider.MODE);
				suggestions.saveRecentQuery(query, null);

				try
				{
					AQUtility.cleanCacheAsync(getActivity(), 0, 0);
					XMLHandler xmlh = new XMLHandler(getActivity(), true);
					FigureListFragment.refresh = false;
					xmlh.setListener(this);
					xmlh.execute(Constants.API_MODE_SEARCH + URLEncoder.encode(query, "UTF-8"));

				}
				catch (UnsupportedEncodingException e)
				{
					Log.e("FigureListFragment", "onResume", e);
				}

				if (Constants.DEBUGMODE) Log.d(Constants.PROJECT_TAG, "onCreate : query " + query);
			} else
			{
				load(FigureListFragment.refresh);
			}
		} else
		{
			load(FigureListFragment.refresh);
		}
		super.onResume();
		refreshTab();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	void openPopup()
	{
		if (dialog != null) dialog.dismiss();
		else dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), false);
		dialog.setCancelable(true);
	}

	private void refreshTab()
	{
		getLoaderManager().restartLoader(0, null, this);
	}

	public void setWeight(float f)
	{
		Log.d("DEBUG", mView.getParent().getClass().toString() + " - weight : " + f);
		mView.setLayoutParams(lp);

	}

}