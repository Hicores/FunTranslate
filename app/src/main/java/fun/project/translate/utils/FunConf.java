package fun.project.translate.utils;

import android.content.SharedPreferences;

import fun.xloader.api.XUtils.XEnv;

public class FunConf {
    public static void setString(String setName,String key,String value){
        SharedPreferences sharedPreferences = XEnv.getAppContext().getSharedPreferences(setName,0);
        sharedPreferences.edit().putString(key,value).commit();
    }
    public static String getString(String setName,String key,String defValue){
        SharedPreferences sharedPreferences = XEnv.getAppContext().getSharedPreferences(setName,0);
        return sharedPreferences.getString(key,defValue);
    }
    public static boolean getBoolean(String setName,String key,boolean defValue){
        SharedPreferences sharedPreferences = XEnv.getAppContext().getSharedPreferences(setName,0);
        return sharedPreferences.getBoolean(key,defValue);
    }
    public static void setBoolean(String setName,String key,boolean value){
        SharedPreferences sharedPreferences = XEnv.getAppContext().getSharedPreferences(setName,0);
        sharedPreferences.edit().putBoolean(key,value).commit();
    }
    public static int getInt(String setName,String key,int defValue){
        SharedPreferences sharedPreferences = XEnv.getAppContext().getSharedPreferences(setName,0);
        return sharedPreferences.getInt(key,defValue);
    }
    public static void setInt(String setName,String key,int value){
        SharedPreferences sharedPreferences = XEnv.getAppContext().getSharedPreferences(setName,0);
        sharedPreferences.edit().putInt(key,value).commit();
    }
    public static void removeConfig(String setName){
        SharedPreferences sharedPreferences = XEnv.getAppContext().getSharedPreferences(setName,0);
        sharedPreferences.edit().clear().commit();
    }
}
