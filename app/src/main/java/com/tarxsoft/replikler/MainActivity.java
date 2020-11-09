package com.tarxsoft.replikler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "TAG";
    private static String JSON_URL = "https://tariksune.com/repliklist.json";

    private AdView adView;
    private PublisherInterstitialAd publisherInterstitialAd;
    ScheduledExecutorService scheduledExecutorService;

    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Quotes> quotes;
    Adapter adapter;
    Context context;
    LottieAnimationView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        recyclerView = findViewById(R.id.quotesList);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        context = MainActivity.this;
        quotes = new ArrayList<>();
        requestPermission();
        webStatus();
        increaseVolumeAuto();
        prepareAds();
        listOfQuotes();
        loadInterstitialAd();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                StyleableToast.makeText(context,"Güncelleniyor...", Toast.LENGTH_SHORT,R.style.mytoastswipe).show();
                clearData();
                listOfQuotes();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


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
        loading = (LottieAnimationView) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
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
                        quote.setQuoteImg(quoteObject.getString("quoteImg"));
                        quote.setQuoteLink(quoteObject.getString("quoteLink"));
                        quotes.add(quote);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loading.setVisibility(View.INVISIBLE);
                Collections.reverse(quotes);
                gridLayoutManager = new GridLayoutManager(getApplicationContext(),1);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(gridLayoutManager);
                //recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new Adapter(getApplicationContext(),quotes);
                recyclerView.setAdapter(adapter);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.INVISIBLE);
                Log.d(TAG,"Hata: "+ error.getMessage());
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
                AdsBetween2Activities();
                return true;
            }
        });

        MenuItem copyright = menu.findItem(R.id.copyright);
        copyright.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getApplicationContext(), CopyrightActivity.class));
                AdsBetween2Activities();
                return true;
            }
        });

        MenuItem about = menu.findItem(R.id.about);
        about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                AdsBetween2Activities();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void webStatus(){
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

    private void prepareAds(){
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        recyclerView.setPadding(0, 0, 0, adView.getHeight());
        recyclerView.setClipToPadding(false);

        publisherInterstitialAd = new PublisherInterstitialAd(this);
        publisherInterstitialAd.setAdUnitId("ca-app-pub-4414005169063679/6225835721");
        publisherInterstitialAd.loadAd(new PublisherAdRequest.Builder().build());
    }

    private void loadInterstitialAd(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean stateAlive = getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
                        if (stateAlive && publisherInterstitialAd.isLoaded()){
                            publisherInterstitialAd.show();
                        }else{

                        }
                        prepareAds();
                    }
                });
            }
        },45,45, TimeUnit.SECONDS);

    }

    public void AdsBetween2Activities() {

        if (publisherInterstitialAd.isLoaded()){
            publisherInterstitialAd.show();
        }else{
            finish();
        }

    }

    public void clearData() {
        quotes.clear();
        adapter.notifyDataSetChanged();
    }

    public void increaseVolumeAuto(){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}