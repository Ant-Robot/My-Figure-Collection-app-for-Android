package net.myfigurecollection.api.legacy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import net.myfigurecollection.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Climbatize
 */
@SuppressWarnings("unused")
public class CookieRequests {

    public static final String PREFERENCES = "net.myfigurecollection_preferences";
    public static final String APP_FOLDER = "/data/data/net.myfigurecollection/";
    private static final String SIGN_IN_URL = "https://secure.myfigurecollection.net/sign/in/";
    private static final String MFC_POST_URL = "http://myfigurecollection.net/items.php?mode=Android&iid=";
    public static CookieManager cookieManager;
    public static final String        DOMAIN                                = ".myfigurecollection.net";
    private static final String        LAST_CONNECTION_OK        = "lastConnection";
    public static final String        LOGIN                                = "Login";
    public static final String        PASSWORD                        = "Password";
    private static CookieRequests mInstance = null;
    private Context mContext;
    private RequestListener mListener = null;
    private AsyncTask<?, ?, ?> mCurrentTask;
    private DefaultHttpClient mHttpClient;
    private String mLoadingString = "Updating";
    private String mId;
    private String mPrevstat;
    private String mStatus;
    private String mNum;
    private String mScore;
    private String mDate;
    private String mShop;
    private String mPaid;
    private String mWishability;
    private SharedPreferences mUserSettings;
    public final static String        CAT_ORDERED                                                = "1";

    public final static String        CAT_OWNED                                                = "2";
    public final static String        CAT_WISHED                                                = "0";

    public static CookieRequests getInstance(Context c, SharedPreferences userSettings) {
        if (mInstance == null) {
            mInstance = new CookieRequests();
        }

        mInstance.mUserSettings = userSettings;
        mInstance.mContext = c;
        return mInstance;
    }



    /**
     * This method allows you to connect to MFC via a post request
     *
     * @throws ClientProtocolException
     * @throws IOException
     */
    public void login(RequestListener r, SharedPreferences prefs) throws ClientProtocolException, IOException {

        mListener = r;
        mUserSettings = prefs;
        if (mHttpClient == null) mHttpClient = new DefaultHttpClient();


        HttpPost httpost = new HttpPost(CookieRequests.SIGN_IN_URL);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", mUserSettings.getString("login", "")));
        nvps.add(new BasicNameValuePair("password", mUserSettings.getString("password", "")));
        nvps.add(new BasicNameValuePair("set_cookie", "1"));
        nvps.add(new BasicNameValuePair("commit", "signin"));
        nvps.add(new BasicNameValuePair("location", "http%3A%2F%2Fmyfigurecollection.net%2F"));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        HttpResponse response = mHttpClient.execute(httpost);
        HttpEntity entity = response.getEntity();

        // LogPlus.i("Login form get: " + response.getStatusLine());
        if (entity != null) {
            entity.consumeContent();
        }

        List<Cookie> cookies = mHttpClient.getCookieStore().getCookies();

        String sessionkey = null;

        Cookie sessionCookie = null;

        if (cookies.isEmpty()) {
            // LogPlus.d("login : no cookie");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                // LogPlus.d("login : cookie " + i + " = " +
                // cookies.get(i).toString());
                Cookie myCookie = cookies.get(i);

                String cookieString = myCookie.getName() + "=" + myCookie.getValue() + "; domain=" + myCookie.getDomain();

                try {
                    CookieSyncManager.createInstance(mContext);
                    cookieManager = CookieManager.getInstance();
                    cookieManager.setCookie(DOMAIN, cookieString);

                } catch (NullPointerException e) {
                    //Log.e("MFC", "NullPointerException :" + cookieString, e);
                }

                if (cookies.get(i).getName().compareTo("tb_session_key") == 0)
                    sessionCookie = (cookies.get(i));
            }
        }

        CookieSyncManager.getInstance().sync();

