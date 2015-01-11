package com.cardan.lab4_chatandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cardan.lab4_chatandroid.fragments.ActiveChats;
import com.cardan.lab4_chatandroid.fragments.Profile;
import com.cardan.lab4_chatandroid.fragments.Rooms;
import com.cardan.lab4_chatandroid.gcm.Config;
import com.cardan.lab4_chatandroid.gcm.RegisterActivity;
import com.google.android.gms.analytics.ac;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v13.app.FragmentPagerAdapter;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Contactables;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MenuActivity extends FragmentActivity implements ActionBar.TabListener, ConnectionCallbacks, OnConnectionFailedListener{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	private GoogleApiClient mGoogleApiClient;
	private GoogleCloudMessaging gcm;
	private Context context;
	private String regId;
	
	private ActiveChats activeChats = null;
	
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	static final String TAG = "Register Activity";

	String addedContact = null;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .build();
		
		regId = registerGCM();
		Log.d("RegisterActivity", "GCM RegId: " + regId);
		
		if (TextUtils.isEmpty(regId)) {
			Toast.makeText(getApplicationContext(), "RegId is empty!",
					Toast.LENGTH_LONG).show();
		} else {
			/*Intent i = new Intent(getApplicationContext(),
					RegisterActivity.class);
			i.putExtra("regId", regId);
			Log.d("RegisterActivity",
					"onClick of Share: Before starting main activity.");
			startActivity(i);
			//getActivity().finish();
			Log.d("RegisterActivity", "onClick of Share: After finish.");*/
			
			System.out.println("REG ID: "+regId);
		}
	}

	protected void onStart(){
		super.onStart();
		mGoogleApiClient.connect();
	}
	
	protected void onStop() {
	    super.onStop();

	    if (mGoogleApiClient.isConnected()) {
	      mGoogleApiClient.disconnect();
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			if (mGoogleApiClient.isConnected()) {
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				mGoogleApiClient.disconnect();
				mGoogleApiClient.connect();
				Toast.makeText(getApplicationContext(), "User is disconnected!", Toast.LENGTH_LONG).show();
				Intent i = new Intent(this, MainActivity.class);
				startActivity(i);
				finish();
		    }
			return true;
		}else if(id == R.id.addUser){
			final EditText input = new EditText(this);
			new AlertDialog.Builder(this)
			.setTitle("Add contact")
			.setMessage("Contact's email")
			.setView(input)
			.setPositiveButton("Add", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					AddConvoToDB addConvoToDb = new AddConvoToDB();
					addedContact = input.getText().toString();
					if(isValidEmail(addedContact)){
						String fromUsername = Plus.AccountApi.getAccountName(mGoogleApiClient);
						String[] usernames = {fromUsername,addedContact};
						addConvoToDb.execute(usernames);
					}else{
						Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
					}
					
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Do nothing
				}
			}).show();
		}
		return super.onOptionsItemSelected(item);
	}

	public final static boolean isValidEmail(CharSequence target) {
	    if (target == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}
	
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch(position){
			case 0:
				activeChats = new ActiveChats();
				return activeChats;
			case 1:
				return new Rooms();
			case 2:
				return new Profile();
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
	
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
			return rootView;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		AddUserToDb addUserToDb = new AddUserToDb();
		addUserToDb.execute(Plus.AccountApi.getAccountName(mGoogleApiClient));

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private class AddUserToDb extends AsyncTask<String, String, String>{

		protected String doInBackground(String... cords) {
			
			String username=cords[0];
            ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("checkReq", "registerAccAndroid"));
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("androidChatId", regId));
            
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

private class AddConvoToDB extends AsyncTask<String, String, String>{
		
		protected String doInBackground(String... params) {
			 String result = "";
			 String fromUsername = params[0];
			 String toUsername = params[1];
			 ArrayList<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>();
			 InputStream is = null;
			 nameValuePairs.add(new BasicNameValuePair("checkReq", "registerConvo"));
			 nameValuePairs.add(new BasicNameValuePair("fromUsername", fromUsername));
			 nameValuePairs.add(new BasicNameValuePair("toUsername", toUsername));
			 
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
			if(result.equalsIgnoreCase("error")){
				System.out.println("No such user");
				return null;
			}else{
				System.out.println("User added!");
				return result;
			}
			
		}

		protected void onPostExecute(String result) {
			if(result == null){
				System.out.println("NO PERSON ADDED!!!!!!!!!!!!!!!!");
				new AlertDialog.Builder(MenuActivity.this)
				.setTitle("Add contact error")
				.setMessage("You can not add yourself, add a person you already are in a conversation in or you added a person that does not exist")
				.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Do nothing
					}
				}).show();
			}else{
				Toast.makeText(getApplicationContext(), "Person added", Toast.LENGTH_SHORT).show();
				activeChats.getAllConvos(Plus.AccountApi.getAccountName(mGoogleApiClient));
			}
			
		}
	}
	
	/*
	 * GCM STUFF
	 */
	
	
	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			Toast.makeText(getApplicationContext(),
					"RegId already available. RegId: " + regId,
					Toast.LENGTH_LONG).show();
		}
		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(RegisterActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
//		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
//		int currentVersion = getAppVersion(context);
//		if (registeredVersion != currentVersion) {
//			Log.i(TAG, "App version changed.");
//			return "";
//		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity",
					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			protected void onPostExecute(String msg) {
				Toast.makeText(getApplicationContext(),
						"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
						.show();
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(RegisterActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}
	
}
