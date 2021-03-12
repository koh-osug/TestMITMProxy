package com.gigsky.testmitmproxy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void showError(Exception e) {
        runOnUiThread(() -> {
            String errorMsg = e.getMessage();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            final AlertDialog alertDialog = alertDialogBuilder
                    .setTitle(R.string.exception)
                    .setMessage(errorMsg)
                    .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                        dialog.dismiss();
                    }).
                            setCancelable(false).create();

            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        });
    }

    private void showOk() {
        runOnUiThread(() -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            final AlertDialog alertDialog = alertDialogBuilder
                    .setTitle(R.string.success)
                    .setMessage(R.string.success_msg)
                    .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                        dialog.dismiss();
                    }).
                            setCancelable(false).create();

            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        });
    }


    public void testCall(View view) {
        new Thread(() ->
        {
            try {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.callTimeout(30, TimeUnit.SECONDS);
                builder.connectTimeout(30, TimeUnit.SECONDS);
                builder.readTimeout(30, TimeUnit.SECONDS);
                builder.writeTimeout(30, TimeUnit.SECONDS);
                OkHttpClient client = builder.build();
                Request request = new Request.Builder()
                        .url("https://www.google.com")
                        .build();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Could not open page.");
                }
                showOk();
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
                showError(e);
            }
        }).start();

    }
}