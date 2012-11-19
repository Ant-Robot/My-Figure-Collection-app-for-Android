/*
 * Copyright (C) 2007 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.myfigurecollection.android.webservices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Looper;
import android.text.format.Time;
import android.util.Log;

/**
 * Subclass of the Apache {@link DefaultHttpClient} that is configured with
 * reasonable default settings and registered schemes for Android, and
 * also lets the user add {@link HttpRequestInterceptor} classes.
 * Don't create this directly, use the {@link #newInstance} factory method.
 * 
 * <p>
 * This client processes cookies but does not retain them by default. To retain
 * cookies, simply add a cookie store to the HttpContext:
 * </p>
 * 
 * <pre>
 * context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
 * </pre>
 */
public final class AndroidHttpClient implements HttpClient
{

	/**
	 * Logs cURL commands equivalent to requests.
	 */
	private class CurlLogger implements HttpRequestInterceptor
	{
		@Override
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException
		{
			LoggingConfiguration configuration = curlConfiguration;
			if (configuration != null && configuration.isLoggable() && request instanceof HttpUriRequest)
			{
				// Never print auth token -- we used to check ro.secure=0 to
				// enable that, but can't do that in unbundled code.
				configuration.println(AndroidHttpClient.toCurl((HttpUriRequest) request, false));
			}
		}
	}

	/**
	 * Logging tag and level.
	 */
	private static class LoggingConfiguration
	{

		private final int		level;
		private final String	tag;

		private LoggingConfiguration(String tag, int level)
		{
			this.tag = tag;
			this.level = level;
		}

		/**
		 * Returns true if logging is turned on for this configuration.
		 */
		private boolean isLoggable()
		{
			return Log.isLoggable(tag, level);
		}

		/**
		 * Prints a message using this configuration.
		 */
		private void println(String message)
		{
			Log.println(level, tag, message);
		}
	}

	private static class TimeOfDay
	{
		int	hour;

		int	minute;
		int	second;

		TimeOfDay(int h, int m, int s)
		{
			hour = h;
			minute = m;
			second = s;
		}
	}

	// Gzip of data shorter than this probably won't be worthwhile
	public static long							DEFAULT_SYNC_MIN_GZIP_BYTES	= 256;

	private static final Pattern				HTTP_DATE_ANSIC_PATTERN		= Pattern.compile(AndroidHttpClient.HTTP_DATE_ANSIC_REGEXP);

	private static final String					HTTP_DATE_ANSIC_REGEXP		= "[ ]([A-Za-z]{3,9})[ ]+([0-9]{1,2})[ ]"
																					+ "([0-9]{1,2}:[0-9][0-9]:[0-9][0-9])[ ]([0-9]{2,4})";

	private static final Pattern				HTTP_DATE_RFC_PATTERN		= Pattern.compile(AndroidHttpClient.HTTP_DATE_RFC_REGEXP);

	/*
	 * Regular expression for parsing HTTP-date.
	 * Wdy, DD Mon YYYY HH:MM:SS GMT
	 * RFC 822, updated by RFC 1123
	 * Weekday, DD-Mon-YY HH:MM:SS GMT
	 * RFC 850, obsoleted by RFC 1036
	 * Wdy Mon DD HH:MM:SS YYYY
	 * ANSI C's asctime() format
	 * with following variations
	 * Wdy, DD-Mon-YYYY HH:MM:SS GMT
	 * Wdy, (SP)D Mon YYYY HH:MM:SS GMT
	 * Wdy,DD Mon YYYY HH:MM:SS GMT
	 * Wdy, DD-Mon-YY HH:MM:SS GMT
	 * Wdy, DD Mon YYYY HH:MM:SS -HHMM
	 * Wdy, DD Mon YYYY HH:MM:SS
	 * Wdy Mon (SP)D HH:MM:SS YYYY
	 * Wdy Mon DD HH:MM:SS YYYY GMT
	 * HH can be H if the first digit is zero.
	 * Mon can be the full name of the month.
	 */
	private static final String					HTTP_DATE_RFC_REGEXP		= "([0-9]{1,2})[- ]([A-Za-z]{3,9})[- ]([0-9]{2,4})[ ]"
																					+ "([0-9]{1,2}:[0-9][0-9]:[0-9][0-9])";

