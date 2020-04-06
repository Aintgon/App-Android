package edu.uw.covidsafe.ui.onboarding;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.settings.PermissionsRecyclerViewAdapter;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class PermissionFragment extends Fragment {

    Button finish;
    Button back;

    View view;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.onboarding_permissions, container, false);
        ((OnboardingActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((OnboardingActivity) getActivity()).getSupportActionBar().show();
        ((OnboardingActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((OnboardingActivity) getActivity()).getSupportActionBar().setTitle("Select your preferences");

        RecyclerView rview3 = view.findViewById(R.id.recyclerViewPerms);
        PermissionsRecyclerViewAdapter adapter3 = new PermissionsRecyclerViewAdapter(getContext(),getActivity(), view);
        rview3.setAdapter(adapter3);
        rview3.setLayoutManager(new LinearLayoutManager(getActivity()));

        finish = (Button) view.findViewById(R.id.skipButton);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        back = (Button) view.findViewById(R.id.nextButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        Button bb = (Button) view.findViewById(R.id.button4);
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
                boolean s1 = prefs.getBoolean(getString(R.string.notifs_enabled_pkey), false);
                boolean s2 = prefs.getBoolean(getString(R.string.gps_enabled_pkey), false);
                boolean s3 = prefs.getBoolean(getString(R.string.ble_enabled_pkey), false);
                Log.e("perms","PERM STATE "+s1+","+s2+","+s3);
            }
        });

        Button bb2 = (Button) view.findViewById(R.id.button5);
        bb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                getActivity().startActivity(intent);
            }
        });

        bb.setVisibility(View.GONE);
        bb2.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("state","permission fragment on resume");
        Constants.CurrentFragment = this;
        Constants.PermissionsFragment = this;

        Log.e("perms","should update switch states? "+Constants.SuppressSwitchStateCheck);
        if (Constants.SuppressSwitchStateCheck) {
            Constants.SuppressSwitchStateCheck = false;
        }
        else {
            Utils.updateSwitchStates(getActivity());
        }
    }
}
