package com.tarxsoft.replikler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        getSupportActionBar().hide();
        final EditText nameContact        = (EditText) findViewById(R.id.nameContact);
        final EditText emailContact       = (EditText) findViewById(R.id.emailContact);
        final EditText subjectContact     = (EditText) findViewById(R.id.subjectContact);
        final EditText messageContact     = (EditText) findViewById(R.id.messageContact);

        Button sendContactButton = (Button) findViewById(R.id.sendContactButton);
        sendContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name      = nameContact.getText().toString();
                String email     = emailContact.getText().toString();
                String subject   = subjectContact.getText().toString();
                String message   = messageContact.getText().toString();

                if (TextUtils.isEmpty(name)){
                    nameContact.setError("İsim kısmı boş bırakılamaz.");
                    nameContact.requestFocus();
                    return;
                }

                Boolean onError = false;
                if (!isValidEmail(email)) {
                    onError = true;
                    emailContact.setError("Email kısmı boş bırakılamaz.");
                    return;
                }

                if (TextUtils.isEmpty(subject)){
                    subjectContact.setError("Konu kısmı boş bırakılamaz.");
                    subjectContact.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(message)){
                    messageContact.setError("Mesaj kısmı boş bırakılamaz.");
                    messageContact.requestFocus();
                    return;
                }
                Intent sendEmail = new Intent(android.content.Intent.ACTION_SEND);
                sendEmail.setType("plain/text");
                sendEmail.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"tarxsoft@gmail.com"});
                sendEmail.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                sendEmail.putExtra(android.content.Intent.EXTRA_TEXT,"İsim: "+name+'\n'+"E-Mail: "+email+'\n'+'\n'+"Mesaj: "+'\n'+message);
                startActivity(Intent.createChooser(sendEmail, "Mesaj gönderildi."));
                nameContact.getText().clear();
                emailContact.getText().clear();
                subjectContact.getText().clear();
                messageContact.getText().clear();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void backImageButton(View view) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}