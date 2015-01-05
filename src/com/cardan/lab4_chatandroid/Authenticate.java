package com.cardan.lab4_chatandroid;

import com.google.android.gms.common.api.GoogleApiClient;

public class Authenticate {
	
	private GoogleApiClient mGoogleApiClient;
	private static Authenticate instance;
	
	private Authenticate(){}

	public static Authenticate getInstance(){
		if(instance == null){
			System.out.println("Null instanceeeee");
			instance = new Authenticate();
		}
		return instance;
	}
	
	public GoogleApiClient getmGoogleApiClient() {
		return mGoogleApiClient;
	}

	public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
		this.mGoogleApiClient = mGoogleApiClient;
	}
	
	
	
}
