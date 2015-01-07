package com.cardan.lab4_chatandroid;

public class User {

	private int id;
	private String email;
	private String androidChatId;
	
	public User(int id, String email, String androidChatId){
		this.id=id;
		this.email=email;
		this.androidChatId=androidChatId;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAndroidChatId() {
		return androidChatId;
	}

	public void setAndroidChatId(String androidChatId) {
		this.androidChatId = androidChatId;
	}
	
	
	
}
