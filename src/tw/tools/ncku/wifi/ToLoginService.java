package tw.tools.ncku.wifi;


import org.apache.http.message.BasicNameValuePair;

import tw.parameters.SchoolCheck;
import tw.references.MyConnectHttp;
import tw.references.MyNotification;
import tw.references.MyOperateState;
import tw.references.MyPreference;
import tw.references.ToListenWifiOffService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ToLoginService extends Service {
	
	public IBinder onBind(Intent arg0) {
		return null;
	}   
	
	public static final String TAG = "`TO_LOGIN_SERVICE";

	private MyNotification mNotif;
	private MyConnectHttp mConnectHttp;
	private ToLoginReceiver toLoginReceiver;
	
	private boolean onToLoginReceiver=false;
	
	
	public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG,"+++++++++++ON START+++++++++++");
        MyOperateState.ToLoginService=true;
        
        if(MyOperateState.D) Toast.makeText(this, "LOGIN SERVICE ONSTART", Toast.LENGTH_SHORT).show();
        
        initSetting();
        
        if(mConnectHttp.getConnectState()){
        	Log.i(TAG, "wifi connected");
        	sendLogIn();
        }
        else{
        	Log.i(TAG, "wifi not connected");
        	registerReceiver(toLoginReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        	onToLoginReceiver=true;
        }
        	
        
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"+++++++++++ON DESTROY+++++++++++");
        MyOperateState.ToLoginService=false;
        
        if(MyOperateState.D) Toast.makeText(this, "LOGIN SERVICE DESTROY", Toast.LENGTH_SHORT).show();
        
        if(onToLoginReceiver){
        	unregisterReceiver(toLoginReceiver);
        	onToLoginReceiver=false;
        }
        
    }
	
	private void initSetting(){
		Log.i(TAG, "----------initSetting()----------");
		mConnectHttp=new MyConnectHttp(this);
		mNotif=new MyNotification(this);
        toLoginReceiver=new ToLoginReceiver();
        
        mNotif.setNotif(MyNotification.NOTIF_LOGIN);
	}
	
	private void sendLogIn(){
		Log.i(TAG, "----------sendLogIn()----------");
		
		final Handler mHandler = new Handler();
		new Thread(new Runnable() {
			public void run() {
				
				String loginResult="";
				String confirmResult="";
				
				if(MyOperateState.TANET){
					Log.i(TAG,"SCHOOL:"+SchoolCheck.school.name);
					SharedPreferences settings = getSharedPreferences(MyPreference.PREF, 0);
					String account = settings.getString(MyPreference.PREF_ACCOUNT, null);
					String password = settings.getString(MyPreference.PREF_PASSWORD, null);
					SchoolCheck.school.LoginDataPair.add(new BasicNameValuePair(SchoolCheck.school.accountPara, account));
					SchoolCheck.school.LoginDataPair.add(new BasicNameValuePair(SchoolCheck.school.passwordPara, password));
					loginResult = mConnectHttp.post_url_contents(SchoolCheck.school.loginHttps, SchoolCheck.school.LoginDataPair);
					Log.i(TAG, "loginResult"+loginResult);
				}
				confirmResult = mConnectHttp.get_http_data(MyConnectHttp.confirmHttps);
				
				
				if(loginResult.indexOf( SchoolCheck.school.loginAppearValue ) != -1){
					Log.i(TAG, "Login Success");
					mNotif.setNotif(MyNotification.NOTIF_INTO_WIFI);
					mNotif.setNotifAutoLogin(false);	
					startService(new Intent(ToLoginService.this, ToListenWifiOffService.class));
					stopSelf();
				}
				else if(confirmResult.indexOf( MyConnectHttp.comfirmAppearValue ) != -1){
					Log.i(TAG, "Already Connected");
					mNotif.setNotif(MyNotification.NOTIF_INTO_WIFI);
					mNotif.setNotifAutoLogin(false);
					
					startService(new Intent(ToLoginService.this, ToListenWifiOffService.class));
					stopSelf();	
				}
				else{
					Log.i(TAG, "Login Failed");
					mNotif.setNotif(MyNotification.NOTIF_CANCEL);
					mNotif.setNotifAutoLogin(false);
					stopSelf();
					
					mHandler.post(new Runnable(){
						public void run(){
							Toast.makeText(ToLoginService.this, "Login Failed", Toast.LENGTH_LONG).show();
						}
					});
				}
				
//				if(MyOperateState.TANET){
//					String result = mConnectHttp.post_url_contents(SchoolCheck.school.loginHttps, SchoolCheck.school.LoginDataPair);
//					if(result!=null && result.indexOf( SchoolCheck.school.loginAppearValue ) != -1){
//						Log.i(TAG, "Login Success");
//						mNotif.setNotif(MyNotification.NOTIF_INTO_WIFI);
//						mNotif.setNotifAutoLogin(false);	
//						startService(new Intent(ToLoginService.this, ToListenWifiOffService.class));
//						stopSelf();
//					}
//				}
//				else{
//					String result = mConnectHttp.get_http_data(MyConnectHttp.confirmHttps);
//					if(result!=null && result.indexOf( MyConnectHttp.comfirmAppearValue ) != -1) {
//						Log.i(TAG, "Already Connected");
//						mNotif.setNotif(MyNotification.NOTIF_INTO_WIFI);
//						mNotif.setNotifAutoLogin(false);
//						
//						startService(new Intent(ToLoginService.this, ToListenWifiOffService.class));
//						stopSelf();
//					}
//					else{
//						Log.i(TAG, "Login Failed");
//						mNotif.setNotif(MyNotification.NOTIF_CANCEL);
//						mNotif.setNotifAutoLogin(false);
//						stopSelf();
//						
//						mHandler.post(new Runnable(){
//							public void run(){
//								Toast.makeText(ToLoginService.this, "Login Failed", Toast.LENGTH_LONG).show();
//							}
//						});
//					}		
//				}
			}
		}).start();
	}	
	
	
	class ToLoginReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "----------ToLoginReceiver----------");
			if(MyOperateState.D) Toast.makeText(ToLoginService.this, "ToLoginReceiver",Toast.LENGTH_SHORT).show();
			
			if (!mConnectHttp.getConnectState() ) {
				if(MyOperateState.D) Toast.makeText(ToLoginService.this, "YET NO INTERNET",Toast.LENGTH_SHORT).show();
				if(MyOperateState.D) Log.i(TAG, "YET NO INTERNET");
			}
			else{
				if(MyOperateState.D) Toast.makeText(ToLoginService.this, "TRY CONNECT TO NCKU WIFI",Toast.LENGTH_SHORT).show();
				if(MyOperateState.D) Log.i(TAG, "TRY CONNECT TO NCKU WIFI");
				sendLogIn();
				unregisterReceiver(toLoginReceiver);
				onToLoginReceiver=false;
			}
		}
	}
    
}