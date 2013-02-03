package tw.tools.ncku.wifi;

import tw.parameters.SchoolCheck;
import tw.references.MyConnectHttp;
import tw.references.MyNotification;
import tw.references.MyOperateState;

import android.app.Service;
import android.content.Intent;
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
        MyOperateState.ToLogoutService=true;
        
        if(MyOperateState.D) Toast.makeText(this, "LOGOUT SERVICE ONSTART", Toast.LENGTH_SHORT).show();
        
        initSetting();
        sendLogOut();
        
        stopSelf();
        
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"+++++++++++ON DESTROY+++++++++++");
        MyOperateState.ToLogoutService=false;
        
        mNotif.setNotif(MyNotification.NOTIF_CANCEL);
        mNotif.setNotifAutoLogin(false);
        stopService(new Intent(ToLogoutService.this, ToWifiService.class));
        stopService(new Intent(ToLogoutService.this, ToLoginService.class));
    }

    
	private void initSetting(){
		Log.i(TAG, "----------initSetting()----------");
		mConnectHttp=new MyConnectHttp(this);
		mNotif=new MyNotification(this);
	}
	
	private void sendLogOut(){
		Log.i(TAG, "----------sendLogOut()----------");
		
		new Thread(new Runnable() {
			public void run() {
				if(MyOperateState.TANET){
					String logoutResult = mConnectHttp.post_url_contents(SchoolCheck.school.logoutHttps, SchoolCheck.school.LogoutDataPair);
//					Log.i(TAG,logoutResult);
					if (logoutResult.indexOf(SchoolCheck.school.logoutAppearValue) != -1) {
						Log.i(TAG, "Logout Success");
					}
				}
				mConnectHttp.setWiFiState(false);
			}
		}).start();
	}
	
	
    
}
