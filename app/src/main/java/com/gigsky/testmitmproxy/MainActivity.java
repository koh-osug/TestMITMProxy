package com.gigsky.testmitmproxy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyStore;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

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
                builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.178.29", 9090)));
                Provider provider = new BouncyCastleProvider();
                Provider jsseProvider = new BouncyCastleJsseProvider();
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
                trustManagerFactory.init((KeyStore) null);
                X509TrustManager trustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
                Log.i(TAG, "Accepted issuers: "+ Arrays.asList(trustManager.getAcceptedIssuers()));
//                SSLContext sslContext = SSLContext.getInstance("TLSv1.2", jsseProvider);
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                builder.sslSocketFactory(sslSocketFactory, trustManager);

                OkHttpClient client = builder.build();
                Request request = new Request.Builder()
                        .url("https://www.google.com")
                        .build();
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Could not open page.");
                }
                Log.i(TAG, "Response: "+ response.body().string());
                showOk();
            } catch (Exception e) {
                Log.e(TAG, "Error", e);
                showError(e);
            }
        }).start();

    }
}