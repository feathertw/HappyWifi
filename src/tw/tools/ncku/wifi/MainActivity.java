package tw.tools.ncku.wifi;

import tw.parameters.SchoolCheck;
import tw.references.MyNotification;
import tw.references.MyOperateState;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String TAG = "`MainActivity";
	
	public static final int MENU_ABOUT=0;
	
	private boolean switchAutoWifi;
	private boolean switchAutoLogin;
	private boolean switchSelectMailType;

	private MyNotification mNotif;
	private MyPreference mPref;
	
	private EditText eAccount;
	private EditText ePassword;
	private Button bLogin;
	private Button bSetting;
	private TextView tMailType;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"+++++++++++ON CREATE+++++++++++");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		init();
		//prevent double open app
		clearService();
		initSetting();
		
		//for auto setting
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
		Log.i(TAG, "----------init()----------");
		mPref=new MyPreference(this);
		mNotif=new MyNotification(this);
		
		eAccount = (EditText) findViewById(R.id.account);
		ePassword = (EditText) findViewById(R.id.password);
		bLogin = (Button) findViewById(R.id.login);
		bSetting = (Button) findViewById(R.id.setting);
		tMailType = (TextView) findViewById(R.id.mail_type);
	}
	
	private void initSetting(){
		Log.i(TAG, "----------initSetting()----------");
		
		switchAutoWifi=mPref.getBoolean(MyPreference.PREF_SWITCH_AUTOWIFI,true);
		switchAutoLogin=mPref.getBoolean(MyPreference.PREF_SWITCH_AUTOLOGIN,false);
		switchSelectMailType=mPref.getBoolean(MyPreference.PREF_SWITCH_SELECTMAILTYPE,false);
		
		if(switchSelectMailType){
			String name=mPref.getString(MyPreference.PREF_SELECT_MAIL_SHOOL_NAME, "XXX");
			tMailType.setText("@"+name);
		}

		bLogin.getBackground().setAlpha(60);
		bSetting.getBackground().setAlpha(60);
		
		bLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if(!MyOperateState.ToWifiService) startService(new Intent(MainActivity.this, ToWifiService.class));
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
    			final CheckBox cMailType=(CheckBox)dialog.findViewById(R.id.cMailType);
    			
    			if(switchAutoWifi)			cAutoWifi.setChecked(true);
    			else						cAutoWifi.setChecked(false);
    			if(switchAutoLogin)			cAutoLogin.setChecked(true);
    			else						cAutoLogin.setChecked(false);
    			if(switchSelectMailType)	cMailType.setChecked(true);
    			else						cMailType.setChecked(false);
    			
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
    			
    			cMailType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
    					
    					if(isChecked){
    						SchoolCheck schooCheck=new SchoolCheck();
    						final String itemName[] = schooCheck.getSchoolName();
    						final String itemMail[] = schooCheck.getSchoolMail();
    						
    		    			final Dialog dialog =new Dialog(MainActivity.this);
    		    			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    		    			dialog.setCanceledOnTouchOutside(true);
    		    			dialog.setContentView(R.layout.custom_listview);
    		    			
    		    			ListView listView=(ListView)dialog.findViewById(R.id.ListView);	
    		    			listView.setAdapter(new ArrayAdapter<Object>(MainActivity.this,android.R.layout.simple_list_item_1,itemMail)); 
    		    			listView.setOnItemClickListener(new OnItemClickListener(){  
								public void onItemClick(AdapterView<?> arg0,View arg1, int which, long arg3) {
        							tMailType.setText("@"+itemName[which]);
        							switchSelectMailType=true;
        							mPref.setBoolean(MyPreference.PREF_SWITCH_SELECTMAILTYPE, switchSelectMailType);
        							mPref.setString(MyPreference.PREF_SELECT_MAIL_SHOOL_NAME, itemName[which]);
        							mPref.setString(MyPreference.PREF_SELECT_MAIL_TYPE, itemMail[which]);	
        							dialog.dismiss();  
								}  
    		    			});
    		    			
        					dialog.setCancelable(true);
    		    			dialog.setOnCancelListener(new OnCancelListener(){
        						public void onCancel(DialogInterface dialog){
        							tMailType.setText("");
            						switchSelectMailType=false;
            						mPref.setBoolean(MyPreference.PREF_SWITCH_SELECTMAILTYPE, switchSelectMailType);
            						mPref.setString(MyPreference.PREF_SELECT_MAIL_SHOOL_NAME, "");
            						mPref.setString(MyPreference.PREF_SELECT_MAIL_TYPE, "");
            						cMailType.setChecked(false);
        						}
        					}); 
    		    			dialog.show();
    					}
    					else{
    						tMailType.setText("");
    						switchSelectMailType=false;
    						mPref.setBoolean(MyPreference.PREF_SWITCH_SELECTMAILTYPE, switchSelectMailType);
    						mPref.setString(MyPreference.PREF_SELECT_MAIL_SHOOL_NAME, "");
    						mPref.setString(MyPreference.PREF_SELECT_MAIL_TYPE, "");
    						cMailType.setChecked(false);
    					}
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
    	Log.i(TAG,"----------onCreateOptionsMenu()----------");
    	menu.add(0,MENU_ABOUT,0,R.string.about).setIcon(android.R.drawable.ic_menu_edit);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
    	Log.i(TAG,"----------onOptionsItemSelected()----------");
		
    	switch(item.getItemId() ){
    		case MENU_ABOUT:
    			new AlertDialog.Builder(this)
    			.setTitle(R.string.about)
    			.setMessage(R.string.about_content)
    			.setPositiveButton(R.string.about_comment, new DialogInterface.OnClickListener() {
    	    		public void onClick(DialogInterface arg0, int arg1) {
    					//open the app website in google play
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