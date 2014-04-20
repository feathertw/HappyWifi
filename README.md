HappyWifi

An android app for helping log into the wifi certificate automatically.
https://play.google.com/store/apps/details?id=tw.tools.ncku.wifi
 
Function:
- Record the account and password
- Open wifi automatically
- Log into wifi certificate automatically
- Provide the list for email type
- Provide for manually selecting the wifi certificate


How to Modify?  
 1. Copy *src/tw/parameters/TEMPLETE.java* to a new file like ***SampleSchool.java*** at the same file directory.
 2. Modify the content of *SampleSchool.java* for your wifi certificate, detail at below.
 3. Add a line ***schoolList.add(SampleSchool.getSchool());*** in *src/tw/parameters/SchoolCheck.java* in function *SchoolCheck()*
 4. Test it works or not.
   
If you don't know wifi certificate information, please refer the html code when logging in in the browser. There are the example logging in and logging out web page. Maybe is not the same but still similar. You can refer this.

SampleLogin.html
```
	<form action="https://Sample.edu.tw/login.php">
	   <input type="text" name="SampleAccount">
	   <input type="password" name="SamplePassword">
	   <input type="hidden" name="SampleLogin1" value="SampleLoginValue1">
	   <input type="hidden" name="SampleLogin2" value="SampleLoginValue2">
	   <input type="submit" value="submit">
	</form>
```

SampleLogout.html
```
	<form action="https://Sample.edu.tw/logout.php">
	   <input type="hidden" name="SampleLogout1" value="SampleLogoutValue1">
	   <input type="submit" value="submit">
	</form>
```


SampleSchool.java
```
   school.name="Sample";
   school.mail="@Sample.edu.tw";
   school.accountPara="SampleAccount";
   school.passwordPara="SamplePassword";
   school.loginHttps="https://Sample.edu.tw/login.php";
   school.logoutHttps="https://Sample.edu.tw/logout.php";
   school.loginAppearValue="Login Successful";    //key word from return value to logging in target
   school.logoutAppearValue="Logout Successful";  //key word from return value to logging out target
   school.LoginDataPair.add(new BasicNameValuePair("SampleLogin1", "SampleLoginValue1"));    //not necessary
   school.LoginDataPair.add(new BasicNameValuePair("SampleLogin2", "SampleLoginValue2"));    //not necessary
   school.LogoutDataPair.add(new BasicNameValuePair("SampleLogout1", "SampleLogoutValue1")); //not necessary
   school.ssid.add("SampleWifiSSID1"); //Wifi SSID
   school.ssid.add("SampleWifiSSID2"); //Wifi SSID
```

If you are from Taiwan's university,
welcome to share the certificate information, I can integrate them to HappyWifi and make everyone more convinient =)

Hope there are no authorization problem.
