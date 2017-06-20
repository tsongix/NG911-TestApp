package com.android.albert.ng911bluetoothtester;

/**
 * Created by Albert on 6/15/2016.
 */

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestsInfo extends AppCompatActivity {
    Button updateButton;
    static ListView listInfo;
    static String url ;
    HttpTx http;
    phoneDB phonedb;
    public Data d;
    static ArrayList<String> listItems=new ArrayList<String>();
    static Set<Integer> clickedset=new HashSet<Integer>();
    static ArrayAdapter<String> adapter;
    static boolean updated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(android.R.style.Theme_Dialog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.capturelogov2_2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //create views
        bindViews();
        bindButtons();
        phonedb = new phoneDB();
        if (phonedb.getServer(getApplicationContext()) != "") {
            url = phonedb.getServer(getApplicationContext());
        } else {
            url = "http://smith-system-f.herokuapp.com/IndoorLocationTest/";
        }

        String urlfinal = url + "getTesterLocationID?building_acr=AM";
        //Transmission
        if (!updated) {
            http = new HttpTx();
            for (int i = 1; i < 2; i++) {
                try {
                    http.HttpGetRequest_TestTable(urlfinal, getApplicationContext(), new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Data d = Data.getInstance();
                            d.setReceived(result);
                            //listItems.add(result);
                            Log.i("BL Mgmt response:", result);
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            d = Data.getInstance();
            //listItems.add("Empty");
            //listItems.add(d.getReceived());
            //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
            listInfo.setAdapter(adapter);
            updated = true;
        }
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

    private void bindViews() {
        listInfo = (ListView) findViewById(R.id.info_list);
        // listInfo.setAdapter(new InfoAdapter(this));
        updateButton = (Button) findViewById(R.id.updateButton);
    }

    private void bindButtons() {
        updateButton.setOnClickListener(new StartOnClickListener());
    }

    public class StartOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.updateButton:
                    Log.i("BL ManagementSystem", "Update button pressed");
                    final Data d=Data.getInstance();
                    listItems=d.getReceivedcaps();
                    //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,listItems);
                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
                    listInfo.setAdapter(adapter);
                    /*
                    Not working...it's for the check functionality
                    for (int i=0; i<listItems.size();i++){
                        if(clickedset.contains(i)){
                            listInfo.getChildAt(i).setBackgroundColor(Color.DKGRAY);
                        }
                    }*/


                    listInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        //Setting item click listener, each click intent is created...
                        @Override
                        public void onItemClick(AdapterView arg0, View arg1, int position,
                                                long arg3) {
                            //i.putExtra("position", position);
                            Log.i("Clicked Pos", String.valueOf(position));
                            //arg1.setBackgroundColor(Color.DKGRAY);
                            clickedset.add(position);
                            Intent i = new Intent(TestsInfo.this, TestActivity.class);
                            //passing position variable to the intent
                            i.putExtra("position", position);
                            i.putExtra("locid", d.getReceivedcap(position).toString().split("\\s+",2)[0]);
                            i.putExtra("location", d.getReceivedcap(position).toString().split("\\s+", 2)[1]);
                            startActivity(i);
                        }

                    });

                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
