package com.project.bmail.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.bmail.databinding.RegistrationViewBinding;
import com.project.bmail.models.RegistraionModel;
import com.project.bmail.utilities.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    RegistrationViewBinding binding;
    private SpeechRecognizer speechRecognizer;
    final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    TextToSpeech tts;
    ArrayList<String> data;
    boolean nameCnf = false; boolean emailCnf = false; boolean passwordCnf = false;
    int reachName = 0; int reachemail = 0; int reachPass = 0;
    DatabaseReference dbRef;
    String test = "";
    SessionManager mManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegistrationViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mManager = SessionManager.getInstance(this);
        tts = new TextToSpeech(this,this);
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }
            @Override
            public void onBeginningOfSpeech() {

            }
            @Override
            public void onRmsChanged(float v) {

            }
            @Override
            public void onBufferReceived(byte[] bytes) {

            }
            @Override
            public void onEndOfSpeech() {

            }
            @Override
            public void onError(int i) {

            }
            @Override
            public void onResults(Bundle bundle) {
                data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                recognizedTextFormatting(data.get(0));
            }
            @Override
            public void onPartialResults(Bundle bundle) {

            }
            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        //Single Tap
        binding.registrationCl.setOnClickListener(v -> {
            test = "";
            if(tts!=null){
                tts.stop();
            }
            if(nameCnf==false){
                if(reachName>0){
                    nameCnf = true;
                    say("name confirmed, Now say email ID after taping on screen once");
                }else{
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }else if(emailCnf == false){
                if(reachemail>0){
                    String email = binding.email.getText().toString();
                    String emailKey = email.replace(".","1");
                    emailKey = emailKey.replace("#","1");
                    checkUserAlreadyExist(emailKey);
                }else{
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }else{
                if(reachPass>0){
                    passwordCnf = true;
                    say("password confirmed!");
                    registerUserDetailsToFirebase();
                }else {
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }
        });

        //Long Press
        binding.registrationCl.setOnLongClickListener(v -> {
            test = "";
            if(tts!=null){
                tts.stop();
            }
            speechRecognizer.startListening(speechRecognizerIntent);
            return true;
        });

    }

    @Override
    public void onInit(int status) {
        say("You are now in registration Page. Say your name, after taping on screen once.");
    }

    public void say(String textToSpeech){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(textToSpeech,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void recognizedTextFormatting(String textData){
        if(textData.contains("at the rate")){
            textData = textData.replaceAll("at the rate","@");
        }else if(textData.contains(" at ")){
            textData = textData.replaceAll(" at ","@");
        }
        if(!nameCnf==false){
            textData = textData.replaceAll("\\s","").toLowerCase();
        }
        say(textData+",  This is what you have said, to confirm, tap screen once, long press and say again to re enter");
        if(nameCnf == false){
            binding.name.setText(textData);
            reachName = 2;
        }else if(emailCnf == false){
            binding.email.setText(textData);
            reachemail = 2;
        }else {
            binding.password.setText(textData);
            reachPass = 2;
        }
    }

    private void registerUserDetailsToFirebase(){
        String name = binding.name.getText().toString();
        String email = binding.email.getText().toString();
        String pass = binding.password.getText().toString();

        String emailKey = email.replace(".","1");
        emailKey = emailKey.replace("#","1");

        Log.d("ttt",emailKey);

        RegistraionModel registraionModel = new RegistraionModel(name,email,pass);
        HashMap<String,Object> regDetails = new HashMap<>();
        try{
            regDetails.put("regData",registraionModel);
            dbRef.child(emailKey).setValue(regDetails);
            mManager.setSignedUserEmail(email);
            mManager.setSignedUserName(name);
            startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
            finish();
        }catch (Exception e){
            Log.i("ss",e.getLocalizedMessage());
        }
    }

    private void checkUserAlreadyExist(String userName){
        test = userName;
        dbRef.child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot!=null&&snapshot.getChildren()!=null&&snapshot.getChildren().iterator().hasNext()){
                    if(test.equals(userName)){
                        say("email ID Already Exists, Retry another email to register!");
                        reachemail = 0;
                        emailCnf = false;
                    }
                }else{
                    if(test.equals(userName)){
                        say("email confirmed, Now say password after tapping on screen once");
                        emailCnf = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onDestroy() {
        stopTts();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        stopTts();
        super.onPause();
    }

    @Override
    protected void onStop() {
        stopTts();
        super.onStop();
    }

    private void stopTts(){
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
