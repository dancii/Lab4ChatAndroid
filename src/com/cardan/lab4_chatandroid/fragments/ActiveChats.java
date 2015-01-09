package com.cardan.lab4_chatandroid.fragments;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.cardan.lab4_chatandroid.ChatWindowActivity;
import com.cardan.lab4_chatandroid.R;
import com.cardan.lab4_chatandroid.model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ActiveChats extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener{
	
	private ArrayList<String> listItems=new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	ListView list=null;
	private String contactChatId = null;
	private GoogleApiClient mGoogleApiClient;
	private ArrayList<User> allActiveChats = new ArrayList<User>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_active_chats, container,false);
		
		
		
		list = (ListView) rootView.findViewById(R.id.listConvos);
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,listItems);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity(),ChatWindowActivity.class);
				i.putExtra("fromContact", Plus.AccountApi.getAccountName(mGoogleApiClient));
				i.putExtra("contactName", allActiveChats.get(arg2).getEmail());
				i.putExtra("contactChatId", allActiveChats.get(arg2).getAndroidChatId());
				startActivity(i);
			}
		});
		
		mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .build();
		return rootView;
	}
	
	public void setContact(String contact, String contactChatId){
		listItems.add(contact);
		this.contactChatId=contactChatId;
		adapter.notifyDataSetChanged();
	}
	
	public void getAllConvos(String fromUsername){
		GetAllConvos getAllConv = new GetAllConvos();
		getAllConv.execute(fromUsername);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		mGoogleApiClient.connect();
	}
	
	
	
private class GetAllConvos extends AsyncTask<String, String, String>{
		
		protected String doInBackground(String... params) {
			 String result = "";
			 String username = params[0];
			 ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
			 InputStream is = null;
			 nameValuePairs.add(new BasicNameValuePair("checkReq", "getAllConvos"));
			 nameValuePairs.add(new BasicNameValuePair("username", username));
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
				System.out.println("No convos added");
				return null;
			}else{
				System.out.println("Convos added!");
				return result;
			}
			
		}
		
		protected void onPostExecute(String gsonResult) {
			
			try{
				if(gsonResult != null){
					Gson gson = new Gson();
					allActiveChats.clear();
					listItems.clear();
					allActiveChats = gson.fromJson(gsonResult, new TypeToken<Collection<User>>(){}.getType());
					for (User usernames : allActiveChats) {
						listItems.add(usernames.getEmail());
					}
					adapter.notifyDataSetChanged();
				}
			}catch(JsonSyntaxException e){
				Toast.makeText(getActivity(), "Server is currently down", Toast.LENGTH_SHORT).show();
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
		System.out.println("Adding convos");
		getAllConvos(Plus.AccountApi.getAccountName(mGoogleApiClient));
	}
	
	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
