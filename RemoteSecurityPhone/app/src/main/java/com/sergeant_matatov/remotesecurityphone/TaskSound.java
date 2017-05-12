package com.sergeant_matatov.remotesecurityphone;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;

/**
 * Created by Yurka on 12.03.2016.
 */
public class TaskSound extends AsyncTask<Void, Void, Void> {
    public Context context;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //включение звука
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_RING, am.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}