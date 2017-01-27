package com.jtb.baodin;

import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String SELECTED_ITEM = "arg_selected_item";

    private BottomNavigationView mBottomNavigationView;
    private int mSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = mBottomNavigationView.getMenu().findItem(mSelectedItem);
        } else {
            selectedItem = mBottomNavigationView.getMenu().getItem(0);
        }
        selectFragment(selectedItem);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = mBottomNavigationView.getMenu().getItem(0);
        if (mSelectedItem != homeItem.getItemId()) {
            // select home item
            selectFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }

    private void selectFragment(MenuItem item) {
        Fragment fragment = null;
        // init corresponding fragment
        switch (item.getItemId()) {
            case R.id.menu_search:
                fragment = SearchFragment.newInstance(getString(R.string.text_search),
                        getColorFromRes(R.color.color_search));
                break;
            case R.id.menu_favorite:
                fragment = SearchFragment.newInstance(getString(R.string.text_favorite),
                        getColorFromRes(R.color.color_favorite));
                break;
            case R.id.menu_user_profile:
                fragment = SearchFragment.newInstance(getString(R.string.text_user_profile),
                        getColorFromRes(R.color.color_user_profile));
                break;
        }

        // update selected item
        mSelectedItem = item.getItemId();

        updateActionBarText(item.getTitle());

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.main_container, fragment, fragment.getTag());
            ft.commit();
        }
    }

    private void updateActionBarText(CharSequence text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    private int getColorFromRes(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }
}
