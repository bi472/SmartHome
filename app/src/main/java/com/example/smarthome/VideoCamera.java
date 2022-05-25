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

        firstCamera.setWebViewClient(new MyWebViewClient());

        firstCamera.loadUrl("https://www.youtube.com/embed/cN1HsJnt-MY");
    }
}