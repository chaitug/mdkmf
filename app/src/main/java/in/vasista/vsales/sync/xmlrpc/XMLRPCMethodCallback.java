package in.vasista.vsales.sync.xmlrpc;

import android.widget.ProgressBar;

public interface XMLRPCMethodCallback {
	void callFinished(Object result, ProgressBar progressBar);

}
