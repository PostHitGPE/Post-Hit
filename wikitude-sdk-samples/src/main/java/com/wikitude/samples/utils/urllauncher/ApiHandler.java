package com.wikitude.samples.utils.urllauncher;

import android.support.annotation.NonNull;

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

    // parameters = "{"pseudo": "test", "password": "test"}";
    public static String sendPost(String stringUrl, String parameters) throws IOException,JSONException {

        URL url = new URL(stringUrl);
        HttpURLConnection conn = (HttpsURLConnection) url.openConnection();
//        conn.setConnectTimeout(5000);//5 secs
//        conn.setReadTimeout(5000);//5 secs
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json; utf-8");

        parameters = "{\"pseudo\": \"test\", \"password\": \"test\"}";
        // test
        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = parameters.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            return response.toString();
        }
/*        // writing parameters
        BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.write(parameters);

        writer.flush();
        writer.close();
        out.close();
        conn.connect();
        String message = conn.getResponseMessage();
        int res = conn.getResponseCode();
        System.out.println(res);
        System.out.println(message);
        if (res == 200) return "Result 200 ok";
  */
    }
    public static int sendPost(String stringUrl) throws IOException,JSONException {

        URL url = new URL(stringUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        //connection.setHostnameVerifier(DO_NOT_VERIFY);
        connection.setConnectTimeout(5000);//5 secs
        connection.setReadTimeout(5000);//5 secs

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        connection.connect();
        String message = connection.getResponseMessage();
        int res = connection.getResponseCode();
        System.out.println(res);
        System.out.println(message);

        return 1;
    }}