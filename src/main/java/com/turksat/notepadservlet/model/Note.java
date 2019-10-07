package com.turksat.notepadservlet.model;

import java.sql.Timestamp;

public class Note {

	private String id;
	private String user_email;
	private String subject;
	private String text;
	private int priority;
	private Timestamp reminder;
	private String[] coordinate;
	private byte[] image;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Timestamp getReminder() {
		return reminder;
	}

	public void setReminder(Timestamp reminder) {
		this.reminder = reminder;
	}

	public String[] getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(String[] coordinate) {
		this.coordinate = coordinate;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
}
