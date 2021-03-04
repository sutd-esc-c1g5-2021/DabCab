package com.cabdab.wifi;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class TTSWebActivity extends AppCompatActivity {

    private String TAG = TTSWebActivity.class.toString();
    WebView webView;

    private boolean madePOSTDeclaration = false;

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
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (madePOSTDeclaration) {
                    Log.d(TAG, "Got post declaration and alert");
                    new AlertDialog.Builder(TTSWebActivity.this)
                            .setTitle("SUTD TTS")
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            result.confirm();
                                            finish();
                                        }
                                    }).setCancelable(false).create().show();

                    return true;
                }
                else if (url.endsWith("tt_temperature_taking_user.aspx")) {
                    Log.d(TAG, "Got temperature taking alert");
                    new AlertDialog.Builder(TTSWebActivity.this)
                            .setTitle("SUTD TTS")
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok,
                                    new AlertDialog.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            result.confirm();
                                            finish();
                                        }
                                    }).setCancelable(false).create().show();

                    return true;
                }
                Log.d(TAG, message);
                return super.onJsAlert(view, url, message, result);
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().endsWith("tt_daily_dec_user.aspx") && request.getMethod().toString().equals("POST"))  {

                    madePOSTDeclaration = true;
                    Log.d(TAG, "Made post declaration");
                }
                //else if (request.getUrl().toString().endsWith("tt_"))
                Log.d(TAG, request.getUrl().toString());
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page finished loading " + url);

                if (madePOSTDeclaration && url.endsWith("tt_daily_dec_user.aspx")) {
                    Log.d(TAG, "Completed declaration, exiting activity");
                   // finish();
                }

                webView.evaluateJavascript("javascript:document.getElementById(\"navigationtop\").style.display=\"none\";", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d(TAG, "Completed JS Execution " + s);
                    }
                });
                webView.evaluateJavascript("javascript:document.getElementById(\"navigationtopmobile\").style.display=\"none\";", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d(TAG, "Completed JS Execution " + s);
                    }
                });
            }
        });
        String declaration = (String) getIntent().getStringExtra("declaration");
        Log.d(TAG, "Expected declaration " + declaration);

        if (declaration.equals("daily")) {
            webView.loadUrl("https://tts.sutd.edu.sg/tt_daily_dec_user.aspx");
        }
        else {
            webView.loadUrl("https://tts.sutd.edu.sg/tt_temperature_taking_user.aspx");
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
