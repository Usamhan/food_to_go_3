package com.samhan.foodtogo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        // Add any additional settings if required

        //webView.loadUrl("https://www.google.com/maps"); // Load the map URL

        double latitude = 37.7749; // Example latitude
        double longitude = -122.4194; // Example longitude

        String mapUrl = "https://www.google.com/maps?q=" + latitude + "," + longitude;
        webView.loadUrl(mapUrl);


    }
}