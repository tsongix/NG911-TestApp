package com.android.albert.ng911bluetoothtester;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by Albert on 1/24/2016.
 */
public class TestActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TEST_ACTIVITY = "TestActivity";
    private BeaconManager beaconManager;
    private TextView textRSSI;
    private TextView textMinor;
    private TextView textStatus;
    private TextView textLoc;
    private Button startButton, showtableButton;
    private ArrayList<Integer> rssi;
    private int numBeacons;
    private ArrayList<Identifier> major, minor, uuid;
    private HttpTx httptx;
    Json json;
    long trialTime, tEnd, tDelta;
    RequestQueue queue;
    BluetoothChecker bluetooth;
    Boolean click = false, first = true, finished = false;
    String status, loc, model, mac, testlocID, teventid;
    int jsonlengthvar, experiment, run, tlocid;
    //String url = "http://nead.bramsoft.com/ng911_test/create_test.php";//ip for Get
    boolean created;
    private Chronometer chron;
    String url;
    phoneDB phonedb;
    Data d;
    //For periodic operations during the 10 seconds sniffing
    Runnable myRunnable, myRunnable2;
    Handler h, h2;
    boolean detected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.capturelogov2_2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //Experiment and run
        phonedb = new phoneDB();
        if (phonedb.getExperiment(getApplicationContext()) != 1) {
            experiment = phonedb.getExperiment(getApplicationContext());
        } else {
            experiment = 1;
        }
        if (phonedb.getRun(getApplicationContext()) != 0) {
            run = phonedb.getRun(getApplicationContext());
        } else {
            run = 0;
        }
        //
        //ID of the location
        testlocID = getIntent().getStringExtra("locid");
        tlocid = Integer.parseInt(testlocID);
        Log.i("Test Location ID", " " + tlocid);

        //Bluetooth turn on
        bluetooth = new BluetoothChecker(getApplicationContext());
        bluetooth.enableBluetooth();
        //Manager for bluetooth beacons
        beaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        /*This signifies that the beacon type will be decoded when an advertisement is found with 0x0215 in bytes 2-3, and a three-part identifier will be pulled out of bytes 4-19,
         bytes 20-21 and bytes 22-23, respectively. A signed power calibration value will be pulled out of byte 24, and a data field will be pulled out of byte 25.
          m - matching byte sequence for this beacon type to parse (exactly one required)
          s - ServiceUuid for this beacon type to parse (optional, only for Gatt-based beacons)
          i - identifier (at least one required, multiple allowed)
          p - power calibration field (exactly one required)...*/
        beaconManager.bind(this);
        beaconManager.setBackgroundScanPeriod(250);
        bindViews();
        bindButtons();

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        mac = info.getMacAddress();
        model = Build.MANUFACTURER + "-" + Build.MODEL;

        httptx=new HttpTx();
        d=new Data();

        //Declaration of the volley que for receiving the http response
        queue = Volley.newRequestQueue(this);
        //json lenght in the url
        jsonlengthvar = 0;
        //test start not clicked
        click = false;
        //data set with phone mac and number created
        created = false;
        //url settings
        if (phonedb.getServer(getApplicationContext()) != "") {
            url = phonedb.getServer(getApplicationContext());
        } else {
            url = "http://nead.bramsoft.com/ng911_test/";
        }

        h = new Handler();
        h2 = new Handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                EditText editTextServer = (EditText) findViewById(R.id.editServer);
                //intent.getStringExtra(R.id.editServer);
                //get server url and save in url value
                // Log.i("Settings",editTextServer.getText().toString());
                startActivity(intent);
                return true;
            // For going back to home. Handle back button toolbar
            case android.R.id.home:
                h.removeCallbacks(myRunnable);
                h.removeCallbacks(myRunnable2);
                click=false;
                finish();
                Log.i("TESTACTIVITY","Closed");
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        //setRangeNotifier specifies a class that should be called each time the BeaconService gets ranging data, which is nominally once per second when beacons are detected.
        beaconManager.setRangeNotifier(new CustomRangeNotifier());
        beaconManager.setMonitorNotifier(new CustomMonitorNotifier());

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    private void bindViews() {
        startButton = (Button) findViewById(R.id.startButton);
        showtableButton = (Button) findViewById(R.id.showtableButton);
        textMinor = (TextView) findViewById(R.id.minorTextView);
        textRSSI = (TextView) findViewById(R.id.rssiTextView);
        textStatus = (TextView) findViewById(R.id.statusText);
        textLoc = (TextView) findViewById(R.id.locText);
        status = "Not Completed";
        loc = getIntent().getStringExtra("location");
        Log.i("Test Location", " " + loc);
        textLoc.setText(loc);
        chron = (Chronometer) findViewById(R.id.chronometer);
    }

    private void bindButtons() {
        startButton.setOnClickListener(new StartOnClickListener());
        showtableButton.setOnClickListener(new StartOnClickListener());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private class StartOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //OnClick for checking the las value info through the app.
                case R.id.startButton:
                    if (!created) {
                    //create test data set
                    String urlcreate = "http://smith-system-f.herokuapp.com/IndoorLocationTest/createTestEvent";
                    httptx.CreateTestEvent(urlcreate, getApplicationContext(), run, tlocid);
                    Log.i("TestApp Cellphone", "Model: " + model);
                    Log.i("TestApp Cellphone", "MAC: " + mac);
                    created = true;
                }
                    chron.setBase(SystemClock.elapsedRealtime());
                    chron.start();
                    click = true;
                    first = true;
                    rssi = new ArrayList<Integer>();
                    major = new ArrayList<Identifier>();
                    minor = new ArrayList<Identifier>();
                    uuid = new ArrayList<Identifier>();
                    json = new Json();
                    numBeacons = 0;
                    trialTime = System.currentTimeMillis();

                    final int delay = 600; //Do handler every 0.6 seconds
                    //if (detected) {
                    h.postDelayed(myRunnable=new Runnable() {
                        public void run() {
                            //Update UI with detected identifiers and rssi
                            try {
                                int last = rssi.size()-1;
                                textRSSI.setText(Integer.toString(rssi.get(last)));
                                textStatus.setText(status);
                                textMinor.setText(minor.get(last).toString());
                            } catch (NullPointerException | IndexOutOfBoundsException e) {
                                Log.i("Bluetooth", "Data not seen now...");
                                textMinor.setText("N/A");
                            }
                            h.postDelayed(this, delay);
                        }
                    }, delay);


                    h2.postDelayed(myRunnable2=new Runnable() {
                        public void run() {
                            // if (detected) {
                            if (first) {
                                Toast.makeText(getApplicationContext(), "Test started to capture!", Toast.LENGTH_LONG).show();
                                first = false;
                            }
                            if (finished) {
                                Toast.makeText(getApplicationContext(), "Test finished!", Toast.LENGTH_LONG).show();
                                //create event response value and sent all gathered data
                                String sresnum="";
                                d = Data.getInstance();
                                sresnum = d.getServerResponse();
                                //sresnum = sresnum.split("\\s+")[sresnum.split("\\s+").length-1];
                                // Enzo changes
                                sresnum = sresnum.substring(0,sresnum.indexOf('}'));
                                teventid = sresnum;
                                Log.i("RESPONSE SERVER", "Test event number => " + teventid);
                                //send json
                                json.prepareJson(teventid);
                                try {
                                    Log.i("Before the for ++",json.lenghtJson().toString());
                                    for (int i=0;i<json.lenghtJson();i=i+2){
                                        //send 2 by 2 because if it is too long the last values won't be received
                                        String j=json.readMyJson().substring(1,json.readMyJson().length()-1);
                                        String j1= "["+j.split("\\},")[i]+"}";
                                        try {j=(j.split("\\}")[i+1] != null) ? j1+j.split("\\}")[i+1]+"}]" : "]";}
                                        catch(IndexOutOfBoundsException e){j="["+j.split("\\},")[i]+"]";}
                                        Log.i("After the for ++",j);
                                        httptx.HttpGetRequest(url, getApplicationContext(),j);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                json.clear();
                                finished = false;
                            }
                            tEnd = System.currentTimeMillis();
                            tDelta = tEnd - trialTime;
                            if (Integer.parseInt(chron.getText().toString().split(":")[1]) >= 10 && status.equals("Not Completed")) {
                                chron.stop();
                                finished = true;
                                Log.i("Timer", "10 Seconds threshold time has been surpassed ");
                                status = "Done";
                                numBeacons=0;
                                textStatus.setText(status);
                            }
                            h2.postDelayed(this, delay);
                        }
                    }, delay);
                    if (Integer.parseInt(chron.getText().toString().split(":")[1]) >= 10 && status.equals("Done")) {
                        h.removeCallbacks(myRunnable);
                        h.removeCallbacks(myRunnable2);
                        click=false;
                        finish();
                        Log.i("TESTACTIVITY","Closed");
                        return;
                    }
                    break;
                case R.id.showtableButton:
                    //Intent i = new Intent(TestActivity.this, ActivityInfo.class);
                    //startActivity(i);
                    break;
            }
        }
    }

    private class CustomMonitorNotifier implements MonitorNotifier {
        @Override
        public void didEnterRegion(Region region) {
            Log.i(TEST_ACTIVITY, "I just saw a beacon for the first time!");
            detected = true;
        }

        @Override
        public void didExitRegion(Region region) {
            Log.i(TEST_ACTIVITY, "I no longer see a beacon");
            detected = false;
        }

        @Override
        public void didDetermineStateForRegion(int state, Region region) {
            Log.i(TEST_ACTIVITY, "I have just switched from seeing/not seeing beacons: " + state);
        }
    }

    private class CustomRangeNotifier implements RangeNotifier {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            if (click) {
                if (beacons.size() > 0) {
                    for (Beacon lastBeacon : beacons) {
                        detected = true;
                        //capture Max-1 BEACONS AROUND (10000)
                        numBeacons++;
                        rssi.add(lastBeacon.getRssi());
                        major.add(lastBeacon.getId2());
                        minor.add(lastBeacon.getId3());
                        uuid.add(lastBeacon.getId1());
                        Log.i(TEST_ACTIVITY, "The beacon " + lastBeacon.toString() + " (major: " + lastBeacon.getId2() + ", minor: " + lastBeacon.getId3() + ").");
                        HttpTx httptx = new HttpTx();
                            try {
                                long tEnd = System.currentTimeMillis();
                                long tDelta = tEnd - trialTime;
                                Log.i("Timer", String.valueOf(tDelta / 1000));
                                if ((tDelta / 1000.0) < 10) {//time since last beacon is less than 10 sec keep sending data
                                    //add next val to json (Not needed but we can use it for something)
                                    json.updateMyJsonIndoor(major.get(numBeacons-1).toString(), minor.get(numBeacons-1).toString(), Integer.toString(rssi.get(numBeacons-1)), "0");
                                    Log.i(TEST_ACTIVITY, json.readMyJson());
//                                    try {
//                                        Thread.sleep(100);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
                                    //HTTP to Server
                                    Log.i("json lenght", json.lenghtJson().toString());
                                    //sending in groups of 5

                                } else {
                                    // Log.i("Timer", "10 Seconds threshold time has been surpassed ");
                                    finished = true;
                                    click = false;
                                    rssi.clear();
                                    major.clear();
                                    minor.clear();
                                    uuid.clear();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                }
            }
        }
    }
}
