package com.android.albert.ng911bluetoothtester;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

/**
 * Created by Albert on 1/30/2016.
 * Creation of the JSON format for the server
 * [{"major":"xx","minor":"xx","rss":"xx"},{"major":"xy","minor":"xy","rss":"xy”}]
 */
public class Json {
    /**
     * @param Major
     * @param Minor
     * @param Rssi
     * @param Testid
     * @return
     */
    String Uuid, Major, Minor, Rssi, Testid;
    JSONObject jsonOb;
    JSONObject jsonObOutdoor;
    JSONArray beaconsJson;
    JSONObject main;

   Json(String Major, String Minor, String Rssi, String Testid){
       this.Uuid=Uuid;
       this.Major=Major;
       this.Minor=Minor;
       this.Rssi=Rssi;
       this.Testid=Testid;
       jsonOb = new JSONObject();
       jsonObOutdoor = new JSONObject();
       beaconsJson = new JSONArray();
    }

    public Json() {
        jsonOb = new JSONObject();
        jsonObOutdoor = new JSONObject();
        beaconsJson = new JSONArray();
    }

    public void createMyJsonIndoor() throws JSONException {
        jsonOb.put("major", Integer.parseInt(Major));
        jsonOb.put("minor", Integer.parseInt(Minor));
        jsonOb.put("rssi", Integer.parseInt(Rssi));
        jsonOb.put("testnum", Integer.parseInt(Testid));
        beaconsJson.put(jsonOb);
        //main.put("location",beaconsJson);
    }
    public void updateMyJsonIndoor ( String Major, String Minor, String Rssi, String Testid) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("major", Integer.parseInt(Major));
        json.put("minor", Integer.parseInt(Minor));
        json.put("rssi", Integer.parseInt(Rssi));
        //json.put("testnum", Integer.parseInt(Testid));
        beaconsJson.put(json);
        //main.put("location",beaconsJson);//
    }

    public void createMyJsonOutdoor(Context c) throws JSONException {
        LocationHelper location=new LocationHelper(c);
        jsonObOutdoor.put("Lat", location.getLocation(c).getLatitude());
        jsonObOutdoor.put("Long", location.getLocation(c).getLongitude());
        beaconsJson.put(jsonObOutdoor);
        //main.put("location",beaconsJson);
    }

    public String readMyJson()throws JSONException {
        return beaconsJson.toString();
        //return main.toString();
    }

    //Return length of jsonArray by json objects
    public Integer lenghtJson()throws JSONException {
        return beaconsJson.length();
    }

    //clear jsonArray
    public void clear() {
        beaconsJson = new JSONArray();
    }

    public void prepareJson(String teventid) {
        JSONObject jo=new JSONObject();
        for(int l=0;l<beaconsJson.length();l++){
            try {
                jo=beaconsJson.getJSONObject(l);
                jo.put("testnum", teventid);
                beaconsJson.put(l,jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
