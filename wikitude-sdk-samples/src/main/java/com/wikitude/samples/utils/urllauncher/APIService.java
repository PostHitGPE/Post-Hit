package com.wikitude.samples.utils.urllauncher;

import android.app.Activity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.wikitude.samples.Constants;
import com.wikitude.samples.MainActivity;
import com.wikitude.samples.Register;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.util.HashMap;
import java.util.Map;

public class APIService {

    private static String API_ENDPOINT = Constants.API_ENDPOINT;
    private static String LOGIN_ROUTE = "user/login_check";
    private static String USERCREATE_ROUTE = "user/";
    private static String BEARER_TOKEN = "";
    private APIService instance = null;

    private APIService(){}

    private static class MySingletonHolder {
        private final static APIService instance = new APIService();
    }

    public static APIService getInstance(){
        return MySingletonHolder.instance;
    }

    public void loginCheck(final MainActivity activity, final String login, final String password){
        StringRequest postRequest = new StringRequest(Request.Method.POST, API_ENDPOINT+LOGIN_ROUTE,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println( "------------------------------------------>"+response);
                        JSONParser parser = new JSONParser();
                        String token = null;
                        String code = null;
                        try {
                            JSONObject json = (JSONObject) parser.parse(response.toString());
                            token = (String) json.get("token");
                            BEARER_TOKEN = token;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // gestion retour api
                        if (token != null) activity.goPostHitActivity(token);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        System.out.println( "------------------------------------------>"+error);
                        Toast.makeText(activity,"Wrong credentials",Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders(){

                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Content-type", "application/json");

                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String your_string_json = "{\"pseudo\": \""+login+"\",\"password\": \""+password+"\"}";
                return your_string_json.getBytes();
            }
        };
        activity.queue.add(postRequest);
    }

    public void userCreate(final Register activity, final String email, final String login, final String password){
        StringRequest postRequest = new StringRequest(Request.Method.POST, API_ENDPOINT+USERCREATE_ROUTE,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        System.out.println( "------------------------------------------>"+response);
                        JSONParser parser = new JSONParser();
                        String pseudo = null;
                        try {
                            JSONObject json = (JSONObject) parser.parse(response.toString());
                            pseudo = (String) json.get("pseudo");
                            Toast.makeText(activity,"User "+pseudo+" successfully created!",Toast.LENGTH_LONG).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
/*                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse.statusCode == 400)
                        Toast.makeText(activity,"ERROR",Toast.LENGTH_LONG).show();*/
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders(){

                Map<String, String>  headers = new HashMap<String, String>();
                headers.put("Content-type", "application/json");

                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String your_string_json = "{\"email\":\"" + email + "\",\"pseudo\": \"" + login + "\",\"password\": \""+password+"\"}";
                return your_string_json.getBytes();
            }
        };
        activity.queue.add(postRequest);
    }
}
