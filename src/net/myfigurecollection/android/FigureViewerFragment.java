
package net.myfigurecollection.android;

import net.myfigurecollection.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class FigureViewerFragment extends Fragment
{
	public interface OnFigureViewListener
	{
		void OnFigureDetailsFinishedLoading(String url);

		void OnfigureDetailsLoading(String url, int percent);

		void OnFigureDetailsStartedLoading(String url);
	}

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
			if (mListener != null) mListener.OnFigureDetailsFinishedLoading(url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			super.onPageStarted(view, url, favicon);
			if (mListener != null) mListener.OnFigureDetailsStartedLoading(url);
		}

	}

	private int						mColor;
	private OnFigureViewListener	mListener;
	private int						mMarginBottom;
	private int						mMarginLeft;
	private int						mMarginRight;
	private int						mMarginTop;
	private FrameLayout				mView;

	private float					mWeight;

	private WebView					viewer	= null;

	public FigureViewerFragment()
	{}

	public FigureViewerFragment(int color, float weight, int margin_left, int margin_right, int margin_top, int margin_bottom, OnFigureViewListener ofvl)
	{
		mColor = color;
		mWeight = weight;
		mMarginLeft = margin_left;
		mMarginRight = margin_right;
		mMarginTop = margin_top;
		mMarginBottom = margin_bottom;
		mListener = ofvl;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		// try
		// {
		// if (display.getRotation() == Surface.ROTATION_0 ||
		// display.getRotation() == Surface.ROTATION_180)
		// setHasOptionsMenu(true);
		// }
		// catch (NoSuchMethodError e)
		// {
		// if (display.getOrientation() == Surface.ROTATION_0 ||
		// display.getOrientation() == Surface.ROTATION_180)
		// setHasOptionsMenu(true);// TODO:
		// }

		if (display.getWidth() < display.getHeight()) setHasOptionsMenu(true);

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		mView = new FrameLayout(getActivity());

		GradientDrawable background = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_rect);
		background.setColor(mColor);

		mView.setBackgroundDrawable(background);
		android.widget.LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, mWeight);
		lp.setMargins(mMarginLeft, mMarginTop, mMarginRight, mMarginBottom);

		mView.setLayoutParams(lp);
		final int padding = (int) getResources().getDimension(R.dimen.list_padding);
		mView.setPadding(padding * 2, padding * 2, padding * 2, padding * 2);

		viewer = (WebView) inflater.inflate(R.layout.figure_view, container, false);
		viewer.setWebViewClient(new WebClient());

		WebSettings webSettings = viewer.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		viewer.setBackgroundColor(0);

		viewer.loadData(
				"<html><head><style type=\"text/css\">p{color:#FFF;font-weight:bold;text-align:center;vertical-align:middle;}</style></head><body><p>Choose a figure to view its details on MFC</p></body></html>",
				"text/html", "utf-8");

		mView.addView(viewer);

		return mView;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onDetach()
	{
		FigureListFragment.EXPANDED_MODE = View.VISIBLE;
		super.onDetach();
	}

	public void updateUrl(final String newUrl)
	{
		if (viewer != null)
		{

			viewer.loadUrl(newUrl);
		}
	}

}
