package com.turksat.notepadservlet.model;

import java.util.List;

public class User {

	private int errorCode;
	private String email;
	private String name;
	private String surname;
	private String password;
	private List notes;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List getNotes() {
		return notes;
	}

	public void setNotes(List notes) {
		this.notes = notes;
	}
}
