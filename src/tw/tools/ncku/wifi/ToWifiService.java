package tw.tools.ncku.wifi;

import java.util.List;

import tw.parameters.SchoolCheck;
import tw.references.MyConnectHttp;
import tw.references.MyNotification;
import tw.references.MyOperateState;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class ToWifiService extends Service {
	public IBinder onBind(Intent arg0) {
		return null;
	} 
	
	public static final String TAG = "`TO_WIFI_SERVICE";
	
//	private WifiManager wifiManager;
	private MyConnectHttp mConnectHttp;	
	private MyNotification mNotif;
	
	private ToWifiReceiver toWifiReceiver;
	private boolean onToWifiReceiver=false;
	
	public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG,"+++++++++++ON START+++++++++++");
        MyOperateState.ToWifiService=true;
        
        if(MyOperateState.D) Toast.makeText(this, "WIFI SERVICE ONSTART", Toast.LENGTH_SHORT).show();
        
        initSetting();
        openWiFi();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"+++++++++++ON DESTROY+++++++++++");
        MyOperateState.ToWifiService=false;
        
        if(MyOperateState.D) Toast.makeText(this, "WIFI SERVICE DESTROY", Toast.LENGTH_SHORT).show();
        
        if(onToWifiReceiver){
        	unregisterReceiver(toWifiReceiver);
        	onToWifiReceiver=false;
        }
        
    }

	private void initSetting(){
		Log.i(TAG, "----------initSetting()----------");
		
//        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        toWifiReceiver=new ToWifiReceiver();
        mConnectHttp=new MyConnectHttp(this);
        
		mNotif=new MyNotification(this);
		mNotif.setNotif(MyNotification.NOTIF_INIT);
	}
	
	private void openWiFi(){
		Log.i(TAG, "----------openWiFi()----------");
		
		if (!mConnectHttp.getWiFiState() ) {
			//no wifi, open wifi, broadcast
			
			Log.i(TAG, "wifi not opened");
			mConnectHttp.setWiFiState(true);
			registerReceiver(toWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			onToWifiReceiver=true;
		}
		else{
			Log.i(TAG, "wifi opened");
			//on wifi, but uncertain
			
//			if(mConnectHttp.getConnectState() ){
//				Log.i(TAG, "connect");
//				stopSelf();
//			}
//			else{
//				Log.i(TAG, "connecting");
//				setCheckOrBulidSSID();
//				stopSelf();
//			}
			
			setCheckOrBulidSSID();
			stopSelf();
		}
	}
	
	public void setCheckOrBulidSSID(){
		Log.i(TAG, "----------setChooseOrBulidSSID()----------");
		
//		String networkSSID = PararmeterValue.WIFI_SSID;
		String networkSSID = null;
		List<ScanResult> wifiScanlist=mConnectHttp.getWifiScanResult();
		
		
//		SharedPreferences settings = getSharedPreferences(MyPreference.PREF, 0);
//		String account = settings.getString(MyPreference.PREF_ACCOUNT, null);
//		String password = settings.getString(MyPreference.PREF_PASSWORD, null);
//		SchoolCheck schooCheck=new SchoolCheck(account,password);
		SchoolCheck schooCheck=new SchoolCheck();
		
		for(ScanResult s : wifiScanlist) {
			if(MyOperateState.D) Log.i("xxxxx","Wifi ssid:"+s.SSID+" level:"+s.level);
			
			SchoolCheck.school=schooCheck.getKey(s.SSID);
			
			if(SchoolCheck.school!=null){
				networkSSID=s.SSID;
				break;
			}
		}
		
		if(SchoolCheck.school==null){
			Log.i(TAG,"NO TANET WIFI");
			Toast.makeText(this, "NO TANET WIFI", Toast.LENGTH_SHORT).show();
			MyOperateState.TANET=false;
			stopSelf();
			//cancel all program?
		}
		else{
			MyOperateState.TANET=true;
			Log.i(TAG,"find:"+SchoolCheck.school.name+" ssid:"+networkSSID);
			
			Boolean ssidExsisted=false;
			WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
			List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
			for (WifiConfiguration i : list) {
				if(MyOperateState.D) System.out.println("SSID:" + i.SSID + "networkId:"+ i.networkId);
				if (i.SSID != null&& i.SSID.equals("\"" + networkSSID + "\"")) {
//					wifiManager.disconnect();
//					wifiManager.enableNetwork(i.networkId, true);
					//force to connect specific TANET?
					ssidExsisted=true;
					break;
				}
			}
			if(!ssidExsisted){
				WifiConfiguration wfc = new WifiConfiguration();
				wfc.SSID = "\"".concat(networkSSID).concat("\"");
				wfc.status = WifiConfiguration.Status.DISABLED;
				wfc.priority = 40;

				wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
				wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
				wfc.allowedAuthAlgorithms.clear();
				wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
				wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
				wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
				wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
				wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
				wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
				
				int wcgID = wifiManager.addNetwork(wfc);
				wifiManager.enableNetwork(wcgID, true);
				wifiManager.disconnect();
				wifiManager.reconnect();
			}
			
			/*need reconnect?*/
		}
//		try {
//			Thread.sleep(6000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		Log.i(TAG,"setCheckOrBulidSSID() complete");
	}
	
	class ToWifiReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "----------ToWifiReceiver----------");
			if(MyOperateState.D) Toast.makeText(ToWifiService.this, "ToWifiReceiver",Toast.LENGTH_SHORT).show();
			
			setCheckOrBulidSSID();
			unregisterReceiver(toWifiReceiver);
			onToWifiReceiver=false;
			stopSelf();
		}
	}
	
    
}
