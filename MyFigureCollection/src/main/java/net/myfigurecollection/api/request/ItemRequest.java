package net.myfigurecollection.api.request;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.http.UrlEncodedContent;
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
    private static final String MFC_POST_URL = "http://myfigurecollection.net/items.php?mode=Android&iid=";
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

    public ItemRequest(Item item, Context cntxt) {
        super(StatusAnswer.class);
        this.mItem = item;
    }

    public void postData(String shop, String wishability, String status, String prevStatus, String number, String score, String formattedDate, String price) {
        this.mPrevstat = prevStatus;
        this.mStatus = status;
        this.mNum = number;
        this.mScore = score;
        this.mDate = formattedDate;
        this.mShop = shop;
        this.mPaid = price;
        this.mWishability = wishability;
    }

    /**
     * @param wishability How much do you want the figure (0 to 5)
     */
    public void wish(String wishability) {
        postData("", wishability, CAT_WISHED, this.mItem.getStatus().toString(), "1", "-1", getFormattedDate(new Date()), "");
    }

    /**
     * @param shop The shop you bought this figure
     * @param paid The price you paid
     */
    public void order(String shop, String paid, String num) {
        postData(shop, "", CAT_ORDERED, this.mItem.getStatus().toString(), num, "-1", getFormattedDate(new Date()), paid);
    }

    /**
     * @param id The figure id
     */
    public void remove(String id, String num) {
        postData("", "", CAT_REMOVED, this.mItem.getStatus().toString(), num, "-1", getFormattedDate(new Date()), "");
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

        HttpRequest request = null;
        HttpRequestFactory requestFactory = getHttpRequestFactory();
        GenericUrl gUrl = new GenericUrl(MFC_POST_URL);
        Map<String, String> params = new TreeMap<String, String>();
        params.put(PARAM_COMMIT, "collect");
        params.put(PARAM_EXIT, "1");
        params.put(PARAM_STATUS, mStatus);
        params.put(PARAM_NUM, mNum);
        params.put(PARAM_DATE, mDate);
        params.put(PARAM_SCORE, mScore);
        params.put(PARAM_CAT, my_category_id);
        params.put(PARAM_WISHABILITY, mWishability);
        params.put(PARAM_PREVIOUS_STATUS, mPrevstat);
        params.put(PARAM_SHOP, mShop);
        params.put(PARAM_VALUE, mPaid);
        params.put(PARAM_BUY_DATE, paid_date);
        params.put(PARAM_SHIP_DATE, shipping_date);
        UrlEncodedContent content = new UrlEncodedContent(params);


        request = requestFactory.buildPostRequest(gUrl, content);
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
