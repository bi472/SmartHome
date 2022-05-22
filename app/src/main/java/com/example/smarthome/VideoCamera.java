package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class VideoCamera extends AppCompatActivity {

    WebView firstCamera;
    WebView secondCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_camera);

        firstCamera = (WebView) findViewById(R.id.firstCamera);
        firstCamera.getSettings().setJavaScriptEnabled(true);
        secondCamera = (WebView) findViewById(R.id.secondCamera);
        secondCamera.getSettings().setJavaScriptEnabled(true);

        firstCamera.setWebViewClient(new MyWebViewClient());
        secondCamera.setWebViewClient(new MyWebViewClient());

        firstCamera.loadUrl("https://youtu.be/WInT7NRq2Ss");
        secondCamera.loadUrl("https://youtu.be/ofYWecmqbDs");
    }
}