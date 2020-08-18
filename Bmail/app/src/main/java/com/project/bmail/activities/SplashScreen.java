package com.project.bmail.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.project.bmail.R;
import com.project.bmail.databinding.SplashScreenLayoutBinding;

import static com.project.bmail.activities.LoginActivity.RecordAudioRequestCode;

public class SplashScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {

    SplashScreenLayoutBinding binding;
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SplashScreenLayoutBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        FirebaseAnalytics.getInstance(this);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        tts = new TextToSpeech(this,this);
        binding.splashScreenCl.setOnClickListener(v -> {
            startActivity(new Intent(SplashScreen.this,LoginActivity.class));
        });
        binding.splashScreenCl.setOnLongClickListener(v -> {
            startActivity(new Intent(SplashScreen.this,RegisterActivity.class));
            return true;
        });

    }

    public void say(String textToSpeech){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(textToSpeech,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        say("Welcome, to B-Mail. Tap once for login, long press the screen, for registration");
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onStop();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
