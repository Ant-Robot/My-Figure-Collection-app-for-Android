package net.myfigurecollection.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.GsonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.myfigurecollection.R;
import net.myfigurecollection.api.request.ConnectionRequest;


/**
 * Created by Climbatize on 22/11/13.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity implements RequestListener<String> {
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";
    public static final int REQ_SIGN_IN = 0;
    private final int REQ_SIGNUP = 1;
    private final String TAG = "MFC Authenticator";
    protected SpiceManager spiceManager = new SpiceManager(GsonGoogleHttpClientSpiceService.class);
    private AccountManager mAccountManager;
    private String mAuthTokenType;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        mAccountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        if (accountName != null) {
            ((TextView) findViewById(R.id.accountName)).setText(accountName);
        }

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                /*Intent signup = new Intent(getBaseContext(), SignUpActivity.class);
                signup.putExtras(getIntent().getExtras());
                startActivityForResult(signup, REQ_SIGNUP);*/
                String url = "https://secure.myfigurecollection.net/signs.php?mode=up";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void submit() {

        final String userName = ((TextView) findViewById(R.id.accountName)).getText().toString();
        final String userPass = ((TextView) findViewById(R.id.accountPassword)).getText().toString();
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        ConnectionRequest request4 = new ConnectionRequest(userName, userPass);
        spiceManager.execute(request4, request4.createCacheKey(), DurationInMillis.ALWAYS_EXPIRED, this);
   }

    private void finishLogin(Intent intent) {
        Log.d("climbatize", TAG + "> finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d("climbatize", TAG + "> finishLogin > addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            Log.d("climbatize", TAG + "> finishLogin > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestFailure(SpiceException e) {

    }

    @Override
    public void onRequestSuccess(String t) {
        final String userName = ((TextView) findViewById(R.id.accountName)).getText().toString();
        final String userPass = ((TextView) findViewById(R.id.accountPassword)).getText().toString();
        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        String authtoken = t;
        Bundle data = new Bundle();
        try {
            //authtoken = sServerAuthenticate.userSignIn(userName, userPass, mAuthTokenType);

            data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
            data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
            data.putString(PARAM_USER_PASS, userPass);

            Log.d("climbatize", TAG + "> LoginCookie > " + t);

        } catch (Exception e) {
            data.putString(KEY_ERROR_MESSAGE, e.getMessage());
        }
          
        if (authtoken==null)data.putString(KEY_ERROR_MESSAGE, "Wrong login/password");

        final Intent res = new Intent();
        res.putExtras(data);

        if (res.hasExtra(KEY_ERROR_MESSAGE)) {
            Toast.makeText(getBaseContext(), res.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
        } else {
            finishLogin(res);
        }

    }
}
