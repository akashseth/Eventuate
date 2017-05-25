package com.bhumca2017.eventuate;

/**
 * Created by toaka on 13-04-2017.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class ServerRequestHandler {

    private static final String LOG_TAG = ServerRequestHandler.class.getSimpleName();
    public String sendGetRequest(String requestURL) throws IOException {

            String jsonResponse = "";
            if(requestURL == null) {
                return jsonResponse;
            }
            URL url = createUrl(requestURL);
            HttpURLConnection connection = null;
            InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(150000);
            connection.setRequestMethod("GET");
            connection.connect();

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                inputStream = connection.getInputStream();
                jsonResponse = getJSONResponse(inputStream);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG,"Problem retrieving the earthquake JSON results.", e);
        }
        finally {
                if(connection != null) {
                    connection.disconnect();
                }
                if(inputStream != null) {
                    inputStream.close();
                }
            }
        return jsonResponse;
    }

    public String sendPostRequest(String requestURL,
                                  HashMap<String, String> postDataParams) throws IOException{

        String jsonResponse = "";
        if(requestURL == null) {
            return jsonResponse;
        }
        URL url = createUrl(requestURL);
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(50000);
            connection.setConnectTimeout(70000);
            connection.setRequestMethod("POST");
            //connection.connect();

           // connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                inputStream = connection.getInputStream();
                jsonResponse = getJSONResponse(inputStream);
            }

        } catch (SocketTimeoutException e) {
            Log.e(LOG_TAG,"Problem retrieving the JSON results.", e);
        }
        catch (IOException e) {
            Log.e(LOG_TAG,"Problem retrieving the JSON results.", e);
        }
        finally {
            if(connection != null) {
                connection.disconnect();
            }
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
       // Log.e("URl",result.toString());
        return result.toString();
    }

    private String getJSONResponse(InputStream inputStream) throws IOException{

        StringBuilder stringBuilder = new StringBuilder();
        if(inputStream!=null) {

            InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(streamReader);
            String line = reader.readLine();

            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }
        }
        return stringBuilder.toString();
    }
    public static URL createUrl(String requestedUrl)
    {
        URL url=null;
        try {
            url=new URL(requestedUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }
}