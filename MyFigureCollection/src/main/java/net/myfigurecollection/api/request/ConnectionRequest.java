package net.myfigurecollection.api.request;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.UrlEncodedContent;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import net.myfigurecollection.api.CollectionMode;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Climbatize on 26/11/13.
 */
public class ConnectionRequest extends GoogleHttpClientSpiceRequest<String> {

    String mUsername, mPassword;
    public final static String URL = "https://secure.myfigurecollection.net/sign/in/";

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
        params.put("location", "http%3A%2F%2Fmyfigurecollection.net%2F");

        UrlEncodedContent content = new UrlEncodedContent(params);

        request = requestFactory.buildPostRequest(gUrl,content);
        HttpResponse response = request.execute();


        return response.getHeaders().getCookie() ;
    }
}
