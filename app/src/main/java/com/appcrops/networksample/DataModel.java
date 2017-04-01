package com.appcrops.networksample;

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

public class DataModel {

    private static DataModel dataModel = null;
    private DataModel() {

    }

    public static DataModel instance() {
        if (dataModel == null) {
            dataModel = new DataModel();
        }
        return dataModel;
    }

    public void getChannelData(final DataModelCallback dataModelCallback) {
        new Thread() {
            @Override
            public void run() {
                Response response = getData("http://data.jioplay.in/guide/v5/lineup/infotel/r4g/5.0/default/live/all-channels.json");
                if (dataModelCallback != null)
                {
                    dataModelCallback.onResponse(response);
                }
            }
        }.start();
    }

    private Response getData(String urlString) {
        InputStream stream = null;
        HttpURLConnection connection = null;
        Response response = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {


                stream = connection.getInputStream();
                JSONObject jsonObject = null;
                if (stream != null) {

                    JSONParser jsonParser = new JSONParser();
                    jsonObject = (JSONObject) jsonParser.parse(
                            new InputStreamReader(stream, "UTF-8"));
                    stream.close();
                }
                connection.disconnect();

                response = new Response(ResponseType.SUCCESS, jsonObject);
            } else {
                response = new Response(ResponseType.IO_ERROR, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
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
