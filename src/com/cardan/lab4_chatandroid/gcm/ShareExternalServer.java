package com.cardan.lab4_chatandroid.gcm;

import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

public class ShareExternalServer {

	//Sends notification when sending messages between two parties.
	
	public String shareRegIdWithAppServer(final Context context, final String regId, final String toEmail, final String fromEmail, final String message) {

		String result = "";
		HttpClient httpClient = null;
		HttpResponse response = null;
        ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("messageType", "convo"));
        nameValuePairs.add(new BasicNameValuePair("regId", regId));
        nameValuePairs.add(new BasicNameValuePair("toEmail", toEmail));
        nameValuePairs.add(new BasicNameValuePair("fromEmail", fromEmail));
        nameValuePairs.add(new BasicNameValuePair("message", message));
        
        try{
        	httpClient=new DefaultHttpClient();
        	HttpPost httpPost=new HttpPost(Config.APP_SERVER_URL);
        	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	response = httpClient.execute(httpPost);
        }catch(Exception e){
        	Log.e("AppUtil", "Error in sharing with App Server: " + e);
        }


		return result;
	}
	
	//Shares the mesage in a room
	public String shareRoomMessage(String roomName, String roomMessage, String fromEmail){
		String result = "";
		HttpClient httpClient = null;
		HttpResponse response = null;
        ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("messageType", "room")); // Check message type room or convo
        nameValuePairs.add(new BasicNameValuePair("roomName", roomName));
        nameValuePairs.add(new BasicNameValuePair("roomMessage", roomMessage));
        nameValuePairs.add(new BasicNameValuePair("fromEmail", fromEmail));
        
        try{
        	httpClient=new DefaultHttpClient();
        	HttpPost httpPost=new HttpPost(Config.APP_SERVER_URL);
        	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	response = httpClient.execute(httpPost);
        }catch(Exception e){
        	Log.e("AppUtil", "Error in sharing with App Server: " + e);
        }
        
		return result;
	}
}