package com.cse406.cw.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Month {

	private String id;
	private String text;
	
	public Month(LocalDateTime ld) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM");  
        this.id = ld.format(dtf);
        dtf = DateTimeFormatter.ofPattern("MMMM yyyy");
        this.text = ld.format(dtf);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
