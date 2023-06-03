package com.example.pengaduan.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefManager {
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    public static final String NAMA_LENGKAP = "namaLengkap";
    public static final String ID = "idPelanggan";
    public static final String USERNAME = "username";
    public static final String SP_PENGADUAN_APP = "Pengaduan";
    public static final String SP_SUDAH_LOGIN = "IsLogin";
    public static final String ROLE = "Role";
    public static final String REAL_ID = "RealId";

    public SharedPrefManager(Context context) {
        sp = context.getSharedPreferences(SP_PENGADUAN_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPBoolean(String keySP, boolean value) {
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public boolean sPSudahLogin() {
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }

    private static SharedPreferences getSharedPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static void editor(Context context, String constant, String string) {
        getSharedPreference(context).edit().putString(constant, string).apply();
    }

    public static String getNamaLengkap(Context context) {
        return getSharedPreference(context).getString(NAMA_LENGKAP, "");
    }

    public static void setNamaLengkap(Context context, String namaLengkap) {
        editor(context, NAMA_LENGKAP, namaLengkap);
    }

    public static String getIdPelanggan(Context context) {
        return getSharedPreference(context).getString(ID, "");
    }

    public static void setIdPelanggan(Context context, String idPelanggan) {
        editor(context, ID, idPelanggan);
    }

    public static void setUsername(Context context, String username) {
        editor(context, USERNAME, username);
    }

    public static String getUsername(Context context) {
        return getSharedPreference(context).getString(USERNAME, "");
    }

    public static void setRealId(Context context, String realId) {
        editor(context, REAL_ID, realId);
    }

    public static String getRealId(Context context) {
        return getSharedPreference(context).getString(REAL_ID, "");
    }

    public static void setRole(Context context, String role) {
        editor(context, ROLE, role);
    }

    public static String getRole(Context context) {
        return getSharedPreference(context).getString(ROLE, "");
    }
}
