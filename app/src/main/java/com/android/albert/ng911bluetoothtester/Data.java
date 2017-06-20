package com.android.albert.ng911bluetoothtester;

import java.util.ArrayList;
import java.util.HashSet;

public class Data {
    private ArrayList<String> cap=new ArrayList<String>();
    private ArrayList<String> capDeployment=new ArrayList<String>();
    private static String serverResponse = new String();
    public ArrayList<String> smajor_db, sminor_db, slocid_db;
    final static Data data= new Data( );

    public Data(){
        smajor_db=new ArrayList<>();
        sminor_db=new ArrayList<>();
        slocid_db=new ArrayList<>();
        serverResponse = new String();
    }

    /* Static 'instance' method */
    public static Data getInstance( ) {
        return data;
    }

    public String getServerResponse()
    {
        return this.serverResponse;
    }

    public ArrayList<String> getReceivedcaps()
    {
        return this.cap;
    }

    public ArrayList<String> getReceivedcapsDeployment()
    {
        return this.capDeployment;
    }

    public String getReceivedcap(int i)
    {
        return cap.get(i);
    }

    public String getReceivedcapDeployment(int i)
    {
        return capDeployment.get(i);
    }

    //save value into arraylist of test info
    public void setReceived(String a)
    {
        // this.captured = captured+"\n"+ a;
        cap.add(a);
    }
//save values into arraylist of deployment info
    public void setReceived(String locid,String location, String major, String minor)
    {
        slocid_db.add(locid);
        smajor_db.add(major);
        sminor_db.add(minor);
        String a=String.format("%1$s  %2$s  %3$10s  %4$s",locid, location,major,minor);
        capDeployment.add(a);
    }


    public void setServerResponse(String res)
    {
        serverResponse = res;
    }

}
