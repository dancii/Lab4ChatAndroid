package com.cardan.lab4_chatandroid.fragments;

import com.cardan.lab4_chatandroid.R;
import com.cardan.lab4_chatandroid.R.id;
import com.cardan.lab4_chatandroid.R.layout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Profile extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener{

	GoogleApiClient mGoogleApiClient;
	TextView txtEmail = null;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_profile, container,false);
		txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
		
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
		// TODO Auto-generated method stub
		txtEmail.setText(Plus.AccountApi.getAccountName(mGoogleApiClient));
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
