package com.jtb.baodin;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jtb.baodin.dummy.DummyContent;
import com.jtb.baodin.dummy.DummyContent.DummyItem;

import com.jtb.utilities.DownloadCallback;
import com.jtb.utilities.NetworkFragment;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements DownloadCallback<String>, UserProfileFragment.OnUserProfileFragmentInteraction, BrowseFragment.OnListFragmentInteractionListener {
    public static final String TAG = "MainActivity";

    private BrowseFragment mBrowseFragment;
    private UserProfileFragment mFavoritesFragment;
    private UserProfileFragment mAppointmentFragment;
    private UserProfileFragment mUserProfileFragment;

    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment mNetworkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;

    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBrowseFragment = BrowseFragment.newInstance(1);
        mFavoritesFragment = UserProfileFragment.newInstance(getString(R.string.bottombar_tab_favorites), getString(R.string.bottombar_tab_favorites));
        mAppointmentFragment = UserProfileFragment.newInstance(getString(R.string.bottombar_tab_appointments), getString(R.string.bottombar_tab_appointments));
        mUserProfileFragment = UserProfileFragment.newInstance(getString(R.string.bottombar_tab_userprofile), getString(R.string.bottombar_tab_userprofile));

        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), getString(R.string.server_url));

        Intent mainIntent = getIntent();
        isLogin = mainIntent.getBooleanExtra(LoginActivity.LOGIN_SUCCESS, false);
        Log.d(TAG, "LOGIN STATUS: " + isLogin);

        if(!isLogin){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.contentContainer, mBrowseFragment, mBrowseFragment.getTag());
        fragmentTransaction.commit();

        // BottomBar Listeners for swapping view fragments
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabID) {
                swapFragment(tabID);
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabID) {
            }
        });
    }

    /**
     * Description:
     * Handles the swapping between different view fragments.
     *
     * @param tabID int of the tab selected on the BottomBar
     */
    private void swapFragment(int tabID) {
        Fragment fragment = null;

        switch (tabID) {
            case R.id.tab_browse:
                fragment = mBrowseFragment;
                updateActionbarText(getString(R.string.bottombar_tab_browse));
                break;
            case R.id.tab_favorites:
                fragment = mFavoritesFragment;
                updateActionbarText(getString(R.string.bottombar_tab_favorites));
                break;
            case R.id.tab_appointments:
                fragment = mAppointmentFragment;
                updateActionbarText(getString(R.string.bottombar_tab_appointments));
                break;
            case R.id.tab_userprofile:
                fragment = mUserProfileFragment;
                updateActionbarText(getString(R.string.bottombar_tab_userprofile));
                break;
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.contentContainer, fragment, fragment.getTag());
            fragmentTransaction.commit();
        }
    }

    /**
     * Description:
     * Updates the Actionbar title text when fragments are swapped.
     *
     */
    private void updateActionbarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    /**
     * Description:
     * Callback for user interaction on the User Profile Fragment.
     *
     */
    public void onUserProfileFragmentInteraction(String string) {
        updateActionbarText(string);
    }

    public void onListFragmentInteraction(DummyItem item) {

    }

    private void startDownload(JSONObject req) {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startDownload(req);
            mDownloading = true;
        }
    }

    /** DownloadCallback Interface implementation START */
    @Override
    public void updateFromDownload(String result) {
        // Update your UI here based on result of download.

        try{
            JSONObject jObject = new JSONObject(result);

            if(jObject.getInt("response") == 0)
                updateActionbarText(jObject.getString("data"));
            else
                updateActionbarText("Server error: " + jObject.getString("data"));

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
            case Progress.ERROR:

                break;
            case Progress.CONNECT_SUCCESS:

                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:

                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }
    /** DownloadCallback Interface implementation END */
}
