
package net.myfigurecollection;

import java.io.IOException;

import Utils.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieSyncManager;

import net.myfigurecollection.android.webservices.MFCService;
import net.myfigurecollection.android.webservices.RequestListener;

import org.apache.http.client.ClientProtocolException;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

public class SplashScreenActivity extends SherlockActivity implements RequestListener
{
	/**
	 * The activity to be started after splash screen
	 */
	@SuppressWarnings("rawtypes")
	public static Class			nextActivity	= Credentials.class;
	private SharedPreferences	mUserSettings;
	private SplashTask			task;

	/**
	 * Minimum duration of the splash screen in milliseconds
	 */
	private static final int	SPLASH_DURATION	= 1000;
	long						startTime;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// AQUtility.cleanCacheAsync(this, 0, 0);
		setTheme(R.style.Theme_Sherlock);
		// optionally set content layout, but prefer theme with window
		// background if only need to display an image
		startTime = System.currentTimeMillis();
		setContentView(R.layout.splash);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		mUserSettings = getSharedPreferences(MFCService.PREFERENCES, Context.MODE_WORLD_WRITEABLE);

		final int elapsedTime = (int) (System.currentTimeMillis() - startTime);
		if (elapsedTime < SplashScreenActivity.SPLASH_DURATION)
		{

			if (mUserSettings.getString(Credentials.PASSWORD, null) != null)
			{
				task = new SplashTask();
				task.execute(new Integer(SplashScreenActivity.SPLASH_DURATION));
			} else
			{
				next();
			}

		} else
		{
			next();
		}

	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (task != null)
		{
			task.cancel(true);
		}
	}

	class SplashTask extends AsyncTask<Integer, Integer, Void>
	{
		MFCService	mfc;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			mfc = new MFCService();
			CookieSyncManager.createInstance(SplashScreenActivity.this);
		}

		@Override
		protected Void doInBackground(final Integer... params)
		{
			try
			{
				SystemClock.sleep(params[0]);
				mfc.login(SplashScreenActivity.this, mUserSettings);
			}
			catch (final ClientProtocolException e)
			{
				Log.e(Constants.PROJECT_TAG, "ClientProtocolException in onClick", e);

			}
			catch (final IOException e)
			{
				Log.e(Constants.PROJECT_TAG, "IOException in onClick", e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(final Void result)
		{
			super.onPostExecute(result);
			if (!isCancelled())
			{
				next();
			}
		}

	}

	/**
	 * Launch the next activity, can be called from any thread
	 */
	private void next()
	{
		if ((task != null) && !task.isCancelled())
		{
			task.cancel(true);
		}

		final Intent i = new Intent(SplashScreenActivity.this, SplashScreenActivity.nextActivity);
		startActivity(i);
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event)
	{
		// intercept back key to set a cancel flag to notify the thread that the
		// aplication must not be started

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onRequestcompleted(final int requestCode, final Object result)
	{
		if (result != null)
		{
			// if(!task.isCancelled())task.cancel(true);
			SplashScreenActivity.nextActivity = MFCApp2Activity.class;

		}

		next();

	}

}