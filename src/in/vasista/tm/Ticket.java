package in.vasista.tm;

import java.util.Date;

public class Ticket {
	int internalId;
	String id;
	Date createdDate;
	double latitude;
	double longitude;	
	String desc;
	String type;
	String status;	
	boolean isSynced;
	
	public Ticket (int internalId, String id, Date createdDate, double latitude, double longitude, String desc,
			String type, String status, boolean isSynced) {
		this.internalId = internalId;
		this.id = id;
		this.createdDate = createdDate;
		this.latitude = latitude;
		this.longitude = longitude;
		this.desc = desc;
		this.type = type;
		this.status = status;		
		this.isSynced = isSynced;
	}	
	
	public int getInternalId() {
		return internalId;
	}
	public String getId() {
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
	public String getDesc() {
		return desc;
	}
	public String getType() {
		return type;
	}
	public String getStatus() {
		return status;
	}
	public boolean isSynced() {
		return isSynced;
	}
	
	
}
