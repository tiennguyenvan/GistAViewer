package com.jldubz.gistaviewer.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jldubz.gistaviewer.R;
import com.jldubz.gistaviewer.ui.gists.DiscoverGistsFragment;
import com.jldubz.gistaviewer.ui.gists.StarGistsFragment;
import com.jldubz.gistaviewer.ui.gists.YourGistsFragment;
import com.jldubz.gistaviewer.viewmodel.MainViewModel;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

/**
 * Used to host a bottom navigation bar for a DiscoverGistsFragment, StarGistsFragment, YourGistsFragment,
 * and ProfileFragment
 *
 * @author Jon-Luke West
 */
@Keep
public class MainActivity extends AppCompatActivity {

    private static final String KEY_SELECTED_TAB_ID = "com.jldubz.gistaviewer.MainActivity.tab";

    private DiscoverGistsFragment mDiscoverGistsFragment;
    private StarGistsFragment mStarGistsFragment;
    private YourGistsFragment mYourGistsFragment;
    private ProfileFragment mProfileFragment;

    private int mSelectedTabId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the shared view model
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        //Get any saved credentials
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.key_pref_file), MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.key_pref_username), "");
        String token = sharedPreferences.getString(getString(R.string.key_pref_token), "");

        if (!username.isEmpty() && !token.isEmpty()) {
            viewModel.logIn(username, token);
        }

        mSelectedTabId = R.id.nav_gist_discover;

        //Resume from the last tab that was open
        if (savedInstanceState != null) {
            mSelectedTabId = savedInstanceState.getInt(KEY_SELECTED_TAB_ID);
            if (mSelectedTabId <= 0) {
                mSelectedTabId = R.id.nav_gist_discover;
            }
        }

        //Config the bottom nav view
        BottomNavigationView bottomNavigation = findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            //Log.d("Gist", "tab selected " + item.getTitle());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction;
            if (itemId == R.id.nav_gist_discover) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, mDiscoverGistsFragment);
                fragmentTransaction.commit();
                return true;
            }
            if (itemId == R.id.nav_gist_star) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, mStarGistsFragment);
                fragmentTransaction.commit();
                return true;
            }
            if (itemId == R.id.nav_gist_your) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, mYourGistsFragment);
                fragmentTransaction.commit();
                return true;
            }
            if (itemId == R.id.nav_profile) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content, mProfileFragment);
                fragmentTransaction.commit();
                return true;
            }
            return false;
        });

        //Make new instances of the fragments
        mDiscoverGistsFragment = DiscoverGistsFragment.newInstance();
        mStarGistsFragment = StarGistsFragment.newInstance();
        mYourGistsFragment = YourGistsFragment.newInstance();
        mProfileFragment = ProfileFragment.newInstance();

        //Set the currently selected tab
        bottomNavigation.setSelectedItemId(mSelectedTabId);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //Save the currently selected tab for resuming later
        outState.putInt(KEY_SELECTED_TAB_ID, mSelectedTabId);
        super.onSaveInstanceState(outState);
    }
}
