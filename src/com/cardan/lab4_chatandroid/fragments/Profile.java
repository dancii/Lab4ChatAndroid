package com.cardan.lab4_chatandroid.fragments;

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

import com.cardan.lab4_chatandroid.ChatWindowActivity;
import com.cardan.lab4_chatandroid.Configuration;
import com.cardan.lab4_chatandroid.R;
import com.cardan.lab4_chatandroid.R.id;
import com.cardan.lab4_chatandroid.R.layout;
import com.cardan.lab4_chatandroid.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Profile extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener{

	private GoogleApiClient mGoogleApiClient;
	private TextView txtEmail = null;
	private ListView lstViewPendingFriends=null;
	private ArrayList<String> listItems=new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private String loggedInName;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_profile, container,false);
		
		txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
		lstViewPendingFriends = (ListView) rootView.findViewById(R.id.lstViewPendingFriends);
		
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,listItems);
		lstViewPendingFriends.setAdapter(adapter);
		lstViewPendingFriends.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				new AlertDialog.Builder(getActivity())
				.setTitle("Add contact")
				.setMessage("Contact's email")
				.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String parameters[] = {loggedInName, listItems.get(arg2), "accept"};
						AcceptOrDeclineFriendRequest acceptFriendRequest = new AcceptOrDeclineFriendRequest();
						acceptFriendRequest.execute(parameters);
						listItems.remove(arg2);
						adapter.notifyDataSetChanged();
					}
				}).setNeutralButton("Not now", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Do nothing
					}
				}).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String parameters[] = {loggedInName, listItems.get(arg2), "decline"};
						AcceptOrDeclineFriendRequest declineFriendRequest = new AcceptOrDeclineFriendRequest();
						declineFriendRequest.execute(parameters);
						listItems.remove(arg2);
						adapter.notifyDataSetChanged();
					}
				}).show();
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

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		loggedInName= Plus.AccountApi.getAccountName(mGoogleApiClient);
		GetAllFriendsRequest getAllFriendRequest = new GetAllFriendsRequest();
		getAllFriendRequest.execute(loggedInName);
		txtEmail.setText(loggedInName);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
private class GetAllFriendsRequest extends AsyncTask<String, String, String>{
		
		protected String doInBackground(String... params) {
			 String result = "";
			 String username = params[0];
			 ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
			 InputStream is = null;
			 nameValuePairs.add(new BasicNameValuePair("checkReq", "getPendingFriendRequest"));
			 nameValuePairs.add(new BasicNameValuePair("username", username));
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
			if(result.equalsIgnoreCase("notfound")){
				System.out.println("No pending requests");
				return null;
			}else{
				System.out.println("Found pending requests");
				return result;
			}
			
		}
		
		protected void onPostExecute(String gsonResult) {
			
			try{
				if(gsonResult != null){
					Gson gson = new Gson();
					ArrayList<String> gsonGetter = new ArrayList<String>();
					listItems.clear();
					gsonGetter = gson.fromJson(gsonResult, new TypeToken<Collection<String>>(){}.getType());
					for (String string : gsonGetter) {
						listItems.add(string);
					}
					adapter.notifyDataSetChanged();
				}
			}catch(JsonSyntaxException e){
				Toast.makeText(getActivity(), "Server is currently down", Toast.LENGTH_SHORT).show();
			}
			
		}

	}
	
private class AcceptOrDeclineFriendRequest extends AsyncTask<String, String, String>{
	
	protected String doInBackground(String... params) {
		 String result = "";
		 String username = params[0];
		 String fromEmail = params[1];
		 String acceptOrDecline = params[2];
		 ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
		 InputStream is = null;
		 if(acceptOrDecline.equalsIgnoreCase("accept")){
			 nameValuePairs.add(new BasicNameValuePair("checkReq", "acceptPendingFriend"));
			 result = "accept";
		 }else{
			 nameValuePairs.add(new BasicNameValuePair("checkReq", "declinePendingFriend"));
			 result = "decline";
		 }
		 nameValuePairs.add(new BasicNameValuePair("username", username));
		 nameValuePairs.add(new BasicNameValuePair("fromEmail", fromEmail));
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
		 return result;
	}
	
	protected void onPostExecute(String result) {
		if(result.equalsIgnoreCase("accept")){
			Toast.makeText(getActivity(), "Accepted friend", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(getActivity(), "Decliend friend", Toast.LENGTH_SHORT).show();
		}
	}

}

}
