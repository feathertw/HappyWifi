package tw.parameters;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

public class School {
	
	public String name;
	public String mail;
	public List<String> ssid= new ArrayList<String>();
	public String loginHttps;
	public String logoutHttps;
	public String loginAppearValue;
	public String logoutAppearValue;
	public List<NameValuePair> LoginDataPair= new ArrayList<NameValuePair>();
	public List<NameValuePair> LogoutDataPair= new ArrayList<NameValuePair>();

	public String accountPara;
	public String passwordPara;
}




