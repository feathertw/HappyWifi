package tw.tools.ncku.wifi;


import tw.references.MyConnectHttp;
import tw.references.MyNotification;
import tw.references.MyPreference;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class MainActivity extends Activity {

	public static final String TAG = "`NCKU_WIFI_LOGIN";
	public static final String WIFI_SSID= "NCKU-WLAN";
	public static final Boolean D=false;
	
	public static final int MENU_ABOUT=0;
	
	private boolean switchAutoWifi;
	private boolean switchAutoLogin;

	private MyNotification mNotif;
	private MyPreference mPref;
	private MyConnectHttp mConnectHttp;	
	
	private EditText eAccount;
	private EditText ePassword;
	private Button bLogin;
	private Button bSetting;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"+++++++++++ON CREATE+++++++++++");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		
		init();
		clearService();
        
		initSetting();
		if(switchAutoWifi){
			startService(new Intent(this, ToWifiService.class));
			
			if(switchAutoLogin){
				mNotif.setNotifAutoLogin(true);
				finish();
				
		        startService(new Intent(this, ToLoginService.class));
			}
		}

		
	}
	
	protected void onResume() {
		super.onResume();
		Log.i(TAG,"+++++++++++ON RESUME+++++++++++");
		restorePREF();
	}

	protected void onPause() {
		super.onPause();
		Log.i(TAG,"+++++++++++ON PAUSE+++++++++++");
		storePREF();
//		finish();//new
	}	

	private void clearService(){
		Log.i(TAG, "----------clearService()----------");
		stopService(new Intent(MainActivity.this, ToWifiService.class));
		stopService(new Intent(MainActivity.this, ToLoginService.class));
		stopService(new Intent(MainActivity.this, ToLogoutService.class));
		
		mNotif.setNotif(MyNotification.NOTIF_CANCEL);
		mNotif.setNotifAutoLogin(false);
	}
	
	private void init(){
		mPref=new MyPreference(this);
		mNotif=new MyNotification(this);
		mConnectHttp=new MyConnectHttp(this);
		
		eAccount = (EditText) findViewById(R.id.account);
		ePassword = (EditText) findViewById(R.id.password);
		bLogin = (Button) findViewById(R.id.login);
		bSetting = (Button) findViewById(R.id.setting);
		
	}
	
	private void initSetting(){
		Log.i(TAG, "----------initSetting()----------");
		
		switchAutoWifi=mPref.getBoolean(MyPreference.PREF_SWITCH_AUTOWIFI,true);
		switchAutoLogin=mPref.getBoolean(MyPreference.PREF_SWITCH_AUTOLOGIN,false);

		bLogin.getBackground().setAlpha(60);
		bSetting.getBackground().setAlpha(60);
		
		bLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
//				storePREF();
				if (!mConnectHttp.getWiFiState() ) 
					mConnectHttp.setWiFiState(true);
				
				finish();
				startService(new Intent(MainActivity.this, ToLoginService.class));
			}
		});
		bSetting.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
    			Dialog dialog =new Dialog(MainActivity.this);
    			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    			dialog.setCanceledOnTouchOutside(true);
    			dialog.setContentView(R.layout.custom_dialog);
    			dialog.show();
    			
				WindowManager.LayoutParams lp = getWindow().getAttributes();  
				lp.alpha=0.3f;
				getWindow().setAttributes(lp);
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    			dialog.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						WindowManager.LayoutParams lp = getWindow().getAttributes();  
						lp.alpha=1.0f;
						getWindow().setAttributes(lp);
					}
				});
    			
    			final CheckBox cAutoWifi=(CheckBox)dialog.findViewById(R.id.cAutoWifi);
    			final CheckBox cAutoLogin=(CheckBox)dialog.findViewById(R.id.cAutoLogin);
    			if(switchAutoWifi)	cAutoWifi.setChecked(true);
    			else				cAutoWifi.setChecked(false);
    			if(switchAutoLogin)	cAutoLogin.setChecked(true);
    			else				cAutoLogin.setChecked(false);
    			
    			if(!switchAutoWifi)	cAutoLogin.setEnabled(false);
    			cAutoWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
    					switchAutoWifi=isChecked;
    					mPref.setBoolean(MyPreference.PREF_SWITCH_AUTOWIFI, switchAutoWifi);
    					
    					if(switchAutoWifi){
    						cAutoLogin.setEnabled(true);
    					}
    					else{
    						switchAutoLogin=false;
    						mPref.setBoolean(MyPreference.PREF_SWITCH_AUTOLOGIN, false);
    						cAutoLogin.setChecked(false);
    						cAutoLogin.setEnabled(false);
    					}
    				}
    			});
    			cAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
    					switchAutoLogin=isChecked;
    					mPref.setBoolean(MyPreference.PREF_SWITCH_AUTOLOGIN, switchAutoLogin);
    				}
    			});
			}
		});
	}

	private void restorePREF() {
		Log.i(TAG, "----------restorePREF()----------");
		SharedPreferences settings = getSharedPreferences(MyPreference.PREF, 0);
		String account = settings.getString(MyPreference.PREF_ACCOUNT, null);
		String password = settings.getString(MyPreference.PREF_PASSWORD, null);

		eAccount.setText(account);
		ePassword.setText(password);
	}

	private void storePREF() {
		Log.i(TAG, "----------storePREF()----------");
		String account = eAccount.getText().toString();
		String password = ePassword.getText().toString();
		SharedPreferences settings = getSharedPreferences(MyPreference.PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(MyPreference.PREF_ACCOUNT, account);
		editor.putString(MyPreference.PREF_PASSWORD, password);
		editor.commit();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
    	if(D) Log.i(TAG,"----------onCreateOptionsMenu()----------");
    	menu.add(0,MENU_ABOUT,0,R.string.about).setIcon(android.R.drawable.ic_menu_edit);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
    	if(D) Log.i(TAG,"----------onOptionsItemSelected()----------");
		
    	switch(item.getItemId() ){
    		case MENU_ABOUT:
    			new AlertDialog.Builder(this)
    			.setTitle(R.string.about)
    			.setMessage(R.string.about_content)
    			.setPositiveButton(R.string.about_comment, new DialogInterface.OnClickListener() {
    	    		public void onClick(DialogInterface arg0, int arg1) {
    					try {
    						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"tw.tools.ncku.wifi")));
    					}catch (android.content.ActivityNotFoundException anfe) {
    						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+"tw.tools.ncku.wifi")));
    					} 			
    	    		}
    	    	})
    	    	.setNegativeButton(R.string.about_close, new DialogInterface.OnClickListener() {
    	    		public void onClick(DialogInterface arg0, int arg1) {
    	    			
    	    		}
    	    	})
    			.show();
    			break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
}