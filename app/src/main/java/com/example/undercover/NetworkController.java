package com.example.undercover;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkController {
    private static final String TAG = "NetwrokController";

    //內部類別
    public abstract static class NetworkControllerCallback implements Callback{
        public abstract void onSuccess(JSONObject responsJson);
        public abstract void onFailure(String errorMsg);

        //private Handler MainThreadHandler
        private Dialog loadingDialog;
        private Handler mainThreadHandler;
        private boolean showToast;
        private Context context;

        public NetworkControllerCallback(Context context){
            mainThreadHandler = new Handler(Looper.getMainLooper());
            this.context = context;
            showToast = false;
        }

        public NetworkControllerCallback showErrorToast(){
            showToast = true;
            return this;
        }

        public NetworkControllerCallback enableLoadingDialog(){
            loadingDialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            loadingDialog.setCancelable(false);
            loadingDialog.setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_loading,null));
            loadingDialog.show();
            return this;
        }

        @Override
        public void onFailure(Call call, final IOException e) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, e.getMessage());
                    NetworkControllerCallback.this.onFailure(e.getMessage());
                    onCompletedP();
                }
            });
        }

        @Override
        public void onResponse(Call call, final Response response){

            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject responseJsonObject = new JSONObject(response.body().string());
                        if(responseJsonObject.getInt("status") != -1){

                            NetworkControllerCallback.this.onFailure(responseJsonObject.getString("msg"));

                            if(showToast){
                                Toast.makeText(context, responseJsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                            }

                        }else{
                            NetworkControllerCallback.this.onSuccess(responseJsonObject.getJSONObject("msg"));
                        }
                        onCompletedP();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });


        }

        private void onCompletedP(){
            if(loadingDialog != null){
                loadingDialog.dismiss();
            }
        }

    }
    //API route
    private static final String API_ROOT = "https://script.google.com/macros/s/AKfycbx139voITd8knGT5xlVBZESmPxGM61jEQoPcSTz-0y3kKc-MKJr/exec";

    //Const
    private static final String A_ROUTE = "";

    private static final String KEY_TOKEN = "";

    //Singleton instance
    private static NetworkController netwrokController;

    //Attributes
    private OkHttpClient okHttpClient;
    private String token;

    //constructor
    public static NetworkController getInstance(){
        if(netwrokController == null){
            netwrokController = new NetworkController();
            netwrokController.okHttpClient = new OkHttpClient();
        }
        return netwrokController;
    }

    public void requirePuzzle(NetworkControllerCallback callback){
        FormBody formbody = new FormBody.Builder()
                .add("command","getPuzzle")
                .build();
        Request request = new Request.Builder()
                .url(API_ROOT)
                .post(formbody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }


}
