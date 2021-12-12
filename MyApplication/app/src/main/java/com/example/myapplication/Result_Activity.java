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
    private String base_url;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_scene);

        btBack_2 = (ImageButton) findViewById(R.id.bt_back);
        btSave = (ImageButton) findViewById(R.id.bt_save);
        imageview_final = (ImageView) findViewById(R.id.imageview_final);

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap_final = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        base_url = getIntent().getStringExtra("url_for_network");


        imageview_final.setImageBitmap(bitmap_final);

        //이 화면에 들어오는 순간 원본 이미지는 아예 삭제해버리는 방향으로 진행한다
        connect();

        //아예 처음 화면으로 돌아가게 바꿔버림
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

    //편집이 완료된 이미지뷰의 사진을 저장하는 기능
    //안드로이드 API 28까지는 문제 없이 해결됨
    //안드로이드 API 30이 도입된 이후로 손 많이 봐야하는 상황
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



                    URL url = new URL(base_url+"/remove_file");
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
                    //dos.writeBytes(twoHyphens + boundary + lineEnd);
                    // 파일 전송시 파라메터명은 file1 파일명은 camera.jpg로 설정하여 전송
                    //dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"input_image.jpg\"" +lineEnd);


                    //dos.writeBytes(lineEnd);
                    // Bitmap을 ByteBuffer로 전환

                    //dos.write(data);
                    //dos.writeBytes(lineEnd);
                    //dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    writer.flush(); // finish upload...
                    out.close();
                    Log.v("http", "send");
                    //response

                    Log.v("http", "response");
                    //Response stream종료
                    response.close();

                    // connection종료
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
