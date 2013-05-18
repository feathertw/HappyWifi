package tw.references;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {
	
	public static final String PREF = "PREF";
	public static final String PREF_ACCOUNT = "PREF_ACCOUNT";
	public static final String PREF_PASSWORD = "PREF_PASSWORD";
	public static final String PREF_SWITCH_AUTOWIFI="PREF_SWITCH_AUTOWIFI";
	public static final String PREF_SWITCH_AUTOLOGIN="PREF_SWITCH_AUTOLOGIN";
	public static final String PREF_SWITCH_SELECTMAILTYPE="PREF_SWITCH_SELECTMAILTYPE";
	public static final String PREF_SELECT_MAIL_SHOOL_NAME="PREF_SELECT_MAIL_SHOOL_NAME";
	public static final String PREF_SELECT_MAIL_TYPE="PREF_SELECT_MAIL_TYPE";
	
	private Context context;
	public MyPreference(Context context){
		this.context=context;
	}
	
	public boolean getBoolean(String key, Boolean mDefault){
		SharedPreferences settings = context.getSharedPreferences(PREF, 0);
		return settings.getBoolean(key, mDefault);
	}
	
	public void setBoolean(String key, boolean value){
		SharedPreferences settings = context.getSharedPreferences(PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public String getString(String key, String mDefault){
		SharedPreferences settings = context.getSharedPreferences(PREF, 0);
		return settings.getString(key, mDefault);
	}	
	public void setString(String key, String value){
		SharedPreferences settings = context.getSharedPreferences(PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}
}

