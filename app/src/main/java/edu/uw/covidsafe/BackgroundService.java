package edu.uw.covidsafe;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.covidsafe.R;

import edu.uw.covidsafe.ble.BluetoothServerHelper;
import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.seed_uuid.UUIDGeneratorTask;
import edu.uw.covidsafe.comms.PullFromServerTask;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackgroundService extends IntentService {

    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1f;
    Messenger messenger;

    public class LocationListener implements android.location.LocationListener {

        String provider;

        public LocationListener(String provider) {
            this.provider = provider;
        }

        @Override
        public void onLocationChanged(Location location) {
            DateFormat dateFormat = new SimpleDateFormat("hh:mm.ss aa");
            Date dd = new Date();
            Log.e("gps", location.getLatitude()+","+location.getLongitude());

            Utils.sendDataToUI(messenger, "gps",location.getLatitude()+","+location.getLongitude());

            Utils.gpsLogToDatabase(getApplicationContext(), location);
        }

        @Override
        public void onProviderDisabled(String provider) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    public BackgroundService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("logme","handle intent");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        Log.e("ex","bundle status "+(bundle==null));
        if (bundle != null) {
            messenger = (Messenger) bundle.get("messenger");
        }

        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        if (Constants.BLUETOOTH_ENABLED) {
            BluetoothUtils.messenger = messenger;
            this.registerReceiver(BluetoothUtils.mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

            Log.e("ble","spin out task "+(messenger==null));
            BluetoothUtils.startBluetoothScan(getApplicationContext(), messenger);
            BluetoothServerHelper.createServer(getApplicationContext(), messenger);
            Log.e("ble","make beacon");

            //initially start beacon
            BluetoothUtils.mkBeacon();
            Constants.uuidGeneartionTask = exec.scheduleWithFixedDelay(new UUIDGeneratorTask(messenger, getApplicationContext()), 0, Constants.UUIDGenerationIntervalInMinutes, TimeUnit.MINUTES);
        }

        if (Constants.GPS_ENABLED) {
            initializeLocationManager();
            try {
                Log.e("logme", "request");

                Constants.locListeners[0] = new LocationListener(LocationManager.NETWORK_PROVIDER);
                Constants.mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        Constants.locListeners[0]);
                Constants.locListeners[1] = new LocationListener(LocationManager.GPS_PROVIDER);
                Constants.mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                        Constants.locListeners[1]);

            } catch (java.lang.SecurityException ex) {
                Log.e("logme", "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.e("logme", "gps provider does not exist " + ex.getMessage());
            } catch (Exception e) {
                Log.e("logme", e.getMessage());
            }
        }

        Constants.pullFromServerTask = exec.scheduleWithFixedDelay(new PullFromServerTask(messenger,getApplicationContext()), 0, Constants.PullFromServerIntervalInMinutes, TimeUnit.MINUTES);
        Constants.logPurgerTask = exec.scheduleWithFixedDelay(new LogPurgerTask(messenger,getApplicationContext()), 0, Constants.LogPurgerIntervalInDays, TimeUnit.DAYS);

        Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notif_message))
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1,notification);

        //////////////////////////////////////////////////////////////////////////////////////////
//        try {
//            Thread.sleep(5000);
//            createNotificationChannel();
//            Intent fullScreenIntent = new Intent(this, MainActivity.class);
//            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
//                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "123")
//                    .setSmallIcon(R.drawable.ic_launcher_background)
//                    .setContentTitle("CovidSafe Alert")
//                    .setContentText("fullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntentfullScreenPendingIntent")
//                    .setPriority(NotificationCompat.PRIORITY_MAX)
//                    .setCategory(NotificationCompat.CATEGORY_CALL)
//                    .setFullScreenIntent(fullScreenPendingIntent, true)
//                    .setAutoCancel(false);
//            // Add the action button
////                .addAction(R.drawable.ic_launcher_foreground, ctx.getString(R.string.snooze),
////                        snoozePendingIntent);
//
//            Uri notification2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(this, notification2);
//            r.play();
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//
//            // notificationId is a unique int for each notification that you must define
//            notificationManager.notify(13, builder.build());
//        }
//        catch(Exception e) {
//            Log.e("ble",e.getMessage());
//        }
        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ("name");
            String description = ("desc");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //this call is not guaranteed by android system
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(BluetoothUtils.mReceiver);
        }
        catch(Exception e) {
            Log.e("ble","unregister fail");
        }
        Log.e("logme", "service destroyed");
    }

    private void initializeLocationManager() {
        Log.e("logme", "initializeLocationManager");
        if (Constants.mLocationManager == null) {
            Log.e("logme", "initializeLocationManager2");
            Constants.mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}