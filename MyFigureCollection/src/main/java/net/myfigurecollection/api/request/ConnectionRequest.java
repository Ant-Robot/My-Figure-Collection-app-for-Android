package net.myfigurecollection.api.request;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.MultipartContent;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.gson.GsonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import net.myfigurecollection.api.CollectionMode;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

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


        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", mUsername));
        nvps.add(new BasicNameValuePair("password", mPassword));
        nvps.add(new BasicNameValuePair("set_cookie", "1"));
        nvps.add(new BasicNameValuePair("commit", "signin"));
        nvps.add(new BasicNameValuePair("location", "http%3A%2F%2Fmyfigurecollection.net%2F"));

        UrlEncodedContent content = new UrlEncodedContent();


        request = requestFactory.buildPostRequest(gUrl,content);
        HttpResponse response = request.execute();


        return finalObject ;
    }
}
