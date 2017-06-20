package com.android.albert.ng911bluetoothtester;

/**
 * Created by Albert on 6/15/2016.
 */
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
//for seeing the captured data in the tables...tbc
public class ActivityInfo extends AppCompatActivity {
    Button updateButton;
    ListView listInfo;
    String url = "http://nead.bramsoft.com/ng911_test/get_all_interactions.php";//ip
    HttpTx http;
    public Data d;
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;

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

        //Transmission
        http=new HttpTx();
       /* for(int i=1;i<2;i++) {
            try {
                //
                http.HttpGetRequest3(url, getApplicationContext(), new VolleyCallback() {
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
        d=Data.getInstance();
        //listItems.add("Empty");
        //listItems.add(d.getReceived());
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,listItems);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        listInfo.setAdapter(adapter);*/
        Log.i("ActivityInfo","Captures List Actrivity");

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
                    Data d=Data.getInstance();
                    listItems=d.getReceivedcaps();
                    adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1,listItems);
                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
                    listInfo.setAdapter(adapter);
                    listInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        //Setting item click listener, each click intent is created...
                        @Override
                        public void onItemClick(AdapterView arg0, View arg1, int position,
                                                long arg3) {
                            //i.putExtra("position", position);
                            Log.i("Clicked Pos", String.valueOf(position));
                            //Intent i = new Intent(MainActivity.this, InfoDetail.class);
                            //passing position variable to the intent
                           // i.putExtra("position", position);
                          //  startActivity(i);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
