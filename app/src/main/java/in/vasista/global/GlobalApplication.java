package in.vasista.global;

import android.app.Application;

/**
 * Created by vasista on 29/1/16.
 */
public class GlobalApplication extends Application {
    boolean prefChange;
    public boolean isPrefChange() {
        return prefChange;
    }

    public void setPrefChange(boolean prefChange) {
        this.prefChange = prefChange;
    }

}
