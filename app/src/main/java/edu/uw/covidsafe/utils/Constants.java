package edu.uw.covidsafe.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.covidsafe.R;
import com.google.common.collect.Lists;

import edu.uw.covidsafe.contact_trace.GpsHistoryRecyclerViewAdapter2;
import edu.uw.covidsafe.contact_trace.HumanRecord;
import edu.uw.covidsafe.contact_trace.NonSwipeableViewPager;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.ui.MainFragment;
import edu.uw.covidsafe.ui.health.TipRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.HistoryRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.NotifRecyclerViewAdapter;
import edu.uw.covidsafe.symptoms.SymptomTrackerFragment;
import edu.uw.covidsafe.ui.health.DiagnosisFragment;

import java.security.KeyStore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import javax.crypto.SecretKey;

import edu.uw.covidsafe.ui.faq.FaqFragment;
import edu.uw.covidsafe.ui.health.HealthFragment;
import edu.uw.covidsafe.ui.settings.SettingsFragment;
import edu.uw.covidsafe.ui.contact_log.ContactLogFragment;
import edu.uw.covidsafe.contact_trace.ContactTraceFragment;

import edu.uw.covidsafe.ui.onboarding.PermissionFragment;
import edu.uw.covidsafe.ui.onboarding.PagerFragment;

public class Constants {

    public enum MessageType {
        Exposure,NarrowCast
    }
    public static Menu menu;

    public static boolean UI_AUTH = false;
    public static boolean WRITE_TO_DISK = false;
    public static boolean DEBUG = true;
    public static boolean PUBLIC_DEMO = true;
    public static boolean NARROWCAST_ENABLE = true;
    public static boolean USE_LAST_QUERY_TIME = true;

    public enum BleDatabaseOps {
        Insert,ViewAll,DeleteAll
    }

    public enum HumanDatabaseOps {
        Insert,Delete
    }

    public enum NotifDatabaseOps {
        Insert,ViewAll,DeleteAll
    }

    public enum SymptomsDatabaseOps {
        Insert,ViewAll,DeleteAll,Delete
    }

    public enum GpsDatabaseOps {
        Insert,ViewAll,DeleteAll
    }

    public enum UUIDDatabaseOps {
        BatchInsert, Insert,ViewAll,DeleteAll
    }

    public static int ContactPageNumber;
    public static String entryPoint = "";
    public static List<SymptomsRecord> symptomRecords;
    public static boolean EnableUUIDGeneration = true;
    public static TipRecyclerViewAdapter MainTipAdapter;
    public static TipRecyclerViewAdapter DiagnosisTipAdapter;
    public static NotifRecyclerViewAdapter NotificationAdapter;
    public static HistoryRecyclerViewAdapter HistoryAdapter;
    public static boolean PullServiceRunning = false;
    public static boolean LoggingServiceRunning = false;
    public static boolean SuppressSwitchStateCheck = false;
    public static int QuarantineLengthInDays = 14;
    public static int MaxPayloadSize = 10000;
    public static int rssiCutoff = -82;
    public static int MaximumGpsPrecision = 4;
    public static int MinimumGpsPrecision = 0;
    public static int SPLASH_DISPLAY_LENGTH = 1000;
    public static String AnalyticsSecret = "4cd15ae0-9294-40ba-a8b5-a8d77b76783b";
    public static int BluetoothScanIntervalInSecondsDebug = 10;
    public static int BluetoothScanIntervalInMinutes = 5;
    public static int BluetoothScanPeriodInSeconds = 10;
    public static int PullFromServerIntervalInMinutes = 60;
    public static int PullFromServerIntervalInMilliseconds = PullFromServerIntervalInMinutes*60*1000;
    public static boolean PullFromServerTaskRunning = false;
    public static int LogPurgerIntervalInDays = 1;
    public static int UUIDGenerationIntervalInMinutes = 15;
    public static int UUIDGenerationIntervalInSecondsDebug = 10;
    public static int UUIDGenerationIntervalInSeconds = UUIDGenerationIntervalInMinutes*60;
    public static int CDCExposureTimeInMinutes = 10;
    public static double CDCExposureTimeInMinutesDebug = 0.5;
    public static int TimestampDeviationInMilliseconds = 10*1000;
    public static int InfectionWindowIntervalDeviationInMilliseconds = 60*1000;
    public static UUID GATT_SERVICE_UUID = UUID.fromString("8cf0282e-d80f-4eb7-a197-e3e0f965848d");
    public static UUID CHARACTERISTIC_UUID = UUID.fromString("d945590b-5b09-4144-ace7-4063f95bd0bb");
    public static UUID BEACON_SERVICE_UUID = UUID.fromString("0000D028-0000-1000-8000-00805F9B34FB");
    public static UUID contactUUID = null;

