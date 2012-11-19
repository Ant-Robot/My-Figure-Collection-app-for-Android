
package net.myfigurecollection;

import java.io.File;
import java.io.IOException;

import Utils.Constants;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import net.myfigurecollection.android.FigureListFragment;
import net.myfigurecollection.android.FigureListFragment.OnFigureSelectedListener;
import net.myfigurecollection.android.FigureViewerFragment.OnFigureViewListener;
import net.myfigurecollection.android.data.SearchFigureProvider;
import net.myfigurecollection.android.data.XMLHandler;
import net.myfigurecollection.android.data.objects.Figure;
import net.myfigurecollection.android.webservices.MFCService;
import net.myfigurecollection.android.webservices.RequestListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.androidquery.util.AQUtility;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;
import com.viewpagerindicator.TitleProvider;

public class MFCApp2Activity extends SherlockFragmentActivity implements OnFigureSelectedListener, OnFigureViewListener, OnPageChangeListener,
		OnNavigationListener, RequestListener
{

	class TitleFragmentAdapter extends FragmentStatePagerAdapter implements TitleProvider
	{

		public FigureListFragment	leftFrag;
		final int					MARGIN	= 16;
		int							mCount	= 3;
		int							mCurrentTab;

		public TitleFragmentAdapter(final FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public int getCount()
		{
			return mCount;
		}

		@Override
		public Fragment getItem(final int position)
		{

			leftFrag = FigureListFragment.newInstance(getResources().getColor(R.color.android_green), 1f, MARGIN, MARGIN, MARGIN, MARGIN,
					position, MFCApp2Activity.this);
			return leftFrag;
		}

		@Override
		public String getTitle(final int position)
		{
			return getTabTitle(position);
		}

		public void setCount(final int count)
		{
			if ((count > 0) && (count <= 10))
			{
				mCount = count;
				notifyDataSetChanged();
			}
		}

	}

	public class WebClient extends WebViewClient
	{

		@Override
		public void onLoadResource(final WebView view, final String url)
		{
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(final WebView view, final String url)
		{
			super.onPageFinished(view, url);
			OnFigureDetailsFinishedLoading(url);
		}

		@Override
		public void onPageStarted(final WebView view, final String url, final Bitmap favicon)
		{
			super.onPageStarted(view, url, favicon);
			OnFigureDetailsStartedLoading(url);

			view.setWebChromeClient(new WebChromeClient() {

				@Override
				public void onProgressChanged(final WebView view, final int newProgress)
				{
					Log.d("FigureViewerFragment.updateUrl(...).new WebChromeClient() {...}", newProgress + "% " + view.getUrl());
					OnfigureDetailsLoading(view.getUrl(), newProgress);
					super.onProgressChanged(view, newProgress);
				}

			});
		}
	}

	static final boolean	IS_HONEYCOMB	= Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	private String			currentUrl;

	private boolean			isLandscape		= false;
	TitleFragmentAdapter	mAdapter;
	PageIndicator			mIndicator;

	ViewPager				mPager;

	private final boolean	showHomeUp		= false;

	// private FigureListFragment leftFrag;
	private final boolean	useLogo			= false;
	private WebView			viewer;

	@Override
	public void changeValue(final int id, final String value)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void changeVisibility(final int id, final int value)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 */
	private void checkSearch()
	{
		if (getIntent().getExtras() != null)
		{
			if (Intent.ACTION_SEARCH.equals(getIntent().getAction()))
			{
				final String query = getIntent().getStringExtra(SearchManager.QUERY);

				final SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchFigureProvider.AUTHORITY, SearchFigureProvider.MODE);
				suggestions.saveRecentQuery(query, null);
			}
		}

	}

	/**
	 * @return
	 */
	private Intent createShareIntent(final String url)
	{
		final Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, url);
		sendIntent.setType("text/plain");
		return sendIntent;
	}

	@SuppressWarnings("unused")
	private void dispatchTakePictureIntent(final int actionCode)
	{
		final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePictureIntent, actionCode);
	}

	public String getTabTitle(final int position)
	{

		switch (position) {
			case 0:
				return getString(R.string.cat_wished);
			case 1:
				return getString(R.string.cat_ordered);
			case 2:
				return getString(R.string.cat_owned);
			case 3:
				return getString(R.string.cat_search);

		}

		return null;
	}

	private void loadInterface()
	{
		setContentView(R.layout.main);
		viewer = (WebView) findViewById(R.id.figureView);
		viewer.setWebViewClient(new WebClient());
		final WebSettings webSettings = viewer.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		viewer.setBackgroundColor(0);

		final ActionBar ab = getSupportActionBar();
		setSupportProgressBarIndeterminateVisibility(false);
		// set defaults for logo & home up
		ab.setDisplayHomeAsUpEnabled(showHomeUp);
		ab.setDisplayUseLogoEnabled(useLogo);

		// set up tabs nav
		// for (int i = 0; i < 3; i++)
		// {
		// ab.addTab(ab.newTab().setText(getTabTitle(i)).setTabListener(this));
		// }

		// showTabsNav();
		mAdapter = new TitleFragmentAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		final TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		indicator.setOnPageChangeListener(this);
		indicator.setFooterIndicatorStyle(IndicatorStyle.Triangle);
		mIndicator = indicator;

		final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		isLandscape = display.getWidth() > display.getHeight();
		int lefWidth;

		if (FigureListFragment.isLandscape = isLandscape)
		{
			FigureListFragment.EXPANDED_MODE = View.GONE;
			viewer.setVisibility(View.VISIBLE);
			lefWidth = (int) getResources().getDimension(R.dimen.left_width_landscape);

		} else
		{
			// ft.add(rightFrag, "hidden");
			FigureListFragment.EXPANDED_MODE = View.VISIBLE;
			viewer.setVisibility(View.GONE);
			lefWidth = (int) getResources().getDimension(R.dimen.left_width_portrait);
		}

		((LinearLayout.LayoutParams) findViewById(R.id.subroot).getLayoutParams()).width = lefWidth;
	}

	@Override
	public void onBackPressed()
	{
		FigureListFragment.EXPANDED_MODE = View.VISIBLE;
		if (viewer.getVisibility() == View.VISIBLE)
		{
			viewer.setVisibility(View.GONE);
			// viewer.startAnimation(new SweetScaleAnimation(1.0f, 1.0f, 1.0f,
			// 0.0f, 500, viewer, true));
		} else
		{
			super.onBackPressed();
		}
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// setContentView(R.layout.main);
		final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		isLandscape = display.getWidth() > display.getHeight();

		FigureListFragment.isLandscape = isLandscape;
		int lefWidth;

		if (FigureListFragment.isLandscape = isLandscape)
		{
			lefWidth = (int) getResources().getDimension(R.dimen.left_width_landscape);
			FigureListFragment.EXPANDED_MODE = View.GONE;
			viewer.setVisibility(View.VISIBLE);
		} else
		{
			// ft.add(rightFrag, "hidden");
			FigureListFragment.EXPANDED_MODE = View.VISIBLE;
			viewer.setVisibility(View.GONE);
			lefWidth = (int) getResources().getDimension(R.dimen.left_width_portrait);
		}

		((LinearLayout.LayoutParams) findViewById(R.id.subroot).getLayoutParams()).width = lefWidth;

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onContextMenu(final int id)
	{
		// startActionMode(mAdapter.leftFrag.getActionMode());
		mAdapter.notifyDataSetChanged();
		Log.d("MFC", "Context");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_PROGRESS);

		final String dir = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + getPackageName() + "/files/images";

		Log.d("MFCApp2Activity", "cache dir: " + dir);

		final File directory = new File(dir);
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		{
			// We can read and write the media

			final File nomedia = new File(dir + "/.nomedia");
			if (!nomedia.exists())
			{
				try
				{
					directory.mkdirs();
					nomedia.createNewFile();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}

		}

		AQUtility.setCacheDir(directory);

		checkSearch();

		loadInterface();

		final Context context = getSupportActionBar().getThemedContext();
		final ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.filters, R.layout.sherlock_spinner_item);
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		// getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// getSupportActionBar().setListNavigationCallbacks(list, this);

		// TODO:new FigureGalleryCursor(this, "5769", this);

		viewer.loadData(
				"<html><head><style type=\"text/css\">p{color:#FFF;font-weight:bold;text-align:center;vertical-align:middle;}</style></head><body><p>Choose a figure to view its details on MFC</p></body></html>",
				"text/html", "utf-8");
		// ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		if (currentUrl != null)
		{
			getSupportMenuInflater().inflate(R.menu.share_action_provider, menu);
			// Set file with share history to the provider and set the share
			// intent.
			final MenuItem actionItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);
			final ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
			actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
			// Note that you can set/change the intent any time,
			// say when the user has selected an image.
			actionProvider.setShareIntent(createShareIntent(currentUrl.replace("#idx_tabs", "/")));
		}

		menu.add(getString(R.string.action_label_search))
				.setIcon(R.drawable.ic_action_search)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(getString(R.string.take_picture))
				.setIcon(R.drawable.ic_action_camera)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
						MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(getString(R.string.refresh))
				.setIcon(R.drawable.ic_action_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void OnFigureDetailsFinishedLoading(final String url)
	{
		setSupportProgressBarIndeterminateVisibility(false);
		setSupportProgressBarVisibility(false);
	}

	@Override
	public void OnfigureDetailsLoading(final String url, final int percent)
	{
		setSupportProgressBarVisibility(true);
		setSupportProgress(percent * 100);
	}

	@Override
	public void OnFigureDetailsStartedLoading(final String url)
	{
		setSupportProgressBarIndeterminateVisibility(true);
	}

	@Override
	public void onFigureSelected(final String tutUrl)
	{
		if (viewer != null)
		{
			// viewer.startAnimation(new SweetScaleAnimation(0, 50, 0, 50, 300,
			// viewer, false));
			viewer.setVisibility(View.VISIBLE);
			// ((LinearLayout.LayoutParams)viewer.getLayoutParams()).weight =
			// 100;

			viewer.loadUrl(tutUrl);
			currentUrl = tutUrl;
			invalidateOptionsMenu();
		}

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onNavigationItemSelected(final int itemPosition, final long itemId)
	{
		switch (itemPosition) {
			case 0:
				FigureListFragment.ADDITIONAL_SORTER = "";
				break;
			case 1:
				FigureListFragment.ADDITIONAL_SORTER = Figure.CATEGORY + " ASC, ";
				break;
			case 2:
				FigureListFragment.ADDITIONAL_SORTER = Figure.MANUFACTURER + " ASC, ";
				break;

			default:
				FigureListFragment.ADDITIONAL_SORTER = "";
				break;
		}

		// mAdapter.leftFrag.getNewsCursor().requery();
		mAdapter.notifyDataSetChanged();

		return false;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{

		if (item.getTitle().equals(getString(R.string.refresh)))
		{
			// mAdapter.leftFrag.load(true);
			setSupportProgressBarIndeterminateVisibility(true);
			final XMLHandler xmlh = new XMLHandler(this, true);
			xmlh.setListener(this);
			xmlh.execute((Constants.API_MODE_COLLECTION + getSharedPreferences(MFCService.PREFERENCES, Context.MODE_WORLD_WRITEABLE).getString("Login",
					"Kumasanmk")));
		} else if (item.getTitle().equals(getString(R.string.take_picture)))
		{
			final Intent i = new Intent(this, PhotoIntentActivity.class);
			startActivity(i);
		}
		else if (item.getTitle().equals(getString(R.string.action_label_search)))
		{
			// mAdapter.leftFrag.load(true);
			onSearchRequested();
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	public void onPageScrolled(final int arg0, final float arg1, final int arg2)
	{

	}

	@Override
	public void onPageScrollStateChanged(final int arg0)
	{

	}

	@Override
	public void onPageSelected(final int arg0)
	{
		if (FigureListFragment.mMode != null)
		{
			FigureListFragment.mMode.finish();
		}
		// Toast.makeText(this, "Changed to page " + arg0,
		// Toast.LENGTH_SHORT).show();

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * net.myfigurecollection.android.webservices.RequestListener#onRequestcompleted
	 * (int, java.lang.Object)
	 */
	@Override
	public void onRequestcompleted(final int requestCode, final Object result)
	{
		setSupportProgressBarIndeterminateVisibility(false);

	}

	public void updateUrl(final String newUrl)
	{
		if (viewer != null)
		{
			viewer.loadUrl(newUrl);
		}
	}

	// public void onJsonReady(JsonCursor cursor)
	// {
	// //TODO:(new AQuery(this)).id(R.id.gallery).adapter(new
	// FigureGalleryAdapter(this, R.id.gallery, cursor));
	// //listAq.id(R.id.gallery).visible();
	// }

}