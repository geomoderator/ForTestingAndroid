package com.example.karamovtimur.fortesting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteActivity extends AppCompatActivity {
    private SharedPreferences mSettings;
    private ArrayList<String> itemsArray;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        ListView mainListView = (ListView) findViewById(R.id.ListViewFavorite);
        itemsArray = getStringArray();
        final ArrayList<String> titleArray = new ArrayList<>();
        for (String item:itemsArray) {
            String[] n = item.split("▬");
            titleArray.add(n[0]);
        }

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list, titleArray);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavoriteActivity.this, ResultActivity.class);
                intent.putExtra("title",String.valueOf(titleArray.toArray()[position]));
                intent.putExtra("link",String.valueOf(itemsArray.toArray()[position]));
                startActivity(intent);
            }
        });
        EditText et = (EditText)findViewById(R.id.editText4);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //посимвольная фильтрация
                FavoriteActivity.this.adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

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
