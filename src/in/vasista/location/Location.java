package in.vasista.location;

import java.util.Date;

public class Location {
	int id;
	Date createdDate;
	double latitude;
	double longitude;	
	boolean isSynced;
	
	public Location (int id, Date createdDate, double latitude, double longitude, boolean isSynced) {
		this.id = id;
		this.createdDate = createdDate;
		this.latitude = latitude;
		this.longitude = longitude;
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
	public boolean isSynced() {
		return isSynced;
	}


}
