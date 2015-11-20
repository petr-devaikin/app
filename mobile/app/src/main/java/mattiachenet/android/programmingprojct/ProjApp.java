package mattiachenet.android.programmingprojct;

import android.app.Application;
import android.graphics.Color;

/**
 * Created by mattiachenet on 31/10/15.
 */
public class ProjApp extends Application {
    private String selectedColor;

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String color) {
        this.selectedColor = color;
    }

    @Override
    public void onLowMemory() {
        Runtime.getRuntime().gc();
    }
}