	/** Interceptor throws an exception if the executing thread is blocked */
	private static final HttpRequestInterceptor	sThreadCheckInterceptor		= new HttpRequestInterceptor() {
																				@Override
																				public void process(HttpRequest request, HttpContext context)
																				{
																					// Prevent
																					// the
																					// HttpRequest
																					// from
																					// being
																					// sent
																					// on
																					// the
																					// main
																					// thread
																					if (Looper.myLooper() != null
																							&& Looper.myLooper() == Looper.getMainLooper()) {
																					throw new RuntimeException("This thread forbids HTTP requests");
																					}
																				}
																			};

	private static final String					TAG							= "AndroidHttpClient";

	public static final int						TIMEOUT						= 10 * 1000;

	/**
	 * Compress data to send to server.
	 * Creates a Http Entity holding the gzipped data.
	 * The data will not be compressed if it is too short.
	 * 
	 * @param data
	 *            The bytes to compress
	 * @return Entity holding the data
	 */
	public static AbstractHttpEntity getCompressedEntity(byte data[], ContentResolver resolver) throws IOException
	{
		AbstractHttpEntity entity;
		if (data.length < AndroidHttpClient.getMinGzipSize(resolver))
		{
			entity = new ByteArrayEntity(data);
		} else
		{
			ByteArrayOutputStream arr = new ByteArrayOutputStream();
			OutputStream zipper = new GZIPOutputStream(arr);
			zipper.write(data);
			zipper.close();
			entity = new ByteArrayEntity(arr.toByteArray());
			entity.setContentEncoding("gzip");
		}
		return entity;
	}

	private static int getDate(String dateString)
	{
		if (dateString.length() == 2) { return (dateString.charAt(0) - '0') * 10 + (dateString.charAt(1) - '0'); }
		return (dateString.charAt(0) - '0');
	}

	/**
	 * Retrieves the minimum size for compressing data.
	 * Shorter data will not be compressed.
	 */
	public static long getMinGzipSize(@SuppressWarnings("unused") ContentResolver resolver)
	{
		return AndroidHttpClient.DEFAULT_SYNC_MIN_GZIP_BYTES; // For now, this
																// is just a
																// constant.
	}

	/*
	 * jan = 9 + 0 + 13 = 22
	 * feb = 5 + 4 + 1 = 10
	 * mar = 12 + 0 + 17 = 29
	 * apr = 0 + 15 + 17 = 32
	 * may = 12 + 0 + 24 = 36
	 * jun = 9 + 20 + 13 = 42
	 * jul = 9 + 20 + 11 = 40
	 * aug = 0 + 20 + 6 = 26
	 * sep = 18 + 4 + 15 = 37
	 * oct = 14 + 2 + 19 = 35
	 * nov = 13 + 14 + 21 = 48
	 * dec = 3 + 4 + 2 = 9
	 */
	private static int getMonth(String monthString)
	{
		int hash = Character.toLowerCase(monthString.charAt(0)) + Character.toLowerCase(monthString.charAt(1)) + Character.toLowerCase(monthString.charAt(2))
				- 3 * 'a';
		switch (hash) {
			case 22:
				return Calendar.JANUARY;
			case 10:
				return Calendar.FEBRUARY;
			case 29:
				return Calendar.MARCH;
			case 32:
				return Calendar.APRIL;
			case 36:
				return Calendar.MAY;
			case 42:
				return Calendar.JUNE;
			case 40:
				return Calendar.JULY;
			case 26:
				return Calendar.AUGUST;
			case 37:
				return Calendar.SEPTEMBER;
			case 35:
				return Calendar.OCTOBER;
			case 48:
				return Calendar.NOVEMBER;
			case 9:
				return Calendar.DECEMBER;
			default:
				throw new IllegalArgumentException();
		}
	}

