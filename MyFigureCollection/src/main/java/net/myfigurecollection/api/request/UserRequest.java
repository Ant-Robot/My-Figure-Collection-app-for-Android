package net.myfigurecollection.api.request;

import android.net.Uri;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import net.myfigurecollection.api.UserMode;


/**
 * Returned items are limited to 20.
 * 
 * Created by Climbatize on 18/11/13.
 */
public class UserRequest extends GoogleHttpClientSpiceRequest<UserMode> {

    public final static String REQUEST_TYPE = "json";
    public final static String MODE = "user";
    public final static String URL_TEMPLATE = "http://myfigurecollection.net/api.php?mode=%s&username=%s&type=%s";
    private String mUser;

    public UserRequest(String user) {
        super(UserMode.class);
        this.mUser = user;
    }

    //@Override
    public UserMode loadDataFromNetwork() throws Exception {

        // With Uri.Builder class we can build our url is a safe manner
        Uri.Builder uriBuilder = Uri.parse(String.format(URL_TEMPLATE, MODE, mUser, REQUEST_TYPE)).buildUpon();

        String url = uriBuilder.build().toString();


        Log.d("URL", url);


        HttpRequest request = null;
        HttpRequestFactory requestFactory = getHttpRequestFactory();
        GenericUrl gUrl = new GenericUrl(url);
        request = requestFactory.buildGetRequest(gUrl);
        request.setParser(new GsonFactory().createJsonObjectParser());
        HttpResponse response = request.execute();

        UserMode finalObject = response.parseAs(getResultType());

        return finalObject;
    }

    /**
     * This method generates a unique cache key for this request. In this case our cache key depends just on the
     * keyword.
     *
     * @return
     */
    public String createCacheKey() {
        return MODE + "." + mUser;
    }
}
