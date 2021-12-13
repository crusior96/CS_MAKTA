package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Portrait_Selection_Activity extends AppCompatActivity {
    ImageView img_temp;
    ImageButton button_1, button_2;
    Button sig_1, sig_2, sig_3;
    JSONObject jsonObject;
    Bitmap res;
    String file_path, result_path;
    int sigma_value;
    private String base_url;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.portrait_selection);

        button_1 = (ImageButton) findViewById(R.id.button1);
        button_2 = (ImageButton) findViewById(R.id.button2);
        sig_1 = (Button) findViewById(R.id.sigma_1);
        sig_2 = (Button) findViewById(R.id.sigma_2);
        sig_3 = (Button) findViewById(R.id.sigma_3);

        img_temp = (ImageView) findViewById(R.id.image_temp);

        Bundle extras = getIntent().getExtras();

        Bitmap bitmap = null;
        file_path = getIntent().getStringExtra("location");

        try{
            String image_path = getCacheDir() + "/" + file_path;
            bitmap = BitmapFactory.decodeFile(image_path);
            img_temp.setImageBitmap(bitmap);
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
        }

        img_temp.setImageBitmap(bitmap);
        base_url = getIntent().getStringExtra("url_for_network");



        //이전 화면으로 돌아가는 버튼
        button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Portrait_Selection_Activity.this, Select_Option_Activity.class);
                intent.putExtra("location", file_path);
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
                Intent intent = new Intent(Portrait_Selection_Activity.this, Result_Activity.class);
                if(img_temp.getDrawable() != null){
                    Bitmap new_bitmap = ((BitmapDrawable)img_temp.getDrawable()).getBitmap();
                    saveBitmapToJpeg(new_bitmap);
                    intent.putExtra("location", result_path);
                    intent.putExtra("url_for_network", base_url);
                    startActivity(intent);
                    finish();
                }
            }
        });

        sig_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sigma_value = 1;
                connect();
            }
        });

        sig_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sigma_value = 2;
                connect();
            }
        });

        sig_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sigma_value = 3;
                connect();
            }
        });


    }

    public void saveBitmapToJpeg(Bitmap bmp){
        File file;
        String timestamp = new SimpleDateFormat("HHmmss").format(new Date());
        file = new File(getCacheDir(), timestamp + "cropped_result.jpg");
        result_path = timestamp + "cropped_result.jpg";
        try{
            file.createNewFile();
            OutputStream outs = null;
            outs = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,outs);
            outs.flush();
            outs.close();
            String msg = "사진 저장 완료";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();


        }catch(IOException e){
            e.printStackTrace();
        }

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


                    jsonObject = new JSONObject();
                    jsonObject.put("sigma", sigma_value);

                    String data = jsonObject.toString();




                    URL url = new URL(base_url+"/portrait_mode");
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
                    writer.write(data);
                    //dos.writeBytes(twoHyphens + boundary + lineEnd);
                    // 파일 전송시 파라메터명은 file1 파일명은 camera.jpg로 설정하여 전송
                    //dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"input_image.jpg\"" +lineEnd);


                    //dos.write(data);
                    //dos.writeBytes(lineEnd);
                    //dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    writer.flush(); // finish upload...
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
