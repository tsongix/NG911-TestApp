package com.android.albert.ng911bluetoothtester;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;

/**
 * Created by Albert on 1/24/2016.
 */
public class MainActivity extends AppCompatActivity {
    protected static final String MAIN_ACTIVITY = "MainActivity";
    private Button testButton,detectButton;
    BluetoothChecker bluetooth;
    Boolean click=false;
    String testnum,loc, model,mac;
    //String url = "http://nead.bramsoft.com/ng911_test/create_test.php";//ip for Get
    boolean created;
    public static boolean updated=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.capturelogov2_2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //Bluetooth turn on
        bluetooth=new BluetoothChecker(getApplicationContext());
        bluetooth.enableBluetooth();
        bindViews();
        bindButtons();
        //Wifi Turn on
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        mac = info.getMacAddress();
        model = Build.MANUFACTURER + "-" + Build.MODEL;
        Toast.makeText(getApplicationContext(),"Remember to change Run and Experiment number",Toast.LENGTH_SHORT).show();
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
    }

    private void bindViews() {
        testButton = (Button) findViewById(R.id.testButton);
        detectButton = (Button) findViewById(R.id.detectButton);
    }

    private void bindButtons() {
        testButton.setOnClickListener(new StartOnClickListener());
        detectButton.setOnClickListener(new StartOnClickListener());
    }

    private class StartOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //OnClick for checking the las value info through the app.
                case R.id.testButton:
                    click = true;
                    // Perform action on click
                    EditText experiment = (EditText) findViewById(R.id.TextExperiment);
                    EditText run = (EditText) findViewById(R.id.TextRun);

                    String experimentfile = "experimentnum.txt";
                    String runfile = "runnum.txt";

                    String experimentstr = experiment.getText().toString();
                    String runstr = run.getText().toString();

                    FileOutputStream outputStream;
                    try {
                        //experiment
                        outputStream = openFileOutput(experimentfile, Context.MODE_PRIVATE);
                        outputStream.write(experimentstr.getBytes());
                        Log.i("Write", experimentstr + " saved as " + experimentfile);
                        outputStream.close();
                        //Run
                        outputStream = openFileOutput(runfile, Context.MODE_PRIVATE);
                        outputStream.write(runstr.getBytes());
                        Log.i("Write", runstr + " saved as " + runfile);
                        //Toast.makeText(getApplicationContext(),"Exp: "+experimentstr + "Run: "+runstr + " saved ",Toast.LENGTH_SHORT).show();
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(MainActivity.this, TestsInfo.class);
                    startActivity(i);
                    break;
                case R.id.detectButton:
                    //Toast.makeText(getApplicationContext(),"Button not working...",Toast.LENGTH_SHORT).show();
                    click = true;
                    Intent i2 = new Intent(MainActivity.this, DetectorActivity.class);
                    startActivity(i2);
                    break;
            }
        }
    }
}
