package com.android.albert.ng911bluetoothtester;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.RequestQueue;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

/**
 * Created by Albert on 8/29/2016.
 */
public class DetectorActivity extends AppCompatActivity implements BeaconConsumer, AdapterView.OnItemSelectedListener {
    public static int MAX = 15000;
    protected static final String DETECTOR_ACTIVITY  = "DetectorActivity";
    private BeaconManager beaconManager;
    private Button pauseButton;
    HttpTx http;
    BluetoothChecker bluetooth;
    RequestQueue queue;
    boolean detected,click;
    String url = "http://nead.bramsoft.com/";//ip
    Data d;
    ListView listInfo;
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private int numBeacons;
    String[] locid, major, minor;
    private String lastseenmajor, lastseenminor;
    private HashSet deletedmajors,deletedminors;
    Runnable runnableCode;
    private Data valset;
    int i;
    Handler handler;
    boolean stopped;
    Semaphore mutex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.capturelogov2_2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //Bluetooth turn on
        bluetooth = new BluetoothChecker(getApplicationContext());
        bluetooth.enableBluetooth();
        //Manager for bluetooth beacons
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
        bindViews();
        bindButtons();
        //vars initialization
        stopped=false;
        detected=click=false;
        locid = new String[MAX];
        major = new String[MAX];
        minor = new String[MAX];
        deletedmajors = new HashSet();
        deletedminors = new HashSet();
        lastseenmajor = "";
        lastseenminor = "";
        //Transmission
        http=new HttpTx();

        mutex = new Semaphore(1);


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            http.HttpGetRequest_DeploymentInfo(url, getApplicationContext());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


        final Data d=Data.getInstance();
        listItems=d.getReceivedcapsDeployment();
        //independent values major minor
        valset=Data.getInstance();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
        listInfo.setAdapter(adapter);

        // Create the Handler object
        handler = new Handler();
        // Define the code block to be executed
        runnableCode = new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                Log.d("Handler", "Called on main thread");
                if(!listItems.isEmpty()) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
                    listInfo.setAdapter(adapter);
                    if (!lastseenmajor.isEmpty()){
                        for(i=0;i<listItems.size()-1;i++) {
                            if (lastseenmajor.equals(valset.smajor_db.get(i)) && lastseenminor.equals(valset.sminor_db.get(i))) {
                                Log.i(DETECTOR_ACTIVITY, "DEVICE" + listItems.get(i).toString() +" DETECTED AND REMOVED FROM LIST");
                                listItems.remove(i);
                                //remove values valset major minor and loc id in the position i
                                valset.smajor_db.remove(i);
                                valset.sminor_db.remove(i);
                                valset.slocid_db.remove(i);
                                //add deleted major and minor to deleted arrays
                                deletedmajors.add(lastseenmajor);
                                deletedminors.add(lastseenminor);
                                lastseenmajor = "";
                                lastseenminor = "";
                            }
                        }
                    }
                }else{
                Log.i(DETECTOR_ACTIVITY, "Detector finished, all the devices were found");
                    // Removes pending code execution
                    handler.removeCallbacks(runnableCode);
                }
                //operation done, release mutex
                mutex.release();
                // Repeat this the same runnable code block again another 2 seconds
                handler.postDelayed(runnableCode, 500);
            }
        };
        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);
        //SPINNER
        Spinner spinner = (Spinner) findViewById(R.id.floors_spinner);
        spinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.floors_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

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

    private void bindViews() {
        listInfo = (ListView) findViewById(R.id.notdetected_list);
        pauseButton = (Button) findViewById(R.id.updateButton);
    }

    private void bindButtons() {
        pauseButton.setOnClickListener(new StartOnClickListener());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        parent.getItemAtPosition(pos);
        Log.i(DETECTOR_ACTIVITY,"Filter Floor:"+pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class StartOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //OnClick for checking the las value info through the app.
                case R.id.updateButton:
                    //stop
                    if (!stopped) {
                        handler.removeCallbacks(runnableCode);
                        stopped = true;
                        pauseButton.setText("Continue");
                    }else{
                        beaconManager.bind((BeaconConsumer) getApplicationContext());
                        handler.post(runnableCode);
                        pauseButton.setText("Pause");
                        stopped=false;}
                    click = true;
                    major = new String[MAX];
                    minor = new String[MAX];
                    Log.i(DETECTOR_ACTIVITY,"Buttone pressed");
                    final Data d=Data.getInstance();
                    listItems=d.getReceivedcapsDeployment();
                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
                    listInfo.setAdapter(adapter);

            }}}

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
            //beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6"), Identifier.parse("1"), null));
            //above filters for uuid and major =1
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    private class CustomMonitorNotifier implements MonitorNotifier {

        @Override
        public void didEnterRegion(Region region) {
            Log.i(DETECTOR_ACTIVITY, "I just saw a beacon for the first time!");
            detected = true;
        }

        @Override
        public void didExitRegion(Region region) {
            Log.i(DETECTOR_ACTIVITY, "I no longer see a beacon");
            detected = false;
        }

        @Override
        public void didDetermineStateForRegion(int state, Region region) {
            Log.i(DETECTOR_ACTIVITY, "I have just switched from seeing/not seeing beacons: " + state);
        }
    }

    private class CustomRangeNotifier implements RangeNotifier {
        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            if (beacons.size() > 0) {
                for (Beacon beacon : beacons) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Log.i(CALL,"The beacon " + beacon.toString() + " is about " + beacon.getDistance() + " meters away.");
                    major[numBeacons] = beacon.getId2().toString();
                    minor[numBeacons] = beacon.getId3().toString();
                    Log.i(DETECTOR_ACTIVITY, "major: " + beacon.getId2() + ", minor: " + beacon.getId3());
                    //If I already deleted the device from the list I do not store last major and minor values
                    if (!deletedminors.contains(minor[numBeacons]) || deletedminors.contains(minor[numBeacons]) && !deletedmajors.contains(major[numBeacons])) {
                        //set with major minors seen, will compare this with out db info and show those that we didn't see
                        try {
                            //next seen bluetooth device info will wait until the previously seen device is deleted
                            mutex.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lastseenmajor = beacon.getId2().toString();
                        lastseenminor = beacon.getId3().toString();
                        numBeacons++;
                        Log.i(DETECTOR_ACTIVITY, "To delete...major: " + beacon.getId2() + ", minor: " + beacon.getId3());
                    }
                    //else if for checking if in my major minor array I have seen before this last major/minor some beacons I have deployed
                }
                }
            }

    }

}


