package net.myfigurecollection.api.request;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import net.myfigurecollection.api.GalleryMode;

import hugo.weaving.DebugLog;

/**
 * Returned items are limited to 20 but you can access full galleries by modifying the page param.
 *
 * Created by Climbatize on 18/11/13.
 */
public class GalleryRequest extends GoogleHttpClientSpiceRequest<GalleryMode> {

    public final static String REQUEST_TYPE = "json";
    public final static String MODE = "gallery";
    public final static String URL_TEMPLATE_USER = "http://myfigurecollection.net/api.php?mode=%s&username=%s&type=%s&page=%s";
    public final static String URL_TEMPLATE_ITEM = "http://myfigurecollection.net/api.php?mode=%s&item=%s&type=%s&page=%s";
    private String mUsername, mPage, mItem;

    public GalleryRequest(String username, String page) {
        super(GalleryMode.class);
        this.mUsername = username;
        this.mPage = page;
    }

    public GalleryRequest(Integer item, String page) {
        super(GalleryMode.class);
        this.mItem = item.toString();
        this.mPage = page;
    }

    //@Override
    public GalleryMode loadDataFromNetwork() throws Exception {

        // With Uri.Builder class we can build our url is a safe manner
        //Uri.Builder uriBuilder = Uri.parse(String.format(URL_TEMPLATE, MODE, mUsername, REQUEST_TYPE, mStatus, mPage, mRoot)).buildUpon();

        // String url = uriBuilder.build().toString();
        String url;
        if (mUsername != null)
            url = String.format(URL_TEMPLATE_USER, MODE, mUsername, REQUEST_TYPE, mPage);
        else
            url = String.format(URL_TEMPLATE_ITEM, MODE, mItem, REQUEST_TYPE, mPage);

        Log.d("URL", url);


        HttpRequest request = null;
        HttpRequestFactory requestFactory = getHttpRequestFactory();
        GenericUrl gUrl = new GenericUrl(url);
        request = requestFactory.buildGetRequest(gUrl);
        request.setParser(new GsonFactory().createJsonObjectParser());
        HttpResponse response = request.execute();

        GalleryMode finalObject = response.parseAs(getResultType());

        return finalObject;
    }

    /**
     * This method generates a unique cache key for this request. In this case our cache key depends just on the
     * keyword.
     *
     * @return
     */
    @DebugLog
    public String createCacheKey() {
        if (mUsername != null)
            return MODE + "." + mUsername + "." + mPage;
        else
            return MODE + "." + mItem + "." + mPage;
    }
}
