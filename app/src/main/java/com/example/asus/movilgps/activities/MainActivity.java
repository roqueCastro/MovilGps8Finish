package com.example.asus.movilgps.activities;

import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.asus.movilgps.R;
import com.example.asus.movilgps.fragments.LoginFragment;
import com.example.asus.movilgps.fragments.MapFragment;
import com.example.asus.movilgps.fragments.WelcomeFragment;

public class MainActivity extends AppCompatActivity {

    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            currentFragment = new LoginFragment();
            changeFragment(currentFragment);
        }
    }


    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }

}
