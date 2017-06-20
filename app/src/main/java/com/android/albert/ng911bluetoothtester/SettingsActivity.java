package com.android.albert.ng911bluetoothtester;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Albert on 3/6/2016.
 * TBC
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //GET SERVER VALUE when onclick and PASS IT TO InternalStorage
        final Button button = (Button) findViewById(R.id.Savebutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                EditText server = (EditText) findViewById(R.id.editServer);
                EditText experiment = (EditText) findViewById(R.id.TextExperiment);
                EditText run = (EditText) findViewById(R.id.TextRun);
                server.setAutoLinkMask(0);
                Log.i("Settings", server.getText().toString() + " " + experiment.getText().toString());
                //file names
                String serverURL = "ServerURL.txt";
                String experimentfile = "experimentnum.txt";
                String runfile = "runnum.txt";

                String serverurl = server.getText().toString();
                String experimentstr = experiment.getText().toString();
                String runstr = run.getText().toString();
                if (!serverurl.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid URL", Toast.LENGTH_LONG).show();
                    return;
                }
                FileOutputStream outputStream;

                try {
                    //server
                    outputStream = openFileOutput(serverURL, Context.MODE_PRIVATE);
                    outputStream.write(serverurl.getBytes());
                    Log.i("Write", serverurl + " saved as " + serverURL);
                    //Toast.makeText(getApplicationContext(),serverurl + " saved ",Toast.LENGTH_SHORT).show();
                    outputStream.close();
                    //experiment
                    outputStream = openFileOutput(experimentfile, Context.MODE_PRIVATE);
                    outputStream.write(experimentstr.getBytes());
                    Log.i("Write", experimentstr + " saved as " + experimentfile);
                    Toast.makeText(getApplicationContext(),"Experiment: "+experimentstr + " saved ",Toast.LENGTH_SHORT).show();
                    outputStream.close();
                    //Run
                    outputStream = openFileOutput(runfile, Context.MODE_PRIVATE);
                    outputStream.write(runstr.getBytes());
                    Log.i("Write", runstr + " saved as " + experimentfile);
                    Toast.makeText(getApplicationContext(),"Run: "+runstr + " saved ",Toast.LENGTH_SHORT).show();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            });

    }
}

