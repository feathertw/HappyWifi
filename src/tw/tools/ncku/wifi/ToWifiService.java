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
			
			setCheckOrBulidSSID();
			stopSelf();
		}
	}
	
	public void setCheckOrBulidSSID(){
		Log.i(TAG, "----------setChooseOrBulidSSID()----------");
		
		String networkSSID = null;
		SchoolCheck schooCheck=new SchoolCheck();
		
		int networkId = -1;
		boolean bConfigured=false;
		boolean bTanet=false;
		
		boolean bConnected = mConnectHttp.getConnectState();
		if(bConnected){
			Log.i("#####","there connnected wifi");
			
			String ssid = mConnectHttp.getCurrentWifiSSID();
			SchoolCheck.school=schooCheck.getKey(ssid);
			if(SchoolCheck.school!=null){
				networkSSID=ssid;
				bTanet=true;
			}
		}
		else{
			Log.i("#####","no connnected wifi");
			List<ScanResult> wifiScanlist=mConnectHttp.getWifiScanResult();
			List<WifiConfiguration> wifiConfiguredList=mConnectHttp.getConfiguredNetworksList();
			
			for(ScanResult s : wifiScanlist) {
				if(MyOperateState.D) Log.i("#####","Wifi ssid:"+s.SSID+" level:"+s.level);
				
				networkId = mConnectHttp.matchConfiguredNetworks(s.SSID,wifiConfiguredList);
				if(networkId!=-1)	bConfigured=true;
				
				SchoolCheck.school=schooCheck.getKey(s.SSID);
				if(SchoolCheck.school!=null){
					networkSSID=s.SSID;
					bTanet=true;
					break;
				}
				
				if(bConfigured) break;
			}
		}
		
		if(bTanet){
			MyOperateState.TANET=true;
			
			if(!bConnected){
				if(bConfigured){
					mConnectHttp.setWifiNetworkId(networkId);
				}
				else if(!bConfigured){
					networkId = mConnectHttp.setNewNetwork(networkSSID);
					mConnectHttp.setWifiNetworkId(networkId);
				}
			}
		}
		else{
			MyOperateState.TANET=false;
			Log.i(TAG,"NOT CHOOSE TANET WIFI");
			Toast.makeText(this, "NO TANET WIFI", Toast.LENGTH_SHORT).show();
		}
		
		Log.i(TAG, "networdId:"+networkId+" bConfigured:"+bConfigured+" bTanet:"+bTanet);
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
