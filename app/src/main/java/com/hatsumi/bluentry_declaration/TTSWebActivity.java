package com.hatsumi.bluentry_declaration;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TTSWebActivity extends AppCompatActivity {

    private String TAG = TTSWebActivity.class.toString();
    WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_web);
        webView = findViewById(R.id.tts_webview);


        String cookieString = SUTD_TTS.getSutd_tts().getCookieString();
        Log.d(TAG, "Cookie string " + cookieString);
        CookieManager.getInstance().setCookie("https://tts.sutd.edu.sg", cookieString);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG,"SDk version above android L so forcibaly enabling ThirdPartyCookies");
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
        }


        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        String declaration = (String) getIntent().getStringExtra("declaration");
        Log.d(TAG, "Expected declaration " + declaration);

        webView.loadUrl("https://tts.sutd.edu.sg/tt_daily_dec_user.aspx");


    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
