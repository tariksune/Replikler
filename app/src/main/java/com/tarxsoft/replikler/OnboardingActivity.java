package com.tarxsoft.replikler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
    private MaterialButton materialButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        //Remove title bar
        getSupportActionBar().hide();

        layoutOnboardingIndicators = findViewById(R.id.indicators);
        materialButton = findViewById(R.id.nextButton);
        setupOnboardingItems();

        final ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(onboardingAdapter);

        setupIndicators();
        setCurrentIndicator(0);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager2.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem shareQuotes = new OnboardingItem();
        shareQuotes.setTitle("Özgürce Paylaş!");
        shareQuotes.setDescription("Tüm replikleri dilediğin platformda paylaşabilirsin.");
        shareQuotes.setImage(R.drawable.share);

        OnboardingItem downloadQuotes = new OnboardingItem();
        downloadQuotes.setTitle("İndirmeyi Unutma!");
        downloadQuotes.setDescription("Replikleri paylaşmadan önce indirmen gerek.");
        downloadQuotes.setImage(R.drawable.download);

        OnboardingItem reviewQuotes = new OnboardingItem();
        reviewQuotes.setTitle("Bizi Oyla!");
        reviewQuotes.setDescription("Uygulamayı beğendiysen lütfen Google Play'de bize oy ver! Eksiklerimiz var ise uygulama içerisinden bize hemen bildirebilirsin.");
        reviewQuotes.setImage(R.drawable.attention);

        onboardingItems.add(shareQuotes);
        onboardingItems.add(downloadQuotes);
        onboardingItems.add(reviewQuotes);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }

    private void setupIndicators(){
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8,0,8,0);
        for (int i = 0; i<indicators.length; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),R.drawable.onboard_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index){
        int childCount = layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i< childCount; i++){
            ImageView imageView = (ImageView)layoutOnboardingIndicators.getChildAt(i);
            if(i==index){
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboard_indicator_active));
            }else{
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboard_indicator_inactive));
            }
        }
        if (index==onboardingAdapter.getItemCount() - 1){
            materialButton.setText("Hadi Başlayalım!");
        }else{
            materialButton.setText("İleri");
        }
    }
}