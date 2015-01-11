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
import com.cardan.lab4_chatandroid.model.Room;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RoomChatWindow extends Activity {

	private static String loggedInName = "";
	private static String roomName = "";
	private static TextView roomMessages = null;
	private static EditText editTxtmessage = null;
	private Button btnSend = null;
	private String message="";
	private ShareExternalServer appUtil;
	private static ArrayList<Room> allMessagesRoom = new ArrayList<Room>();
	private static String messageRoomHistory = "";
	
	private static int resumed = 0;
    private static int paused = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_chat_window);
		
		roomMessages = (TextView) findViewById(R.id.txtViewMessages);
		editTxtmessage = (EditText) findViewById(R.id.editTxtMessageRoom);
		btnSend = (Button) findViewById(R.id.btnSend);
		
		appUtil = new ShareExternalServer();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			loggedInName = extras.getString("fromContact");
			roomName = extras.getString("roomName");
		}
		
		updateRoomMessages();
		
		btnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				message = editTxtmessage.getText().toString();
				String[] saveRoomMessageParams={roomName,loggedInName,message};
				SaveRoomMessageToDb saveRoomMessageToDb = new SaveRoomMessageToDb();
				saveRoomMessageToDb.execute(saveRoomMessageParams);
				updateRoomMessages();
			}
		});
		
		
	}
	
	private class SaveRoomMessageToDb extends AsyncTask<String, String, String>{

		protected String doInBackground(String... params) {
			
			String roomName=params[0];
			String username=params[1];
			String messageToRoom=params[2];
            ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("checkReq", "sendMessageToRoom"));
            nameValuePairs.add(new BasicNameValuePair("roomName", roomName));
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("messageToRoom", messageToRoom));
            
            appUtil.shareRoomMessage(roomName, messageToRoom);
            
            try{
            	HttpClient httpClient=new DefaultHttpClient();
            	HttpPost httpPost=new HttpPost(Configuration.SERVER_URL);
            	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            	httpClient.execute(httpPost);
            }catch(Exception e){
            	
            }
			return null;
		}	
	}
	
	public static void updateRoomMessages(){
		GetAllRoomMessages getAllRoomMessages = new GetAllRoomMessages();
		getAllRoomMessages.execute(roomName);
	}
	
	
private static class GetAllRoomMessages extends AsyncTask<String, String, String>{
		
		protected String doInBackground(String... params) {
			 String result = "";
			 String roomName = params[0];
			 ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
			 InputStream is = null;
			 nameValuePairs.add(new BasicNameValuePair("checkReq", "getAllRoomMessages"));
			 nameValuePairs.add(new BasicNameValuePair("roomName", roomName));
			 try{
	            	HttpClient httpClient=new DefaultHttpClient();
	            	HttpPost httpPost = new HttpPost(Configuration.SERVER_URL);
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
			if(result.equalsIgnoreCase("noRoomMessages")){
				System.out.println("No room messages");
				return null;
			}else{
				System.out.println("Found room messages");
				return result;
			}
			
		}
		
		protected void onPostExecute(String gsonResult) {
			if(gsonResult == null){
				
			}else{
				allMessagesRoom.clear();
				messageRoomHistory = "";
				Gson gson = new Gson();
				allMessagesRoom=gson.fromJson(gsonResult, new TypeToken<Collection<Room>>(){}.getType());
				for (Room roomMessageUser : allMessagesRoom) {
					if(roomMessageUser.getRoomUsername().equalsIgnoreCase(loggedInName)){
						messageRoomHistory +="You: "+roomMessageUser.getMessage()+"\n";
					}else{
						messageRoomHistory += roomMessageUser.getRoomUsername()+": "+roomMessageUser.getMessage()+"\n";
					}
				}
				roomMessages.setText(messageRoomHistory);
				editTxtmessage.setText("");
			}
		}
	}

	@Override
	protected void onPause(){
		System.out.println("Paused: "+paused+" Resumed: "+resumed);
		super.onPause();
		++paused;
	}
	
	@Override
	protected void onResume(){
		System.out.println("Paused: "+paused+" Resumed: "+resumed);
		super.onResume();
		++resumed;
	}
	
	public static boolean isApplicationInForeground() {
		System.out.println("Paused: "+paused+" Resumed: "+resumed);
	    return resumed > paused;
	}


}
