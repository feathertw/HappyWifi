package tw.references;


import tw.tools.ncku.wifi.R;
import tw.tools.ncku.wifi.ToLogoutService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class MyNotification {
	
	public static final int NOTIF_INIT=0;
	public static final int NOTIF_LOGIN=1;
	public static final int NOTIF_INTO_WIFI=2;
	public static final int NOTIF_CANCEL=3;
	
	private Context context;
	public MyNotification(Context context){
		this.context=context;
	}
	public void setNotif(int which){
        
		NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(context,ToLogoutService.class); 
        PendingIntent appIntent=PendingIntent.getService(context,0,notifyIntent,0);
        String title=context.getString(R.string.title_activity_main);
        Notification notification = new Notification();
        notification.when=0;
        notification.flags=Notification.FLAG_AUTO_CANCEL;
        
        switch(which){
        	case NOTIF_INIT:
        		notification.icon=R.drawable.wifi_off;
                notification.setLatestEventInfo(context,title,"here to cancel connect",appIntent);
                notificationManager.notify(0,notification);
                break;
        	case NOTIF_LOGIN:
        		notification.icon=R.drawable.wifi_off;
                notification.tickerText="WAIT FOR WIFI CONNECT";
                notification.setLatestEventInfo(context,title,"wait for Wifi connect, here to cancel",appIntent);
                notificationManager.notify(0,notification);
        		break;
        	case NOTIF_INTO_WIFI:
        		notification.icon=R.drawable.wifi_on;
                MyConnectHttp mConnectHttp=new MyConnectHttp(context);
                notification.tickerText="CONNECT TO "+mConnectHttp.getWifiSSID();
                notification.setLatestEventInfo(context,title,"here to logout and turn off wifi",appIntent);
                notification.defaults=Notification.DEFAULT_LIGHTS;
                notificationManager.notify(0,notification);
        		break;
        	case NOTIF_CANCEL:
        		notificationManager.cancel(0);
        		break;
        		
        }
	}
	
	public void setNotifAutoLogin(Boolean onRun){
		final NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		if(onRun){
	        Intent notifyIntent = new Intent(context,ToCancelAutoLoginService.class); 
	        PendingIntent appIntent=PendingIntent.getService(context,0,notifyIntent,0);
	        String title="Auto Logging";
	        Notification notification = new Notification();
//	        notification.when=Long.MAX_VALUE;//modify
	        notification.icon=R.drawable.pnull;
	        notification.flags=Notification.FLAG_AUTO_CANCEL;
	        notification.setLatestEventInfo(context,title,"here to cancel the auto login",appIntent);
	        notificationManager.notify(1,notification);	
		}
		else{
			notificationManager.cancel(1);
		}
	}
}