	private static TimeOfDay getTime(String timeString)
	{
		// HH might be H
		int i = 0;
		int hour = timeString.charAt(i++) - '0';
		if (timeString.charAt(i) != ':') hour = hour * 10 + (timeString.charAt(i++) - '0');
		// Skip ':'
		i++;

		int minute = (timeString.charAt(i++) - '0') * 10 + (timeString.charAt(i++) - '0');
		// Skip ':'
		i++;

		int second = (timeString.charAt(i++) - '0') * 10 + (timeString.charAt(i++) - '0');

		return new TimeOfDay(hour, minute, second);
	}

	/**
	 * Gets the input stream from a response entity. If the entity is gzipped
	 * then this will get a stream over the uncompressed data.
	 * 
	 * @param entity
	 *            the entity whose content should be read
	 * @return the input stream to read from
	 * @throws IOException
	 */
	public static InputStream getUngzippedContent(HttpEntity entity) throws IOException
	{
		InputStream responseStream = entity.getContent();
		if (responseStream == null) return responseStream;
		Header header = entity.getContentEncoding();
		if (header == null) return responseStream;
		String contentEncoding = header.getValue();
		if (contentEncoding == null) return responseStream;
		if (contentEncoding.contains("gzip")) responseStream = new GZIPInputStream(responseStream);
		return responseStream;
	}

	private static int getYear(String yearString)
	{
		if (yearString.length() == 2)
		{
			int year = (yearString.charAt(0) - '0') * 10 + (yearString.charAt(1) - '0');
			if (year >= 70) { return year + 1900; }
			return year + 2000;
		} else if (yearString.length() == 3)
		{
			// According to RFC 2822, three digit years should be added to 1900.
			int year = (yearString.charAt(0) - '0') * 100 + (yearString.charAt(1) - '0') * 10 + (yearString.charAt(2) - '0');
			return year + 1900;
		} else if (yearString.length() == 4)
		{
			return (yearString.charAt(0) - '0') * 1000 + (yearString.charAt(1) - '0') * 100 + (yearString.charAt(2) - '0') * 10 + (yearString.charAt(3) - '0');
		} else
		{
			return 1970;
		}
	}

	/**
	 * Modifies a request to indicate to the server that we would like a
	 * gzipped response. (Uses the "Accept-Encoding" HTTP header.)
	 * 
	 * @param request
	 *            the request to modify
	 * @see #getUngzippedContent
	 */
	public static void modifyRequestToAcceptGzipResponse(HttpRequest request)
	{
		request.addHeader("Accept-Encoding", "gzip");
	}

	/**
	 * Create a new HttpClient with reasonable defaults (which you can update).
	 * 
	 * @param userAgent
	 *            to report in your HTTP requests.
	 * @return AndroidHttpClient for you to use for all your requests.
	 */
	public static AndroidHttpClient newInstance(String userAgent)
	{
		return AndroidHttpClient.newInstance(userAgent, null /* session cache */);
	}

	/**
	 * Create a new HttpClient with reasonable defaults (which you can update).
	 * 
	 * @param userAgent
	 *            to report in your HTTP requests
	 * @param context
	 *            to use for caching SSL sessions (may be null for no caching)
	 * @return AndroidHttpClient for you to use for all your requests.
	 */
	public static AndroidHttpClient newInstance(String userAgent, Context context)
	{
		HttpParams params = new BasicHttpParams();

		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		// Default connection and socket timeout of 20 seconds. Tweak to taste.
		HttpConnectionParams.setConnectionTimeout(params, AndroidHttpClient.TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, AndroidHttpClient.TIMEOUT);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// Don't handle redirects -- return them to the caller. Our code
		// often wants to re-POST after a redirect, which we must do ourselves.
		HttpClientParams.setRedirecting(params, false);

		// Use a session cache for SSL sockets
		// SSLSessionCache sessionCache = context == null ? null : new
		// SSLSessionCache(context);

		// Set the specified user agent and register standard protocols.
		HttpProtocolParams.setUserAgent(params, userAgent);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);

