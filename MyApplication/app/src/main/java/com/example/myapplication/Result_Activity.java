package com.example.myapplication;

import static android.os.Environment.getExternalStoragePublicDirectory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Output;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class Result_Activity extends AppCompatActivity {
    ImageButton btBack_2, btSave;
    ImageView imageview_final;
    String file_path;
    Bitmap bitmap_final;
    private String base_url;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_scene);

        btBack_2 = (ImageButton) findViewById(R.id.bt_back);
        btSave = (ImageButton) findViewById(R.id.bt_save);
        imageview_final = (ImageView) findViewById(R.id.imageview_final);

        Bundle extras = getIntent().getExtras();

        bitmap_final = null;
        base_url = getIntent().getStringExtra("url_for_network");
        file_path = getIntent().getStringExtra("location");

        try{
            String image_path = getCacheDir() + "/" + file_path;
            bitmap_final = BitmapFactory.decodeFile(image_path);
            imageview_final.setImageBitmap(bitmap_final);
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
        }


        //??? ????????? ???????????? ?????? ?????? ???????????? ?????? ?????????????????? ???????????? ????????????
        connect();

        //?????? ?????? ???????????? ???????????? ????????????
        btBack_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CropperActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveBitmapToJpeg(bitmap_final);
            }
        });
    }

    //????????? ????????? ??????????????? ????????? ???????????? ??????
    //??????????????? API 28????????? ?????? ?????? ?????????
    //??????????????? API 30??? ????????? ????????? ??? ?????? ???????????? ??????
    public void saveBitmapToJpeg(Bitmap bmp){
        File file;
        String path = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        String timestamp = new SimpleDateFormat("HHmmss").format(new Date());
        file = new File(path, timestamp + "cropped.jpg");

        try{
            OutputStream outs = null;
            outs = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,outs);
            outs.flush();
            outs.close();
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
                    InputStream response = null; // ?????? ????????? ????????? ??????.
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";



                    URL url = new URL(base_url+"/remove_file");
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
                    //dos.writeBytes(twoHyphens + boundary + lineEnd);
                    // ?????? ????????? ?????????????????? file1 ???????????? camera.jpg??? ???????????? ??????
                    //dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"input_image.jpg\"" +lineEnd);


                    //dos.writeBytes(lineEnd);
                    // Bitmap??? ByteBuffer??? ??????

                    //dos.write(data);
                    //dos.writeBytes(lineEnd);
                    //dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    writer.flush(); // finish upload...
                    out.close();
                    Log.v("http", "send");
                    //response

                    Log.v("http", "response");
                    //Response stream??????
                    response.close();

                    // connection??????
                    con.disconnect();


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

        }catch (InterruptedException e){

            e.printStackTrace();

        }
    }

}
