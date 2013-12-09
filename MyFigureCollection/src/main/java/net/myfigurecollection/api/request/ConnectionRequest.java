package net.myfigurecollection.api.request;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
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
        params.put("username", mUsername);
        params.put("password", mPassword);
        params.put("set_cookie", "1");
        params.put("commit", "signin");
        params.put("location", "http://myfigurecollection.net/");

        UrlEncodedContent content = new UrlEncodedContent(params);


        request = requestFactory.buildPostRequest(gUrl, content);


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


        return res[0];
    }

    @DebugLog
    public String createCacheKey() {
        return "connecting " + mUsername;
    }
}
