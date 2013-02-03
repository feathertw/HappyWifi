package tw.references;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ToListenWifiOffService extends Service {
	
	public IBinder onBind(Intent arg0) {
		return null;
	}   
	
	public static final String TAG = "`TO_LISTEN_WIFIOFF_SERVICE";
	private MyConnectHttp mConnectHttp;
	private MyNotification mNotif;
	private ToListenWifiOffReceiver toListenWifiOffReceiver;
	
	public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG,"+++++++++++ON START+++++++++++");
        MyOperateState.ToListenWifiOffService=true;
        
        mConnectHttp=new MyConnectHttp(this);
        mNotif=new MyNotification(this);
        
        toListenWifiOffReceiver=new ToListenWifiOffReceiver();
        registerReceiver(toListenWifiOffReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        
//        startService(new Intent(this, ToLogoutService.class));
//        stopSelf();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"+++++++++++ON DESTROY+++++++++++");
        MyOperateState.ToListenWifiOffService=false;
        
        unregisterReceiver(toListenWifiOffReceiver);
        
        Log.i("xxxxx","ToWifiService:"+MyOperateState.ToWifiService);
        Log.i("xxxxx","ToLoginService:"+MyOperateState.ToLoginService);
        Log.i("xxxxx","ToLogoutService:"+MyOperateState.ToLogoutService);
        Log.i("xxxxx","ToCancelAutoLoginService:"+MyOperateState.ToCancelAutoLoginService);
        Log.i("xxxxx","ToListenWifiOffService:"+MyOperateState.ToListenWifiOffService);
        
    }

	class ToListenWifiOffReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "----------ToLoginReceiver----------");
			if(MyOperateState.D) Toast.makeText(ToListenWifiOffService.this, "ToListenWifiOffReceiver",Toast.LENGTH_SHORT).show();
			
			if (!mConnectHttp.getConnectState() ) {
				mNotif.setNotif(MyNotification.NOTIF_CANCEL);
				stopSelf();
			}

		}
	}
}
