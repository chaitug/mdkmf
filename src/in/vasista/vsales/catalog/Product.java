package in.vasista.vsales.catalog;

public class Product {
	String id;
	String name;
	String description;
	float price;
	int sequenceNum;
	
	public Product(String id, String name, String description, int sequenceNum, float price) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.sequenceNum = sequenceNum;
		this.price = price;
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	
	public int getSequenceNum() {
		return sequenceNum;
	}
	
	public float getPrice() {
		return price;
	}
	
	public String toString() {
		return id + " " + name;
	}
}
