package com.example.undercover;

import android.os.Handler;

public class DoubleClick {

    public interface Click {
        void clickOnce();
        void clickDouble();
    }
    private Click c1;
    private boolean isClicked;
    private int time;
    private Handler hd;

    public DoubleClick(){
        hd = new Handler();
    }

    public DoubleClick(Click c1, int millsec){
        this();
        isClicked = false;
        this.c1 = c1;
        time = millsec;

    }
    public void click(){
        if(!isClicked){
            isClicked = true;
            c1.clickOnce();
            hd.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isClicked = false;
                }
            },time);
        }else{
            c1.clickDouble();
            isClicked = false;
        }
    }

    public void setC1(Click c1){
        this.c1 = c1;
    }
    public void setTime(int millsec){
        this.time = millsec;
    }
}
