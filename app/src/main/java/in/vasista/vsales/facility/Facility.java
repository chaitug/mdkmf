package in.vasista.vsales.facility;

public class Facility {
	String id;
	String name;
	String category;
	String ownerPhone="";
	String salesRep="";	
	String amRouteId;
	String pmRouteId;
	String latitude;
	String longitude;
	
	public Facility(String id, String name, String category, String ownerPhone, String salesRep,
			String amRouteId, String pmRouteId, String latitude, String longitude) {
		super();
		this.id = id;
		this.name = name;
		this.category = category;
		this.ownerPhone = ownerPhone;
		this.salesRep = salesRep;
		this.amRouteId = amRouteId;
		this.pmRouteId = pmRouteId;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getCategory() {
		return category;
	}
	public String getOwnerPhone() {
		return ownerPhone;
	}
	public String getSalesRep() {
		return salesRep;
	}	
	public String getAmRouteId() {
		return amRouteId;
	}
	public String getPmRouteId() {
		return pmRouteId;
	}
	
	@Override
	public String toString() {
		return id;
	}	
}
