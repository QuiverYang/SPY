package com.example.undercover;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class KillerChoose extends AppCompatActivity {
    private static final String TAG = "KillerChoose";
    private Bundle bundle;
    private String[] nicknames,answers;
    private RecyclerView rv;
    private Button btnExit,btnShowAnswer;
    private TextView tvHint1,tvHint2;
    private ArrayList<Player> players;
    private int playerNum, citizenNum, underoverNum,whiteBoardNum;
    private boolean hasWhiteBoard;
    private RecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_killer_choose);
        init();
        setBtnExit();
        setBtnShowAnswer();
    }

    private void setBtnExit(){
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KillerChoose.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setBtnShowAnswer(){
        btnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < playerNum; i++){
                    players.get(i).setIsDead(true);
                }
                adapter.notifyDataSetChanged();//查看所有改變過後的viewHolder狀態值
                tvHint1.setText("遊戲結束");
                tvHint2.setText("謎底揭曉");
                AlertDialog.Builder builder = new AlertDialog.Builder(KillerChoose.this);
                View dialogContent = LayoutInflater.from(KillerChoose.this).inflate(R.layout.dialog_show_answer,null);
                TextView tva1 = dialogContent.findViewById(R.id.tva1);
                TextView tva2 = dialogContent.findViewById(R.id.tva2);
                tva1.setText(bundle.getString(GameActivity.ANSWER_CITIZEN));
                tva2.setText(bundle.getString(GameActivity.ANSWER_UNDERCOVER));
                AlertDialog dialog = builder.setView(dialogContent).create();

                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


    }

    private void init(){
        btnExit = findViewById(R.id.btnExit);
        btnShowAnswer = findViewById(R.id.btnShowAnswer);
        tvHint1 = findViewById(R.id.tvHint1);
        tvHint2 = findViewById(R.id.tvHint2);
        bundle = getIntent().getExtras();
        playerNum = bundle.getInt(GameActivity.PLAYER_NUM);
        citizenNum = bundle.getInt(GameActivity.CITIZEN_NUM);
        underoverNum = bundle.getInt(GameActivity.UNDERCOVER_NUM);
        whiteBoardNum = bundle.getInt(GameActivity.WHITEBOARD);
        rv = findViewById(R.id.rv);
        initParams();

    }

    private void initParams(){
        players = bundle.getParcelableArrayList(GameActivity.PLAYER_LIST);
        nicknames = bundle.getStringArray(GameActivity.NICKNAMES);
        answers = bundle.getStringArray(GameActivity.ANSWERS);
        hasWhiteBoard = bundle.getBoolean(GameActivity.HASWHITEBOARD);
        RecyclerView.LayoutManager gm = new GridLayoutManager(this,2);
        adapter = new RecyclerViewAdapter(players,this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(gm);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<Player> players;
        Context mContext;

        public RecyclerViewAdapter(ArrayList<Player> players, Context mContext){
            this.players = players;
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if(i==0){
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_killer_choose,viewGroup,false);
                return new mViewHolder(view);
            }else{
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_killer_chosen,viewGroup,false);
                return new mViewHolder2(view);
            }
        }
        @Override
        public int getItemViewType(int position){
            if(players.get(position).isDead()){
                return 1;
            }
            return 0;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            if(!players.get(i).isDead() && viewHolder instanceof mViewHolder){

                ((mViewHolder)viewHolder).tvPlayerName.setText(players.get(i).name);

            }else{
                ((mViewHolder2)viewHolder).tvPlayerName2.setText(players.get(i).name);
                ((mViewHolder2)viewHolder).tvPlayerCharacter.setText(players.get(i).character);

            }

        }

        @Override
        public int getItemCount() {
            return players.size();
        }

        public class mViewHolder extends RecyclerView.ViewHolder{
            TextView tvPlayerName;
            LinearLayout llBox;
            public mViewHolder(@NonNull final View itemView) {
                super(itemView);
                tvPlayerName = itemView.findViewById(R.id.tvPlayerName);

                llBox = itemView.findViewById(R.id.llBox);
                llBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        View dialogContent = LayoutInflater.from(mContext).inflate(R.layout.dialog_kill_cancle,null);
                        Button btnCancle = dialogContent.findViewById(R.id.btnCancle);
                        Button btnKill = dialogContent.findViewById(R.id.btnKill);
                        final AlertDialog dialog = builder.setView(dialogContent).setCancelable(false).create();
                        dialog.show();
                        btnCancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        btnKill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                players.get(getLayoutPosition()).setIsDead(true);
                                if(players.get(getLayoutPosition()).answer.equals(bundle.getString(GameActivity.ANSWER_CITIZEN))){
                                    citizenNum--;
                                }else if(players.get(getLayoutPosition()).answer.equals(bundle.getString(GameActivity.ANSWER_UNDERCOVER))){
                                    underoverNum--;
                                }else{
                                    whiteBoardNum--;
                                }
                                Log.d(TAG, "whiteBoard: " + whiteBoardNum + " undercover: " + underoverNum + " citizen: "+ citizenNum);
                                //獲勝機制
                                if(hasWhiteBoard && whiteBoardNum > 0){
                                    if(underoverNum == 0){
                                        tvHint1.setText("遊戲結束");
                                        tvHint2.setText("臥底全部死亡,白板獲勝");
                                    }else if(citizenNum == 0){
                                        tvHint1.setText("遊戲結束");
                                        tvHint2.setText("臥底獲勝");
                                    }
                                }else{
                                    if(underoverNum == 0){
                                        tvHint1.setText("遊戲結束");
                                        tvHint2.setText("臥底全部死亡,平民獲勝");
                                    }else if(citizenNum <= underoverNum){
                                        tvHint1.setText("遊戲結束");
                                        tvHint2.setText("平民數太少,臥底獲勝");
                                    }
                                }
                                RecyclerViewAdapter.this.notifyDataSetChanged();//查看所有改變過後的viewHolder狀態值
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        }

        public class mViewHolder2 extends RecyclerView.ViewHolder{
            TextView tvPlayerName2, tvPlayerCharacter;
            public mViewHolder2(@NonNull View itemView) {
                super(itemView);
                tvPlayerName2 = itemView.findViewById(R.id.tvPlayerName2);
                tvPlayerCharacter = itemView.findViewById(R.id.tvPlayerCharacter);
            }
        }
    }
}
