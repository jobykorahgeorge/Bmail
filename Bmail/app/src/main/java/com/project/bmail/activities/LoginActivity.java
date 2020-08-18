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
import com.project.bmail.databinding.LoginViewBinding;
import com.project.bmail.models.RegistraionModel;
import com.project.bmail.utilities.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    LoginViewBinding binding;
    TextToSpeech tts;
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    boolean userIdCnf  = false;
    int useridAsked = 0;
    int passwordAsked = 0;
    ArrayList<String> data;
    DatabaseReference dbRef;
    SessionManager mManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mManager = SessionManager.getInstance(this);
        dbRef = FirebaseDatabase.getInstance().getReference("users");
        tts = new TextToSpeech(this,this);
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

        binding.loginCl.setOnClickListener(v -> {
            if(tts!=null){
                tts.stop();
            }
            if(userIdCnf == false){
                if(useridAsked == 1){
                    say("user ID confirmed, Now say your password, after taping once on the screen");
                    userIdCnf = true;
                }else{
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }else{
                if(passwordAsked == 1){
                    validateUser();
                }else{
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }
        });

        binding.loginCl.setOnLongClickListener(v -> {
            if(tts!=null){
                tts.stop();
            }
            if (userIdCnf == false) {
                if(useridAsked == 1){
                    speechRecognizer.startListening(speechRecognizerIntent);
                    useridAsked = 0;
                }else{
                    speechRecognizer.stopListening();
                }
            }else{
                if(passwordAsked == 1){
                    speechRecognizer.startListening(speechRecognizerIntent);
                    passwordAsked = 0;
                }else {
                    speechRecognizer.stopListening();
                }
            }
            return true;
        });

    }

    private void recognizedTextFormatting(String textData){
        textData = textData.toLowerCase();
        textData = textData.replaceAll("at the rate", "@");
        textData = textData.replaceAll("\\s","");
        say(textData+", This is what you have said, tap screen once, to confirm, long press and say again to re enter");
        useridAsked = 1;
        if(userIdCnf){
            passwordAsked = 1;
            binding.password.setText(textData);
        }else{
            binding.userName.setText(textData);
        }
    }

    private void validateUser(){
        String userName = binding.userName.getText().toString().replace(".","1");
        userName = userName.replace("#","1");
        try{
            dbRef.child(userName).child("regData").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot!=null&&snapshot.getChildren()!=null&&snapshot.getChildren().iterator().hasNext()){
                        RegistraionModel reg = snapshot.getValue(RegistraionModel.class);
                        if(reg.password!=null){
                            if(reg.password.equals(binding.password.getText())){
                                say("login was Successfull");
                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                finish();
                                mManager.setSignedUserEmail(reg.email);
                                mManager.setSignedUserName(reg.name);
                            }else{
                                userIdCnf  = false;
                                useridAsked = 0;
                                passwordAsked = 0;
                                say("Invalid user credentials, say your email ID, after taping on the screen once");
                            }
                        }
                    }else{
                        userIdCnf  = false;
                        useridAsked = 0;
                        passwordAsked = 0;
                        say("Invalid user credentials, say your email ID, after taping on the screen once");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch(Exception e){
            Log.d("excep",e.getLocalizedMessage());
        }
    }

    @Override
    public void onInit(int status) {
        say("You are now in log in page. Please Say Your User ID after tapping once on the screen.");
    }

    public void say(String textToSpeech){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(textToSpeech,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null);
        }
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
