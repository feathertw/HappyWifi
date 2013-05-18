package tw.references;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class MyConnectHttp {
	
	private Context context;
	
	public static final String confirmHttps="http://ctc8631.qov.tw/internet_check.html";
	public static final String comfirmAppearValue="*FOR_ANDROID_NCKU_WIFI*";
	
	public MyConnectHttp(Context context){
		this.context=context;
	}
	
	public boolean getWiFiState(){
		
		WifiManager wifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		return wifiManager.isWifiEnabled();
		
		if(wifiManager.getWifiState()==WifiManager.WIFI_STATE_DISABLED||wifiManager.getWifiState()==WifiManager.WIFI_STATE_DISABLING)
			return false;
		else
			return true;
	}
	
	public void setWiFiState(Boolean wifiOn){
		
		WifiManager wifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if(wifiOn && !wifiManager.isWifiEnabled()){
			wifiManager.setWifiEnabled(true);
		}
		else if(!wifiOn && wifiManager.isWifiEnabled()){
			wifiManager.setWifiEnabled(false);
		}
	}
	
	public boolean getConnectState(){
		
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networInfo = conManager.getActiveNetworkInfo(); 
		if (networInfo == null || !networInfo.isAvailable()) { 
			return false;
		}
		return true;
	}

//	public Boolean getConnectSpecifyWifi(){
//		WifiManager wifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo wifiInfo= wifiManager.getConnectionInfo();
//		
//		String nowWifiSSIF=wifiInfo.getSSID();
//		if(nowWifiSSIF!=null && nowWifiSSIF.equals(MainActivity.WIFI_SSID) ){
//			return true;
//		}	
//		return false;
//	}
	
	public String getWifiSSID(){
		WifiManager wifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo= wifiManager.getConnectionInfo();
		
		return wifiInfo.getSSID();
	}

	public String getCurrentWifiSSID(){
		WifiManager wifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		return wifiManager.getConnectionInfo().getSSID();
	}
	
	public List<ScanResult> getWifiScanResult(){
		WifiManager wifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		return wifiManager.getScanResults();
	}
	
	public void setWifiNetworkId(int networkId){
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.disconnect();
		wifiManager.enableNetwork(networkId, true);
		wifiManager.reconnect();
	}
	
	public int setNewNetwork(String networkSSID){
		
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
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
		wifiManager.saveConfiguration();
		return wcgID;
//		wifiManager.enableNetwork(wcgID, true);
//		wifiManager.disconnect();
//		wifiManager.reconnect();
	}
	
	public List<WifiConfiguration> getConfiguredNetworksList(){
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getConfiguredNetworks();	
	}
	
	public int matchConfiguredNetworks(String SSID, List<WifiConfiguration> list){
		
		int networkId=-1;
		for (WifiConfiguration i : list) {
			if(MyOperateState.D) System.out.println("SSID:" + i.SSID + "networkId:"+ i.networkId);
			if (i.SSID != null&& i.SSID.equals("\"" + SSID + "\"")) {
				return i.networkId;
			}
		}		
		return networkId;
	}
	
	public String get_http_data(String url) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			HttpEntity result = response.getEntity();
			if (result != null)
				return EntityUtils.toString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
//		return null;
	}

	public String post_url_contents(String url, List<NameValuePair> params) {
		try {
			HttpClient client = MySSLSocketFactory.createMyHttpClient();
			HttpPost mHttpPost = new HttpPost(url);
			if (params != null && params.size() > 0)
				mHttpPost
						.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(mHttpPost);
			HttpEntity result = response.getEntity();
			if (result != null)
				return EntityUtils.toString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
//		return null;
	}
}