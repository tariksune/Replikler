package com.tarxsoft.replikler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String JSON_URL = "https://www.tariksune.com/replik-list.json";

    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    List<Quote> quotes;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.quotesList);
        quotes = new ArrayList<>();



        listOfQuotes();


    }


    private void listOfQuotes() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject quoteObject = response.getJSONObject(i);
                        Quote quote = new Quote();
                        quote.setQuoteText(quoteObject.getString("quoteText").toString());
                        quote.setQuoteName(quoteObject.getString("quoteName").toString());
                        quote.setQuoteLink(quoteObject.getString("quoteLink"));
                        quotes.add(quote);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                gridLayoutManager = new GridLayoutManager(MainActivity.this,2);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(gridLayoutManager);
                //recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new Adapter(getApplicationContext(),quotes);
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonArrayRequest);



    }
}