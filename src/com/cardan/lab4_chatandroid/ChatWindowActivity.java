package com.cardan.lab4_chatandroid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.cardan.lab4_chatandroid.gcm.ShareExternalServer;
import com.cardan.lab4_chatandroid.model.Message;
import com.cardan.lab4_chatandroid.model.User;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChatWindowActivity extends Activity implements ActivityLifecycleCallbacks{

	private static String fromEmail=null;
	private static String contactName=null;
	private String contactChatId=null;
	private Button btnSend=null;
	private static TextView txtMessages=null;
	private EditText editTxtMessage=null;
	private String message="";
	private static String messageHistory="";
	private ShareExternalServer appUtil;
	private AsyncTask<Void, Void, String> shareRegidTask;
	private static ArrayList<Message> allMessages = new ArrayList<Message>();
	
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
			fromEmail = extras.getString("fromContact");
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
					// TA BORT messageHistory härifrån, ny asynctask, skicka message till server.
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
	
	
	public static void updateMessages(){
		GetAllMessages getAllMessage = new GetAllMessages();
		String[] toFromEmail = {fromEmail, contactName};
		getAllMessage.execute(toFromEmail);
	}
	
private static class GetAllMessages extends AsyncTask<String, String, String>{
		
		protected String doInBackground(String... params) {
			String result ="";
			 String fromEmail = params[0];
			 String toEmail = params[1];
			 ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
			 InputStream is = null;
			 nameValuePairs.add(new BasicNameValuePair("checkReq", "getAllMessages"));
			 nameValuePairs.add(new BasicNameValuePair("fromUsername", fromEmail));
			 nameValuePairs.add(new BasicNameValuePair("toUsername", toEmail));
			 
			 try{
	            	HttpClient httpClient=new DefaultHttpClient();
	            	HttpPost httpPost = new HttpPost("http://dancii.net:8080/GCM-App-Server/AuthServlet");
	            	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            	HttpResponse response=httpClient.execute(httpPost);
	            	HttpEntity entity=response.getEntity();
	            	is=entity.getContent();
	         }catch(Exception e){
	            	/*Log.e("log_tag", "Error in http connection "+e.toString());
	            	MainActivity.this.runOnUiThread(new Runnable() {
	                    public void run() {
	                    	Toast.makeText(MainActivity.this, "Server is currently down", Toast.LENGTH_LONG).show();
	                    }
	                });*/
	         }
			 
			 try{
		        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		        StringBuilder sb = new StringBuilder();
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		                sb.append(line);
		        }
		        is.close();
		 
		        result=sb.toString();
			}catch(Exception e){
				Log.e("log_tag", "Error converting result "+e.toString());
			}
			System.out.println("Result: "+result);
			//Add user to a textfile or sqlite, save convo
			if(result.equalsIgnoreCase("notfound")){
				System.out.println("No messages");
				return null;
			}else{
				return result;
			}
			
		}

		protected void onPostExecute(String result) {
			allMessages.clear();
			messageHistory = "";
			Gson gson = new Gson();
			allMessages = gson.fromJson(result, new TypeToken<Collection<Message>>(){}.getType());
			for (Message message : allMessages) {
				if(message.getFromEmail().equalsIgnoreCase(fromEmail)){
					messageHistory+="You: "+message.getMessage()+"\n";
				}else{
					messageHistory+=message.getToEmail()+": "+message.getMessage()+"\n";
				}
			}
			updateMessage(messageHistory);
		}
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
				txtMessages.setText(message);
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
