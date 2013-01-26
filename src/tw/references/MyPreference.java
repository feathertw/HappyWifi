package tw.references;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {
	
	public static final String PREF = "PREF";
	public static final String PREF_ACCOUNT = "PREF_ACCOUNT";
	public static final String PREF_PASSWORD = "PREF_PASSWORD";
	public static final String PREF_SWITCH_AUTOWIFI="PREF_SWITCH_AUTOWIFI";
	public static final String PREF_SWITCH_AUTOLOGIN="PREF_SWITCH_AUTOLOGIN";
	
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
	
	
}

