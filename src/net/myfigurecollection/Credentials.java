
package net.myfigurecollection;

import java.io.IOException;

import Utils.Constants;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.myfigurecollection.android.webservices.MFCService;
import net.myfigurecollection.android.webservices.RequestListener;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.cookie.Cookie;

import com.actionbarsherlock.app.SherlockActivity;

public class Credentials extends SherlockActivity implements RequestListener
{

	class Login extends AsyncTask<Void, Void, Void>
	{

		private MFCService	mfc;

		@Override
		protected Void doInBackground(final Void... params)
		{
			try
			{
				SystemClock.sleep(1000);
				mfc.login(Credentials.this, mUserSettings);
			}
			catch (final ClientProtocolException e)
			{
				publishProgress();
				Log.e(Constants.PROJECT_TAG, "ClientProtocolException in onClick", e);

			}
			catch (final IOException e)
			{
				publishProgress();
				Log.e(Constants.PROJECT_TAG, "IOException in onClick", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Void result)
		{
			pd.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute()
		{
			mUserSettings.edit().putString(Credentials.LOGIN, ((EditText) findViewById(R.id.EditTextLogin)).getText().toString().trim()).commit();
			mUserSettings.edit().putString(Credentials.PASSWORD, ((EditText) findViewById(R.id.EditTextPassword)).getText().toString()).commit();
			mfc = new MFCService();

			CookieSyncManager.createInstance(Credentials.this);
			Credentials.cookieManager = CookieManager.getInstance();

			if (Credentials.sessionCookie != null)
			{
				// delete old cookies
				Credentials.cookieManager.removeSessionCookie();
			}

			if (pd == null)
			{

				pd = new ProgressDialog(Credentials.this);
				pd.setCancelable(false);
				pd.setMessage("Logging in");
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			}
			pd.setIndeterminate(true);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(final Void... values)
		{
			Toast.makeText(Credentials.this, R.string.offline_mode, Toast.LENGTH_LONG).show();
			final Intent i = new Intent(Credentials.this, MFCApp2Activity.class);
			startActivity(i);
			pd.hide();
			pd.dismiss();
			super.onProgressUpdate(values);
		}

	}

	public static CookieManager	cookieManager;
	public static final String	DOMAIN				= ".myfigurecollection.net";
	private static final String	LAST_CONNECTION_OK	= "lastConnection";
	public static final String	LOGIN				= "Login";
	public static final String	PASSWORD			= "Password";
	public static String		sessionCookie;
	private SharedPreferences	mUserSettings;

	// private static boolean firstlaunch = true;

	// OnFocusChangeListener ofcl = new OnFocusChangeListener() {
	//
	// public void onFocusChange(View v, boolean hasFocus) {
	// if (hasFocus) ((Button) v).performClick();
	//
	// }
	// };

	OnClickListener				ocl					= new OnClickListener() {
														@Override
														public void onClick(final View v)
														{

															final Login login = new Login();
															login.execute();

														}
													};

	ProgressDialog				pd;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.layout_login);
		mUserSettings = getSharedPreferences(MFCService.PREFERENCES, Context.MODE_WORLD_WRITEABLE);

		((EditText) findViewById(R.id.EditTextLogin)).setText(mUserSettings.getString(Credentials.LOGIN, ""));
		((EditText) findViewById(R.id.EditTextPassword)).setText(mUserSettings.getString(Credentials.PASSWORD, ""));

		((EditText) findViewById(R.id.EditTextLogin)).setHint(Credentials.LOGIN);
		((EditText) findViewById(R.id.EditTextPassword)).setHint(Credentials.PASSWORD);
		((Button) findViewById(R.id.ButtonLogin)).setOnClickListener(ocl);
		((Button) findViewById(R.id.ButtonSignup)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v)
			{
				final Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://secure.myfigurecollection.net/signs.php?mode=up&ln=en"));
				startActivity(i);
			}
		});
		// ((Button)
		// findViewById(R.id.ButtonLogin)).setOnFocusChangeListener(ofcl);

		getSupportActionBar().setSubtitle("version " + getString(R.string.app_version));

	}

	@SuppressLint("NewApi")
	@Override
	public void onRequestcompleted(final int requestCode, final Object result)
	{

		runOnUiThread(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					if ((pd != null) && pd.isShowing())
					{
						pd.dismiss();
					}
				}
				catch (final IllegalArgumentException e)
				{}

			}
		});

		if ((result != null) || mUserSettings.getBoolean(Credentials.LAST_CONNECTION_OK, false))
		{
			mUserSettings.edit().putBoolean(Credentials.LAST_CONNECTION_OK, true).commit();
			Credentials.sessionCookie = ((Cookie) result).getName() + "=" + ((Cookie) result).getValue();

			//
			try
			{
				final BackupManager bm = new BackupManager(this);
				bm.dataChanged();
			}
			catch (final NoClassDefFoundError e)
			{
				Log.e("Credentials", "NoClassDefFoundError", e);
			}

			// TODO:List
			final Intent i = new Intent(this, MFCApp2Activity.class);
			// i.putExtra(FigureListFragment.FIGURE_TAB, 0);
			startActivity(i);

		} else
		{
			runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					Toast.makeText(Credentials.this, R.string.login_password_incorrect_or_mfc_down, Toast.LENGTH_LONG).show();
				}
			});
		}

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// menu.add("Create account").setIcon(R.drawable.signup).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
	// | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	//
	// return super.onCreateOptionsMenu(menu);
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// Intent i = new Intent(Intent.ACTION_VIEW,
	// Uri.parse(getString(R.string.signup_url)));
	// startActivity(i);
	// return super.onOptionsItemSelected(item);
	// }

}
