package tw.references;

import tw.tools.ncku.wifi.ToLogoutService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ToCancelAutoLoginService extends Service {
	
	public IBinder onBind(Intent arg0) {
		return null;
	}   
	
	public static final String TAG = "`TO_CANCEL_AUTO_LOGIN_SERVICE";
	private MyPreference mPref;
	private MyNotification mNotif;
	
	public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG,"+++++++++++ON START+++++++++++");
        MyOperateState.ToCancelAutoLoginService=true;
        
        mPref=new MyPreference(this);
//        mPref.setBoolean(MyPreference.PREF_SWITCH_AUTOWIFI, false);
        mPref.setBoolean(MyPreference.PREF_SWITCH_AUTOLOGIN, false);
        
        mNotif=new MyNotification(this);//need?
        mNotif.setNotif(MyNotification.NOTIF_CANCEL);
        
        startService(new Intent(this, ToLogoutService.class));
        stopSelf();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"+++++++++++ON DESTROY+++++++++++");
        MyOperateState.ToCancelAutoLoginService=false;
//        startActivity(new Intent(this, MainActivity.class));//error
    }

}
