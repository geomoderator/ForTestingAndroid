package com.example.karamovtimur.fortesting;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.*;

import java.util.*;


public class ResultActivity extends AppCompatActivity {
    private SharedPreferences mSettings;
    public String link;
    public String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        link = getIntent().getStringExtra("link");
        title = getIntent().getStringExtra("title");
        WebView wv = (WebView)findViewById(R.id.MainWebView);
        wv.loadUrl(link);
        updateSetting();
    }
    public void updateSetting(){
        ArrayList<String>arr=  getStringArray();
        Button b = (Button)findViewById(R.id.buttonFavorite);
        b.setText(String.valueOf(arr.size()));
    }
    public void addToFavoriteList(View view){
        setStringArray(title+"▬"+link);
        updateSetting();
    }
    //Два повторяющихся метода для работы с локальным хранилищем. Можно вынести в отдельный класс.
    public void setStringArray(String item) {
        if(item != null) {
            mSettings = getSharedPreferences("mySettings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            Set<String> hs = mSettings.getStringSet("Links", new HashSet<String>());
            hs.add(item);
            editor.putStringSet("Links",hs);
            editor.clear();
            editor.commit();
        }
    }
    public ArrayList<String> getStringArray() {
        mSettings = getSharedPreferences("mySettings", Context.MODE_PRIVATE);
        Set<String> hs = mSettings.getStringSet("Links", new HashSet<String>());
        ArrayList<String> l = new ArrayList<String>(hs);
        return l;
    }

}
