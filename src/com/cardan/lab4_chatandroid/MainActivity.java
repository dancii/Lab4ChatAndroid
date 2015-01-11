/*
 * 
 * Authors: Pontus Carlsson, Danijel Kljakic
 * Jan 2015
 * 
 */


package com.cardan.lab4_chatandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks, com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener{

	private static final int RC_SIGN_IN = 0;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;
	private SignInButton btnSignIn;
	private Authenticate auth = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnSignIn = (SignInButton) findViewById(R.id.sign_in_button);
		
		buttonListeners();
		
		//Connects to googles + service
		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .build();
		
		
	}
	
	private void buttonListeners(){
		btnSignIn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()) {
					mSignInClicked = true;
					resolveSignInError();
				}
			}
		});
		
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
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//G+ method
	private void resolveSignInError() {
		  if (mConnectionResult.hasResolution()) {
		    try {
		      mIntentInProgress = true;
		      startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
		          RC_SIGN_IN, null, 0, 0, 0);
		    } catch (SendIntentException e) {
		      // The intent was canceled before it was sent.  Return to the default
		      // state and attempt to connect to get an updated ConnectionResult.
		      mIntentInProgress = false;
		      mGoogleApiClient.connect();
		    }
		  }
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
		if (!result.hasResolution()) {
	        GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
	        return;
	    }
		// TODO Auto-generated method stub
		if (!mIntentInProgress) {
		    // Store the ConnectionResult so that we can use it later when the user clicks
		    // 'sign-in'.
		    mConnectionResult = result;

		    if (mSignInClicked) {
		      // The user has already clicked 'sign-in' so we attempt to resolve all
		      // errors until the user is signed in, or they cancel.
		      resolveSignInError();
		    }
		  }
	}
	
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		 if (requestCode == RC_SIGN_IN) {
			    if (responseCode != RESULT_OK) {
			      mSignInClicked = false;
			    }

			    mIntentInProgress = false;

			    if (!mGoogleApiClient.isConnecting()) {
			      mGoogleApiClient.connect();
			    }
		 }
	}
	
	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		Authenticate instance = Authenticate.getInstance();
		instance.setmGoogleApiClient(mGoogleApiClient);
		Intent i = new Intent(this, MenuActivity.class);
		
		startActivity(i);
		finish();
	}


	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
	}
}
