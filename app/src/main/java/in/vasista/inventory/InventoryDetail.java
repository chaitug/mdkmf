package in.vasista.inventory;

import java.math.BigDecimal;

public class InventoryDetail {
	String productId;
	String name;
	String description;
	String categoryId;	
	String uom;
	String specification;
	String supplierId;
	String supplierName;
	BigDecimal supplierRate;
	String lastSupplyDate;	
	BigDecimal inventoryCount;
	BigDecimal inventoryCost;
	public String getProductId() {
		return productId;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getCategoryId() { 
		return categoryId;
	}	
	public String getUom() {
		return uom;
	}
	public String getSpecification() {
		return specification;
	}
	public String getSupplierId() {
		return supplierId;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public BigDecimal getSupplierRate() {
		return supplierRate;
	}
	public String getLastSupplyDate() {
		return lastSupplyDate;
	}	
	public BigDecimal getInventoryCount() {
		return inventoryCount;
	}
	public BigDecimal getInventoryCost() {
		return inventoryCost;
	}	

	public String toString() {
		return productId + " " + name + " " + description + " " + uom + " " + inventoryCount + " " + inventoryCost;
	}
}
