package com.android.albert.ng911bluetoothtester;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Albert on 2/11/2016.
 */
public class HttpTx {
//Example desired format
// http://nead.bramsoft.com/ng911_test/start_test.php?major=1&minor=4&rssi=-40&TestNum=1&Location=1
    //url val is
    //http://localhost/ng911_test/start_test.php
    String result;
    int next=0;
    ArrayList<String> pid= new ArrayList<>();
    ArrayList<String> major= new ArrayList<>();
    ArrayList<String> minor= new ArrayList<>();
    ArrayList<String> rssi= new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<String> testnum= new ArrayList<>();
    ArrayList<String> location = new ArrayList<>();
    ArrayList<String> locid= new ArrayList<>();
    public RequestQueue startqueue,queue;
    public RequestQueue queue2,queue3;

    public void CreateTestEvent(String urlcreate, Context applicationContext, int runnum, int testnum) {
        //get to create test 'testnum' in the database
        if (startqueue == null)
            startqueue = Volley.newRequestQueue(applicationContext);
        String urlfinal=urlcreate + "?" + "runnumber"+"="+runnum+"&"+"testlocationid"+"="+testnum;
        Log.i("HTTP Get Request val", urlfinal);
        StringRequest myPost = new StringRequest(Request.Method.GET, urlfinal,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        String [] r=response.split(";");
                        Log.i("HTTP Response", r[r.length - 1]);
                        String num=r[r.length - 1].split(":")[r[r.length - 1].split(":").length-1];
                        Data d = Data.getInstance();
                        d.setServerResponse(num);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("response Error /home", error + "");
                        if (error instanceof NoConnectionError) {
                            Log.d("NoConnectionError", "NoConnectionError.......");
                        } else if (error instanceof AuthFailureError) {
                            Log.d("AuthFailureError", "AuthFailureError.......");
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError", "ServerError.......");
                        } else if (error instanceof NetworkError) {
                            Log.d("NetworkError", "NetworkError.......");
                        } else if (error instanceof ParseError) {
                            Log.d("ParseError", "ParseError.......");
                        }else if (error instanceof TimeoutError) {
                            Log.d("TimeoutError", "TimeoutError.......");
                        }
                    }
                }
        );
        startqueue.add(myPost);
    }

    //GETCAPTURED.PHP?JSON=[{MAJOR:...}{...}...
    //Add data rows from a single set of 5 captured BL packages
    public void HttpGetRequest(String urlb,Context context, String jsonArray) throws JSONException {
        String url="http://smith-system-f.herokuapp.com/IndoorLocationTest/storeTestData?jsonArray=";
        //get to the server
        if (queue==null)
            queue = Volley.newRequestQueue(context);
        String urlfinal=url + jsonArray;
        Log.i("HTTP Get Request val", urlfinal);
        StringRequest myReq = new StringRequest(Request.Method.GET, urlfinal,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("response Error /home", error + "");
                        if (error instanceof NoConnectionError) {
                            Log.d("NoConnectionError", "NoConnectionError.......");
                        } else if (error instanceof AuthFailureError) {
                            Log.d("AuthFailureError", "AuthFailureError.......");
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError", "ServerError.......");
                        } else if (error instanceof NetworkError) {
                            Log.d("NetworkError", "NetworkError.......");
                        } else if (error instanceof ParseError) {
                            Log.d("ParseError", "ParseError.......");
                        }else if (error instanceof TimeoutError) {
                            Log.d("TimeoutError", "TimeoutError.......");
                        }
                    }
                }
        );
        queue.add(myReq);
    }

