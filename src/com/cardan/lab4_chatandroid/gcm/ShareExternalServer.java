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

	public String shareRegIdWithAppServer(final Context context, final String regId, final String sendFromUsername, final String message) {

		String result = "";
		HttpClient httpClient = null;
		HttpResponse response = null;
        ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("regId", regId));
        nameValuePairs.add(new BasicNameValuePair("username", sendFromUsername));
        nameValuePairs.add(new BasicNameValuePair("message", message));
        
        try{
        	httpClient=new DefaultHttpClient();
        	HttpPost httpPost=new HttpPost(Config.APP_SERVER_URL);
        	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	response = httpClient.execute(httpPost);
        }catch(Exception e){
        	Log.e("AppUtil", "Error in sharing with App Server: " + e);
        }
        int status = response.getStatusLine().getStatusCode();
		if (status == 200) {
			result = "RegId shared with Application Server. RegId: " + regId;
		} else {
			result = "Post Failure." + " Status: " + status;
		}
        
		
		/*Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("regId", regId);
		try {
			URL serverUrl = null;
			try {
				serverUrl = new URL(Config.APP_SERVER_URL);
			} catch (MalformedURLException e) {
				Log.e("AppUtil", "URL Connection Error: "
						+ Config.APP_SERVER_URL, e);
				result = "Invalid URL: " + Config.APP_SERVER_URL;
			}

			StringBuilder postBody = new StringBuilder();
			Iterator<Entry<String, String>> iterator = paramsMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry param = iterator.next();
				postBody.append(param.getKey()).append('=')
						.append(param.getValue());
				if (iterator.hasNext()) {
					postBody.append('&');
				}
			}
			String body = postBody.toString();
			byte[] bytes = body.getBytes();
			HttpURLConnection httpCon = null;
			try {
				httpCon = (HttpURLConnection) serverUrl.openConnection();
				httpCon.setDoOutput(true);
				httpCon.setUseCaches(false);
				httpCon.setFixedLengthStreamingMode(bytes.length);
				httpCon.setRequestMethod("POST");
				httpCon.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded;charset=UTF-8");
				OutputStream out = httpCon.getOutputStream();
				out.write(bytes);
				out.close();

				int status = httpCon.getResponseCode();
				if (status == 200) {
					result = "RegId shared with Application Server. RegId: "
							+ regId;
				} else {
					result = "Post Failure." + " Status: " + status;
				}
			} finally {
				if (httpCon != null) {
					httpCon.disconnect();
				}
			}

		} catch (IOException e) {
			result = "Post Failure. Error in sharing with App Server.";
			Log.e("AppUtil", "Error in sharing with App Server: " + e);
		}*/
		return result;
	}
}