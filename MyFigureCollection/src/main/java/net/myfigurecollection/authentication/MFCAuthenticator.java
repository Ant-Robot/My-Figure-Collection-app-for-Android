package net.myfigurecollection.authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.apache.ApacheHttpTransport;

import net.myfigurecollection.api.request.ConnectionRequest;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static net.myfigurecollection.authentication.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
import static net.myfigurecollection.authentication.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
import static net.myfigurecollection.authentication.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY;
import static net.myfigurecollection.authentication.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY_LABEL;

/**
 * Created with IntelliJ IDEA.
 * User: Udini
 * Date: 19/03/13
 * Time: 18:58
 */
public class MFCAuthenticator extends AbstractAccountAuthenticator {


    private final Context mContext;
    private String TAG = "MFCAuthenticator";

    public MFCAuthenticator(Context context) {
        super(context);

        // I hate you! Google - set mContext as protected!
        this.mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d("climbatize", TAG + "> addAccount");

        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        Log.d("climbatize", TAG + "> getAuthToken");

        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY) && !authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);


        // Lets give another try to authenticate the user
        String authToken = null;
        String password = null;

        if (am != null) {
            password = am.getPassword(account);
        }
        if (password != null) {
            try {
                Log.d("climbatize", TAG + "> re-authenticating with the existing password");


                HttpTransport transport = new ApacheHttpTransport();
                HttpRequestFactory requestFactory = transport.createRequestFactory();
                GenericUrl gUrl = new GenericUrl(ConnectionRequest.URL);


                Map<String, String> params = new TreeMap<String, String>();
                params.put(ConnectionRequest.PARAM_USERNAME, account.name);
                params.put(ConnectionRequest.PARAM_PASSWORD, password);
                params.put(ConnectionRequest.PARAM_SET_COOKIE, ConnectionRequest.VALUE_SET_COOKIE);
                params.put(ConnectionRequest.PARAM_COMMIT, ConnectionRequest.VALUE_COMMIT);
                params.put(ConnectionRequest.PARAM_LOCATION, ConnectionRequest.VALUE_LOCATION);

                UrlEncodedContent content = new UrlEncodedContent(params);


                HttpRequest request = requestFactory.buildPostRequest(gUrl, content);


                final String[] res = {null};

                //Paradoxically MFC return a 302 found status to the connection request success
                request.setUnsuccessfulResponseHandler(new HttpUnsuccessfulResponseHandler() {
                    @Override
                    public boolean handleResponse(HttpRequest httpRequest, HttpResponse httpResponse, boolean b) throws IOException {

                        res[0] = httpResponse.getHeaders().get(ConnectionRequest.HEADER_SET_COOKIE).toString();

                        return false;
                    }
                });

                request.execute();

                authToken = res[0];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_NAME, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
            return AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
        else if (AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
            return AUTHTOKEN_TYPE_READ_ONLY_LABEL;
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
}
