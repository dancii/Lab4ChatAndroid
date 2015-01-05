package com.cardan.lab4_chatandroid.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cardan.lab4_chatandroid.ChatWindowActivity;
import com.cardan.lab4_chatandroid.MenuActivity;
import com.cardan.lab4_chatandroid.R;
import com.cardan.lab4_chatandroid.fragments.ActiveChats;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				if(ChatWindowActivity.isApplicationInForeground()){
					System.out.println("In foreground!!!!!!!!!!!!!");
					ChatWindowActivity.updateMessage(extras.get(Config.MESSAGE_KEY).toString());
				}else{
					System.out.println("In BACKGROUND!!!!");
					sendNotification("Message Received from Google GCM Server: " + extras.get(Config.MESSAGE_KEY));
				}
				
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MenuActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("GCM Notification")
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		Log.d(TAG, "Notification sent successfully.");
	}
}