package in.vasista.rest;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import in.vasista.inventory.InventoryDetail;
import in.vasista.vsales.catalog.Product;


public interface VbizAPI {
	
	@GET("/materialmgmt/fetchMaterials")
	List<Product> fetchMaterials();
	
	@GET("/materialmgmt/fetchMaterialInventory")
	void fetchMaterialInventory(@Query("productId") String productId, Callback <InventoryDetail> callback);	
}
