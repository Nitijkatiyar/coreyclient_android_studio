package com.coremobile.coreyhealth;
import android.content.SharedPreferences;

// LocalPrefs is a singleton which uses shared preferences as the storage and
// gives simpler apis to access them the rest of the application
public enum LocalPrefs
{
    INSTANCE;

    private SharedPreferences mSharedPrefs;

    // called by MyApplication when the application starts
    public void setSharedPrefs(SharedPreferences sharedPrefs)
    {
        mSharedPrefs = sharedPrefs;
    }

    public boolean showPhoneCalls()
    {
        return mSharedPrefs.getBoolean("show_phone_calls", false);
    }

    public void setShowPhoneCalls(boolean val)
    {
        putBoolean("show_phone_calls", val);
    }

    public boolean hideLogo()
    {
        return mSharedPrefs.getBoolean("hide_logo", false);
    }

    public void setHideLogo(boolean val)
    {
        putBoolean("hide_logo", val);
    }

    public boolean phoneCalls()
    {
        return mSharedPrefs.getBoolean("phone_calls", false);
    }

    public void setPhoneCalls(boolean val)
    {
        putBoolean("phone_calls", val);
    }

    public boolean locationSync()
    {
        return mSharedPrefs.getBoolean("location_sync", false);
    }

    public void setLocationSync(boolean val)
    {
        putBoolean("location_sync", val);
    }

    public boolean hasPickedFeatures()
    {
        return mSharedPrefs.getBoolean("has_picked_features", false);
    }

    public void setHasPickedFeatures(boolean val)
    {
        putBoolean("has_picked_features", val);
    }

    public boolean hasAppCredentials()
    {
        return mSharedPrefs.getBoolean("has_app_credentials", false);
    }

    public void setHasAppCredentials(boolean val)
    {
        putBoolean("has_app_credentials", val);
        if (!hasPickedFeatures())
        {
            boolean phoneCalls = !MyApplication.INSTANCE.inDemoMode();
            setPhoneCalls(phoneCalls);
            if (phoneCalls)
            {
                MyApplication.INSTANCE.registerPhoneCallsListener();
            }
            else
            {
                MyApplication.INSTANCE.unregisterPhoneCallsListener();
            }
        }
    }

    public String username() {
        return mSharedPrefs.getString("Username", null);
    }

    public String password() {
        return mSharedPrefs.getString("Password", null);
    }

    public String organization() {
        return mSharedPrefs.getString("Organization", null);
    }

    public boolean autoSync() {
	    return mSharedPrefs.getBoolean("RequireAutoSync", false);
    }

    public void setHasUserSignedIn(boolean val) {
        putBoolean("UserSignedIn", val);
    }

    public boolean hasUserSignedIn() {
        return mSharedPrefs.getBoolean("UserSignedIn", false);
    }

    public void setDeviceId(String val) {
        putString(
            MyApplication.USE_GCM? "gcmDeviceId" : "deviceId", val);
    }

    public String deviceId() {
        return mSharedPrefs.getString(
            MyApplication.USE_GCM? "gcmDeviceId" : "deviceId", null);
    }

    public void setAppVersion(int val) {
        putInt("gcmAppVersion", val);
    }

    public int appVersion() {
        return mSharedPrefs.getInt("gcmAppVersion", Integer.MIN_VALUE);
    }

    ////////////////////////////////////
    ////////// PRIVATE methods //////////
    ////////////////////////////////////

    private void putString(String key, String val)
    {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString(key, val);
        editor.commit();
    }

    private void putBoolean(String key, boolean val)
    {
        if (mSharedPrefs.getBoolean(key, false) != val)
        {
            SharedPreferences.Editor editor = mSharedPrefs.edit();
            editor.putBoolean(key, val);
            editor.commit();
        }
    }

    private void putInt(String key, int val)
    {
        if (mSharedPrefs.getInt(key, 0) != val)
        {
            SharedPreferences.Editor editor = mSharedPrefs.edit();
            editor.putInt(key, val);
            editor.commit();
        }
    }

    private void putLong(String key, int val)
    {
        if (mSharedPrefs.getLong(key, 0) != val)
        {
            SharedPreferences.Editor editor = mSharedPrefs.edit();
            editor.putLong(key, val);
            editor.commit();
        }
    }
}
