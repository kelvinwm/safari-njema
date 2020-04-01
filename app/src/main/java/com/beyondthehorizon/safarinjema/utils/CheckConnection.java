package com.beyondthehorizon.safarinjema.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class CheckConnection {

    static private Boolean aBoolean = false;
    private static final String TAG = "TAGME";
    public static String REG_APP_PREFERENCES = "profilePref";
    public static final String MyPhoneNumber = "MyPhoneNumber";
    public static final String SaccoName = "SaccoName";
    public static final String PassengerDetails = "PassengerDetails";
    public static final String DriverName = "DriverName";
    public static final String VehicleNumber = "VehicleNumber";
    public static final String ConductorName = "ConductorName";
    public static final String UserFrom = "userFrom";
    public static final String Destination = "destination";
    public static final String TotalPassengers = "totalPassengers";
    public static final String CountControl = "countControl";
    public static final String IsLoggedIn = "IsLoggedIn";

    public static boolean hasActiveInternetConnection() {
        return aBoolean;
    }

    public static class InternetCheck extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
                int returnVal = p1.waitFor();
                return (returnVal == 0);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean internet) {
            Log.d(TAG, "onPostExecute: " + internet);
            aBoolean = internet;
        }
    }

    public static boolean getConnectivityStatusString(Context context) {
        boolean status = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        } else {
            return false;
        }
        return status;
    }
}

