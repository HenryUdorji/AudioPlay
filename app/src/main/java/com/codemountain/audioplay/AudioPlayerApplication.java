package com.codemountain.audioplay;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.codemountain.audioplay.utils.Extras;

import static com.codemountain.audioplay.utils.Constants.SAVE_EQ;
import static com.codemountain.audioplay.utils.Constants.TAG_METADATA;

public class AudioPlayerApplication extends Application {
    private static SharedPreferences mPreferences, metaData, eqPref;

    private AudioPlayerApplication instance;
    private Context mContext;

    public static AudioPlayerApplication get(Activity activity) {
        return (AudioPlayerApplication) activity.getApplication();
    }

    public static SharedPreferences getmPreferences() {
        return mPreferences;
    }

    public static SharedPreferences getMetaData() {
        return metaData;
    }

    public static SharedPreferences getEqPref() {
        return eqPref;
    }

    public Context getContext() {
        return mContext;
    }

    public AudioPlayerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        instance = this;

        Extras.init();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        metaData = mContext.getSharedPreferences(TAG_METADATA, MODE_PRIVATE);
        eqPref = mContext.getSharedPreferences(SAVE_EQ, MODE_PRIVATE);
        Extras.getInstance().setWidgetPosition(100);
        //Extras.getInstance().eqSwitch(false);

    }


}
