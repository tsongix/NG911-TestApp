package com.android.albert.ng911bluetoothtester;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Albert on 9/26/2016.
 * Interaction with the locally stored info: Experiment, Run and Server
 */
public class phoneDB {
    //GetServer value from internal phone storage
    public String getServer(Context context) {
        FileInputStream fis = null;
        String line = null;
        try {
            fis = context.openFileInput("ServerURL.txt");
        } catch (FileNotFoundException |java.lang.NullPointerException e) {
            return "";
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException |java.lang.NullPointerException e) {
            return "";
        }
        return sb.toString();
    }

    //Get experiment number value from internal phone storage
    public int getExperiment(Context context) {
        FileInputStream fis = null;
        String line = null;
        try {
            fis = context.openFileInput("experimentnum.txt");
        } catch (FileNotFoundException|java.lang.NullPointerException e) {
            return 1;
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException|java.lang.NullPointerException e) {
            return 1;
        }
        return Integer.parseInt(sb.toString());
    }

    //Get run number value from internal phone storage
    public int getRun(Context context) {
        FileInputStream fis = null;
        String line = null;
        try {
            fis = context.openFileInput("runnum.txt");
        } catch (FileNotFoundException|java.lang.NullPointerException e) {
            return 1;
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException|java.lang.NullPointerException e) {
            return 1;
        }
        return Integer.parseInt(sb.toString());
    }
}
