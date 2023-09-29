package com.Gogo.Manga.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Constant {
    public  static final String base_url ="https://readm.org/";

    public static   boolean TESTMODE_UNITY_ADS = false;
    private static SharedPreferences mySharedPreferences;
    private static final String PREF = "pref";
    private static final String AdsType= "AdsType";
    private static final String AdsId="AdsId";
    private static final String BannerId= "BannerId";
    private static final String InterId="InterId";
    private static final String NativeId="NativeId";
    private static final String NativePerItem="NativePerItem";
    private static final String InterPerClick="InterPerClick";
    private static final String IsFirstTime="IsFirstTime";
    private static final String Email="Email";
    private static final String Name="Name";

    public static void setName(Context context, String keyId){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putString(Name, keyId);
        myEditor.commit();
        myEditor.apply();
    }

    public static String getName(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return mySharedPreferences.getString(Name,null);
    }

    public static void setKey(Context context, String keyId){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putString(Email, keyId);
        myEditor.commit();
        myEditor.apply();
    }

    public static String getKey(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return mySharedPreferences.getString(Email,null);
    }

    public static void setNotFirstTime(Context context, boolean isFirstTime){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putBoolean(IsFirstTime, isFirstTime);
        myEditor.commit();
        myEditor.apply();
    }

    public static boolean IsFirtTime(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return mySharedPreferences.getBoolean(IsFirstTime,true);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void setAdsType(Context context, int type ){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putInt(AdsType, type);
        myEditor.commit();
        myEditor.apply();
    }

    public static int getAdsType(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return mySharedPreferences.getInt(AdsType,0);
    }

    public static void setAdsId(Context context, String id ){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putString(AdsId, id);
        myEditor.commit();
        myEditor.apply();
    }
    public static String getAdsId(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return mySharedPreferences.getString(AdsId,null);
    }

    public static void setBannerId(Context context, String id){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putString(BannerId, id);
        myEditor.commit();
        myEditor.apply();
    }

    public static String getBannerId(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return mySharedPreferences.getString(BannerId,null);
    }

    public static void setInterId(Context context, String id){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putString(InterId, id);
        myEditor.commit();
        myEditor.apply();
    }

    public static String getInterId(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return mySharedPreferences.getString(InterId,null);
    }
    public static void setInterPerClick(Context context, int perClick){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putInt(InterPerClick, perClick);
        myEditor.commit();
        myEditor.apply();
    }

    public static int getInterPerClick(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return mySharedPreferences.getInt(InterPerClick,0);
    }

    public static void setNativeId(Context context, String id){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putString(NativeId, id);
        myEditor.commit();
        myEditor.apply();
    }

    public static String getNativeId(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return mySharedPreferences.getString(NativeId,null);
    }

    public static void setNativePerItem(Context context, int perItem){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putInt(NativePerItem, perItem);
        myEditor.commit();
        myEditor.apply();
    }

    public static int getNativePerItem(Context context){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return mySharedPreferences.getInt(NativePerItem,0);
    }


    public static void setPageView(Context context, String ActivityName, int count){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putInt(ActivityName, count);
        myEditor.commit();
        myEditor.apply();
    }

    public static int getPageView(Context context, String ActivityName){
        mySharedPreferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return mySharedPreferences.getInt(ActivityName,0);
    }


}
