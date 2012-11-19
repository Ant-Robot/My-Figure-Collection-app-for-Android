
package net.myfigurecollection;

import java.util.List;

import net.myfigurecollection.android.FigureListFragment;
import net.myfigurecollection.android.FigureListFragment.OnFigureSelectedListener;
import net.myfigurecollection.android.FigureViewerFragment.OnFigureViewListener;
import net.myfigurecollection.android.data.SearchFigureProvider;
import net.myfigurecollection.android.data.objects.Figure;
import net.myfigurecollection.android.webservices.RequestListener;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class SearchResultActivity extends SherlockFragmentActivity implements OnFigureSelectedListener, OnFigureViewListener, OnNavigationListener,
		RequestListener
{

	public class WebClient extends WebViewClient
	{

		@Override
		public void onLoadResource(WebView view, String url)
		{
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			super.onPageFinished(view, url);
			OnFigureDetailsFinishedLoading(url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			super.onPageStarted(view, url, favicon);
			OnFigureDetailsStartedLoading(url);

			view.setWebChromeClient(new WebChromeClient() {

				@Override
				public void onProgressChanged(WebView view, int newProgress)
				{
					Log.d("FigureViewerFragment.updateUrl(...).new WebChromeClient() {...}", newProgress + "% " + view.getUrl());
					OnfigureDetailsLoading(view.getUrl(), newProgress);
					super.onProgressChanged(view, newProgress);
				}

			});

		}
	}

	static final boolean		IS_HONEYCOMB	= Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	private static final int	SEARCHPOSITION	= -1;
	private boolean				isLandscape		= false;

	private final boolean		showHomeUp		= true;

	// private FigureListFragment leftFrag;
	private final boolean		useLogo			= false;

	private WebView				viewer;

	@Override
	public void changeValue(int id, String value)
	{
		// TODO Auto-generated method stub
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.main_menu, menu);
	//
	// // set up a listener for the refresh item
	// refresh = menu.findItem(R.id.menu_refresh);
	// refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() {
	// // on selecting show progress spinner for 1s
	// @Override
	// public boolean onMenuItemClick(MenuItem item) {
	// // item.setActionView(R.layout.progress_action);
	// handler.postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// refresh.setActionView(null);
	// }
	// }, 1000);
	// return false;
	// }
	// });
	// return super.onCreateOptionsMenu(menu);
	// }

	@Override
	public void changeVisibility(int id, int value)
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
				String query = getIntent().getStringExtra(SearchManager.QUERY);

				SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchFigureProvider.AUTHORITY, SearchFigureProvider.MODE);
				suggestions.saveRecentQuery(query, null);
			}
		}

	}

	@SuppressWarnings("unused")
	private void dispatchTakePictureIntent(int actionCode)
	{
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePictureIntent, actionCode);
	}

	public String getTabTitle(int position)
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
		setContentView(R.layout.searchresult);
		viewer = (WebView) findViewById(R.id.figureView);
		viewer.setWebViewClient(new WebClient());
		WebSettings webSettings = viewer.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		viewer.setBackgroundColor(0);

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		ListFragment leftFrag = FigureListFragment.newInstance(getResources().getColor(R.color.android_green), 1f, 0, 0, 0, 0,
				SearchResultActivity.SEARCHPOSITION, SearchResultActivity.this);

		fragmentTransaction.add(R.id.subroot, leftFrag);
		fragmentTransaction.commit();

		final ActionBar ab = getSupportActionBar();
		setSupportProgressBarIndeterminateVisibility(false);
		// set defaults for logo & home up
		ab.setDisplayHomeAsUpEnabled(showHomeUp);
		ab.setDisplayUseLogoEnabled(useLogo);

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

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
		}
		else super.onBackPressed();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// setContentView(R.layout.main);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

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

	}

	@Override
	public void onContextMenu(int id)
	{
		// startActionMode(mAdapter.leftFrag.getActionMode());
		Log.d("MFC", "Context");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_PROGRESS);

		checkSearch();

		loadInterface();

		viewer.loadData(
				"<html><head><style type=\"text/css\">p{color:#FFF;font-weight:bold;text-align:center;vertical-align:middle;}</style></head><body><p>Choose a figure to view its details on MFC</p></body></html>",
				"text/html", "utf-8");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		menu.add(getString(R.string.action_label_search))
				.setIcon(R.drawable.ic_action_search)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void OnFigureDetailsFinishedLoading(String url)
	{
		setSupportProgressBarIndeterminateVisibility(false);
		setSupportProgressBarVisibility(false);
	}

	@Override
	public void OnfigureDetailsLoading(String url, int percent)
	{
		setSupportProgressBarVisibility(true);
		setSupportProgress(percent * 100);
	}

	@Override
	public void OnFigureDetailsStartedLoading(String url)
	{
		setSupportProgressBarIndeterminateVisibility(true);
	}

	@Override
	public void onFigureSelected(String tutUrl)
	{
		if (viewer != null)
		{
			// viewer.startAnimation(new SweetScaleAnimation(0, 50, 0, 50, 300,
			// viewer, false));
			viewer.setVisibility(View.VISIBLE);
			// ((LinearLayout.LayoutParams)viewer.getLayoutParams()).weight =
			// 100;

			viewer.loadUrl(tutUrl);
		}

	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId)
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

		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			ActivityManager mngr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

			List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

			if (taskList.get(0).numActivities == 1 &&
					taskList.get(0).topActivity.getClassName().equals(this.getClass().getName()))
			{
				Intent i = new Intent(this, MFCApp2Activity.class);
				startActivity(i);
			} else
			{
				onBackPressed();
			}
		}
		else if (item.getTitle().equals(getString(R.string.action_label_search)))
		{
			// mAdapter.leftFrag.load(true);
			onSearchRequested();
		}
		return super.onOptionsItemSelected(item);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.myfigurecollection.android.webservices.RequestListener#onRequestcompleted
	 * (int, java.lang.Object)
	 */
	@Override
	public void onRequestcompleted(int requestCode, Object result)
	{
		setSupportProgressBarIndeterminateVisibility(false);

	}

	public void updateUrl(String newUrl)
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