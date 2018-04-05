package com.bf.popularmovies.utility;

/*
 * @author frielb 
 * Created on 05/04/2018
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    ConnectivityManager m_connectivityManagerAndroid;
    NetworkInfo mWifiInfo, mMobileInfo;

    public Boolean isConnected(Context con){

        try{
            if(this.isConnectedWIFI(con) || this.isConnectedMobileData(con))
            {
                Log.d(TAG,"isConnected returning TRUE");
                return true;
            }
        }
        catch(Exception e){
            Log.e(TAG,"isConnected Exception:["+e.getMessage()+"]");
        }

        Log.d(TAG,"isConnected returning FALSE");
        return false;
    }

    public Boolean isConnectedWIFI(Context con){

        try{
            m_connectivityManagerAndroid = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            mWifiInfo = m_connectivityManagerAndroid.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(mWifiInfo.isConnected()){
                Log.d(TAG,"isConnectedWIFI returning TRUE");
                return true;
            }
        }
        catch(Exception e){
            Log.e(TAG,"isConnectedWIFI Exception:["+e.getMessage()+"]");
        }

        Log.d(TAG,"isConnectedWIFI returning FALSE");
        return false;
    }

    public Boolean isConnectedMobileData(Context con){

        try{
            m_connectivityManagerAndroid = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            mMobileInfo = m_connectivityManagerAndroid.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(mMobileInfo.isConnected()){
                Log.d(TAG,"isConnectedMobileData returning TRUE");
                return true;
            }
        }
        catch(Exception e){
            Log.e(TAG,"isConnectedMobileData Exception:["+e.getMessage()+"]");//$$$ TODO exc string
        }

        Log.d(TAG,"isConnectedMobileData returning FALSE");
        return false;
    }

}
