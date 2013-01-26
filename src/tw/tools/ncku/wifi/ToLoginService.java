package tw.tools.ncku.wifi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import tw.references.MyConnectHttp;
import tw.references.MyNotification;
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
        if(MainActivity.D) Toast.makeText(this, "LOGIN SERVICE ONSTART", Toast.LENGTH_SHORT).show();
        
        initSetting();
        
        if(mConnectHttp.getConnectState()){
        	Log.i(TAG, "connected");
        	sendLogIn();
        }
        else{
        	Log.i(TAG, "not connected");
        	registerReceiver(toLoginReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        	onToLoginReceiver=true;
        }
        	
        
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"+++++++++++ON DESTROY+++++++++++");
        
        if(MainActivity.D) Toast.makeText(this, "LOGIN SERVICE DESTROY", Toast.LENGTH_SHORT).show();
        
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
				
				SharedPreferences settings = getSharedPreferences(MyPreference.PREF, 0);
				String account = settings.getString(MyPreference.PREF_ACCOUNT, null);
				String password = settings.getString(MyPreference.PREF_PASSWORD, null);

				List<NameValuePair> dataPairs = new ArrayList<NameValuePair>();
				dataPairs.add(new BasicNameValuePair("buttonClicked", "4"));
				dataPairs.add(new BasicNameValuePair("username", account));
				dataPairs.add(new BasicNameValuePair("password", password));
				final String result = mConnectHttp.post_url_contents("https://wlan.ncku.edu.tw/login.html", dataPairs);
				final String sresult = mConnectHttp.get_http_data("http://ctc8631.qov.tw/internet_check.html");/*can modify*/
//				Log.i(TAG,""+result);
//				Log.i(TAG,""+sresult);
				mHandler.post(new Runnable(){
    				public void run(){
    					if(result!=null && result.indexOf("Login Successful") != -1) {
    						if(MainActivity.D) Toast.makeText(ToLoginService.this, "Login Success", Toast.LENGTH_LONG).show();
    						if(MainActivity.D) Log.i(TAG, "Login Success");
    						mNotif.setNotif(MyNotification.NOTIF_INTO_WIFI);
    						mNotif.setNotifAutoLogin(false);
    						
    						startService(new Intent(ToLoginService.this, ToListenWifiOffService.class));
    						stopSelf();
    						
    						
    					} 
    					else {
    						if(sresult!=null && sresult.indexOf("*FOR_ANDROID_NCKU_WIFI*") != -1) {
    							if(MainActivity.D) Toast.makeText(ToLoginService.this, "Already Connected", Toast.LENGTH_LONG).show();
    							if(MainActivity.D) Log.i(TAG, "Already Connected");
    							mNotif.setNotif(MyNotification.NOTIF_INTO_WIFI);
    							mNotif.setNotifAutoLogin(false);
    							
    							startService(new Intent(ToLoginService.this, ToListenWifiOffService.class));
    							stopSelf();
    						}else {
    							Toast.makeText(ToLoginService.this, "Login Failed", Toast.LENGTH_LONG).show();
    							Log.i(TAG, "Login Failed");
    							mNotif.setNotif(MyNotification.NOTIF_CANCEL);
    							mNotif.setNotifAutoLogin(false);
    							stopSelf();
    						}
    					}				
    					
    				} 
    			});
			
				
			}
		}).start();

	}	
	
	
	class ToLoginReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "----------ToLoginReceiver----------");
			if(MainActivity.D) Toast.makeText(ToLoginService.this, "ToLoginReceiver",Toast.LENGTH_SHORT).show();
			
			if (!mConnectHttp.getConnectState() ) {
				if(MainActivity.D) Toast.makeText(ToLoginService.this, "YET NO INTERNET",Toast.LENGTH_SHORT).show();
				if(MainActivity.D) Log.i(TAG, "YET NO INTERNET");
			}
			else{
				if(MainActivity.D) Toast.makeText(ToLoginService.this, "TRY CONNECT TO NCKU WIFI",Toast.LENGTH_SHORT).show();
				if(MainActivity.D) Log.i(TAG, "TRY CONNECT TO NCKU WIFI");
				sendLogIn();
				unregisterReceiver(toLoginReceiver);
				onToLoginReceiver=false;
			}
			

		}
	}
    
}
