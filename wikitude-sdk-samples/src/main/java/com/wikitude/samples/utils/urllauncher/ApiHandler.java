package com.wikitude.samples.utils.urllauncher;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.Thread.sleep;

public class ApiHandler {

    public static int sendPost(String stringUrl, String parameters) throws IOException,JSONException {

        URL url = new URL(stringUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        //connection.setHostnameVerifier(DO_NOT_VERIFY);
        connection.setConnectTimeout(5000);//5 secs
        connection.setReadTimeout(5000);//5 secs

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.write(parameters);


        writer.flush();
        writer.close();
        out.close();
        connection.connect();
        String message = connection.getResponseMessage();
        int res = connection.getResponseCode();
        System.out.println(res);
        System.out.println(message);
        /*        URL object=new URL(stringUrl);

        HttpsURLConnection urlConnection = (HttpsURLConnection) object.openConnection();

        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");

        urlConnection.setDoOutput(true);
        try{
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(parameters.toString());
            wr.flush();
            wr.close();
        }
        catch(IOException e){
            String ex = e.getMessage();
        }

        int response = urlConnection.getResponseCode();
        String resMessage = urlConnection.getResponseMessage();
        InputStream input = urlConnection.getInputStream();
        OutputStream output = urlConnection.getOutputStream();
        urlConnection.disconnect();
*/
        return 1;
    }
}