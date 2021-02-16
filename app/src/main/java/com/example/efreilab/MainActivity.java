package com.example.efreilab;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import org.json.*;

public class MainActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editPass;
    private Button buttonConfirm;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        editPass = findViewById(R.id.editPass);
        buttonConfirm = findViewById(R.id.button);
        result = findViewById(R.id.result);

        editName.addTextChangedListener(loginTextWatcher);
        editPass.addTextChangedListener(loginTextWatcher);

        Intent intent = new Intent(this, MainActivity2.class);


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThread thread = new MyThread();
                thread.start();

                startActivity(intent);
            }
        });
    }


    public class MyThread extends Thread {
        public void run()
        {
            System.out.println("My Thread is running");
            URL url = null;
            String s = "";
            try {
                url = new URL("https://httpbin.org/basic-auth/bob/sympa");
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                String basicAuth = "Basic " + Base64.encodeToString((editName.getText() + ":" + editPass.getText()).getBytes(), Base64.NO_WRAP);
                urlConnection.setRequestProperty("Authorization", basicAuth);
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    s = readStream(in);
                    Log.i("JFL", s);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String finalS = s;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    result.setText(finalS);
                }
            });
        }

        private String readStream(InputStream is) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while(i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String usernameInput = editName.getText().toString().trim();
            String passwordInput = editPass.getText().toString().trim();

            buttonConfirm.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