		// We use a factory method to modify superclass initialization
		// parameters without the funny call-a-static-method dance.
		return new AndroidHttpClient(manager, params);
	}

	/**
	 * Returns the date of the given HTTP date string. This method can identify
	 * and parse the date formats emitted by common HTTP servers, such as
	 * <a href="http://www.ietf.org/rfc/rfc0822.txt">RFC 822</a>,
	 * <a href="http://www.ietf.org/rfc/rfc0850.txt">RFC 850</a>,
	 * <a href="http://www.ietf.org/rfc/rfc1036.txt">RFC 1036</a>,
	 * <a href="http://www.ietf.org/rfc/rfc1123.txt">RFC 1123</a> and
	 * <a href="http://www.opengroup.org/onlinepubs/007908799/xsh/asctime.html">
	 * ANSI
	 * C's asctime()</a>.
	 * 
	 * @return the number of milliseconds since Jan. 1, 1970, midnight GMT.
	 * @throws IllegalArgumentException
	 *             if {@code dateString} is not a date or
	 *             of an unsupported format.
	 */
	public static long parseDate(String timeString)
	{
		int date = 1;
		int month = Calendar.JANUARY;
		int year = 1970;
		TimeOfDay timeOfDay;

		Matcher rfcMatcher = AndroidHttpClient.HTTP_DATE_RFC_PATTERN.matcher(timeString);
		if (rfcMatcher.find())
		{
			date = AndroidHttpClient.getDate(rfcMatcher.group(1));
			month = AndroidHttpClient.getMonth(rfcMatcher.group(2));
			year = AndroidHttpClient.getYear(rfcMatcher.group(3));
			timeOfDay = AndroidHttpClient.getTime(rfcMatcher.group(4));
		} else
		{
			Matcher ansicMatcher = AndroidHttpClient.HTTP_DATE_ANSIC_PATTERN.matcher(timeString);
			if (ansicMatcher.find())
			{
				month = AndroidHttpClient.getMonth(ansicMatcher.group(1));
				date = AndroidHttpClient.getDate(ansicMatcher.group(2));
				timeOfDay = AndroidHttpClient.getTime(ansicMatcher.group(3));
				year = AndroidHttpClient.getYear(ansicMatcher.group(4));
			} else
			{
				throw new IllegalArgumentException();
			}
		}

		// FIXME: Y2038 BUG!
		if (year >= 2038)
		{
			year = 2038;
			month = Calendar.JANUARY;
			date = 1;
		}

		Time time = new Time(Time.TIMEZONE_UTC);
		time.set(timeOfDay.second, timeOfDay.minute, timeOfDay.hour, date, month, year);
		return time.toMillis(false /* use isDst */);
	}

	/**
	 * Generates a cURL command equivalent to the given request.
	 */
	private static String toCurl(HttpUriRequest request, boolean logAuthToken) throws IOException
	{
		StringBuilder builder = new StringBuilder();

		builder.append("curl ");

		for (Header header : request.getAllHeaders())
		{
			if (!logAuthToken && (header.getName().equals("Authorization") || header.getName().equals("Cookie")))
			{
				continue;
			}
			builder.append("--header \"");
			builder.append(header.toString().trim());
			builder.append("\" ");
		}

		URI uri = request.getURI();

		// If this is a wrapped request, use the URI from the original
		// request instead. getURI() on the wrapper seems to return a
		// relative URI. We want an absolute URI.
		if (request instanceof RequestWrapper)
		{
			HttpRequest original = ((RequestWrapper) request).getOriginal();
			if (original instanceof HttpUriRequest)
			{
				uri = ((HttpUriRequest) original).getURI();
			}
		}

		builder.append("\"");
		builder.append(uri);
		builder.append("\"");

		if (request instanceof HttpEntityEnclosingRequest)
		{
			HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) request;
			HttpEntity entity = entityRequest.getEntity();
			if (entity != null && entity.isRepeatable())
			{
				if (entity.getContentLength() < 1024)
				{
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					entity.writeTo(stream);
					String entityString = stream.toString();

					// TODO: Check the content type, too.
					builder.append(" --data-ascii \"").append(entityString).append("\"");
				} else
				{
					builder.append(" [TOO MUCH DATA TO INCLUDE]");
				}
			}
		}

		return builder.toString();
	}

	/** cURL logging configuration. */
	private volatile LoggingConfiguration	curlConfiguration;

	private final HttpClient				delegate;

	/* cURL logging support. */

	private RuntimeException				mLeakedException	= new IllegalStateException("AndroidHttpClient created and never closed");

	private AndroidHttpClient(ClientConnectionManager ccm, HttpParams params)
	{
		delegate = new DefaultHttpClient(ccm, params) {
			@Override
			protected HttpContext createHttpContext()
			{
				// Same as DefaultHttpClient.createHttpContext() minus the
				// cookie store.
				HttpContext context = new BasicHttpContext();
				context.setAttribute(ClientContext.AUTHSCHEME_REGISTRY, getAuthSchemes());
				context.setAttribute(ClientContext.COOKIESPEC_REGISTRY, getCookieSpecs());
				context.setAttribute(ClientContext.CREDS_PROVIDER, getCredentialsProvider());
				return context;
			}

			@Override
			protected BasicHttpProcessor createHttpProcessor()
			{
				// Add interceptor to prevent making requests from main thread.
				BasicHttpProcessor processor = super.createHttpProcessor();
				processor.addRequestInterceptor(AndroidHttpClient.sThreadCheckInterceptor);
				processor.addRequestInterceptor(new CurlLogger());

				return processor;
			}
		};
	}

	/**
	 * Release resources associated with this client. You must call this,
	 * or significant resources (sockets and memory) may be leaked.
	 */
	public void close()
	{
		if (mLeakedException != null)
		{
			getConnectionManager().shutdown();
			mLeakedException = null;
		}
	}

	/**
	 * Disables cURL logging for this client.
	 */
	public void disableCurlLogging()
	{
		curlConfiguration = null;
	}

	/**
	 * Enables cURL request logging for this client.
	 * 
	 * @param name
	 *            to log messages with
	 * @param level
	 *            at which to log messages (see {@link android.util.Log})
	 */
	public void enableCurlLogging(String name, int level)
	{
		if (name == null) { throw new NullPointerException("name"); }
		if (level < Log.VERBOSE || level > Log.ASSERT) { throw new IllegalArgumentException("Level is out of range [" + Log.VERBOSE + ".." + Log.ASSERT + "]"); }

		curlConfiguration = new LoggingConfiguration(name, level);
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException
	{
		return delegate.execute(target, request);
	}

	@Override
	public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException
	{
		return delegate.execute(target, request, context);
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException
	{
		return delegate.execute(target, request, responseHandler);
	}

	@Override
	public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException,
			ClientProtocolException
	{
		return delegate.execute(target, request, responseHandler, context);
	}

	@Override
	public HttpResponse execute(HttpUriRequest request) throws IOException
	{
		return delegate.execute(request);
	}

	@Override
	public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException
	{
		return delegate.execute(request, context);
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException
	{
		return delegate.execute(request, responseHandler);
	}

	@Override
	public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException
	{
		return delegate.execute(request, responseHandler, context);
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		if (mLeakedException != null)
		{
			Log.e(AndroidHttpClient.TAG, "Leak found", mLeakedException);
			mLeakedException = null;
		}
	}

	@Override
	public ClientConnectionManager getConnectionManager()
	{
		return delegate.getConnectionManager();
	}

	@Override
	public HttpParams getParams()
	{
		return delegate.getParams();
	}
}
