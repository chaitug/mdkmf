package in.vasista.vsales.indent;

public class IndentItem {
	String productId;
	String productName;
	int qty;


	double unitPrice;
	
	public IndentItem(String productId, String productName, int qty, double unitPrice) {
		this.productId = productId;
		this.productName = productName;		
		this.qty = qty;
		this.unitPrice = unitPrice;
	}

	public String getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
	}
	
	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public double getUnitPrice() {
		return unitPrice;
	}

	@Override
	public String toString() {
		return "OrderItem [productId=" + productId + ", qty=" + qty
				+ ", unitPrice=" + unitPrice + "]";
	}
	
}
