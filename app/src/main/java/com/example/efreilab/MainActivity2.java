package com.example.efreilab;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.*;


public class MainActivity2 extends AppCompatActivity {


    private Button buttonConfirm;
    private ImageView image;

    private String linkOfPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        buttonConfirm = findViewById(R.id.button_image2);
        image = findViewById(R.id.imageView);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncFlickrJSONData downloadPictures = new AsyncFlickrJSONData();
                downloadPictures.execute("https://www.flickr.com/services/feeds/photos_public.gne?tags=trees&format=json");

            }
        });
    }

    private class AsyncFlickrJSONData extends AsyncTask<String, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... strings) {
            URL url = null;
            String s = "";
            JSONObject jsonobj = null;
            try {
                System.out.println(strings[0]);
                url = new URL(strings[0]);
                System.out.println("doInbackground");
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                //String basicAuth = "Basic " + Base64.encodeToString((editName.getText() + ":" + editPass.getText()).getBytes(), Base64.NO_WRAP);
                //urlConnection.setRequestProperty("Authorization", basicAuth);
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    s = readStream(in);
                    Log.i("JFL", s);


                    try {
                        int debut = s.indexOf("(");
                        int fin = s.lastIndexOf(")");
                        String newJsonFormat = s.substring(debut + 1, fin);
                        jsonobj = new JSONObject(newJsonFormat);
                        System.out.println("50");
                    } catch (JSONException err){
                        System.out.println("04404");
                        Log.d("Error", err.toString());
                    }
                } finally {
                    System.out.println("60");
                    urlConnection.disconnect();
                }
            }
            catch (MalformedURLException e) {
                System.out.println("70");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("90");
                e.printStackTrace();
            }
            System.out.println(jsonobj);
            return jsonobj;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            System.out.println(jsonObject);
            try {
                System.out.println(jsonObject);
                linkOfPicture = jsonObject.getJSONArray("items").getJSONObject(1).getString("media");
                int debut = linkOfPicture.indexOf("https");
                int fin = linkOfPicture.lastIndexOf("g");
                linkOfPicture = linkOfPicture.substring(debut, fin + 1 );
                System.out.println("My Thread is running");

                System.out.println(linkOfPicture);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AsyncBitmapDownloader DownloadBitMap = new AsyncBitmapDownloader();
            DownloadBitMap.execute(linkOfPicture);
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

    private class AsyncBitmapDownloader extends AsyncTask<String, Void, Bitmap> implements com.example.efreilab.AsyncBitmapDownloader {

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            Bitmap Render = null;
            try {
                System.out.println("2");
                url = new URL(strings[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(in);
                //Render = Bitmap.createScaledBitmap(bm,  600 ,600, true);//thi
                Render = bm;
                System.out.println("1");
            } catch (MalformedURLException e) {
                System.out.println("4");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("5");
                e.printStackTrace();
            }
            return Render;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            System.out.println("10");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("20");

                    image.setImageBitmap(bitmap);
                }
            });
        }

    }
}