//Get for getting the info from the test locations table
    public void HttpGetRequest_TestTable(String url, final Context context, final VolleyCallback callback) throws JSONException {
        //get to the server for retrieving iBeacons
        if (queue2==null)
            queue2 = Volley.newRequestQueue(context.getApplicationContext());
        next=0;
      //url .../get_all_interactions.php
        String urlfinal=url;
        StringRequest myReq2 = new StringRequest(Request.Method.GET, urlfinal,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result = response;
                        Log.d("Response", response);
                        //response=response.substring(51);
                        //String[] r = response.split("successfully");
                        //response=r[1];
                        Log.i("JSON",response);
                        JSONObject json=null;
                        JSONArray jsona=null;
                        try {
                            json = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //while(Integer.parseInt(split[2].split(",")[0])!=next);
                            // Log.i("testt1",jsona.getJSONArray(0).toString());
                            try {
                                jsona=json.getJSONArray("rows");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println(jsona);
                            try{
                                    for (int i=0; i < jsona.length(); i++) {
                                        json = jsona.getJSONObject(i);
                                        pid.add(json.getString("tester_id"));
                                        location.add(json.getString("tester_build_acr"));
                                        Log.d("json2", "values " + pid.toString() + " " + location.toString());
                                        //String minor=object.getJSONObject();
                                        Data d = Data.getInstance();
                                        d.setReceived(pid.get(next) + "        " + location.get(next));
                                        next++;
                                    }
                        //Log.i("BL Mgmt response:", d.getReceived());
                } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e){
                                Toast.makeText(context,"Table empty",Toast.LENGTH_SHORT).show();
                            }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response Error HTTP GET", error + "");

                        // in case of an error, create an empty response with no location on it
                        // and send it on the INVITE
                        String response = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><presence></presence>";
                        Data d = Data.getInstance();
                        d.setReceived(response);

                        if (error instanceof NoConnectionError) {
                            Log.d("NoConnectionError", "NoConnectionError.......");
                        } else if (error instanceof AuthFailureError) {
                            Log.d("AuthFailureError", "AuthFailureError.......");
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError", "ServerError.......");
                        } else if (error instanceof NetworkError) {
                            Log.d("NetworkError", "NetworkError.......");
                        } else if (error instanceof ParseError) {
                            Log.d("ParseError", "ParseError.......");
                        } else if (error instanceof TimeoutError) {
                            Log.d("TimeoutError", "TimeoutError.......");
                        }
                    }
                }
        );
        queue2.add(myReq2);
    }

    //Get test table (last 200 values of the table)
    public void HttpGetRequest3(String url, final Context context,final VolleyCallback callback) throws JSONException {
        //get to the server for retrieving iBeacons
        if (queue3==null)
            queue3 = Volley.newRequestQueue(context.getApplicationContext());
        next=0;
        //url .../get_all_interactions.php
        String urlfinal=url;
        StringRequest myReq3 = new StringRequest(Request.Method.GET, urlfinal,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result = response;
                        Log.d("Response", response);
                        //response=response.substring(51);
                        String[] r = response.split("successfully");
                        response=r[1];
                        Log.i("JSON",response);
                        JSONObject json=new JSONObject();
                        JSONArray jsona=new JSONArray();
                        try {
                            json = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsona=json.getJSONArray("interactions");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(jsona);
                        try{
                            int i = jsona.length()-pid.size();
                            if (i<=0) {
                                for (i=0; i < jsona.length(); i++) {
                                    json = jsona.getJSONObject(i);
                                    pid.add(json.getString("pid"));
                                    major.add(json.getString("major"));
                                    minor.add(json.getString("minor"));
                                    rssi.add(json.getString("rssi"));
                                    location.add(json.getString("Location"));
                                    testnum.add(json.getString("TestNum"));
                                    time.add(json.getString("captured_time"));
                                    Log.d("json2", "values " + rssi + " " + time + " " + testnum + " " + location);
                                    //String minor=object.getJSONObject();
                                    Data d = Data.getInstance();
                                    //d.setReceived(rssi[next] + "        " + time[next] + "        " + testnum[next] + "        " + location[next]);
                                    next++;
                                }
                            }else{
                                for (i=jsona.length()-pid.size(); i < jsona.length(); i++) {
                                    json = jsona.getJSONObject(i);
                                    pid.add(json.getString("pid"));
                                    major.add(json.getString("major"));
                                    minor.add(json.getString("minor"));
                                    rssi.add(json.getString("rssi"));
                                    location.add(json.getString("Location"));
                                    testnum.add(json.getString("TestNum"));
                                    time.add(json.getString("captured_time"));
                                    Log.d("json2", "values " + rssi + " " + time + " " + testnum + " " + location);
                                    //String minor=object.getJSONObject();
                                    Data d = Data.getInstance();
                                    //d.setReceived(rssi[next] + "        " + time[next] + "        " + testnum[next] + "        " + location[next]);
                                    next++;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e){
                            Toast.makeText(context,"Table empty",Toast.LENGTH_SHORT).show();
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response Error HTTP GET", error + "");

                        // in case of an error, create an empty response with no location on it
                        // and send it on the INVITE
                        String response = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><presence></presence>";
                        Data d = Data.getInstance();
                        d.setReceived(response);

                        if (error instanceof NoConnectionError) {
                            Log.d("NoConnectionError", "NoConnectionError.......");
                        } else if (error instanceof AuthFailureError) {
                            Log.d("AuthFailureError", "AuthFailureError.......");
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError", "ServerError.......");
                        } else if (error instanceof NetworkError) {
                            Log.d("NetworkError", "NetworkError.......");
                        } else if (error instanceof ParseError) {
                            Log.d("ParseError", "ParseError.......");
                        } else if (error instanceof TimeoutError) {
                            Log.d("TimeoutError", "TimeoutError.......");
                        }
                    }
                }
        );
        queue3.add(myReq3);
    }

    //Get readable deployment location major and minor for doing the scann of the environment
    //before testing
    //Returns length
    public int HttpGetRequest_DeploymentInfo(String url, final Context context) throws JSONException {
        //get to the server
        result=null;
        next=0;
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        String urlfinal = url + "911deployment/get_deploymentlocations.php";
        Log.i("[BL HTTP Get val] ", urlfinal);
        StringRequest myReq = new StringRequest(Request.Method.GET, urlfinal,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result = response;
                        // Log.d("Response", response);
                        String[] r = response.split("successfully");
                        response=r[1];
                        Log.i("JSON",response);
                        JSONObject json=null;
                        JSONArray jsona=null;
                        try {
                            json = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            jsona=json.getJSONArray("deploymentlocations");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(jsona);
                        for (int i=0; i < jsona.length(); i++) {
                            try {
                                json = jsona.getJSONObject(i);
                                locid.add(json.getString("LocID"));
                                location.add(json.getString("Location"));
                                major.add(json.getString("Major"));
                                minor.add(json.getString("Minor"));
                                Log.d("json2", "values " + locid.get(next).toString() + " " + location.get(next).toString()+ " " + major.get(next).toString()+ " " + minor.get(next).toString());
                                //String minor=object.getJSONObject();
                                Data data = Data.getInstance();
                                data.setReceived(locid.get(next), location.get(next), major.get(next), minor.get(next));
                                next++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Response Error HTTP GET", error + "");
                        if (error instanceof NoConnectionError) {
                            Log.d("NoConnectionError", "NoConnectionError.......");
                        } else if (error instanceof AuthFailureError) {
                            Log.d("AuthFailureError", "AuthFailureError.......");
                        } else if (error instanceof ServerError) {
                            Log.d("ServerError", "ServerError.......");
                        } else if (error instanceof NetworkError) {
                            Log.d("NetworkError", "NetworkError.......");
                        } else if (error instanceof ParseError) {
                            Log.d("ParseError", "ParseError.......");
                        } else if (error instanceof TimeoutError) {
                            Log.d("TimeoutError", "TimeoutError.......");
                        }
                    }
                }
        );
        queue.add(myReq);
        return next+1;
    }


}