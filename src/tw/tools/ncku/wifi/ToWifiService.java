package tw.tools.ncku.wifi;

import java.util.List;

import tw.references.MyConnectHttp;
import tw.references.MyNotification;
import tw.references.PararmeterValue;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
	
	private WifiManager wifiManager;
	private MyConnectHttp mConnectHttp;	
	private MyNotification mNotif;
	
	private ToWifiReceiver toWifiReceiver;
	private boolean onToWifiReceiver=false;
	
	public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG,"+++++++++++ON START+++++++++++");
        
        if(MainActivity.D) Toast.makeText(this, "WIFI SERVICE ONSTART", Toast.LENGTH_SHORT).show();
        
        initSetting();
        openWiFi();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"+++++++++++ON DESTROY+++++++++++");
        
        if(MainActivity.D) Toast.makeText(this, "WIFI SERVICE DESTROY", Toast.LENGTH_SHORT).show();
        
        if(onToWifiReceiver){
        	unregisterReceiver(toWifiReceiver);
        	onToWifiReceiver=false;
        }
        
    }

	private void initSetting(){
		Log.i(TAG, "----------initSetting()----------");
		
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        toWifiReceiver=new ToWifiReceiver();
        mConnectHttp=new MyConnectHttp(this);
        
		mNotif=new MyNotification(this);
		mNotif.setNotif(MyNotification.NOTIF_INIT);
	}
	
	private void openWiFi(){
		Log.i(TAG, "----------openWiFi()----------");
		
		if (!mConnectHttp.getWiFiState() ) {
			//no wifi, open wifi, broadcast
			Log.i(TAG, "disconnect");
			mConnectHttp.setWiFiState(true);
			registerReceiver(toWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			onToWifiReceiver=true;
		}
		else{
			//on wifi, but uncertain
			
			if(mConnectHttp.getConnectState() ){
				Log.i(TAG, "connect");
				stopSelf();
			}
			else{
				Log.i(TAG, "connecting");
				setCheckOrBulidSSID();
				stopSelf();
			}
		}
	}
	
	public void setCheckOrBulidSSID(){
		Log.i(TAG, "----------setChooseOrBulidSSID()----------");
		
		String networkSSID = PararmeterValue.WIFI_SSID;
		
		Boolean ssidExsisted=false;
		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
		for (WifiConfiguration i : list) {
			System.out.println("SSID:" + i.SSID + "networkId:"+ i.networkId);
			if (i.SSID != null&& i.SSID.equals("\"" + networkSSID + "\"")) {
//				wifiManager.disconnect();
//				wifiManager.enableNetwork(i.networkId, true);
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
	
	class ToWifiReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "----------ToWifiReceiver----------");
			if(MainActivity.D) Toast.makeText(ToWifiService.this, "ToWifiReceiver",Toast.LENGTH_SHORT).show();
			
			setCheckOrBulidSSID();
			unregisterReceiver(toWifiReceiver);
			onToWifiReceiver=false;
			stopSelf();
		}
	}
	
    
}
