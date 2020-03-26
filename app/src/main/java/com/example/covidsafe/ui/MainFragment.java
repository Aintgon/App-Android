package com.example.covidsafe.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.covidsafe.ble.BleOpsAsyncTask;
import com.example.covidsafe.ble.BluetoothHelper;
import com.example.covidsafe.gps.GpsOpsAsyncTask;
import com.example.covidsafe.utils.ByteUtils;
import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.BackgroundService;
import com.example.covidsafe.R;
import com.example.covidsafe.utils.Utils;

import java.util.UUID;

public class MainFragment extends Fragment {

    Button trackButton;
    Button uploadGpsButton;
    Button uploadBleButton;
    Button rotateButton;
    TextView bleBeaconId;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (Constants.DEBUG) {
//            view = inflater.inflate(R.layout.fragment_main_debug, container, false);
//        }
//        else {
            view = inflater.inflate(R.layout.fragment_main_release, container, false);
//        }

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.main_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Switch bleSwitch = (Switch)getActivity().findViewById(R.id.bleSwitch);
        bleSwitch.setChecked(Constants.BLUETOOTH_ENABLED);
        bleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constants.BLUETOOTH_ENABLED = isChecked;
            }
        });

        Switch gpsSwitch = (Switch)getActivity().findViewById(R.id.gpsSwitch);
        gpsSwitch.setChecked(Constants.GPS_ENABLED);
        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constants.GPS_ENABLED = isChecked;
            }
        });

        Constants.CurrentFragment = this;
        Constants.MainFragment = this;

        bleBeaconId = (TextView)getActivity().findViewById(R.id.textView);

        Utils.gpsResults = (TextView)getActivity().findViewById(R.id.gpsResults);
        Utils.gpsResults.setText("");
        Utils.gpsResults.setMovementMethod(new ScrollingMovementMethod());
        Utils.gpsLines = 0;

        Utils.bleResults = (TextView)getActivity().findViewById(R.id.bleResults);
        Utils.bleResults.setText("");
        Utils.bleResults.setMovementMethod(new ScrollingMovementMethod());
        Utils.bleLines = 0;

        trackButton = (Button)getActivity().findViewById(R.id.trackButton);
        uploadGpsButton = (Button)getActivity().findViewById(R.id.uploadGpsButton);
        uploadBleButton = (Button)getActivity().findViewById(R.id.uploadBleButton);
        rotateButton = (Button)getActivity().findViewById(R.id.rotateButton);
        updateUI();

        Constants.contactUUID = UUID.randomUUID();
        bleBeaconId.setText(Constants.contactUUID.toString());

        uploadGpsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                new GpsDbAsyncTask(getContext(), new GpsDbRecord(System.currentTimeMillis(),42,43, LocationManager.NETWORK_PROVIDER)).execute();
                new GpsOpsAsyncTask(getActivity()).execute();
            }
        });

        uploadBleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                new BleDbAsyncTask(getContext(), new BleDbRecord("1234",System.currentTimeMillis(),false,false)).execute();
                new BleOpsAsyncTask(getActivity()).execute();
            }
        });

        rotateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Constants.contactUUID = UUID.randomUUID();
                bleBeaconId.setText(Constants.contactUUID.toString());

                if (Constants.blueAdapter != null && Constants.blueAdapter.getBluetoothLeAdvertiser() != null) {
                    Constants.blueAdapter.getBluetoothLeAdvertiser().stopAdvertising(BluetoothHelper.callback);
                    AdvertiseSettings settings = new AdvertiseSettings.Builder()
                            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                            .setConnectable(true)
                            .build();

                    AdvertiseData advertiseData = new AdvertiseData.Builder()
                            .setIncludeDeviceName(false)
                            .addServiceUuid(new ParcelUuid(Constants.serviceUUID))
                            .addServiceData(new ParcelUuid(Constants.serviceUUID), ByteUtils.uuid2bytes(Constants.contactUUID))
                            .build();
                    Constants.blueAdapter.getBluetoothLeAdvertiser().startAdvertising(settings, advertiseData, BluetoothHelper.callback);
                }
            }
        });

        trackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!Constants.tracking) {
                    Constants.startingToTrack = true;
                    try {
                        Log.e("logme","start service");

                        BluetoothManager bluetoothManager =
                                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
                        Constants.blueAdapter = bluetoothManager.getAdapter();

                        if (Constants.BLUETOOTH_ENABLED && !Utils.hasBlePermissions(getActivity())) {
                            Log.e("aa","NO BLE PERMS");
                            ActivityCompat.requestPermissions(getActivity(), Constants.blePermissions, 1);
                        }

                        if (Utils.hasBlePermissions(getActivity()) &&
                            Constants.BLUETOOTH_ENABLED && (Constants.blueAdapter == null || !Constants.blueAdapter.isEnabled())) {
                            Log.e("aa","BLE");
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            getActivity().startActivityForResult(enableBtIntent, 0);
                        }

                        if (Constants.GPS_ENABLED && !Utils.hasGpsPermissions(getActivity())) {
                            Log.e("aa","PERMS");
                            ActivityCompat.requestPermissions(getActivity(), Constants.gpsPermissions, 2);
                        }

                        if ((Constants.GPS_ENABLED || Constants.BLUETOOTH_ENABLED) && Utils.permCheck(getActivity())) {
                            Utils.startBackgroundService(getActivity());
                        }

                        if (!Constants.GPS_ENABLED && !Constants.BLUETOOTH_ENABLED) {
                            Utils.mkSnack(getActivity(), view, getString(R.string.prompt_to_enable_error));
                        }
                    } catch (SecurityException e) {
                        Log.e("log", e.getMessage());
                    }
                }
                else {
                    Log.e("logme","stop service");
                    getActivity().stopService(new Intent(getActivity(), BackgroundService.class));
                    if (Constants.uploadTask!=null) {
                        Constants.uploadTask.cancel(true);
                    }
                    Constants.tracking = false;
                }
                updateUI();
            }
        });
    }

    public void updateUI() {
        Log.e("aa","updateui");
        if (Constants.tracking) {
            Log.e("aa","yes");
            trackButton.setText(getString(R.string.stop));
            trackButton.setBackgroundResource(R.drawable.stopbutton);
        }
        else {
            Log.e("aa","no");
            trackButton.setText(getString(R.string.start));
            trackButton.setBackgroundResource(R.drawable.startbutton);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.MainFragment = this;
        Constants.CurrentFragment = this;
    }
}
