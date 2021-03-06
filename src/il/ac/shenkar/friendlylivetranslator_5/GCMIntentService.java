package il.ac.shenkar.friendlylivetranslator_5;

import java.io.IOException;
import java.io.InputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService 
{
	public static String PROJECT_NUMBER = "";
	public static int msgCount;

	/**
	 * Intent used to display a message in the screen
	 **/
	static final String DISPLAY_MESSAGE_ACTION = "il.ac.shenkar.friendlylivetranslator_5.DISPLAY_MESSAGE";

	/**
	 * Intent's extra that contains the message to be displayed
	 **/
	static final String EXTRA_MESSAGE = "message";
	static final String REGISTRATION_ID = "registartionId";

	public GCMIntentService() 
	{
		super(PROJECT_NUMBER);
	}

	@Override
	protected void onError(Context arg0, String msg) 
	{
		Log.i(TAG, "onError " + msg);
	}

	@Override
	protected void onMessage(Context context, Intent intent) 
	{
		Log.i(TAG, "Received message "+ intent.getExtras().getString("message"));
		String message = intent.getExtras().getString("message");
		displayMessage(context, message);
		generateNotification(context, message);
	}

	static void setProjectNO(String projectNo) 
	{
		PROJECT_NUMBER = projectNo;
	}

	@Override
	protected void onRegistered(Context context, String regId) 
	{
		Log.i(TAG, "Device registered: regId = " + regId);
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(REGISTRATION_ID, regId);
		context.sendBroadcast(intent);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) 
	{
		Log.i(TAG, "Device unRegistered: regId = " + arg1);
	}

	/**
	 * Notifies UI to display a message
	 **/
	public void displayMessage(Context context, String message) 
	{
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message
	 **/
	public void generateNotification(Context context, String message) 
	{
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent;
		notificationIntent = new Intent(context, HomePage.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.putExtra("message", message);
		PendingIntent intent = PendingIntent.getActivity(context, 0,notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		Bitmap bmp = getBitmapFromAssets();
		Notification notification = new NotificationCompat.Builder(context)
				.setContentTitle("Friendly Live Translator").setContentText(message)
				.setContentIntent(intent)
				.setNumber(++msgCount)
				.setSmallIcon(android.R.drawable.menuitem_background)
				.setWhen(when).setLargeIcon(bmp).setLights(Color.YELLOW, 1, 2)
				.setAutoCancel(true).build();
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);
	}

	public Bitmap getBitmapFromAssets() 
	{
		AssetManager assetManager = getAssets();
		InputStream istr;
		try 
		{
			istr = assetManager.open("app42/images/icon.gif");
			Bitmap bitmap = BitmapFactory.decodeStream(istr);
			return bitmap;
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static void resetMsgCount()
	{
		msgCount=0;
	}
}
