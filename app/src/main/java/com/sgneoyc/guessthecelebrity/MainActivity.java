package com.sgneoyc.guessthecelebrity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.GridLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout mainConstraintLayout;
    GridLayout answerGridLayout ;
    ImageView imageImageView ;
    ImageView answerImageView;
    Button goButton;
    ArrayList<String> celeImgsArray = new ArrayList<String>();
    ArrayList<String> celeNameArray = new ArrayList<String>();
    int maxCelebrityCount;
    int celebrityPtr;
    int correctCount;
    int totalQuestion;
    int highScore;
    Button answerBtn0;
    Button answerBtn1;
    Button answerBtn2;
    Button answerBtn3;
    TextView timerTextView;
    TextView scoreTextView;
    TextView messageTextView;
    TextView highScoreTextView;
    TextView highScoreLabel;
    CountDownTimer countDownTimer;
    SharedPreferences sharedPreferences;


    public void startGame(View view) {
        correctCount = 0;
        totalQuestion = 0;
        goButton.setVisibility(View.INVISIBLE);
        messageTextView.setText("LOADING...");
        messageTextView.setVisibility(View.VISIBLE);
        setupGame();
        messageTextView.setVisibility(View.INVISIBLE);
        timerTextView.setVisibility(View.VISIBLE);
        scoreTextView.setVisibility(View.VISIBLE);
        highScoreTextView.setVisibility(View.VISIBLE);
        highScoreLabel.setVisibility(View.VISIBLE);
        imageImageView.setVisibility(View.VISIBLE);
        answerGridLayout.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(30100, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                // game ends
                calculateScore();

            }
        }.start();
        nextQuestion();

    }


    protected void calculateScore() {

        timerTextView.setVisibility(View.INVISIBLE);
        scoreTextView.setVisibility(View.INVISIBLE);
        imageImageView.setVisibility(View.INVISIBLE);
        answerGridLayout.setVisibility(View.INVISIBLE);
        highScoreTextView.setVisibility(View.INVISIBLE);
        highScoreLabel.setVisibility(View.INVISIBLE);
        goButton.setText("PLAY");
        goButton.setVisibility(View.VISIBLE);
        if (correctCount > highScore)   {
            messageTextView.setText("A new high score " + String.valueOf(correctCount)+"!");
            highScore = correctCount;
            highScoreTextView.setText(String.valueOf(highScore));
            sharedPreferences.edit().putInt("highscore", correctCount).apply();
        } else {
            messageTextView.setText("Your score is " + String.valueOf(correctCount));
        }
        messageTextView.setVisibility(View.VISIBLE);


    }


    public void setupGame() {
        if (celeImgsArray != null) {
            celeImgsArray.clear();
        }
        if (celeNameArray != null) {
            celeNameArray.clear();
        }
        FileLoaderTask task = new FileLoaderTask();
        String result = "";
        try {
            result = task.execute("R.id.celebrity3").get();
            //Log.i("result:", result);

        } catch (Exception e) {
            //Log.i("result:", result);
            e.printStackTrace();
        }


        Pattern p = Pattern.compile("<img>(.*?)</img>");
        Matcher m = p.matcher(result);
        while (m.find()) {
            celeImgsArray.add(m.group(1));
        }
        p = Pattern.compile("<name>(.*?)</name>");
        m = p.matcher(result);
        while (m.find()) {
            celeNameArray.add(m.group(1));
        }
        //Log.i("Array size:", String.valueOf(celeNameArray.size()));
        maxCelebrityCount = celeNameArray.size();

        /* for (int x=0; x<celeNameArray.size(); x++) {
            Log.i("Name at "+String.valueOf(x)+" is ", celeImgsArray.get(x));
        } */
    }

    public void nextQuestion() {
        totalQuestion++;
        Random rand = new Random();
        celebrityPtr = rand.nextInt(maxCelebrityCount);
        int answerInButton = rand.nextInt(4); // between 0-3

        //ImageDownloaderTask task = new ImageDownloaderTask();

        try {
            String imageUrl = (String) celeImgsArray.get(celebrityPtr);
            // imageImageView.setImageBitmap(task.execute(imageUrl).get());
            // BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.imagenotavailable);
            //

            imageImageView.setImageResource(getResources().getIdentifier(imageUrl,"drawable", getPackageName()));
            String[] selectedFour = new String[4];
            int i;
            for (i=0; i<4; i++) {
                if (i == answerInButton) {
                    selectedFour[i] = celeNameArray.get(celebrityPtr);
                } else {
                    String wrongCeleb = "";
                    do {
                        wrongCeleb = celeNameArray.get(rand.nextInt(maxCelebrityCount));
                        if (wrongCeleb.equals(celeNameArray.get(celebrityPtr))) {
                            wrongCeleb = "";
                        } else {
                            if (i!=0)  {

                                for (int j = 0; j <= i; j++) {
                                    if (wrongCeleb.equals(selectedFour[j])) {
                                        wrongCeleb = "";
                                    }
                                }
                            }
                        }
                    } while (wrongCeleb.equals(""));
                    selectedFour[i] = wrongCeleb;
                }
            }

            answerBtn0.setTag(selectedFour[0]);
            answerBtn0.setText(selectedFour[0]);
            answerBtn1.setTag(selectedFour[1]);
            answerBtn1.setText(selectedFour[1]);
            answerBtn2.setTag(selectedFour[2]);
            answerBtn2.setText(selectedFour[2]);
            answerBtn3.setTag(selectedFour[3]);
            answerBtn3.setText(selectedFour[3]);



        } catch (Exception e) {
            e.printStackTrace();
            //BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.imagenotavailable);
        }

    }

    public void checkAnswer(View view) {
        if (((View) view).getTag().toString().equals(celeNameArray.get(celebrityPtr))) {
            correctCount++;
            //Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
            answerImageView.setImageResource(R.drawable.correct);

        } else {
            //Toast.makeText(getApplicationContext(), "Wrong!", Toast.LENGTH_SHORT).show();
            correctCount--;
            if (correctCount<0) correctCount=0;
            answerImageView.setImageResource(R.drawable.wrong);
        }
        new CountDownTimer(500, 100) { // 5000 = 5 sec

            public void onTick(long millisUntilFinished) {
                answerImageView.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                answerImageView.setVisibility(View.INVISIBLE);                }
        }.start();
        scoreTextView.setText(String.valueOf(correctCount)+"/"+String.valueOf(totalQuestion));
        nextQuestion();
    }

    public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url;
            HttpURLConnection httpUrlConnection = null;
            HttpsURLConnection httpsUrlConnection = null;
            InputStream in = null;

            try {
                url = new URL(urls[0]);
                if (url.getProtocol().equals("https")) {
                    httpsUrlConnection = (HttpsURLConnection) url.openConnection();
                    httpsUrlConnection.connect();
                    in = httpsUrlConnection.getInputStream();

                } else {
                    httpUrlConnection = (HttpURLConnection) url.openConnection();
                    httpUrlConnection.connect();
                    in = httpUrlConnection.getInputStream();
                }
                Bitmap imgBitmap = BitmapFactory.decodeStream(in);
                return imgBitmap;
                //imageImageView.setImageDrawable(Drawable.createFromStream(in, "src"));
                //imageImageView.setImageBitmap(Drawable.createFromStream(in, "src");
                //return "Ok";

            } catch (Exception e) {
                e.printStackTrace();
                return BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.imagenotavailable);
            }

        }
    }

    public class FileLoaderTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            InputStream inputStream = getResources().openRawResource(R.raw.celebrities3);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int i;
            try {
                i = inputStream.read();
                while (i != -1)
                {
                    byteArrayOutputStream.write(i);
                    i = inputStream.read();
                }
                inputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return byteArrayOutputStream.toString();
        }
    }
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpsURLConnection httpsURLConnection = null;
            HttpURLConnection httpURLConnection = null;
            InputStream in;
            try {
                url = new URL(urls[0]);
                if (url.getProtocol().equals("https")) {
                    httpsURLConnection = (HttpsURLConnection) url.openConnection();
                    in = httpsURLConnection.getInputStream();
                } else {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    in = httpURLConnection.getInputStream();
                }

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char cuurrent = (char) data;

                    result += cuurrent;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "Failed Read Text File!";
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mainConstraintLayout = (ConstraintLayout)findViewById(R.id.mainConstraintLayout);
        goButton = findViewById(R.id.goButton);

        sharedPreferences = this.getSharedPreferences("com.sgneoyc.guessthecelebrity", Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("highscore", 0);
        highScoreLabel = findViewById(R.id.highScoreLabel);
        highScoreLabel.setVisibility(View.INVISIBLE);
        highScoreTextView = findViewById(R.id.highScoreTextView);
        highScoreTextView.setText(String.valueOf(highScore));
        highScoreTextView.setVisibility(View.INVISIBLE);

        celeImgsArray = new ArrayList();
        celeNameArray = new ArrayList();

        answerBtn0 = findViewById(R.id.button0);
        answerBtn1 = findViewById(R.id.button1);
        answerBtn2 = findViewById(R.id.button2);
        answerBtn3 = findViewById(R.id.button3);

        messageTextView = (TextView) findViewById(R.id.messageTextView);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        messageTextView.setVisibility(View.INVISIBLE);
        timerTextView.setVisibility(View.INVISIBLE);
        scoreTextView.setVisibility(View.INVISIBLE);

        imageImageView = (ImageView) findViewById(R.id.imageImageView);
        answerGridLayout = (GridLayout) findViewById(R.id.answerGridLayout);
        imageImageView.setVisibility(View.INVISIBLE);
        answerGridLayout.setVisibility(View.INVISIBLE);

        answerImageView = (ImageView) findViewById(R.id.answerImageView);
        answerImageView.setVisibility(View.INVISIBLE);
    }
}
