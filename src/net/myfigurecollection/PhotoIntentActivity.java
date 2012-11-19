
package net.myfigurecollection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import Utils.FigureAdapter;
import Utils.GLToolbox;
import Utils.TextureRenderer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import net.myfigurecollection.android.FigureListFragment;
import net.myfigurecollection.android.data.objects.Category;
import net.myfigurecollection.android.data.objects.Figure;
import net.myfigurecollection.android.data.storage.AlbumStorageDirFactory;
import net.myfigurecollection.android.data.storage.BaseAlbumDirFactory;
import net.myfigurecollection.android.data.storage.FroyoAlbumDirFactory;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class PhotoIntentActivity extends SherlockFragmentActivity implements LoaderCallbacks<Cursor>, GLSurfaceView.Renderer
{

	private static final int	ACTION_TAKE_PHOTO_B					= 1;
	private static final int	ACTION_TAKE_PHOTO_S					= 2;

	protected static final int	ACTIVITY_SELECT_IMAGE				= 0;
	private static final String	BITMAP_STORAGE_KEY					= "viewbitmap";
	private static final String	IMAGEVIEW_VISIBILITY_STORAGE_KEY	= "imageviewvisibility";
	private static final String	JPEG_FILE_PREFIX					= "IMG_";
	private static final String	JPEG_FILE_SUFFIX					= ".jpg";
	private static final String	REQUEST_URL							= "http://myfigurecollection.net/picture/upload/";

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 * 
	 * @param context
	 *            The application's environment.
	 * @param action
	 *            The Intent action to check for availability.
	 * 
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(final Context context, final String action)
	{
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		final List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private FigureAdapter			figureDatabaseAdapter;

	protected boolean				isFormReady					= false;

	private AQuery					listAq;
	private AlbumStorageDirFactory	mAlbumStorageDirFactory		= null;
	private Button					mButtonGallery;
	private Button					mButtonSend;
	private TextView				mCatDescription;

	private String[]				mCatDescriptions;

	private int[]					mCatIds;
	private Spinner					mCatSpinner;
	private CompoundButton			mCheckboxFigure;
	private CompoundButton			mCheckboxNSFW;
	private final BroadcastReceiver	mConnReceiver				= new BroadcastReceiver() {
																	private NetworkInfo	currentNetworkInfo;
																	private boolean		noConnectivityisFailover;
																	private String		noConnectivityreason;
																	private NetworkInfo	otherNetworkInfo;

																	@Override
																	public void onReceive(final Context context, final Intent intent)
																	{
																		noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,
																				false);
																		noConnectivityreason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
																		noConnectivityisFailover = intent.getBooleanExtra(
																				ConnectivityManager.EXTRA_IS_FAILOVER, false);

																		currentNetworkInfo = (NetworkInfo) intent
																				.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
																		otherNetworkInfo = (NetworkInfo) intent
																				.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

																		mButtonSend.setEnabled(canSend());
																	}
																};
	protected int					mCurCatId;
	private String					mCurrentPhotoPath;
	private Bitmap					mImageBitmap;
	private ImageView				mImageView;
	private GLSurfaceView			mEffectView;

	Button.OnClickListener			mPickPicOnClickListener		=
																		new Button.OnClickListener() {
																			@Override
																			public void onClick(final View v)
																			{
																				dispatchTakePictureIntent(PhotoIntentActivity.ACTIVITY_SELECT_IMAGE);

																			}
																		};
	private Spinner					mSpinner;

	Button.OnClickListener			mTakePicOnClickListener		=
																		new Button.OnClickListener() {
																			@Override
																			public void onClick(final View v)
																			{
																				dispatchTakePictureIntent(PhotoIntentActivity.ACTION_TAKE_PHOTO_B);
																			}
																		};

	Button.OnClickListener			mTakePicSOnClickListener	=
																		new Button.OnClickListener() {
																			@Override
																			public void onClick(final View v)
																			{
																				dispatchTakePictureIntent(PhotoIntentActivity.ACTION_TAKE_PHOTO_S);
																			}
																		};
	private boolean					noConnectivity				= false;
	private final int[]				mTextures					= new int[2];
	private final TextureRenderer	mTexRenderer				= new TextureRenderer();
	private int						mImageWidth;
	private int						mImageHeight;
	private EffectContext			mEffectContext;
	private boolean					mInitialized;
	private int						mCurrentEffect;
	private Effect					mEffect;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layoutphoto);

		mButtonSend = (Button) findViewById(R.id.buttonSendPhoto);
		mButtonGallery = (Button) findViewById(R.id.buttonGallery);

		mButtonSend.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(final View v)
			{
				sendPic();

			}
		});

		registerReceivers();

		mSpinner = (Spinner) findViewById(R.id.spinner1);

		if (Build.VERSION.SDK_INT >= 14)
		{

			mEffectView = (GLSurfaceView) findViewById(R.id.effectsview);
			mEffectView.setEGLContextClientVersion(2);
			mEffectView.setRenderer(this);
			mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
			mCurrentEffect = R.id.none;
			mCheckboxNSFW = (Switch) findViewById(R.id.switch1);
			mCheckboxFigure = (Switch) findViewById(R.id.switchNoFigure);

		}
		else
		{
			mImageView = (ImageView) findViewById(R.id.imageView1);
			mCheckboxNSFW = (CheckBox) findViewById(R.id.switch1);
			mCheckboxFigure = (CheckBox) findViewById(R.id.switchNoFigure);
		}

		mCheckboxFigure.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
			{
				mSpinner.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			}
		});

		mImageBitmap = null;
		mCatSpinner = (Spinner) findViewById(R.id.spinnerCat);
		mCatDescription = (TextView) findViewById(R.id.textViewCat);

		listAq = new AQuery(this);

		mCatDescriptions = getResources().getStringArray(R.array.pic_categories_descriptions);
		mCatIds = getResources().getIntArray(R.array.pic_categories_values);

		mCatDescription.setText(mCatDescriptions[0]);
		mCatSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id)
			{
				mCurCatId = mCatIds[position];
				mCatDescription.setText(mCatDescriptions[position]);
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent)
			{
				// TODO Auto-generated method stub

			}
		});

		figureDatabaseAdapter = new FigureAdapter(this, R.layout.cell_figure, getProjection(), new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }, listAq, 0);

		mSpinner.setAdapter(figureDatabaseAdapter);

		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.pic_categories, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCatSpinner.setAdapter(adapter);

		getSupportLoaderManager().initLoader(0, savedInstanceState, this);

		final Button picBtn = (Button) findViewById(R.id.btnIntend);
		setBtnListenerOrDisable(
				picBtn,
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE);

		setBtnListenerOrDisable(
				mButtonGallery,
				mPickPicOnClickListener,
				Intent.ACTION_PICK);

		if (Build.VERSION.SDK_INT >= 8)
		{
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else
		{
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
	}

	/**
	 * @return
	 */
	private boolean canSend()
	{
		// TODO Auto-generated method stub
		return isFormReady && !noConnectivity;
	}

	private File createImageFile() throws IOException
	{
		// Create an image file name
		final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		final String imageFileName = PhotoIntentActivity.JPEG_FILE_PREFIX + timeStamp + "_";
		final File albumF = getAlbumDir();
		final File imageF = File.createTempFile(imageFileName, PhotoIntentActivity.JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private void dispatchTakePictureIntent(final int actionCode)
	{

		Intent takePictureIntent = null;

		switch (actionCode) {
			case ACTION_TAKE_PHOTO_B:
				File f = null;

				try
				{
					takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					f = setUpPhotoFile();
					mCurrentPhotoPath = f.getAbsolutePath();
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				}
				catch (final IOException e)
				{
					e.printStackTrace();
					f = null;
					mCurrentPhotoPath = null;
				}
				break;
			case ACTIVITY_SELECT_IMAGE:
				takePictureIntent = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				// takePictureIntent.setType("image/*");

				break;
			default:
				break;
		} // switch

		if (takePictureIntent != null)
		{
			startActivityForResult(takePictureIntent, actionCode);
		}
	}

	private void galleryAddPic()
	{
		final Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		final File f = new File(mCurrentPhotoPath);
		final Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	private File getAlbumDir()
	{
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
		{

			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null)
			{
				if (!storageDir.mkdirs())
				{
					if (!storageDir.exists())
					{
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}

		} else
		{
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	/* Photo album for this application */
	private String getAlbumName()
	{
		return getString(R.string.picture_directory);
	}

	public String[] getProjection()
	{
		return new String[] { Figure._ID, Figure.ID, Figure.NAME, Figure.DATE, Figure.MANUFACTURER, Figure.PRICE, Category.COLOR,
				Figure.SCORE };
	}

	private void handleBigCameraPhoto()
	{

		if (mCurrentPhotoPath != null)
		{
			setPic(mCurrentPhotoPath);
			galleryAddPic();
			// mCurrentPhotoPath = null;
		}

	}

	private void handleSmallCameraPhoto(final Intent intent)
	{
		final Bundle extras = intent.getExtras();
		mImageBitmap = (Bitmap) extras.get("data");
		mImageView.setImageBitmap(mImageBitmap);
		mImageView.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		switch (requestCode) {
			case ACTION_TAKE_PHOTO_B:
			{
				if (resultCode == Activity.RESULT_OK)
				{
					handleBigCameraPhoto();
				}
				break;
			} // ACTION_TAKE_PHOTO_B

			case ACTION_TAKE_PHOTO_S:
			{
				if (resultCode == Activity.RESULT_OK)
				{
					handleSmallCameraPhoto(data);
				}
				break;
			} // ACTION_TAKE_PHOTO_S

			case ACTIVITY_SELECT_IMAGE:
			{
				if (resultCode == Activity.RESULT_OK)
				{
					final Uri selectedImage = data.getData();
					final String[] columns = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME };

					final Cursor cursor = getContentResolver().query(selectedImage, columns, null, null, null);
					if (cursor != null)
					{
						cursor.moveToFirst();

						final int columnIndex = cursor.getColumnIndex(MediaColumns.DATA);
						if (columnIndex != -1)
						{
							// regular processing for gallery files
							final String fileName = cursor.getString(columnIndex);
							if (fileName != null)
							{
								setPic(fileName);
							} else
							{
								extractPicassa(selectedImage);
							}
						}
					} else
					{
						extractPicassa(selectedImage);
					}

					// Uri selectedImage = data.getData();
					// String[] filePathColumn = { MediaColumns.DATA };
					//
					// Cursor cursor = getContentResolver().query(selectedImage,
					// filePathColumn, null, null, null);
					// cursor.moveToFirst();
					//
					// int columnIndex =
					// cursor.getColumnIndex(filePathColumn[0]);
					// String filePath = cursor.getString(columnIndex);
					// cursor.close();
					// setPic(filePath);
				}
				break;
			} // ACTIVITY_SELECT_IMAGE

		} // switch
	}

	protected void extractPicassa(Uri selectedImage)
	{
		// this is not gallery provider
		{
			try
			{
				Log.d(this.getClass().getName(), "Actual URI: " + selectedImage.toString());
				if (selectedImage.toString().startsWith("content://com.android.gallery3d.provider"))
				{
					// use the com.google provider, not the
					// com.android provider.
					selectedImage = Uri.parse(selectedImage.toString().replace("com.android.gallery3d", "com.google.android.gallery3d"));
				}
				Log.d(this.getClass().getName(), "Effective URI: " + selectedImage.toString());

				final InputStream inputStream = getContentResolver().openInputStream(selectedImage);
				// read this file into InputStream

				// write the inputStream to a FileOutputStream
				final File f = createImageFile();
				final OutputStream out = new FileOutputStream(f);

				int read = 0;
				final byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1)
				{
					out.write(bytes, 0, read);
				}

				inputStream.close();
				out.flush();
				out.close();

				Log.d("PhotoIntentActivity", "New file created!");

				setPic(f.getAbsolutePath());
			}
			catch (final IOException e)
			{
				Log.e("PhotoIntentActivity", "io", e);
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int arg0, final Bundle arg1)
	{

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(this, Figure.CONTENT_URI,
				getProjection(), "status>=?",
				new String[] { "0" },
				FigureListFragment.ADDITIONAL_SORTER + Figure.NAME + " ASC");
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unRegisterReceivers();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android
	 * .support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset(final Loader<Cursor> arg0)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android
	 * .support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished(final Loader<Cursor> arg0, final Cursor arg1)
	{
		if (figureDatabaseAdapter != null)
		{
			figureDatabaseAdapter.swapCursor(arg1);
		}

	}

	@Override
	protected void onRestoreInstanceState(final Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(PhotoIntentActivity.BITMAP_STORAGE_KEY);

		if (mImageView != null)
		{
			mImageView.setImageBitmap(mImageBitmap);
		} else if (mEffectView != null)
		{
			loadTextures(mImageBitmap);
		}
		if (mImageView != null)
		{
			mImageView.setVisibility(
					savedInstanceState.getBoolean(PhotoIntentActivity.IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
							View.VISIBLE : View.INVISIBLE
					);
		}

	}

	private void loadTextures(final Bitmap _bitmap)
	{

		Bitmap bitmap = _bitmap;
		if (_bitmap == null)
		{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			// options.inPurgeable = true;

			bitmap = BitmapFactory.decodeResource(getResources(), R.raw.klanklan, options);
		}

		// Generate textures
		GLES20.glGenTextures(2, mTextures, 0);

		// Load input bitmap

		mImageWidth = bitmap.getWidth();
		mImageHeight = bitmap.getHeight();
		mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

		runOnUiThread(new Runnable() {
			@Override
			public void run()
			{
				final LinearLayout.LayoutParams lp = (LayoutParams) mEffectView.getLayoutParams();
				final float width = lp.width;
				final float height = lp.height * (width / mImageWidth);

				lp.height = (int) height;

				mEffectView.setLayoutParams(lp);
			}
		});

		// Upload to texture
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

		// Set texture parameters
		GLToolbox.initTexParams();

	}

	public void setCurrentEffect(final int effect)
	{
		mCurrentEffect = effect;
	}

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(final Bundle outState)
	{
		outState.putParcelable(PhotoIntentActivity.BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putBoolean(PhotoIntentActivity.IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
		super.onSaveInstanceState(outState);
	}

	/*
	 * method to be invoked to register the receiver
	 */
	private void registerReceivers()
	{
		registerReceiver(mConnReceiver,
				new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

	}

	/**
	 * 
	 */
	protected void sendPic()
	{
		final Map<String, Object> params = new HashMap<String, Object>();

		params.put("pyon[]", new File(mCurrentPhotoPath));
		params.put("categoryid", mCurCatId);
		params.put("album", 1);
		params.put("aid", 0);
		params.put("new_album", "");
		params.put("source", "");
		params.put("sn", "0");
		params.put("commit", "upload");
		params.put("uploader", "Upload");
		params.put("nsfw", mCheckboxNSFW.isChecked() ? "1" : "0");

		final AQuery aq = new AQuery(getApplicationContext());
		aq.ajax(PhotoIntentActivity.REQUEST_URL, params, String.class, new AjaxCallback<String>() {

			@Override
			public void callback(final String url, final String object, final AjaxStatus status)
			{
				switch (status.getCode()) {

					case AjaxStatus.TRANSFORM_ERROR:
						showResult("unable to transform result to String", status);
						break;
					case AjaxStatus.NETWORK_ERROR:
						showResult("network error without response from server", status);
						break;
					case AjaxStatus.AUTH_ERROR:
						showResult("authentication error", status);
						break;
					default:
						showResult("other errors", status);
						break;
				}
			}
		});

	}

	private void setBtnListenerOrDisable(
			final Button btn,
			final Button.OnClickListener onClickListener,
			final String intentName
			)
	{
		if (PhotoIntentActivity.isIntentAvailable(this, intentName))
		{
			btn.setOnClickListener(onClickListener);
		} else
		{
			btn.setText(
					getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setEnabled(false);
		}
	}

	private void setPic(final String filePath)
	{

		mCurrentPhotoPath = filePath;

		if (mImageView != null)
		{
			/*
			 * There isn't enough memory to open up more than a couple camera
			 * photos
			 */
			/* So pre-scale the target bitmap into which the file is decoded */

			/* Get the size of the ImageView */
			final int targetW = mImageView.getWidth();
			final int targetH = mImageView.getHeight();

			/* Get the size of the image */
			final BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, bmOptions);
			final int photoW = bmOptions.outWidth;
			final int photoH = bmOptions.outHeight;

			/* Figure out which way needs to be reduced less */
			int scaleFactor = 1;
			if ((targetW > 0) || (targetH > 0))
			{
				scaleFactor = Math.min(photoW / targetW, photoH / targetH);
			}

			/* Set bitmap options to scale the image decode target */
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;

			/* Decode the JPEG file into a Bitmap */
			final Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);

			/* Associate the Bitmap to the ImageView */
			mImageView.setImageBitmap(bitmap);
			mImageView.setVisibility(View.VISIBLE);
		} else if (mEffectView != null)
		{
			loadTextures(BitmapFactory.decodeFile(filePath));
		}
		mButtonSend.setEnabled(canSend());
		isFormReady = true;
	}

	private File setUpPhotoFile() throws IOException
	{

		final File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();

		return f;
	}

	/**
	 * @param string
	 * @param status
	 */
	protected void showResult(final String string, final AjaxStatus status)
	{
		Log.d("PhotoIntentActivity", "ajax response: " + string + " and message: " + status.getMessage());
	}

	private void unRegisterReceivers()
	{
		unregisterReceiver(mConnReceiver);
	}

	@TargetApi(14)
	private void initEffect()
	{
		final EffectFactory effectFactory = mEffectContext.getFactory();
		if (mEffect != null)
		{
			mEffect.release();
		}
		/**
		 * Initialize the correct effect based on the selected menu/action item
		 */
		switch (mCurrentEffect) {

			case R.id.none:
				break;

			case R.id.autofix:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_AUTOFIX);
				mEffect.setParameter("scale", 0.5f);
				break;

			case R.id.bw:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_BLACKWHITE);
				mEffect.setParameter("black", .1f);
				mEffect.setParameter("white", .7f);
				break;

			case R.id.brightness:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_BRIGHTNESS);
				mEffect.setParameter("brightness", 2.0f);
				break;

			case R.id.contrast:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_CONTRAST);
				mEffect.setParameter("contrast", 1.4f);
				break;

			case R.id.crossprocess:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_CROSSPROCESS);
				break;

			case R.id.documentary:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_DOCUMENTARY);
				break;

			case R.id.duotone:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_DUOTONE);
				mEffect.setParameter("first_color", Color.YELLOW);
				mEffect.setParameter("second_color", Color.DKGRAY);
				break;

			case R.id.filllight:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_FILLLIGHT);
				mEffect.setParameter("strength", .8f);
				break;

			case R.id.fisheye:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_FISHEYE);
				mEffect.setParameter("scale", .5f);
				break;

			case R.id.flipvert:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_FLIP);
				mEffect.setParameter("vertical", true);
				break;

			case R.id.fliphor:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_FLIP);
				mEffect.setParameter("horizontal", true);
				break;

			case R.id.grain:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_GRAIN);
				mEffect.setParameter("strength", 1.0f);
				break;

			case R.id.grayscale:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_GRAYSCALE);
				break;

			case R.id.lomoish:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_LOMOISH);
				break;

			case R.id.negative:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_NEGATIVE);
				break;

			case R.id.posterize:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_POSTERIZE);
				break;

			case R.id.rotate:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_ROTATE);
				mEffect.setParameter("angle", 180);
				break;

			case R.id.saturate:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_SATURATE);
				mEffect.setParameter("scale", .5f);
				break;

			case R.id.sepia:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_SEPIA);
				break;

			case R.id.sharpen:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_SHARPEN);
				break;

			case R.id.temperature:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_TEMPERATURE);
				mEffect.setParameter("scale", .9f);
				break;

			case R.id.tint:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_TINT);
				mEffect.setParameter("tint", Color.MAGENTA);
				break;

			case R.id.vignette:
				mEffect = effectFactory.createEffect(
						EffectFactory.EFFECT_VIGNETTE);
				mEffect.setParameter("scale", .5f);
				break;

			default:
				break;

		}
	}

	@TargetApi(14)
	private void applyEffect()
	{

		if (mEffect == null)
		{
			final EffectFactory effectFactory = mEffectContext.getFactory();
			mEffect = effectFactory.createEffect(
					EffectFactory.EFFECT_ROTATE);
			mEffect.setParameter("angle", 0);
		}
		mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
	}

	private void renderResult()
	{
		if (mCurrentEffect != R.id.none)
		{
			// if no effect is chosen, just render the original bitmap
			mTexRenderer.renderTexture(mTextures[1]);
		}
		else
		{
			// render the result of applyEffect()
			mTexRenderer.renderTexture(mTextures[0]);
		}
	}

	@TargetApi(14)
	@Override
	public void onDrawFrame(final GL10 gl)
	{
		if (!mInitialized)
		{
			// Only need to do this once
			mEffectContext = EffectContext.createWithCurrentGlContext();
			mTexRenderer.init();
			loadTextures(mImageBitmap);
			mInitialized = true;
		}
		if (mCurrentEffect != R.id.none)
		{
			// if an effect is chosen initialize it and apply it to the texture
			initEffect();
			applyEffect();
		}
		renderResult();
	}

	@Override
	public void onSurfaceChanged(final GL10 gl, final int width, final int height)
	{
		if (mTexRenderer != null)
		{
			mTexRenderer.updateViewSize(width, height);
		}
	}

	@Override
	public void onSurfaceCreated(final GL10 gl, final EGLConfig config)
	{}

	@Override
	public boolean onCreateOptionsMenu(final com.actionbarsherlock.view.Menu _menu)
	{
		final com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.effects, _menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final com.actionbarsherlock.view.MenuItem item)
	{
		setCurrentEffect(item.getItemId());
		mEffectView.requestRender();
		return true;
	}

}