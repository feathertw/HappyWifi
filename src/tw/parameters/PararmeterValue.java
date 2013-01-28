package tw.parameters;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


public class PararmeterValue {
	
	public static final String WIFI_SSID= "NCKU-WLAN";
	
//	public static final String loginHttps="https://wlan.ncku.edu.tw/login.html";
//	public static final String logoutHttps="https://wlan.ncku.edu.tw/logout.html";
	public static final String loginHttps="http://140.133.2.240:8080/";
	public static final String logoutHttps="http://140.133.2.240:8080/?status=ok&url=";
//	public static final String confirmHttps="http://ctc8631.qov.tw/internet_check.html";
	
//	public static final String loginAppearValue="Login Successful";
//	public static final String logoutAppearValue="Click here to close window";
	public static final String loginAppearValue="USER INFOMATION";
	public static final String logoutAppearValue="USER LOGIN";
//	public static final String comfirmAppearValue="*FOR_ANDROID_NCKU_WIFI*";
	
	
	public static List<NameValuePair> getLoginDataPair(String account, String password){
		List<NameValuePair> dataPairs = new ArrayList<NameValuePair>();
//		dataPairs.add(new BasicNameValuePair("buttonClicked", "4"));
//		dataPairs.add(new BasicNameValuePair("username", account));
//		dataPairs.add(new BasicNameValuePair("password", password));
		dataPairs.add(new BasicNameValuePair("AuthenticateUser", account));
		dataPairs.add(new BasicNameValuePair("AuthenticatePassword", password));
		return dataPairs;
	}
	
	public static List<NameValuePair> getLogoutDataPair(){
		List<NameValuePair> dataPairs = new ArrayList<NameValuePair>();
//		dataPairs.add(new BasicNameValuePair("userStatus", "1"));
//		dataPairs.add(new BasicNameValuePair("err_flag", "0"));
//		dataPairs.add(new BasicNameValuePair("err_msg", ""));
		dataPairs.add(new BasicNameValuePair("", ""));
		return dataPairs;
	}
}




