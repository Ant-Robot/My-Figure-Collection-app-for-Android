package net.myfigurecollection.api.request;

import android.app.Activity;
import android.preference.PreferenceManager;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.gson.GsonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import net.myfigurecollection.api.Item;
import net.myfigurecollection.api.StatusAnswer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import hugo.weaving.DebugLog;

/**
 * Created by Climbatize on 26/11/13.
 */
public class ItemRequest extends GoogleHttpClientSpiceRequest<StatusAnswer> {


    public final static String CAT_ORDERED = "1";
    public final static String CAT_OWNED = "2";
    public final static String CAT_WISHED = "0";
    public static final String CAT_REMOVED = "9";
    public static final String CAT_ADDED = "8";
    public static final String PARAM_COMMIT = "commit";
    public static final String PARAM_EXIT = "exit";
    public static final String PARAM_STATUS = "status";
    public static final String PARAM_NUM = "num";
    public static final String PARAM_DATE = "odate";
    public static final String PARAM_SCORE = "score";
    public static final String PARAM_CAT = "cid";
    public static final String PARAM_WISHABILITY = "wishability";
    public static final String PARAM_PREVIOUS_STATUS = "previous_status";
    public static final String PARAM_SHOP = "location";
    public static final String PARAM_VALUE = "value";
    public static final String PARAM_BUY_DATE = "bdate";
    public static final String PARAM_SHIP_DATE = "sdate";
    private static final String MFC_POST_URL = "http://myfigurecollection.net/items.php?iid=%s";
    private String shipping_date;
    private String paid_date;
    private String mPaid;
    private String mShop;
    private String mPrevstat;
    private String mWishability;
    private String my_category_id;
    private String mScore;
    private String mNum;
    private String mDate;
    private String mStatus;
    private Item mItem;
    private Activity mContext;

    public ItemRequest(Item item, Activity cntxt) {
        super(StatusAnswer.class);
        this.mItem = item;
        this.mContext = cntxt;
        this.shipping_date = this.paid_date = this.mDate = "0000-00-00";
    }

    public void postData(String shop, String wishability, String status, String prevStatus, String number, String score, String formattedDate, String price) {

        switch (Integer.parseInt(status))
        {
            case 1:
                paid_date = formattedDate;
                break;
            case 2:
                mDate = formattedDate;
                break;
        }

        this.mPrevstat = prevStatus;
        this.mStatus = status;
        this.mNum = number;
        this.mScore = score;
        this.mShop = shop;
        this.mPaid = price;
        this.mWishability = wishability;
    }

    /**
     * @param wishability How much do you want the figure (0 to 5)
     */
    public void wish(String wishability) {
        postData("", wishability, CAT_WISHED, this.mItem.getStatus().toString(), "1", "-1", null, "");
    }

    /**
     * @param shop The shop you bought this figure
     * @param paid The price you paid
     */
    public void order(String shop, String paid, String num) {
        postData(shop, mItem.getMycollection().getWishability(), CAT_ORDERED, this.mItem.getStatus().toString(), num, "-1", getFormattedDate(new Date()), paid);
    }

    /**
     */
    public void remove(String num) {
        postData("", mItem.getMycollection().getWishability(), CAT_REMOVED, this.mItem.getStatus().toString(), num, "-1", getFormattedDate(new Date()), "");
    }

    /**
     * @param num  The number of figures with this id in your profile
     * @param shop The shop you bought this figure
     * @param paid The price you paid
     */
    public void own(String shop, String paid, String num) {
        postData(shop, "", CAT_OWNED, this.mItem.getStatus().toString(), num, "-1", getFormattedDate(new Date()), paid);
    }

    public String getFormattedDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = "0000-00-00";
        if (d != null)
            date = sdf.format(d);
        return date;
    }

    @Override
    public StatusAnswer loadDataFromNetwork() throws Exception {


        String cookie = PreferenceManager.getDefaultSharedPreferences(mContext).getString("cookie", "");
        cookie = cookie.substring(cookie.indexOf('[')+1,cookie.lastIndexOf(']'));

        HttpRequest request = null;
        HttpRequestFactory requestFactory = getHttpRequestFactory();
        GenericUrl gUrl = new GenericUrl(String.format(MFC_POST_URL, mItem.getData().getId()));
        Map<String, String> params = new TreeMap<String, String>();
        params.put(PARAM_COMMIT, "collect");
        params.put(PARAM_STATUS, mStatus);
        params.put(PARAM_NUM, mNum);
        params.put(PARAM_SCORE, mScore);
        params.put(PARAM_DATE, mDate);
        params.put(PARAM_WISHABILITY, mWishability);
        params.put(PARAM_VALUE, mPaid);
        params.put(PARAM_SHOP, mShop);
        params.put("method", "0"); //TODO: shipping method
        params.put(PARAM_BUY_DATE, paid_date);
        params.put(PARAM_SHIP_DATE, shipping_date);
        params.put(PARAM_PREVIOUS_STATUS, mPrevstat);
        //params.put(PARAM_EXIT, "1");
        params.put("reload", "0");
        UrlEncodedContent content = new UrlEncodedContent(params);


        request = requestFactory.buildPostRequest(gUrl, content);
        request.setParser(new GsonFactory().createJsonObjectParser());

        request.getHeaders().setCookie(cookie);

        final String[] res = {null};

        //Paradoxically MFC return a 302 found status to the connection request success
        request.setUnsuccessfulResponseHandler(new HttpUnsuccessfulResponseHandler() {
            @Override
            public boolean handleResponse(HttpRequest httpRequest, HttpResponse httpResponse, boolean b) throws IOException {
                res[0] = httpResponse.getHeaders().get("Set-Cookie").toString();
                return false;
            }
        });

        HttpResponse response = request.execute();
        StatusAnswer answer = response.parseAs(getResultType());

        return answer;
    }

    @DebugLog
    public String createCacheKey() {
        return "setting " + mItem.getData().getId() + "cat" + my_category_id;
    }
}
