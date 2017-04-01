package com.appcrops.networksample;

import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by mraavi on 01/04/17.
 */

public class AsyncDataModel {

    private String TAG = getClass().getName();

    private static AsyncDataModel asyncDataModel = null;
    private AsyncDataModel() {

    }

    public static AsyncDataModel instance() {
        if (asyncDataModel == null) {
            asyncDataModel = new AsyncDataModel();
        }
        return asyncDataModel;
    }

    public void getChannelData(final DataModelCallback dataModelCallback) {
        //Response response = getData("http://data.jioplay.in/guide/v5/lineup/infotel/r4g/5.0/default/live/all-channels.json");
        new NetworkOperation().execute("http://data.jioplay.in/guide/v5/lineup/infotel/r4g/5.0/default/live/all-channels.json", dataModelCallback);
    }


    private class NetworkOperation extends AsyncTask<Object, String, Response> {
        private DataModelCallback dataModelCallback = null;
        @Override
        public void onPreExecute() {
            Log.d(TAG, "NetworkOperation-onPreExecute");
        }
        @Override
         public Response doInBackground(Object... parms) {
            String urlString = (String)parms[0];
            dataModelCallback = (DataModelCallback)parms[1];
            InputStream stream = null;
            HttpURLConnection connection = null;
            Response response = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                publishProgress("Connection Established");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    stream = connection.getInputStream();
                    JSONObject jsonObject = null;
                    if (stream != null) {

                        JSONParser jsonParser = new JSONParser();
                        jsonObject = (JSONObject) jsonParser.parse(
                                new InputStreamReader(stream, "UTF-8"));

                        publishProgress("Json parsing done");

                        stream.close();
                    }
                    connection.disconnect();

                    publishProgress("All Good");
                    response = new Response(ResponseType.SUCCESS, jsonObject);
                } else {
                    publishProgress("Error Happened");
                    response = new Response(ResponseType.IO_ERROR, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            Log.d(TAG, "NetworkOperation-onPostExecute: " + response.responseType);
            if (dataModelCallback != null) {
                dataModelCallback.onResponse(response);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d(TAG, "NetworkOperation-onProgressUpdate: " + values[0]);
        }
    }


    public interface DataModelCallback {
        public void onResponse(Response response);
    }

    public enum ResponseType {
        SUCCESS,
        NETWORK_ERROR,
        IO_ERROR
    }

    public class Response {
        public JSONObject data;
        public ResponseType responseType;
        public Response(ResponseType responseType, JSONObject data) {
            this.responseType = responseType;
            this.data = data;
        }
    }
}
