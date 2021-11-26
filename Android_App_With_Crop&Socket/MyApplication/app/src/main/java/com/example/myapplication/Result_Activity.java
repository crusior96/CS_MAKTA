package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Result_Activity extends AppCompatActivity {
    Button change_btn_back_2;
    Button save_btn;
    ImageView imageview_capture4;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_scene);

        change_btn_back_2 = (Button) findViewById(R.id.change_btn_back_2);
        save_btn = (Button) findViewById(R.id.save_btn);
        imageview_capture4 = (ImageView) findViewById(R.id.imageview_capture4);

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageview_capture4.setImageBitmap(bitmap);

        change_btn_back_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Change_Pose_Selection_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String msg = "이미지 저장 완료";
                saveBitmapToJpeg(bitmap);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    //편집이 완료된 이미지뷰의 사진을 저장하는 기능
    //현재시간을 기준으로 한 파일명제작
    public void saveBitmapToJpeg(Bitmap bitmap){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File tempFile = new File(getCacheDir(), imageFileName);
        try{
            tempFile.createNewFile();
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            Toast.makeText(getApplicationContext(), "파일 저장 대성공", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "파일 저장 멸망", Toast.LENGTH_SHORT).show();
        }
    }
}