    //GPS_TIME_INTERVAL and GPS_LOCATION_INTERVAL used to control frequency of location updates
    //to optimize for power, note that GPS_TIME_INTERVAL is the primary method by which
    // power is conserverd.
    public static final int GPS_TIME_INTERVAL_IN_MINUTES = 10;
    public static final int GPS_TIME_INTERVAL_IN_MILLISECONDS = 1000*60*GPS_TIME_INTERVAL_IN_MINUTES;

    public static final int GPS_TIME_INTERVAL_IN_SECONDS_DEBUG = 1;
    public static final int GPS_TIME_INTERVAL_IN_MILLISECONDS_DEBUG = 1000*GPS_TIME_INTERVAL_IN_SECONDS_DEBUG;

    // our maximum GPS precision corresponds to ~7km
    // our spatial sampling rate should be 2x less than that
    public static final float GPS_LOCATION_INTERVAL_IN_METERS = 3500;
    public static final float GPS_LOCATION_INTERVAL_IN_METERS_DEBUG = 1;

    public static boolean NOTIFS_ENABLED = false;
    public static boolean GPS_ENABLED = false;
    public static boolean BLUETOOTH_ENABLED = false;
    public static boolean LOG_TO_DISK = false;

    public static Switch gpsSwitch;
    public static Switch bleSwitch;
    public static TextView bleDesc;
    public static Switch notifSwitch;

    public static ViewPager contactViewPager;
    public static SecretKey secretKey;
    public static KeyStore keyStore;
    public static int IV_LEN = 12;
    public static int GCM_TLEN = 128;
    public static String CharSet = "UTF-8";
    public static String AES_SETTINGS = "AES/GCM/NoPadding";
    public static String RSA_SETTINGS = "RSA/ECB/PKCS1Padding";
    public static String KEY_PROVIDER = "AndroidKeyStore";
    public static String KEY_ALIAS = "mykeys";
    public static BluetoothGattServer gattServer;
    public static BluetoothAdapter blueAdapter;
    public static int statusSubmitted = -1;
    public static ScheduledFuture uuidGeneartionTask;
    public static ScheduledFuture bluetoothScanTask;
    public static Timer pullFromServerTaskTimer;
    public static ScheduledFuture logPurgerTask;
    public static boolean startingToTrack = false;
    public static String SHARED_PREFENCE_NAME = "preferences";
    public static String NOTIFICATION_CHANNEL = "channel";
    public static Fragment MainFragment;
    public static Fragment HealthFragmentState;
    public static Fragment MainFragmentState;
    public static Fragment HealthFragment;
    public static Fragment SymptomTrackerFragment;
    public static Fragment DiagnosisFragment;
    public static Fragment SettingsFragment;
    public static Fragment FaqFragment;
    public static Fragment ContactLogFragment;
    public static Fragment CurrentFragment;
    public static Fragment PermissionsFragment;
    public static Fragment PagerFragment;
    public static Fragment ContactTraceFragment;
    public static String notifDirName = "notif";
    public static String gpsDirName = "gps";
    public static String bleDirName = "ble";
    public static String symptomsDirName = "symptoms";
    public static String uuidDirName = "uuid";
    public static String lastSentName = "lastsent";
    public static boolean tracking = false;
    public static int NumFilesToDisplay = 14;
    public static int SubmitThresh = 1;
    public static int DefaultInfectionWindowInDays = 14;
    public static int DefaultInfectionWindowInDaysDebug = 1;
    public static int DefaultDaysOfLogsToKeep = DefaultInfectionWindowInDays;
    public static int DefaultDaysOfLogsToKeepDebug = DefaultInfectionWindowInDaysDebug;
    public static LocationManager mLocationManager = null;
    public static HashSet<String> scannedUUIDs;
    public static HashMap<String,Integer> scannedUUIDsRSSIs;
    public static HashMap<String,Long> scannedUUIDsTimes;
    public static HashSet<String> writtenUUIDs;
    public static int pageNumber = -1;
    public static ViewPager healthViewPager;
    public static Calendar contactLogMonthCalendar = Calendar.getInstance();
    public static Calendar symptomTrackerMonthCalendar = Calendar.getInstance();

