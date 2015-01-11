package com.cardan.lab4_chatandroid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Terminal extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener{

	private GoogleApiClient mGoogleApiClient;
	private String loggedInName;
	String[] joinCreateRoom=null;
	private TextView txtViewOutput=null;
	private EditText editTxtCommands = null;
	private Button btnEnter=null;
	private String outputHistory="";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_terminal, container,false);
		txtViewOutput = (TextView) rootView.findViewById(R.id.txtViewOutput);
		editTxtCommands = (EditText) rootView.findViewById(R.id.editTxtCommands);
		btnEnter = (Button) rootView.findViewById(R.id.btnEnter);
		
		btnEnter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String command = editTxtCommands.getText().toString();
				if(command.charAt(0) == '/'){
					if(command.substring(1, 5).equalsIgnoreCase("join")){
						joinCreateRoom = new String[3];
						joinCreateRoom[0] = command.substring(6);
						joinCreateRoom[1] = loggedInName;
						joinCreateRoom[2] = "join";
						JoinCreateRoomAddToDb joinCreateRoomAddToDb = new JoinCreateRoomAddToDb();
						joinCreateRoomAddToDb.execute(joinCreateRoom);
					}
				}
				editTxtCommands.setText("");
				command="";
			}
		});
		
		mGoogleApiClient = new GoogleApiClient.Builder(rootView.getContext())
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .build();
		
		return rootView;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		mGoogleApiClient.connect();
	}
	
	@Override
	public void onStop() {
	    super.onStop();

	    if (mGoogleApiClient.isConnected()) {
	      mGoogleApiClient.disconnect();
	    }
	}
	
	
	private class JoinCreateRoomAddToDb extends AsyncTask<String, String, String>{
		
		protected String doInBackground(String... params) {
			 String result = "";
			 String roomName = params[0];
			 String username = params[1];
			 String createJoinState = params[2];
			 
			 ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
			 InputStream is = null;
			 nameValuePairs.add(new BasicNameValuePair("checkReq", "createJoinRoom"));
			 nameValuePairs.add(new BasicNameValuePair("roomName", roomName));
			 nameValuePairs.add(new BasicNameValuePair("username", username));
			 nameValuePairs.add(new BasicNameValuePair("createJoinState", createJoinState));
			 
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
			System.out.println("Result rooms: "+result);
			//Add user to a textfile or sqlite, save convo
			if(result.equalsIgnoreCase("roomNotExist")){
				System.out.println("Room not exist");
				return result;
			}else if(result.equalsIgnoreCase("Joining")){
				System.out.println("Joining");
				
				return result;
			}else{
				System.out.println("Adding room!");
				return result;
			}
			
		}

		protected void onPostExecute(String result) {
			if(result.equalsIgnoreCase("roomNotExist")){
				System.out.println("NO ROOM ADDED!!!!!!!!!!!!!!!!");
				new AlertDialog.Builder(getActivity())
				.setTitle("Create room")
				.setMessage("Room does not exist, want to create?")
				.setPositiveButton("Yes" ,new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						JoinCreateRoomAddToDb joinCreateRoomAddToDb = new JoinCreateRoomAddToDb();
						joinCreateRoom[2]="create";
						joinCreateRoomAddToDb.execute(joinCreateRoom);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Do nothing
					}
				}).show();
			}else if(result.equalsIgnoreCase("Joining")){
				txtViewOutput.setText("");
				outputHistory+="Room found, check 'ROOM' tab!\n";
				txtViewOutput.setText(outputHistory);
			}else{
				Toast.makeText(getActivity(), "Room created!", Toast.LENGTH_SHORT).show();
			}
			
		}
	}

	
	

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		loggedInName = Plus.AccountApi.getAccountName(mGoogleApiClient);
	}


	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
