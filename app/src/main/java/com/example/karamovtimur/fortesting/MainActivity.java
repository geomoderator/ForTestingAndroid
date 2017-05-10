package com.example.karamovtimur.fortesting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;


public class MainActivity extends AppCompatActivity {
    private SharedPreferences mSettings;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RequestTask().execute("http://api.stackexchange.com/2.2/questions?page=3&order=desc&sort=activity&site=stackoverflow");
        ArrayList<String>arr =  getStringArray();
        Button b = (Button)findViewById(R.id.button2);
        b.setText(String.valueOf(arr.size()));
        final EditText et = (EditText)findViewById(R.id.editText);
        et.addTextChangedListener(new TextWatcher() {//живой обработчик ввода текста
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                MainActivity.this.adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
    }
//Кнопка Избранного
    public void ButtonClick(View view){
        Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
        startActivity(intent);
    }
//Асинхронный класс http запроса
    class RequestTask extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... uri) {
            String response = "";
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(uri[0]);
            httpGet.setHeader("Content-Type", "application/json");

            try {
                HttpResponse execute = client.execute(httpGet);
                //Log.v("AAAAA напиши, что не так..", execute.getEntity().getContentEncoding().toString());
                InputStream content = new GZIPInputStream(execute.getEntity().getContent());

                BufferedReader buffer = new BufferedReader(new InputStreamReader(content),8192);
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Проблема с подключением!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                Toast.makeText(getApplicationContext(), ":-("+e.toString(), Toast.LENGTH_LONG).show();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ListView mainListView = (ListView) findViewById(R.id.MainListView);
            final List<String> itemsArray =new ArrayList<>();
            final List<String> itemsLinksArray =new ArrayList<>();
            adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list, itemsArray);
            JSONObject dataJsonObj = null;
            try {
                dataJsonObj = new JSONObject(result);
                JSONArray itemsJson = dataJsonObj.getJSONArray("items");
                for(int i =0; i<itemsJson.length();i++){
                    JSONObject item = itemsJson.getJSONObject(i);
                    final String title = item.getString("title");
                    final String link = item.getString("link");
                    itemsArray.add(title);
                    itemsLinksArray.add(link);
                }

            }catch (Exception ex){
                Log.v("",ex.toString());
            }


            mainListView.setAdapter(adapter);
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putExtra("link",String.valueOf(itemsLinksArray.toArray()[position]));
                    intent.putExtra("title",String.valueOf(itemsArray.toArray()[position]));
                    startActivity(intent);
                }
            });

        }

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
