package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class Pose_Number_Selection_Activity extends AppCompatActivity {
    ImageView img_temp;
    ImageButton button_1, button_2;
    BottomNavigationView BNV;
    JSONObject jsonObject;
    Bitmap res;
    String file_path, result_path;
    int pose_number;
    private String base_url;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.pose_number_selection);

        button_1 = (ImageButton) findViewById(R.id.button);
        button_2 = (ImageButton) findViewById(R.id.button2);
        img_temp = (ImageView) findViewById(R.id.image_temp);
        BNV = (BottomNavigationView) findViewById(R.id.pose_bottom_menu);

        Bundle extras = getIntent().getExtras();
        Bitmap bitmap = null;
        file_path = getIntent().getStringExtra("location");

        try{
            String image_path = getCacheDir() + "/" + file_path;
            bitmap = BitmapFactory.decodeFile(image_path);
            img_temp.setImageBitmap(bitmap);
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
        }

        img_temp.setImageBitmap(bitmap);
        base_url = getIntent().getStringExtra("url_for_network");

        //?????? ???????????? ???????????? ??????
        button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Pose_Number_Selection_Activity.this, Select_Option_Activity.class);
                intent.putExtra("location", file_path);
                intent.putExtra("url_for_network", base_url);
                startActivity(intent);
                finish();
            }
        });

        //result ???????????? ???????????? ??????
        button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Pose_Number_Selection_Activity.this, Result_Activity.class);
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

        BNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.first_pose:
                        pose_number = 1;
                        connect("/posetransfer");
                        return true;
                    case R.id.second_pose:
                        pose_number = 2;
                        connect("/posetransfer");
                        return true;
                    case R.id.third_pose:
                        pose_number = 3;
                        connect("/posetransfer");
                        return true;
                    case R.id.fourth_post:
                        pose_number = 5;
                        connect("/posetransfer");
                        return true;
                }
                return false;
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


        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void connect(String option){
        Thread uThread = new Thread() {
            @Override
            public void run() {
                try {
                    Bitmap res_bitmap = null;
                    InputStream response = null; // ?????? ????????? ????????? ??????.
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";


                    jsonObject = new JSONObject();
                    jsonObject.put("pose", pose_number);

                    String data = jsonObject.toString();




                    URL url = new URL(base_url+option);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true); //input ??????
                    con.setDoOutput(true);  // output ??????
                    con.setUseCaches(false);   // cache copy??? ???????????? ?????????.
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Connection", "Keep-Alive");
                    con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                    // write data
                    OutputStream out = new DataOutputStream(con.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    writer.write(data);
                    //dos.writeBytes(twoHyphens + boundary + lineEnd);
                    // ?????? ????????? ?????????????????? file1 ???????????? camera.jpg??? ???????????? ??????
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
                    //Response stream??????
                    response.close();

                    // connection??????
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

            img_temp.setImageBitmap(res);
            img_temp.invalidate();

        }catch (InterruptedException e){

            e.printStackTrace();

        }
    }

}
