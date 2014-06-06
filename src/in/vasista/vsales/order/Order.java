package in.vasista.vsales.order;

import java.util.Date;

public class Order {
	int id;
	Date supplyDate;
	String subscriptionType;	
	double total;
	
	public Order (int id, Date supplyDate, String subscriptionType, double total) {
		this.id = id;
		this.supplyDate = supplyDate;
		this.subscriptionType = subscriptionType;		
		this.total = total;
	}

	public Date getSupplyDate() {
		return supplyDate;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}
	
	public int getId() {
		return id;
	}
	
	public double getTotal() {
		return total;
	}
	
	public void setTotal(double total) {
		this.total = total;
	}	

	@Override
	public String toString() {
		return "Order [id=" + id + ", supplyDate=" + supplyDate + ", subscriptionType=" + subscriptionType
				+ ", total=" + total + "]";
	}
}
