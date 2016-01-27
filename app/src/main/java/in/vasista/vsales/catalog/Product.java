package in.vasista.vsales.catalog;

public class Product {
	String id;
	String name = "";
	String description = "";
	float price;
	float mrpPrice;	
	int sequenceNum;


	String productCategoryId = "";
	boolean trackInventory;
	boolean trackSales;
	
	
	public Product(String id, String name, String description, int sequenceNum, float price, float mrpPrice,
			String productCategoryId, boolean trackInventory, boolean trackSales) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.sequenceNum = sequenceNum;
		this.price = price;
		this.mrpPrice = mrpPrice;
		this.productCategoryId = productCategoryId;
		this.trackInventory = trackInventory;
		this.trackSales = trackSales;
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return (name == null) ? "" : name;
	}
	public String getDescription() {
		return (description == null) ? "" : description;
	}
	
	public int getSequenceNum() {
		return sequenceNum;
	}
	
	public float getPrice() {
		return price;
	}

	public float getMrpPrice() {
		return mrpPrice;
	}
	
	public String getProductCategoryId() {
		return (productCategoryId == null) ? "" : productCategoryId;
	}

	public boolean isTrackInventory() {
		return trackInventory;
	}

	public boolean isTrackSales() {
		return trackSales;
	}	
	
	public String toString() {
		return id + " " + name + " " + description + " " + price + " " + mrpPrice;
	}
}
