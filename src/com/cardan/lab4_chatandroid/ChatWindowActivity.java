package com.cardan.lab4_chatandroid;

import com.cardan.lab4_chatandroid.gcm.ShareExternalServer;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChatWindowActivity extends Activity {

	private String contactName=null;
	private String contactChatId=null;
	private Button btnSend=null;
	private static TextView txtMessages=null;
	private EditText editTxtMessage=null;
	private String message="";
	private static String messageHistory="";
	private ShareExternalServer appUtil;
	private AsyncTask<Void, Void, String> shareRegidTask;
	
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
						System.out.println("HERLOOOOO  ContactId: "+contactChatId+" Contactname: "+contactName+" Message: "+message);
						String result = appUtil.shareRegIdWithAppServer(getApplicationContext(), contactChatId, contactName,message);
						return result;
					}

					protected void onPostExecute(String result) {
						shareRegidTask = null;
						System.out.println("HERLOOOOO  ContactId: "+contactChatId+" Contactname: "+contactName+" Message: "+message);
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
}
