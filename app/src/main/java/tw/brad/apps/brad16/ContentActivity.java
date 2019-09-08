package tw.brad.apps.brad16;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;

public class ContentActivity extends AppCompatActivity {
    private String pic, content;
    private ImageView img;
    private TextView tvContent;
    private Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        pic = getIntent().getStringExtra("pic");
        content = getIntent().getStringExtra("content");

        img = findViewById(R.id.img);
        tvContent = findViewById(R.id.content);
        tvContent.setText(content);

    }

    private  void fetchImage(){
        new Thread(){
            @Override
            public void run() {
                super.run();

                try{
                    URL url = new URL(pic) ;
                  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                  conn.connect();


                }catch (Exception e){
                    Log.v("brad",e.toString());
                }
            }
        }.start();
    }
}
