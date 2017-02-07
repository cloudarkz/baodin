package com.jtb.baodin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.jtb.utilities.DownloadCallback;
import com.jtb.utilities.NetworkController;

import org.json.JSONObject;

//Google Cloud Console:
//Username: jtb.dev.2017@gmail.com
//Password: jtbdev2017

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, DownloadCallback<String> {
    private static final String TAG = "LoginActivity";
    public static final String LOGIN_SUCCESS = "com.jtb.baodin.LOGIN_SUCCESS";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

    // Keep a reference to the NetworkController, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkController mNetworkController;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mNetworkController = NetworkController.getInstance(/*getSupportFragmentManager()*/this, getString(R.string.server_url));

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id_debug))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
//            GoogleSignInAccount acct = result.getSignInAccount();
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
//            updateUI(true);
            loginSuccess();
        } else {
            // Signed out, show unauthenticated UI.
            mStatusTextView.setText(getString(R.string.signed_in_err));
            updateUI(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            findViewById(R.id.status).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.login_classic).setVisibility(View.GONE);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.login_classic).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.status).setVisibility(View.GONE);
        }
    }

    /** Called when the user clicks the Login button */
    public void authenticateLogin(View view){
        EditText emailText = (EditText) findViewById(R.id.login_email);
        String email = emailText.getText().toString();

        EditText passwordText = (EditText) findViewById(R.id.login_password);
        String password = passwordText.getText().toString();

        try{
            JSONObject req = new JSONObject();

            req.put("cmd", "authenticate");
            req.put("email", email);
            req.put("password", password);

            startDownload(req);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void loginSuccess(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LOGIN_SUCCESS, true);
        startActivity(intent);
    }

    /** DownloadCallback Interface implementation START */
    private void startDownload(JSONObject req) {
        if (!mDownloading && mNetworkController != null) {
            // Execute the async download.
            mNetworkController.startDownload(req);
            mDownloading = true;
        }
    }

    @Override
    public void updateFromDownload(String result) {
        // Update your UI here based on result of download.

        try{
            Log.d(TAG, "Database Result: " + result);
            JSONObject jObject = new JSONObject(result);

            if(jObject.getInt("response") == 0){
                Log.d(TAG, "Login Successful for accountID: " + jObject.getString("data"));
                SharedPreferences settings = getSharedPreferences(MainActivity.ACCOUNTS_DETAILS, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("accountID", jObject.getString("data"));
                editor.commit();
                loginSuccess();
            }
            else{
                Log.d(TAG, "Server error: " + jObject.getString("data"));
                TextView errorText = (TextView) findViewById(R.id.login_error);
                errorText.setText(jObject.getString("data"));
            }


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case DownloadCallback.Progress.ERROR:

                break;
            case DownloadCallback.Progress.CONNECT_SUCCESS:

                break;
            case DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS:

                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:

                break;
            case DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS:

                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkController != null) {
            mNetworkController.cancelDownload();
        }
    }
    /** DownloadCallback Interface implementation END */
}
