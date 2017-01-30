package com.jtb.baodin;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);

        textView = (TextView) findViewById(R.id.textView);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_browse) {
                    textView.setText("browse");
                }
                else if (tabId == R.id.tab_favorites) {
                    textView.setText("favorites");
                }
                else if (tabId == R.id.tab_appointments) {
                    textView.setText("appointments");
                }
                else if (tabId == R.id.tab_userprofile) {
                    textView.setText("user profile");
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_browse) {
                    textView.setText("browse reselected");
                }
                else if (tabId == R.id.tab_favorites) {
                    textView.setText("favorites reselected");
                }
                else if (tabId == R.id.tab_appointments) {
                    textView.setText("appointments reselected");
                }
                else if (tabId == R.id.tab_userprofile) {
                    textView.setText("user profile reselected");
                }
            }
        });
    }
}
