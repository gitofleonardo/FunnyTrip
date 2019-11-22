package cn.huangchengxi.funnytrip.activity.home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.tips.MyTipsActivity;
import cn.huangchengxi.funnytrip.activity.tips.StarTipActivity;

public class TipsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView back;
    private EditText searchBar;
    private ImageView info;
    private WebView webView;
    private String currentUrl="http://m.ctrip.com/html5";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        init();
    }
    private void init(){
        String intentUrl=getIntent().getStringExtra("url");
        if (intentUrl!=null){
            currentUrl=intentUrl;
        }
        progressBar=findViewById(R.id.progress);
        progressBar.setProgress(0);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        info=findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomOptions();
            }
        });
        searchBar=findViewById(R.id.search);
        webView=findViewById(R.id.web);
        WebSettings settings=webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDomStorageEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.supportMultipleWindows();
        settings.setSupportMultipleWindows(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(webView.getContext().getCacheDir().getAbsolutePath());
        settings.setNeedInitialFocus(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                searchBar.setText(request.getUrl().toString());
                currentUrl=request.getUrl().toString();
                return false;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });
        webView.loadUrl(currentUrl);
        searchBar.setText(currentUrl);
    }
    private void showBottomOptions(){
        final BottomSheetDialog bsd=new BottomSheetDialog(this);
        View view=View.inflate(this,R.layout.view_tips_bottom,null);
        view.findViewById(R.id.starred).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.dismiss();
                StarTipActivity.startStarTipsActivity(TipsActivity.this,webView.getUrl());
            }
        });
        view.findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.dismiss();
                Uri uri=Uri.parse(webView.getUrl());
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.copy_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.dismiss();
                ClipboardManager manager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data=ClipData.newPlainText("Label", webView.getUrl());
                manager.setPrimaryClip(data);
                Toast.makeText(TipsActivity.this, "链接已复制", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.dismiss();
                AlertDialog.Builder builder=new AlertDialog.Builder(TipsActivity.this);
                builder.setTitle("数据来源:携程网").setMessage(webView.getUrl()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        view.findViewById(R.id.my_tips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.dismiss();
                startActivity(new Intent(TipsActivity.this, MyTipsActivity.class));
            }
        });
        view.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.dismiss();
                webView.reload();
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.dismiss();
            }
        });
        bsd.setContentView(view);
        bsd.show();
    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
            currentUrl=webView.getUrl();
        }else{
            super.onBackPressed();
        }
    }
    public static void startTipsActivity(Context context,String url){
        Intent intent=new Intent(context,TipsActivity.class);
        intent.putExtra("url",url);
        context.startActivity(intent);
    }
}
