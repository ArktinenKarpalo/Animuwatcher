package com.vatsul.awatcher.gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MalRow {
	
	private int malID;
	private SimpleStringProperty title;
	private SimpleIntegerProperty seenEps;
	private SimpleIntegerProperty totalEps;
	private SimpleIntegerProperty myScore;
	private SimpleStringProperty type;
	private int updatedDate;
	private long startDate;
	
	public MalRow(int malID, String title, long startDate, Integer seenEps, Integer totalEps,
			int myScore, String type, int updatedDate) {
		this.malID = malID;
		this.title = new SimpleStringProperty(title);
		this.seenEps = new SimpleIntegerProperty(seenEps);
		this.totalEps = new SimpleIntegerProperty(totalEps);
		this.myScore = new SimpleIntegerProperty(myScore);
		this.type = new SimpleStringProperty(type);
		this.updatedDate = updatedDate;
		this.startDate = startDate;
	}
	
	public int getMalID() {
		return malID;
	}
	
	public int getUpdatedDate() {
		return updatedDate;
	}
	
	public long getStartDate() {
		return startDate;
	}

	public SimpleStringProperty titleProperty() {
		return title;
	}
	
	public SimpleIntegerProperty seenEpsProperty() {
		return seenEps;
	}
	
	public SimpleIntegerProperty totalEpsProperty() {
		return totalEps;
	}
	
	public SimpleIntegerProperty myScoreProperty() {
		return myScore;
	}
	
	public SimpleStringProperty typeProperty() {
		return type;
	}
	
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	
	public void setTitle(String title) {
		this.title.set(title);
	}
	
	public void setSeenEps(int seenEps) {
		this.seenEps.set(seenEps);
	}
	
	public void setTotalEps(int totalEps) {
		this.totalEps.set(totalEps);
	}
	
	public void setMyScore(int myScore) {
		this.myScore.set(myScore);
	}
	
	public void setType(String type) {
		this.type.set(type);
	}
	
	public void setUpdatedDate(int updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	public void setMalID(int malID) {
		this.malID = malID;
	}
}
