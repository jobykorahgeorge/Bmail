package com.project.bmail.activities;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.bmail.databinding.HomeViewBinding;
import com.project.bmail.utilities.SessionManager;

public class HomeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    HomeViewBinding binding;
    TextToSpeech tts;
    SessionManager mManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tts = new TextToSpeech(this,this);
        mManager = SessionManager.getInstance(this);
    }

    public void say(String textToSpeech){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        say("Hai, "+mManager.getSignedUserName()+", You are in Home screen now.  ");
    }
}
