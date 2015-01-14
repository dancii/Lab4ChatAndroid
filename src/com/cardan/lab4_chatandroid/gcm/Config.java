package com.cardan.lab4_chatandroid.gcm;

public interface Config {

	// used to share GCM regId with application server - using php app server
	//static final String APP_SERVER_URL = "http://192.168.1.116/gcm/gcm.php?shareRegId=1";

	// GCM server using java
	 static final String APP_SERVER_URL = "http://dancii.net:8080/GCM-App-Server/GCMNotification?shareRegId=1&fromWho=android";

	// Google Project Number
	static final String GOOGLE_PROJECT_ID = "520614404467";
	static final String MESSAGE_KEY = "message";

}