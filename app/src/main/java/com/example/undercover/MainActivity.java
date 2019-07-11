package com.example.undercover;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView tvPlayerNum,tvUnderCoverNum,tvCitizenNum;
    private Button btnAdd, btnMinus, btnStart;
    private SeekBar seekBar;
    private Switch swAddWhiteBoard,swNickname, swAnswer;
    private int playerNum,underCoverNum,citizenNum,coverMax,whiteBoardNum;
    private SharedPreferences sp;
    private boolean isHasNickname, isHasAnswer, isHasWhiteBoard;
    private String answerCitizen, answerUndercover,defaultAnswerCitizen, defaultAnswerUndercover;
    private String[] nicknames;
    private RecyclerView rv;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initParams();
        setSwAnswer();
        setSeekBar();
        setBtnAdd();
        setBtnMinus();
        setSwAddWhiteBoard();
        setSwNickname();
        setBtnStart();

    }

    private void setBtnStart() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                Intent indent = new Intent(MainActivity.this,GameActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray(GameActivity.NICKNAMES,nicknames);
                bundle.putInt(GameActivity.PLAYER_NUM,playerNum);
                bundle.putInt(GameActivity.CITIZEN_NUM, citizenNum);
                bundle.putInt(GameActivity.UNDERCOVER_NUM, underCoverNum);
                bundle.putInt(GameActivity.WHITEBOARD, whiteBoardNum);
                bundle.putBoolean(GameActivity.HASWHITEBOARD, isHasWhiteBoard);
                if(answerCitizen == null || answerUndercover==null){
                    bundle.putString(GameActivity.ANSWER_CITIZEN, defaultAnswerCitizen);
                    bundle.putString(GameActivity.ANSWER_UNDERCOVER, defaultAnswerUndercover);
                }else{
                    bundle.putString(GameActivity.ANSWER_CITIZEN, answerCitizen);
                    bundle.putString(GameActivity.ANSWER_UNDERCOVER, answerUndercover);
                }

                indent.putExtras(bundle);
                startActivity(indent);
            }

        });
    }

    private void setSwNickname() {
        swNickname.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                nicknames = new String[playerNum];
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogContent = LayoutInflater.from(MainActivity.this).inflate(R.layout.nickname,null);
                final AlertDialog dialog = builder.setView(dialogContent).setCancelable(false).create();
                Button btnNickname = dialogContent.findViewById(R.id.btnNickname);
                RecyclerView.LayoutManager ll = new LinearLayoutManager(dialogContent.getContext());
                final RecyclerViewAdapter adapter = new RecyclerViewAdapter(dialog);
                rv = dialogContent.findViewById(R.id.rvNickname);
                rv.setAdapter(adapter);
                rv.setLayoutManager(ll);


                btnNickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0; i < nicknames.length; i++){
                            if(nicknames[i]==null||nicknames[i].equals("")){
                                nicknames[i] = "玩家" + (i+1);
                            }
                        }
                        dialog.dismiss();

                    }
                });
                dialog.show();
            }else{
                setDefaultNicknames();
            }
            }
        });
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        AlertDialog dialog;

        public RecyclerViewAdapter(AlertDialog dialog){
            this.dialog = dialog;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_nickname,null);
            return new mViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ((mViewHolder)viewHolder).etName.setText(nicknames[i]);
            int temp = i+1;
            String s = "玩家 " + temp + ": ";
            ((mViewHolder)viewHolder).tvHint.setText(s);
            ((mViewHolder)viewHolder).etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                }
            });


        }

        @Override
        public int getItemCount() {
            return playerNum;
        }

        public class mViewHolder extends RecyclerView.ViewHolder{
            TextView tvHint;
            EditText etName;
            public mViewHolder(@NonNull View itemView) {
                super(itemView);
                tvHint = itemView.findViewById(R.id.tvNameHint);
                etName = itemView.findViewById(R.id.etName);
                etName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        nicknames[getAdapterPosition()] = etName.getText().toString();
                    }
                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }
    }

    private void setSwAddWhiteBoard() {
        swAddWhiteBoard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && (whiteBoardNum == 1 || whiteBoardNum == 0)){
                    citizenNum--;
                    whiteBoardNum++;
                    isHasWhiteBoard = true;
                }else{
                    citizenNum++;
                    whiteBoardNum--;
                    isHasWhiteBoard = false;
                }
                setTvNum();

            }
        });
    }

    private void setBtnMinus() {
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(underCoverNum > 1){
                    underCoverNum--;
                    citizenNum++;
                }
                setTvNum();

            }
        });
    }

    private void setBtnAdd(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverMax = playerNum < 10 ? 2:3;

                if(underCoverNum < coverMax){
                    underCoverNum++;
                    citizenNum--;
                }
                setTvNum();

            }
        });
    }

    private void setSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                playerNum = seekBar.getProgress();
                citizenNum = playerNum-underCoverNum-whiteBoardNum;
                if(playerNum <10 && underCoverNum>2){
                    underCoverNum--;
                    citizenNum = playerNum-underCoverNum-whiteBoardNum;
                }
                setTvNum();
                setDefaultNicknames();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setSwAnswer(){
        swAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View dialogContent = LayoutInflater.from(MainActivity.this).inflate(R.layout.answer,null);
                    Button button = dialogContent.findViewById(R.id.button);
                    final EditText etAnswerCitizen = dialogContent.findViewById(R.id.etAnswerCitizen);
                    final EditText etAnswerUndercover = dialogContent.findViewById(R.id.etAnswerUndercover);

                    final AlertDialog dialog = builder.setView(dialogContent).setCancelable(false).create();

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            isHasAnswer = true;
                            String enteredCitizen = etAnswerCitizen.getText().toString();
                            String enteredUndercover = etAnswerUndercover.getText().toString();

                            if(!enteredCitizen.equals("") && !enteredUndercover.equals("")){
                                answerCitizen = enteredCitizen;
                                answerUndercover = enteredUndercover;
                            }

                        }
                    });
                    dialog.show();
                }else{
                    answerCitizen = defaultAnswerCitizen;
                    answerUndercover = defaultAnswerUndercover;
                }
            }
        });
    }

    private void init(){
        seekBar = findViewById(R.id.seekBar);
        tvPlayerNum = findViewById(R.id.tvPlayerNum);
        tvUnderCoverNum = findViewById(R.id.tvUnderCoverNum);
        tvCitizenNum = findViewById(R.id.tvCitizenNum);
        btnAdd = findViewById(R.id.btnAdd);
        btnMinus = findViewById(R.id.btnMinus);
        btnStart = findViewById(R.id.btnStart);
        swAddWhiteBoard = findViewById(R.id.swAddWhiteBoard);
        swNickname = findViewById(R.id.swNickname);
        swAnswer = findViewById(R.id.swAnswer);
    }

    private void initParams(){
        sp = this.getSharedPreferences("DATA", MODE_PRIVATE);
        playerNum = sp.getInt("PLAYER_NUM", 5);
        citizenNum = sp.getInt("CITIZEN_NUM", 5);
        underCoverNum = sp.getInt("UNDERCOVER_NUM", 1);
        whiteBoardNum = sp.getInt("WHITEBOARD_NUM", 0);
        isHasAnswer = false;
        isHasNickname = sp.getBoolean("HASNICKNAME",false);
        isHasWhiteBoard = sp.getBoolean("HASWHITEBOARD", false);
        setDefaultNicknames();
        seekBar.setProgress(playerNum);
        seekBar.setMax(15);
        seekBar.setMin(5);
        setTvNum();
        swAddWhiteBoard.setChecked(isHasWhiteBoard);
        swAnswer.setChecked(false);
        swNickname.setChecked(false);

        apiConnect();

    }

    private void apiConnect(){
        NetworkController.getInstance().requirePuzzle(new NetworkController.NetworkControllerCallback(MainActivity.this) {
            @Override
            public void onSuccess(JSONObject responsJson) {
                try {
                    JSONArray jsonArray = responsJson.getJSONArray("puzzles");

                    int r = (int)(Math.random()*jsonArray.length());
                    defaultAnswerCitizen = answerCitizen = jsonArray.getJSONObject(r).getString("p1");
                    defaultAnswerUndercover = answerUndercover = jsonArray.getJSONObject(r).getString("p2");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }.enableLoadingDialog());
    }

    private void setDefaultNicknames(){
        nicknames = new String[playerNum];
        for(int i = 0; i < playerNum; i++){
            nicknames[i] = "玩家 " + (i+1);
        }
    }

    private void setTvNum(){
        tvPlayerNum.setText(String.valueOf(playerNum));
        tvUnderCoverNum.setText(String.valueOf(underCoverNum));
        tvCitizenNum.setText(String.valueOf(citizenNum));
    }

    private void save(){
        sp.edit().putInt("PLAYER_NUM",playerNum)
                .putInt("CITIZEN_NUM",citizenNum)
                .putInt("UNDERCOVER_NUM", underCoverNum)
                .putInt("WHITEBOARD_NUM", whiteBoardNum)
                .putBoolean("HASANSWER",isHasAnswer)
                .putBoolean("HASNICKNAME",isHasNickname)
                .putBoolean("HASWHITEBOARD",isHasWhiteBoard)
                .putString("ANSWER_CITIZEN", answerCitizen)
                .putString("ANSWER_UNDERCOVER", answerUndercover)
                .apply();
    }

    private void reset(){
        sp.edit().putInt("PLAYER_NUM",5)
                .putInt("CITIZEN_NUM",5)
                .putInt("UNDERCOVER_NUM", 0)
                .putInt("WHITEBOARD_NUM", 0)
                .putBoolean("HASWHITEBOARD",false)
                .putBoolean("HASANSWER",false)
                .putBoolean("HASNICKNAME",false)
                .putString("NICKNAME",null)
                .putString("ANSWER_CITIZEN", null)
                .putString("ANSWER_UNDERCOVER", null)
                .apply();

    }


}
