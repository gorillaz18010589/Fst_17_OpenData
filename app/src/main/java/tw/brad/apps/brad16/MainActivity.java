package tw.brad.apps.brad16;
//抓死open data
//從雲端灌進來從list view
//1,

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private SimpleAdapter adapter;
    private String[] from = {"title", "type"};
    private int[] to = {R.id.item_title, R.id.item_type};
    private LinkedList<HashMap<String,String>> data = new LinkedList<>();
    private UIHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new UIHandler();

        listView = findViewById(R.id.listView);//抓到listView
        initListView();

        fetchRemoteData();
    }
    //加入到listView
    private void initListView(){
        adapter = new SimpleAdapter(this //這個頁面
                ,data //灌入的資料
                ,R.layout.item //
                ,from, //陣列隔式
                to);//灌到哪邊
        listView.setAdapter(adapter);

        //每隔listView欄位
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);

                intent.putExtra("pic", data.get(i).get("pic"));//設定data取得的那個值,取得的pic
                intent.putExtra("content", data.get(i).get("content"));

                startActivity(intent);
            }
        });
    }

        //抓取open data
    private void fetchRemoteData(){
        new Thread(){
            @Override
            public void run() {
                try{
                    URL url = new URL("http://data.coa.gov.tw/Service/OpenData/RuralTravelData.aspx");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(conn.getInputStream()));
                    String line = null; StringBuffer sb = new StringBuffer();
                    while ( (line = reader.readLine()) != null){
                        sb.append(line);
                    }
                    reader.close();
                    Log.v("brad", sb.toString());
                    parseJSONData(sb.toString()); //先解json隔式
                }catch (Exception e){
                    Log.v("brad", e.toString());
                }
            }
        }.start();
    }
    //解析OpenData資料
    private void parseJSONData(String json){
        try {
            data.clear();
            JSONArray root = new JSONArray(json);
            for (int i=0; i<root.length(); i++){
                JSONObject row = root.getJSONObject(i);

                HashMap<String,String> d = new HashMap<>();
                d.put(from[0], row.getString("Title"));//陣列0,抓到的tilte資料,掛上去
                d.put(from[1], row.getString("TravelType")//陣列1.抓到的TravelType
                        .replace('\n',' ')
                        .replace('\r', ' ')
                        .replace("  ",""));
                d.put("pic", row.getString("PhotoUrl"));
                d.put("content", row.getString("Contents").replace('\r',' '));

                data.add(d);//把資料掛上去
            }

            handler.sendEmptyMessage(0);
        }catch (Exception e){
            Log.v("brad", e.toString());
        }
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            adapter.notifyDataSetChanged();

        }
    }



}