package rahul.jagtap.dmas.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.Set;

import rahul.jagtap.dmas.model.User;


public class Preferences {
    private static final String LOGGED_IN_USER = "LOGGED_IN_USER";
    private static final String LOGGED_IN_USER_EMAIL = "LOGGED_IN_USER_EMAIL";
    private static final String TOKEN = "TOKEN";
    private static final String ADMIN_TOKEN = "ADMIN_TOKEN";
    private static final String LAT = "LAT";
    private static final String LNG = "LNG";
    private static final String CART_DATA = "CART_DATA";
    private static final String IS_GUEST = "IS_GUEST";
    private static final String FIREBASE_UID = "FIREBASE_UID";
    private static final String ABOUT_TEAM_PDF_URL = "ABOUT_TEAM_PDF_URL";
    private static final String SHOULD_SHOW_POPUP = "SHOULD_SHOW_POPUP";

    private Context context;
    Set<String> strings;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Preferences(Context context) {
        this.context = context;
    }

    protected SharedPreferences getSharedPreferences(String key) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private String getString(String key, String def) {
        SharedPreferences prefs = getSharedPreferences(key);
        return prefs.getString(key, def);
    }

    public void setString(String key, String val) {
        SharedPreferences prefs = getSharedPreferences(key);
        Editor e = prefs.edit();
        e.putString(key, val);
        e.apply();
    }

    private long getLong(String key, long def) {
        SharedPreferences prefs = getSharedPreferences(key);
        return prefs.getLong(key, def);
    }

    public void setLong(String key, long val) {
        SharedPreferences prefs = getSharedPreferences(key);
        Editor e = prefs.edit();
        e.putLong(key, val);
        e.apply();
    }

    private boolean getBoolean(String key, boolean def) {
        SharedPreferences prefs = getSharedPreferences(key);
        boolean b = prefs.getBoolean(key, def);
        return b;
    }

    private void setBoolean(String key, boolean val) {
        SharedPreferences prefs = getSharedPreferences(key);
        Editor e = prefs.edit();
        e.putBoolean(key, val);
        e.apply();
    }

    public boolean isLoggedInUser() {
        String json = getString(LOGGED_IN_USER, null);
        return json != null && !TextUtils.isEmpty(json);
    }

    public void logOutUser() {
        SharedPreferences prefs = getSharedPreferences(LOGGED_IN_USER);
        Editor e = prefs.edit();
        e.clear();
        e.apply();
    }

    public User getLoggedInUser() {
        String json = getString(LOGGED_IN_USER, null);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return new Gson().fromJson(json, User.class);
    }

    public void setLoggedInUser(User user) {
        setString(LOGGED_IN_USER, new Gson().toJson(user));
    }

    public String getLoggedInUserId() {
        return getString(FIREBASE_UID, null);
    }

    public void setLoggedInUserId(String uid) {
        setString(FIREBASE_UID, uid);
    }

    public String getLoggedInUserEmail() {
        return getString(LOGGED_IN_USER_EMAIL, null);
    }

    public void setLoggedInUserEmail(String value) {
        setString(LOGGED_IN_USER_EMAIL, value);
    }

    private Set<String> getStringSet(String key, Set<String> def) {
        SharedPreferences prefs = getSharedPreferences(key);
        return prefs.getStringSet(key, def);
    }

    public void setStringSet(String key, Set<String> val) {
        SharedPreferences prefs = getSharedPreferences(key);
        Editor e = prefs.edit();
        e.putStringSet(key, val);
        e.apply();
    }

    public String getToken() {
        return getString(TOKEN, null);
    }

    public void setToken(String token) {
        setString(TOKEN, token);
    }

    public String getAdminToken() {
        return getString(ADMIN_TOKEN, null);
    }

    public void setAdminToken(String token) {
        setString(ADMIN_TOKEN, token);
    }

    public String getLat() {
        return getString(LAT, null);
    }

    public void setLat(String lat) {
        setString(LAT, lat);
    }

    public String getLng() {
        return getString(LNG, null);
    }

    public void setLng(String lng) {
        setString(LNG, lng);
    }

    public boolean isGuest() {
        return getBoolean(IS_GUEST, true);
    }

    public void setIsGuest(boolean isGuest) {
        setBoolean(IS_GUEST, isGuest);
    }

    public String getAboutTeamPdfUrl() {
        return getString(ABOUT_TEAM_PDF_URL, "");
    }

    public void setAboutTeamPdfUrl(String aboutTeamPdfUrl) {
        setString(ABOUT_TEAM_PDF_URL, aboutTeamPdfUrl);
    }

    public boolean shouldShowPopup() {
        return getBoolean(SHOULD_SHOW_POPUP, true);
    }

    public void setShouldShowPopup(boolean shouldShowPopup) {
        setBoolean(SHOULD_SHOW_POPUP, shouldShowPopup);
    }

    /**
     * Generic raw-string cache accessors. Used for stale-while-revalidate caching of network payloads
     * (e.g. the e-suvidha grid JSON) so screens can render instantly from disk while refreshing in the
     * background. Callers pick their own namespaced keys.
     */
    public String getCachedString(String key) {
        return getString(key, null);
    }

    public void setCachedString(String key, String val) {
        setString(key, val);
    }
}
