package com.jtb.baodin;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity implements UserProfileFragment.OnUserProfileFragmentInteraction {

    private UserProfileFragment mBrowseFragment;
    private UserProfileFragment mFavoritesFragment;
    private UserProfileFragment mAppointmentFragment;
    private UserProfileFragment mUserProfileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBrowseFragment = UserProfileFragment.newInstance(getString(R.string.bottombar_tab_browse), getString(R.string.bottombar_tab_browse));
        mFavoritesFragment = UserProfileFragment.newInstance(getString(R.string.bottombar_tab_favorites), getString(R.string.bottombar_tab_favorites));
        mAppointmentFragment = UserProfileFragment.newInstance(getString(R.string.bottombar_tab_appointments), getString(R.string.bottombar_tab_appointments));
        mUserProfileFragment = UserProfileFragment.newInstance(getString(R.string.bottombar_tab_userprofile), getString(R.string.bottombar_tab_userprofile));

 //       Intent intent = new Intent(this, LoginActivity.class);
 //       startActivity(intent);

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
}
