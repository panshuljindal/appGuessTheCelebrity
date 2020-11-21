package com.panshuljindal.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView imageview;
    Integer answerLocation;
    Button button0,button1,button2,button3;
    ArrayList<String> answers = new ArrayList<String>();
    ArrayList<String> celebUrl = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int choosenCeleb=0;
    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                URLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public class DownloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!=-1){
                    char current = (char)data;
                    result +=current;
                    data=reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }
    }
    public void choosenAnswer(View view){
        if(view.getTag().toString().equals(Integer.toString(answerLocation))){
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }
        newQuestion();
        Log.i("Tag:",view.getTag().toString());
    }
    public void newQuestion(){

        Random rand = new Random();
        choosenCeleb = rand.nextInt(celebUrl.size());
        String celebImage= celebUrl.get(choosenCeleb);
        Log.i("Image",celebImage);
        Log.i("Name",celebNames.get(choosenCeleb));
        ImageDownloader imageTask = new ImageDownloader();
        try {
            Bitmap myImage = imageTask.execute(celebImage).get();
            imageview.setImageBitmap(myImage);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        answerLocation = rand.nextInt(4);
        Log.i("Answer Location",Integer.toString(answerLocation));
        int incorrectAnswerLocation;
        for(int i=0;i<4;i++) {
            if (i==answerLocation) {
                answers.add(celebNames.get(choosenCeleb));
            }else {
                incorrectAnswerLocation = rand.nextInt(celebNames.size());
                while(incorrectAnswerLocation==choosenCeleb){
                    incorrectAnswerLocation=rand.nextInt(celebNames.size());
                }
                answers.add(celebNames.get(incorrectAnswerLocation));
            }
        }
        button0.setText(answers.get(0));
        button1.setText(answers.get(1));
        button2.setText(answers.get(2));
        button3.setText(answers.get(3));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageview=findViewById(R.id.imageView);
        button0 = findViewById(R.id.button1);
        button1 = findViewById(R.id.button4);
        button2 = findViewById(R.id.button5);
        button3 =findViewById(R.id.button6);
        String result;
        result = null;
        DownloadTask task = new DownloadTask();
        try {
            result=task.execute("http://www.posh24.se/kandisar").get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] celebs = result.split("<div class=\"listedArticles\">");
        String celeb = celebs[0];
        Pattern p = Pattern.compile("img src=\"(.*?)\"");
        Matcher m = p.matcher(celeb);
        while (m.find()) {
            celebUrl.add(m.group(1));
        }
        p = Pattern.compile("alt=\"(.*?)\"/");
        m = p.matcher(celeb);
        while (m.find()) {
            celebNames.add(m.group(1));
        }
        Log.i("Info:",celebNames.toString());
        celebUrl.set(4,"https://img1.nickiswift.com/img/gallery/straight-celebs-whove-been-in-gay-relationships-upgrade/intro-1523464103.jpg");
        celebNames.set(4,"Nicki Swift");
        newQuestion();
    }

}
