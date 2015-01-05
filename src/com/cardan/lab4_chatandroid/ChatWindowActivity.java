package com.cardan.lab4_chatandroid;

import com.cardan.lab4_chatandroid.gcm.ShareExternalServer;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChatWindowActivity extends Activity implements ActivityLifecycleCallbacks{

	private String contactName=null;
	private String contactChatId=null;
	private Button btnSend=null;
	private static TextView txtMessages=null;
	private EditText editTxtMessage=null;
	private String message="";
	private static String messageHistory="";
	private ShareExternalServer appUtil;
	private AsyncTask<Void, Void, String> shareRegidTask;
	
	private static int resumed = 0;
    private static int paused = 0;
    private static int started = 0;
    private static int stopped = 0;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_window);
		btnSend = (Button) findViewById(R.id.btnSend);
		txtMessages = (TextView) findViewById(R.id.txtViewMessages);
		editTxtMessage = (EditText) findViewById(R.id.edtxtMessage);
		appUtil = new ShareExternalServer();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			System.out.println("Bundle here!!!!!");
			contactName = extras.getString("contactName");
			contactChatId = extras.getString("contactChatId");
		}
		
		System.out.println("ContactId: "+contactChatId+" Contactname: "+contactName+" Message: "+message);
		btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				message +=editTxtMessage.getText().toString();
				messageHistory+="You: "+message+"\n";
				shareRegidTask = new AsyncTask<Void,Void,String>() {
					@Override
					protected String doInBackground(Void... params) {
						String result = appUtil.shareRegIdWithAppServer(getApplicationContext(), contactChatId, contactName,message);
						message = "";
						return result;
					}

					protected void onPostExecute(String result) {
						shareRegidTask = null;
						txtMessages.setText(messageHistory);
						Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
						editTxtMessage.setText("");
					}

				};
				shareRegidTask.execute(null, null, null);
			}
		});
		

	}
	public static Handler UIHandler;
	static{
		UIHandler = new Handler(Looper.getMainLooper());
	}
	
	public static void runOnUI(Runnable runnable){
		UIHandler.post(runnable);
	}
	
	public static void updateMessage(final String message){
		runOnUI(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				messageHistory+=message+"\n";
				txtMessages.setText(messageHistory);
			}
		});
		
	}
	
    // And these two public static functions
    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityPaused(Activity activity) {
		// TODO Auto-generated method stub
		++paused;
	}

	@Override
	public void onActivityResumed(Activity activity) {
		// TODO Auto-generated method stub
		++resumed;
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActivityStarted(Activity activity) {
		// TODO Auto-generated method stub
		++started;
	}

	@Override
	public void onActivityStopped(Activity activity) {
		// TODO Auto-generated method stub
		++stopped;
	}
}
