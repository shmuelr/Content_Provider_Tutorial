package com.shmuelrosansky.contentprovidertutorial;

import android.app.Application;
import android.util.Log;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by user on 11/1/15.
 */
public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "Creating calligraphy");
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/custom_font.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
