package in.vasista.vsales.sync.xmlrpc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class XMLRPCApacheAdapter {
	public static final String module = XMLRPCApacheAdapter.class.getName();
	private XmlRpcClient client;
	private Context context;
	private URL url; 
    private final Map<String, Object> credentialsMap;

	public XMLRPCApacheAdapter(Context context) throws MalformedURLException  {
		this.context = context;		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String urlStr = prefs.getString("serverURL", "");
		String userName = prefs.getString("userName", "");
		String password = prefs.getString("password", "");
		String tenantId = prefs.getString("tenantId", "");		
		Log.d( module, "serverURL=" + urlStr +"; userName=" + userName + "; password=" + password +
				";tenantid=" + tenantId); 
		credentialsMap = new HashMap<String, Object>();
		credentialsMap.put("login.username", userName);
		credentialsMap.put("login.password", password);
		credentialsMap.put("tenantId", tenantId);		
		
		this.url = new URL(urlStr);
	    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	    config.setServerURL(url);
	    config.setEnabledForExceptions(true);
	    config.setEnabledForExtensions(true);
	   
	    client = new XmlRpcClient();
	    client.setConfig(config);
	}
	
	public Object callSync(String method, Map<String, Object> params) {
		Map allParamsMap = new HashMap<String, Object>();
		allParamsMap.putAll(credentialsMap);
		if (params != null) {
			allParamsMap.putAll(params);
		}
	    Object[] allParams = {
	    		allParamsMap,
	    };	
	    Object result = null;
		try {
			long t0 = System.currentTimeMillis();
			result = client.execute(method, allParams);
			long t1 = System.currentTimeMillis();
			Log.d( module, "XML-RPC call took " + (t1-t0) + "ms");			
		} catch (final Exception e) {
			Log.e( module, "Error " + e, e);
			//Toast.makeText( context, "Remote call failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}
		return result;
	}
	
	public void call(String method, Map<String, Object> params, ProgressBar progressBar, XMLRPCMethodCallback callBack) {		
		Map allParamsMap = new HashMap<String, Object>();
		allParamsMap.putAll(credentialsMap);
		if (params != null) {
			allParamsMap.putAll(params);
		}
	    Object[] allParams = {
	    		allParamsMap,
	    };	
Log.d( module, "method=" + method); 	    
		XMLRPCMethod xmlRpcMethod = new XMLRPCMethod(context, method, allParams, progressBar, callBack);
		xmlRpcMethod.start();
	}

	class XMLRPCMethod extends Thread {
		private String method;
		private Object[] params;
		private ProgressBar progressBar;
		private Handler handler;
		private XMLRPCMethodCallback callBack;
		public XMLRPCMethod(Context context, String method, Object[] params, ProgressBar progressBar, XMLRPCMethodCallback callBack) {
			this.params = params;
			this.progressBar = progressBar;
			this.method = method;
			this.callBack = callBack;
			handler = new Handler();
		}   
		public void call() {   
			start();
		}

		@Override
		public void run() {
			try {
				final long t0 = System.currentTimeMillis();
				final Object result = client.execute(method, params);
				final long t1 = System.currentTimeMillis();
				handler.post(new Runnable() {
					public void run() { 
						Log.d( module, "XML-RPC call took " + (t1-t0) + "ms");
						callBack.callFinished(result, progressBar);
					}
				});
			} catch (final Exception e) { 
				handler.post(new Runnable() {
					public void run() {
						Log.e( module, "Error", e);
						if (progressBar != null) {
							progressBar.setVisibility(View.INVISIBLE);  
						} 
						Log.d( module,"Remote call failed: " + e);
						//Toast.makeText( context, "Remote call failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
					}
				});
			}
		}
	}
}
