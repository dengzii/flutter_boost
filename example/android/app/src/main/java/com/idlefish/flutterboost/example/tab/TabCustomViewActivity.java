package com.idlefish.flutterboost.example.tab;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.idlefish.flutterboost.FlutterBoost;
import com.idlefish.flutterboost.containers.FlutterBoostView;
import com.idlefish.flutterboost.example.R;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.android.RenderMode;
import io.flutter.embedding.android.TransparencyMode;

public class TabCustomViewActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, FlutterBoostView.Callback {
    SparseArray<FlutterBoostView> mTabs = new SparseArray<>();
    TabView mTabView;
    private int lastId = -1;

    private  FlutterBoostView createFlutterBoostView(String url) {
        HashMap<String, String> params = new HashMap<>();
        params.put("url", url);
        return FlutterBoostView.withCachedEngine(FlutterBoost.ENGINE_ID)
                .transparencyMode(TransparencyMode.transparent)
                .renderMode(RenderMode.texture)
                .url(url)
                .params(params)
                .build(this, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_custom_view);

        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        FrameLayout container = findViewById(R.id.container);

        mTabs.put(R.id.navigation_flutter1, createFlutterBoostView("tab_flutter1"));
        mTabs.put(R.id.navigation_flutter2, createFlutterBoostView("tab_flutter2"));
        mTabView = new TabView(this);

        container.addView(mTabView, -1, -1);
        for (int i = 0; i < mTabs.size(); i++) {
            FlutterBoostView tabContainer = mTabs.valueAt(i);
            container.addView(tabContainer, -1, -1);
        }

        lastId = R.id.navigation_flutter1;
        bottomNavigation.setSelectedItemId(lastId);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (lastId == R.id.navigation_native) {
            mTabView.onPause();
        } else {
            mTabs.get(lastId).onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (lastId == R.id.navigation_native) {
            mTabView.onResume();
        } else {
            mTabs.get(lastId).onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < mTabs.size(); i++) {
            FlutterBoostView tabContainer = mTabs.valueAt(i);
            tabContainer.onDestroy();
        }
        mTabView.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        android.util.Log.e("xlog", "#onNavigationItemSelected: id=" + id + ", lastId=" + lastId);
        switch (id) {
            case R.id.navigation_flutter1:
            case R.id.navigation_flutter2: {
                if (lastId == R.id.navigation_native) {
                    mTabView.setVisibility(View.GONE);
                } else {
                    mTabs.get(lastId).setVisibility(View.GONE);
                }

                FlutterBoostView selectedTab = mTabs.get(id);
                selectedTab.setVisibility(View.VISIBLE);

                android.util.Log.e("xlog", "#onNavigationItemSelected: selectedTab=" + selectedTab);
                break;
            }
            case R.id.navigation_native:{
                mTabView.setVisibility(View.VISIBLE);
                if (lastId != R.id.navigation_native) {
                    mTabs.get(lastId).setVisibility(View.GONE);
                }
                android.util.Log.e("xlog", "#onNavigationItemSelected: selectedTab=" + mTabView);
                break;
            }
        }
        lastId = id;
        return true;
    }

    @Override
    public void onBackPressed() {
        if (lastId == R.id.navigation_native) {
            finish();
        } else {
            FlutterBoostView currentTab = mTabs.get(lastId);
            currentTab.onBackPressed();
        }
    }

    @Override
    public void finishContainer(Map<String, Object> result) {
        android.util.Log.e("xlog", "#finishContainer, " + this);
        finish();
    }

    @Override
    public void onFlutterUiDisplayed() {
        android.util.Log.e("xlog", "#onFlutterUiDisplayed, " + this);
    }

    @Override
    public void onFlutterUiNoLongerDisplayed() {
        android.util.Log.e("xlog", "#onFlutterUiNoLongerDisplayed, " + this);
    }
}