        mListener.onRequestcompleted(response.getStatusLine().getStatusCode(), sessionCookie);

    }

    /**
     * This method allows you to send figures datas related to your profile
     *
     * @param id          The figure id
     * @param prevstat    The current status in your collection
     * @param status      The wanted status
     * @param num         The number of figures with this id in your profile
     * @param score       The score you want to five to this figure (-1 to 9)
     * @param date        The date you own this figure
     * @param shop        The shop you bought this figure
     * @param paid        The price you paid
     * @param wishability How much you want this figure
     */
    public void postData(String id,
                         String prevstat,
                         String status,
                         String num,
                         String score,
                         String date,
                         String shop,
                         String paid,
                         String wishability,
                         RequestListener r) {

        this.mId = id;
        this.mPrevstat = prevstat;
        this.mStatus = status;
        this.mNum = num;
        this.mScore = score;
        this.mDate = date;
        this.mShop = shop;
        this.mPaid = paid;
        this.mWishability = wishability;

        request(CookieRequests.MFC_POST_URL + id, r);

    }

    /**
     * @param id          The id of the wished figure
     * @param prevstat    The current status of the figure
     * @param wishability How much do you want the figure (0 to 5)
     */
    public void wish(String id, String prevstat, String wishability, RequestListener r) {
        postData(id, prevstat, CAT_WISHED, "1", "-1", "0000-00-00", "", "", wishability, r);
        // Toast.makeText(v, "Figure wished", Toast.LENGTH_LONG).show();

    }

    /**
     * @param id       The figure id
     * @param prevstat The current status in your collection
     * @param shop     The shop you bought this figure
     * @param paid     The price you paid
     */
    public void order(String id, String prevstat, String shop, String paid, RequestListener r) {
        postData(id, prevstat, CAT_ORDERED, "1", "-1", "0000-00-00", shop, paid, "0", r);
        // Toast.makeText(v, "Figure ordered", Toast.LENGTH_LONG).show();

    }

    /**
     * @param id       The figure id
     * @param prevstat The current status in your collection
     */
    public void remove(String id, String prevstat, RequestListener r) {
        postData(id, prevstat, "9", "1", "-1", "0000-00-00", "", "", "0", r);
        // Toast.makeText(v, "Figure removed", Toast.LENGTH_LONG).show();

    }

    /**
     * @param id       The figure id
     * @param prevstat The current status in your collection
     * @param num      The number of figures with this id in your profile
     * @param date     The date you own this figure
     * @param shop     The shop you bought this figure
     * @param paid     The price you paid
     */
    public void own(String id, String prevstat, String shop, String paid, String num, String date, RequestListener r) {
        postData(id, prevstat, CAT_OWNED, num, "-1", date, shop, paid, "0", r);
        // Toast.makeText(v, "Figure owned", Toast.LENGTH_LONG).show();

    }

    public void cancelTask() {
        if (mCurrentTask != null && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
            mCurrentTask.cancel(true);
        }
    }

    public void request(String url, RequestListener listener) {
        this.mListener = listener;

        cancelTask();
        mCurrentTask = new AddFigure().execute(url);

    }

   

    /**
     * An AsyncTask to add a figure in the local database
     *
     * @author Climbatize
     */
    private class AddFigure extends AsyncTask<String, Integer, HttpResponse> {

        private String paid_date = "0000-00-00";
        private String shipping_date = "0000-00-00";
        private String my_category_id = "0";

        @Override
        protected HttpResponse doInBackground(String... params) {

            // Create a new HttpClient and Post Header
            if (mHttpClient == null) try {
                login(mListener, mUserSettings);
            } catch (ClientProtocolException e1) {
                Log.e("MFC", "Impossible to log in", e1);
            } catch (IOException e1) {
                Log.e("MFC", "Impossible to log in", e1);
            }

            HttpPost httppost = new HttpPost(params[0]);
            HttpResponse response = null;

            try {
                // Add your data

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("commit", "collect"));
                nameValuePairs.add(new BasicNameValuePair("exit", "1"));
                nameValuePairs.add(new BasicNameValuePair("status", mStatus));
                nameValuePairs.add(new BasicNameValuePair("num", mNum));
                nameValuePairs.add(new BasicNameValuePair("odate", mDate));
                nameValuePairs.add(new BasicNameValuePair("score", mScore));
                nameValuePairs.add(new BasicNameValuePair("cid", my_category_id));
                nameValuePairs.add(new BasicNameValuePair("wishability", mWishability));
                nameValuePairs.add(new BasicNameValuePair("previous_status", mPrevstat));
                nameValuePairs.add(new BasicNameValuePair("location", mShop));
                nameValuePairs.add(new BasicNameValuePair("value", mPaid));
                nameValuePairs.add(new BasicNameValuePair("bdate", paid_date));
                nameValuePairs.add(new BasicNameValuePair("sdate", shipping_date));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpUriRequest tst = httppost;
                tst.addHeader("Accept-Encoding", "gzip");
                // Execute HTTP Post Request
                response = mHttpClient.execute(tst);

                // convertStreamToString(new
                // GZIPInputStream(response.getEntity().getContent()),
                // "result.html");
            } catch (ClientProtocolException e) {
                Log.e("MFC", "Impossible to download", e);
            } catch (IOException e) {
                Log.e("MFC", "Impossible to download or save result", e);
            } catch (IllegalStateException e) {
                Log.e("MFC", "IllegalStateException in doInBackground", e);
            }
            // handler.sendEmptyMessage(0);

            return response;
        }

        @Override
        protected void onPostExecute(HttpResponse result) {
            mListener.onRequestcompleted(0, result);
        }
    }
}