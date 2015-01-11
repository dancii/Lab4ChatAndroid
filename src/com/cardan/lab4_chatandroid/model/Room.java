package com.cardan.lab4_chatandroid.model;

public class Room {

	private int id;
	private String roomName;
	private String roomUsername;
	private String message;
	
	public Room(String roomUsername, String roomMessage){
		this.roomUsername=roomUsername;
		this.message=roomMessage;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	public String getRoomUsername() {
		return roomUsername;
	}

	public void setRoomUsername(String roomUsername) {
		this.roomUsername = roomUsername;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
