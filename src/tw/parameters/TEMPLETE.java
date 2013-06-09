package tw.parameters;

import org.apache.http.message.BasicNameValuePair;

public class TEMPLETE {
	
	public static School getSchool(){
		
		School school = new School();
		
		school.name="";
		school.mail="@";
		school.accountPara="username";
		school.passwordPara="password";
		
		school.loginHttps="https://xxx";
		school.logoutHttps="https://xxx";
		school.loginAppearValue="Login Successful";
		school.logoutAppearValue="Logout Successful";
		
		school.LoginDataPair.add(new BasicNameValuePair("xxx", "x"));
		school.LogoutDataPair.add(new BasicNameValuePair("xxx", "x"));
		
		school.ssid.add("WIFI-SSID");
		
		return school;
	}
}




