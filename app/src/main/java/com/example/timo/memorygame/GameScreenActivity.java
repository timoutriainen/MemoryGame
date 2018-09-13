package com.example.timo.memorygame;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "MyActivity";
    private boolean isShowing = false, gameLost = false;
    private int currentScore, currentIndex = 0;
    private List<Integer> buttonList = new ArrayList<>();
    private Button[] buttons;
    private int[] button_ids = {R.id.firstButton, R.id.secondButton, R.id.thirdButton, R.id.fourthButton, R.id.fifthButton,
                                R.id.sixthButton, R.id.seventhButton, R.id.eighthButton, R.id.ninthButton};
    private int delayValue = 300;
    private Random random;
    Button clickedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "GameScreenActivity::onCreate()");
        buttons = new Button[9];
        setContentView(R.layout.activity_game_screen);
        for (int i = 0; i < 9; i++) {
            buttons[i] = findViewById(button_ids[i]);
            buttons[i].setOnClickListener(this);
        }
        random = new Random();
        for (int i = 0; i < 3; i++) {
            createNextNumber();
        }
        setResult(RESULT_CANCELED);
        GameLogic temp = new GameLogic();
        temp.execute(null,null,null);
    }

    private void createNextNumber() {
        Log.d(TAG, "GameScreenActivity::createNextNumber()");
        int temp = random.nextInt(9) + 1;
        buttonList.add(temp);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "GameScreenActivity::onClick()");
        if (isShowing) {
            Log.d(TAG, "GameScreenActivity::onClick() - isShowing is true, returning");
            return;
        }

        if (v.getId() != button_ids[buttonList.get(currentIndex)-1]) {
            Log.d(TAG, "GameScreenActivity::onClick() - wrong button");
            gameLost = true;
            Toast.makeText(this, "Sorry, you lost!!! Your score:"+currentScore, Toast.LENGTH_SHORT).show();
            Intent data = new Intent();
            data.putExtra("result", currentScore);
            setResult(RESULT_OK, data);
            finish();
        } else {
            Log.d(TAG, "GameScreenActivity::onClick() - correct button");
            clickedButton = findViewById(v.getId());
            clickedButton.setBackgroundColor(getResources().getColor(R.color.gameRed, null));
            ConfirmLogic confirm = new ConfirmLogic();
            confirm.execute(null, null, null);
            Log.d(TAG, "GameScreenActivity::onClick() - button is now red");
            clickedButton.setBackgroundColor(getResources().getColor(R.color.gameGreen, null));
            Log.d(TAG, "GameScreenActivity::onClick() - button is now green");
            currentIndex++;
            if (currentIndex == buttonList.size()) {
                currentScore++;
                createNextNumber();
                currentIndex = 0;
                Toast.makeText(this, "Next round", Toast.LENGTH_SHORT).show();
                GameLogic temp = new GameLogic();
                temp.execute(null, null, null);
            }
        }
    }

    private class ConfirmLogic extends AsyncTask<Void, Void, Void> {

        private boolean isRed = false;

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "ConfirmLogic::doInBackground()");
            isRed = true;
            Log.d(TAG, "ConfirmLogic::doInBackground() - set to red");
            publishProgress();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                finish();
            }
            isRed = false;
            Log.d(TAG, "ConfirmLogic::doInBackground() - set to green");
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "ConfirmLogic::onProgressUpdate()");
            if (!isRed) {
                clickedButton.setBackgroundColor(getResources().getColor(R.color.gameGreen, null));
            } else {
                clickedButton.setBackgroundColor(getResources().getColor(R.color.gameRed, null));
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "ConfirmLogic::onPreExecute()");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "ConfirmLogic:onPostExecute()");
        }
    }

    private class GameLogic extends AsyncTask<Void, Void, Void> {

        private boolean isRed = false;
        private int currentButton = 0;

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "GameLogic::doInBackground()");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                finish();
            }

            for (int i = 0; i < buttonList.size(); i++) {
                currentButton = buttonList.get(i);
                Log.d(TAG, "GameLogic::doInBackground() - set to red");
                isRed = true;
                publishProgress();
                try {
                    Thread.sleep(delayValue);
                } catch (InterruptedException e) {
                    finish();
                }
                Log.d(TAG, "GameLogic::doInBackground() - set to green");
                isRed = false;
                publishProgress();
                try {
                    Thread.sleep(delayValue);
                } catch (InterruptedException e) {
                    finish();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "GameLogic::onProgressUpdate()");
            Button temp = findViewById(button_ids[currentButton-1]);
            if (isRed) {
                temp.setBackgroundColor(getResources().getColor(R.color.gameRed, null));
            } else {
                temp.setBackgroundColor(getResources().getColor(R.color.gameGreen, null));
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "GameLogic::onPreExecute()");
            isShowing = true;
            Toast.makeText(GameScreenActivity.this, "Memorize this", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "GameLogic:onPostExecute()");
            isShowing = false;
            Toast.makeText(GameScreenActivity.this, "Your turn", Toast.LENGTH_SHORT).show();
        }
    }
}

