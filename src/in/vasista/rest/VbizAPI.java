package in.vasista.rest;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import in.vasista.vsales.catalog.Product;


public interface VbizAPI {
	
	@GET("/fetchMaterials/json")
	void fetchMaterials(Callback <List<Product>> callback);
}
