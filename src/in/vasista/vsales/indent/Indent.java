package in.vasista.vsales.indent;

import java.util.Date;

public class Indent {
	int id;
	Date createdDate;
	Date supplyDate;
	String subscriptionType;	
	String status;
	boolean isSynced;
	double total;
	
	public Indent (int id, Date createdDate, Date supplyDate, String subscriptionType, String status, boolean isSynced, double total) {
		this.id = id;
		this.supplyDate = supplyDate;
		this.subscriptionType = subscriptionType;		
		this.createdDate = createdDate;
		this.status = status;
		this.isSynced = isSynced;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getStatus() {
		return status;
	}
	
	public boolean isSynced() {
		return isSynced;
	}

	public void setSynced(boolean isSynced) {
		this.isSynced = isSynced;
	}
	
	public double getTotal() {
		return total;
	}
	
	public void setTotal(double total) {
		this.total = total;
	}	

	@Override
	public String toString() {
		return "Order [id=" + id + ", createDate=" + createdDate + ", status=" + status
				+ ", total=" + total + "]";
	}
}
