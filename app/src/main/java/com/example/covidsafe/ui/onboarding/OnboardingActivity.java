package com.example.covidsafe.ui.onboarding;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.covidsafe.R;
import com.example.covidsafe.ui.MainActivity;
import com.example.covidsafe.ui.PermissionLogic;
import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.utils.Utils;


public class OnboardingActivity extends AppCompatActivity {
    private Context mContext;

    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        boolean b = prefs.getBoolean(getString(R.string.onboard_enabled_pkey),true);
        Log.e("onboarding","should start onboarding? "+b);
        if (b) {
            setContentView(R.layout.activity_onboarding);
        }
        else {
            startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionLogic.permissionLogic(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.e("aa","onactivityresult "+requestCode+","+resultCode);
        //bluetooth is now turned on

        Switch bleSwitch = (Switch) findViewById(R.id.bleSwitch);
        if (requestCode == 0 && resultCode == 0) {
            if (bleSwitch != null) {
                bleSwitch.setChecked(false);
                Constants.BLUETOOTH_ENABLED = false;
                SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
                editor.putBoolean(getString(R.string.ble_enabled_pkey), false);
                editor.commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("state","onboarding onresume");

        if (Constants.CurrentFragment != null) {
            Log.e("state","mainactivity - initview "+Constants.CurrentFragment.toString());
            if (Constants.CurrentFragment.toString().toLowerCase().contains("start") ||
                Constants.CurrentFragment.toString().toLowerCase().contains("permission")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_onboarding, Constants.CurrentFragment).commit();
            }
        }
        else {
            SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
            boolean b = prefs.getBoolean(getString(R.string.onboard_enabled_pkey), true);
            Log.e("onboarding", "should start onboarding? " + b);
            if (b) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_onboarding, new StartFragment()).commit();
            }
        }
    }
}
