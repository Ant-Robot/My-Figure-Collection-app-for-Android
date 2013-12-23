package net.myfigurecollection.api.request;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.http.UrlEncodedContent;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import hugo.weaving.DebugLog;

/**
 * Created by Climbatize on 26/11/13.
 */
public class ConnectionRequest extends GoogleHttpClientSpiceRequest<String> {

    public final static String URL = "https://secure.myfigurecollection.net/sign/in/";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_SET_COOKIE = "set_cookie";
    public static final String PARAM_COMMIT = "commit";
    public static final String PARAM_LOCATION = "location";
    public static final String VALUE_LOCATION = "http://myfigurecollection.net/";
    public static final String VALUE_COMMIT = "signin";
    public static final String VALUE_SET_COOKIE = "1";
    public static final String HEADER_SET_COOKIE = "Set-Cookie";

    String mUsername, mPassword;

    public ConnectionRequest(String username, String password) {
        super(String.class);
        this.mUsername = username;
        this.mPassword = password;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {


        HttpRequest request = null;
        HttpRequestFactory requestFactory = getHttpRequestFactory();
        GenericUrl gUrl = new GenericUrl(URL);


        Map<String, String> params = new TreeMap<String, String>();
        params.put(PARAM_USERNAME, mUsername);
        params.put(PARAM_PASSWORD, mPassword);
        params.put(PARAM_SET_COOKIE, VALUE_SET_COOKIE);
        params.put(PARAM_COMMIT, VALUE_COMMIT);
        params.put(PARAM_LOCATION, VALUE_LOCATION);

        UrlEncodedContent content = new UrlEncodedContent(params);


        request = requestFactory.buildPostRequest(gUrl, content);


        final String[] res = {null};

        //Paradoxically MFC return a 302 found status to the connection request success
        request.setUnsuccessfulResponseHandler(new HttpUnsuccessfulResponseHandler() {
            @Override
            public boolean handleResponse(HttpRequest httpRequest, HttpResponse httpResponse, boolean b) throws IOException {

                res[0] = httpResponse.getHeaders().get(HEADER_SET_COOKIE).toString();

                return false;
            }
        });

        HttpResponse response = request.execute();


        return res[0];
    }

    @DebugLog
    public String createCacheKey() {
        return "connecting " + mUsername;
    }
}