    public static GpsHistoryRecyclerViewAdapter2 contactGpsAdapter;
    public static List<HumanRecord> changedContactHumanRecords;
    public static List<SymptomsRecord> changedContactSympRecords;
    public static List<GpsRecord> changedContactGpsRecords;

    public static List<String> symptoms = Lists.newArrayList(
        "Fever",
        "Abdominal pain",
        "Chills",
        "Cough",
        "Diarrhea",
        "Difficulty breathing (not severe)",
        "Headache",
        "Sore throat",
        "Vomiting"
    );
    public static List<String> symptomDesc = Lists.newArrayList(
        "A high temperature of over 100°F - you feel hot to touch on your chest or back.",
        "Pain from inside the abdomen or the outer muscle wall, ranging from mild and temporary to severe.",
        "The feeling of being cold, though not necessarily in a cold environment, often accompanied by shivering or shaking.",
        "A sudden, forceful hacking sound to release air and clear an irritation in the throat or airway.",
        "Loose, watery bowel movements that may occur frequently and with a sense of urgency.",
        "Shortness of breath, or dyspnea, is an uncomfortable condition that makes it difficult to fully get air into your lungs.",
        "A painful sensation in any part of the head, ranging from sharp to dull, that may occur with other symptoms.",
        "Pain or irritation in the throat that can occur with or without swallowing, often accompanies infections.",
        "Forcefully expelling the stomach's contents out of the mouth."
    );

    public static String[] gpsPermissions= {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public static String[] gpsPermissionsLite= {
//            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public static String[] blePermissions= {
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH,
    };
    public static String[] miscPermissions= {
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.INTERNET
    };

    public static void init(Activity av) {
        Log.e("logme","constants init");
        MainFragment = new MainFragment();
        MainFragmentState = MainFragment;
        SettingsFragment = new SettingsFragment();

        ContactLogFragment = new ContactLogFragment();

        DiagnosisFragment = new DiagnosisFragment();
        HealthFragment = new HealthFragment();

        FaqFragment = new FaqFragment();

        PermissionsFragment = new PermissionFragment();
        PagerFragment = new PagerFragment();

        SymptomTrackerFragment = new SymptomTrackerFragment();
        HealthFragmentState = SymptomTrackerFragment;

        ContactTraceFragment = new ContactTraceFragment();

        if (!DEBUG) {
            LOG_TO_DISK = false;
        }
        else {
            LOG_TO_DISK = true;
        }

        SharedPreferences prefs = av.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        Constants.BLUETOOTH_ENABLED = prefs.getBoolean(av.getString(R.string.ble_enabled_pkey), Constants.BLUETOOTH_ENABLED);
        Constants.GPS_ENABLED = prefs.getBoolean(av.getString(R.string.gps_enabled_pkey), Constants.GPS_ENABLED);
        Constants.NOTIFS_ENABLED = prefs.getBoolean(av.getString(R.string.notifs_enabled_pkey), Constants.NOTIFS_ENABLED);
    }
}
