package in.vasista.location;

import java.util.Date;

public class Location {
	int id;
	Date createdDate;
	double latitude;
	double longitude;	
	String noteName;
	String noteInfo;
	String address;
	boolean isSynced;
	
	public Location (int id, Date createdDate, double latitude, double longitude, String noteName,
			String noteInfo,String address, boolean isSynced) {
		this.id = id;
		this.createdDate = createdDate;
		this.latitude = latitude;
		this.longitude = longitude;
		this.noteName = noteName;
		this.noteInfo = noteInfo;
		this.address = address;
		this.isSynced = isSynced;
	}
	
	public int getId() {
		return id;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public double getLatitude() {  
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public String getNoteName() {
		return noteName;
	}
	public String getNoteInfo() {
		return noteInfo;
	}
	public String getAddress() {
		return address;
	}
	public boolean isSynced() {
		return isSynced;
	}


}
