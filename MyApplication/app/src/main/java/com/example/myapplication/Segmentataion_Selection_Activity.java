package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Segmentataion_Selection_Activity extends AppCompatActivity {
    ImageView img_temp;
    ImageButton button_1, button_2, button_3;
    JSONObject jsonObject;
    Bitmap res;
    private String base_url;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.segmentation_selection);

        button_1 = (ImageButton) findViewById(R.id.button1);
        button_2 = (ImageButton) findViewById(R.id.button2);
        button_3 = (ImageButton) findViewById(R.id.button3);
        img_temp = (ImageView) findViewById(R.id.image_temp);

        Bundle extras = getIntent().getExtras();

        //Select_Option_Activity로부터 받아온 원본 이미지
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        img_temp.setImageBitmap(bitmap);
        base_url = getIntent().getStringExtra("url_for_network");



        //이전 화면으로 돌아가는 버튼
        button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Segmentataion_Selection_Activity.this, Select_Option_Activity.class);
                float scale = (float) (1024/(float)bitmap.getWidth());
                int image_w = (int) (bitmap.getWidth() * scale);
                int image_h = (int) (bitmap.getHeight() * scale);
                Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("image", byteArray);
                intent.putExtra("url_for_network", base_url);
                startActivity(intent);
                finish();
            }
        });

        //result 화면으로 진행하는 버튼
        button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Segmentataion_Selection_Activity.this, Result_Activity.class);
                if(img_temp.getDrawable() != null){
                    Bitmap new_bitmap = ((BitmapDrawable)img_temp.getDrawable()).getBitmap();
                    float scale = (float) (1024/(float)new_bitmap.getWidth());
                    int image_w = (int) (new_bitmap.getWidth() * scale);
                    int image_h = (int) (new_bitmap.getHeight() * scale);
                    Bitmap resize = Bitmap.createScaledBitmap(new_bitmap, image_w, image_h, true);
                    resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("image", byteArray);
                    intent.putExtra("url_for_network", base_url);
                    startActivity(intent);
                    finish();
                }
            }
        });

        button_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                connect();
            }
        });

    }

    public void connect(){
        Thread uThread = new Thread() {
            @Override
            public void run() {
                try {
                    Bitmap res_bitmap = null;
                    InputStream response = null; // 요청 결과를 저장할 변수.
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";




                    URL url = new URL(base_url+"/segmentation");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true); //input 허용
                    con.setDoOutput(true);  // output 허용
                    con.setUseCaches(false);   // cache copy를 허용하지 않는다.
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Connection", "Keep-Alive");
                    con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                    // write data
                    OutputStream out = new DataOutputStream(con.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    //writer.write(data);
                    //dos.writeBytes(twoHyphens + boundary + lineEnd);
                    // 파일 전송시 파라메터명은 file1 파일명은 camera.jpg로 설정하여 전송
                    //dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"input_image.jpg\"" +lineEnd);


                    //dos.write(data);
                    //dos.writeBytes(lineEnd);
                    //dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    //writer.flush(); // finish upload...
                    out.close();
                    Log.v("http", "send");
                    //response
                    response = con.getInputStream();
                    res_bitmap = BitmapFactory.decodeStream(response);

                    Log.v("http", "response");
                    //Response stream종료
                    response.close();

                    // connection종료
                    con.disconnect();
                    res = res_bitmap;


                } catch (Exception e) {
                    e.printStackTrace();
                }


                //NetworkTask networkTask = new NetworkTask(url, null);
                //networkTask.execute();
            }
        };
        uThread.start();
        try{
            uThread.join();
            img_temp.setScaleType(ImageView.ScaleType.FIT_CENTER);
            img_temp.setImageBitmap(res);
            img_temp.invalidate();
        }catch (InterruptedException e){

            e.printStackTrace();

        }
    }

}
