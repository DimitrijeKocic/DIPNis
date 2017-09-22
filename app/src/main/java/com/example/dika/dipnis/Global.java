package com.example.dika.dipnis;

import android.app.Application;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Laza on 08-Sep-17.
 */

public final class Global extends Application {

    //public static final String homeUrl = "http://24.135.176.151:8080/dipNisServer/";
    //public static final String homeUrl = "http://160.99.9.136/dipnis/";
    public static final String homeUrl = "http://192.168.1.125/dipnis/";

    public String dataString, resultString;

    //funkcija za kreiranje stringa od JSON-a koji vrati skripta
    public String getJSON(String scriptURL, boolean postMethod, ArrayList<String> keys, ArrayList<String> values) {
        try {
            URL url = new URL(scriptURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20000);
            httpURLConnection.setReadTimeout(21000);
            if (postMethod) {
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                dataString = new String();
                for (int i = 0; i < keys.size(); i++) {
                    dataString += URLEncoder.encode(keys.get(i), "UTF-8") + "=" + URLEncoder.encode(values.get(i), "UTF-8");
                    if (i != keys.size() - 1)
                        dataString += "&";
                }
                bufferedWriter.write(dataString);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
            }
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            while((resultString = bufferedReader.readLine()) != null) {
                stringBuilder.append(resultString + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return stringBuilder.toString().trim();
        } catch (SocketTimeoutException e) {
            return "ConnectTimeout";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
