package com.tarxsoft.replikler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static String JSON_URL = "https://www.tariksune.com/replik-list.json";

    private AdView adView;
    private PublisherInterstitialAd publisherInterstitialAd;
    ScheduledExecutorService scheduledExecutorService;

    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    List<Quotes> quotes;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.quotesList);
        quotes = new ArrayList<>();
        requestPermission();
        webStatus();
        prepareInterstitialAd();
        loadUnterstitialAd();
        listOfQuotes();
    }

    public void requestPermission(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }, 100);
    }

    private boolean hasPermission(Context context, String permission){
        return ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode ==1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.permission_layout);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

                Button permissionButton = dialog.findViewById(R.id.permissionButton);
                Button againButton = dialog.findViewById(R.id.againButton);
                permissionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

                againButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recreate();
                    }
                });
                dialog.show();
            }
        }
    }

    private void listOfQuotes() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject quoteObject = response.getJSONObject(i);
                        Quotes quote = new Quotes();
                        quote.setQuoteId(quoteObject.getString("quoteId"));
                        quote.setQuoteText(quoteObject.getString("quoteText"));
                        quote.setQuoteName(quoteObject.getString("quoteName"));
                        quote.setQuoteLink(quoteObject.getString("quoteLink"));
                        quotes.add(quote);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
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

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView xSearchView = (SearchView) search.getActionView();
        xSearchView.setQueryHint("Film veya Dizi Adı..");
        xSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        MenuItem contact = menu.findItem(R.id.contact);
        contact.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getApplicationContext(), ContactActivity.class));
                return true;
            }
        });

        MenuItem copyright = menu.findItem(R.id.copyright);
        copyright.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getApplicationContext(), CopyrightActivity.class));
                return true;
            }
        });

        MenuItem about = menu.findItem(R.id.about);
        about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void webStatus(){
        if (!isNetworkConnected()){
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.connection_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

            Button connectionAgainButton = dialog.findViewById(R.id.connectionAgainButton);
            connectionAgainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            dialog.show();
        }
    }

    private void prepareInterstitialAd(){
        publisherInterstitialAd = new PublisherInterstitialAd(this);
        publisherInterstitialAd.setAdUnitId("/6499/example/interstitial");
        publisherInterstitialAd.loadAd(new PublisherAdRequest.Builder().build());

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        recyclerView.setPadding(0, 0, 0, adView.getHeight());
        recyclerView.setClipToPadding(false);

    }

    private void loadUnterstitialAd(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (publisherInterstitialAd.isLoaded()){
                            publisherInterstitialAd.show();
                        }else{

                        }
                        prepareInterstitialAd();
                    }
                });
            }
        },60,60, TimeUnit.SECONDS);

    }

    @Override
    protected void onDestroy() {

        scheduledExecutorService.shutdown();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        scheduledExecutorService.shutdown();
        super.onBackPressed();
    }

}