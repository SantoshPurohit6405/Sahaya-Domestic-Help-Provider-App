package com.example.sahayaapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;

public class ApiHelper {

    // Replace "your_localhost_ip" with your actual local IP (e.g., 192.168.1.100)
    public static final String BASE_URL = "http://192.168.0.108:3000/sahaya_api/";

    public static void postRequest(String urlString, HashMap<String, String> params, ApiResponseListener listener) {
        new PostRequestTask(urlString, params, listener).execute();
    }

    private static class PostRequestTask extends AsyncTask<Void, Void, String> {
        private String urlString;
        private HashMap<String, String> params;
        private ApiResponseListener listener;

        public PostRequestTask(String urlString, HashMap<String, String> params, ApiResponseListener listener) {
            this.urlString = urlString;
            this.params = params;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(params));
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (listener != null) {
                listener.onResponse(result);
            }
        }
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) first = false;
            else result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    // Callback interface for handling API responses
    public interface ApiResponseListener {
        void onResponse(String response);
    }
}


