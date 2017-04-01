package com.appcrops.networksample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
    private String TAG = getClass().getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataModel.instance().getChannelData(new DataModel.DataModelCallback() {
            @Override
            public void onResponse(final DataModel.Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response != null) {
                            Log.d(TAG, "DataModel.instance().getChannelData- onResponse: Type" + response.responseType);
                            Log.d(TAG, "DataModel.instance().getChannelData- onResponse: Data" + response.data);
                        }
                    }
                });
            }
        });


        AsyncDataModel.instance().getChannelData(new AsyncDataModel.DataModelCallback() {
            @Override
            public void onResponse(AsyncDataModel.Response response) {
                if (response != null) {
                    Log.d(TAG, "DataModel.instance().getChannelData- onResponse: Type" + response.responseType);
                    Log.d(TAG, "DataModel.instance().getChannelData- onResponse: Data" + response.data);
                }
            }
        });
    }
}
