package com.example.undercover;

import android.os.Parcel;
import android.os.Parcelable;


public class Player implements Parcelable {

    String name,character,answer;
    boolean isDead;
    public Player(){

    }
    public Player(String name, String answer, String character){
        this.name = name;
        this.character = character;
        this.answer = answer;
    }

    protected Player(Parcel in) {
        name = in.readString();
        character = in.readString();
        answer = in.readString();
        isDead = in.readByte() != 0;
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            String name = in.readString();
            String character = in.readString();
            String answer = in.readString();
            byte b = in.readByte();
            Player player = new Player(name,answer, character);
            if(b == 1){
                player.isDead = true;
            }else{
                player.isDead = false;
            }
            return player;
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public boolean isDead(){
        return isDead;
    }

    public void setIsDead(boolean isDead){
        this.isDead = isDead;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(character);
        dest.writeString(answer);
        byte b;
        if(isDead){
            b = 1;
        }else{
            b = 0;
        }
        dest.writeByte(b);
    }
}
