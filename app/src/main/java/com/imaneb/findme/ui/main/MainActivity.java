package com.imaneb.findme.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.internal.$Gson$Preconditions;
import com.imaneb.findme.MapsActivity;
import com.imaneb.findme.R;
import com.imaneb.findme.SettingsActivity;
import com.imaneb.findme.ui.account.AccountActivity;
import com.imaneb.findme.ui.main.connections.ConnectionsPageAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intToolbar();
        intView();
        initBottomNavigation();


    }

    private void intView() {
        viewPager2 = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager2.setAdapter(new ConnectionsPageAdapter(this));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Chats");
                        tab.setIcon(R.drawable.ic_chat);
                        break;
                    case 1:
                        tab.setText("Requests");
                        tab.setIcon(R.drawable.ic_notifications);
                        BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorBadge));
                        badgeDrawable.setVisible(true);
                        badgeDrawable.setNumber(1);
                        break;
                    case 2:
                        tab.setText("Users");
                        tab.setIcon(R.drawable.ic_people);
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
    }

    private void intToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTextAppearance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find ME");
        toolbar.setNavigationIcon(R.drawable.location);
    }
    private  void initBottomNavigation(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.page_home) {
                    moveToMain();
                }
                if (id == R.id.page_location) {
                    moveToMap();
                }
                if (id == R.id.page_account) {
                    moveToAccountSetting();
                }
                if (id == R.id.page_settings) {
                    moveToSettings();
                }
                return false;

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            moveToAccountSetting();
        }
        if (id == R.id.action_locations) {
            moveToMap();
        }
        return super.onOptionsItemSelected(item);
    }

    private void moveToMap() {
        startActivity( new Intent(MainActivity.this, MapsActivity.class));
    }
    private void moveToMain() {
        startActivity( new Intent(MainActivity.this, MainActivity.class));
    }
    private void moveToSettings() {
        startActivity( new Intent(MainActivity.this, AccountActivity.class));
    }
    private void moveToAccountSetting() {
        startActivity( new Intent(MainActivity.this, AccountActivity.class));
    }
}
