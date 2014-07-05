package in.vasista.vsales;

import in.vasista.vsales.R; 
import android.app.Activity;  
import android.app.ProgressDialog;  
import android.content.Intent;
import android.os.AsyncTask;  
import android.os.Bundle;   
import android.view.View;
import android.widget.ProgressBar;

public class SplashScreenActivity extends Activity  
{  
	private ProgressBar progressBar;  

	/** Called when the activity is first created. */  
	@Override  
	public void onCreate(Bundle savedInstanceState)     
	{   
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_splash);
		//Initialize a LoadViewTask object and call the execute() method  
		new LoadViewTask().execute();         

	}  

	//To use the AsyncTask, it must be subclassed  
	private class LoadViewTask extends AsyncTask<Void, Integer, Void>  
	{  
		//Before running code in separate thread   
		@Override  
		protected void onPreExecute()  
		{  
			progressBar = (ProgressBar)findViewById(R.id.splashProgress);
			progressBar.setVisibility(View.VISIBLE);   
		}  

		//The code to be executed in a background thread.  
		@Override  
		protected Void doInBackground(Void... params)  
		{  
			/* This is just a code that delays the thread execution 4 times, 
			 * during 850 milliseconds and updates the current progress. This 
			 * is where the code that is going to be executed on a background 
			 * thread must be placed. 
			 */  
			try  
			{  
				//Get the current thread's token  
				synchronized (this)  
				{  
					//Initialize an integer (that will act as a counter) to zero  
					int counter = 0;  
					//While the counter is smaller than four  
					while(counter <= 4)  
					{  
						this.wait(500);  
						//Increment the counter  
						counter++;  

					}  
				}  
			}  
			catch (InterruptedException e)  
			{  
				e.printStackTrace();  
			}  
			return null;  
		}  

		//Update the progress  
		@Override  
		protected void onProgressUpdate(Integer... values)  
		{  

		}  

		//after executing the code in the thread  
		@Override  
		protected void onPostExecute(Void result)  
		{  
  
			progressBar.setVisibility(View.INVISIBLE);
			Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i); 
		}  
	}  
}  

