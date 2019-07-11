package com.example.undercover;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DialogTitle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    public static final String NICKNAMES = "NICKNAMES";
    public static final String PLAYER_NUM = "PLAYER_NUM";
    public static final String CITIZEN_NUM = "CITIZEN_NUM";
    public static final String UNDERCOVER_NUM = "UNDERCOVER_NUM";
    public static final String WHITEBOARD = "WHITEBOARD";
    public static final String HASWHITEBOARD = "HASWHITEBOARD";
    public static final String ANSWERS = "ANSWERS";
    public static final String PLAYER_LIST = "PLAYER_LIST";
    public static final String ANSWER_CITIZEN = "ANSWER_CITIZEN";
    public static final String ANSWER_UNDERCOVER = "ANSWER_UNDERCOVER";
    public static final String CHARACTERS = "CHARACTERS";


    private TextView tvPlayer1, tvAnswer;
    private String[] nicknames,answers,characters;
    private int playerNum, citizenNum, underoverNum,whiteBoard;
    private DoubleClick dc;
    private DoubleClick.Click c1; // click linearLayout once and double click action
    private LinearLayout ll;
    private Bundle bundle;
    private int clickCount;
    private boolean isChangeScene;
    private String answerUndercover,answerCitizen;
    ArrayList<Player> players;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        init();
        tvPlayer1.setText("點擊兩下後開始遊戲");
        tvAnswer.setText("");
        clickLinearLayoutAction();

    }

    private void setAnswers(){
        answers = new String[playerNum];
        characters = new String[playerNum];
        String[] answerElement = new String[3];
        String[] characterElement = {"平民","臥底","白板"};
        answerElement[0] = answerCitizen;
        answerElement[1] = answerUndercover;
        answerElement[2] = "白板";
        Log.d("whiteBoard123 ", whiteBoard+"");
        Log.d("citizenNum123 ", citizenNum+"");
        Log.d("underCover123", underoverNum+"");
        int[] answerElementCount ={citizenNum,underoverNum,whiteBoard};
        int len = answerElementCount.length;
        for(int i = 0; i < playerNum; i++){
            int index = (int)(Math.random()*len);
            if(answerElementCount[index] == 0){
                swap(answerElementCount,index, len-1);
                swap(answerElement,index, len-1);
                swap(characterElement,index,len-1);
                len--;
                index = (int)(Math.random()*len);
            }
            answers[i] = answerElement[index];
            characters[i] = characterElement[index];
            answerElementCount[index]--;

        }

        for(int i = 0; i < answers.length; i++){
            Log.d("answer", answers[i] + " " + i);
        }

    }
    private void swap(String[] arr, int i1, int i2){
        String temp = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = temp;
    }
    private void swap(int[] arr, int i1, int i2){
        int temp = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = temp;
    }

    private void clickLinearLayoutAction(){
        c1 = new DoubleClick.Click() {
            @Override
            public void clickOnce() {
                Toast.makeText(GameActivity.this,"請連續點兩下", Toast.LENGTH_LONG).show();
            }

            @Override
            public void clickDouble() {
                if(isChangeScene && clickCount<playerNum){
                    tvPlayer1.setText("點擊兩下換下一個玩家");
                    tvAnswer.setText("");
                    isChangeScene = false;
                }else{
                    if(clickCount<playerNum){
                        tvAnswer.setText(answers[clickCount]);
                        tvPlayer1.setText(nicknames[clickCount++]);
                        isChangeScene = true;

                    }else{

                        Intent intent = new Intent(GameActivity.this, KillerChoose.class);
                        bundle.putStringArray(ANSWERS,answers);
                        bundle.putParcelableArrayList(PLAYER_LIST,players);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };
        dc.setC1(c1);
        dc.setTime(2000);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dc.click();
            }
        });
    }

    private void init(){
        bundle = getIntent().getExtras();
        answerCitizen = bundle.getString(ANSWER_CITIZEN);
        answerUndercover = bundle.getString(ANSWER_UNDERCOVER);
        nicknames = bundle.getStringArray(NICKNAMES);
        characters = bundle.getStringArray(CHARACTERS);
        tvPlayer1 = findViewById(R.id.tvPlayer1);
        tvAnswer = findViewById(R.id.tvAnswer);
        playerNum = bundle.getInt(PLAYER_NUM);
        citizenNum = bundle.getInt(CITIZEN_NUM);
        underoverNum = bundle.getInt(UNDERCOVER_NUM);
        whiteBoard = bundle.getInt(WHITEBOARD);
        ll = findViewById(R.id.gameActivity);
        initParams();
    }

    private void initParams(){
        setAnswers();
        dc = new DoubleClick();
        clickCount = 0;
        isChangeScene = false;
        players = new ArrayList<>();
        for(int j = 0; j < playerNum; j++){
            players.add( new Player(nicknames[j], answers[j],characters[j]));
        }
    }
}
