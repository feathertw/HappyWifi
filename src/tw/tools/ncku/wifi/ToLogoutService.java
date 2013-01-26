package tw.tools.ncku.wifi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import tw.references.MyConnectHttp;
import tw.references.MyNotification;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ToLogoutService extends Service {
	
	public IBinder onBind(Intent arg0) {
		return null;
	}   
	
	public static final String TAG = "`TO_LOGOUT_SERVICE";
	private MyConnectHttp mConnectHttp;
	private MyNotification mNotif;
	
	public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG,"+++++++++++ON START+++++++++++");
        if(MainActivity.D) Toast.makeText(this, "LOGOUT SERVICE ONSTART", Toast.LENGTH_SHORT).show();
        
        initSetting();
        sendLogOut();
        
        stopSelf();
        
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"+++++++++++ON DESTROY+++++++++++");
        
        mNotif.setNotif(MyNotification.NOTIF_CANCEL);
        mNotif.setNotifAutoLogin(false);
        stopService(new Intent(ToLogoutService.this, ToWifiService.class));
        stopService(new Intent(ToLogoutService.this, ToLoginService.class));
        if(MainActivity.D) Toast.makeText(this, "LOGOUT SERVICE DESTROY", Toast.LENGTH_SHORT).show();
        
    }

    
	private void initSetting(){
		Log.i(TAG, "----------initSetting()----------");
		mConnectHttp=new MyConnectHttp(this);
		mNotif=new MyNotification(this);
	}
	
	private void sendLogOut(){
		Log.i(TAG, "----------sendLogOut()----------");
		
		final Handler mHandler = new Handler();
		new Thread(new Runnable() {
			public void run() {
				
				List<NameValuePair> dataPairs = new ArrayList<NameValuePair>();
				dataPairs.add(new BasicNameValuePair("userStatus", "1"));
				dataPairs.add(new BasicNameValuePair("err_flag", "0"));
				dataPairs.add(new BasicNameValuePair("err_msg", ""));
				
				final String result = mConnectHttp.post_url_contents("https://wlan.ncku.edu.tw/logout.html", dataPairs);
				mHandler.post(new Runnable(){
    				public void run(){
						if (result!=null && result.indexOf("Click here to close window") != -1) {
							if(MainActivity.D) Toast.makeText(ToLogoutService.this, "Logout Success", Toast.LENGTH_LONG).show();
							if(MainActivity.D) Log.i(TAG, "Logout Success");
						}
					} 
				});
				mConnectHttp.setWiFiState(false);
			}
		}).start();
	}
	
	
    
}
