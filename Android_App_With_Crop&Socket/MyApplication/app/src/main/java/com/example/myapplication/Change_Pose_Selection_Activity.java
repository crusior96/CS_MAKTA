package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Change_Pose_Selection_Activity extends AppCompatActivity {
    Button change_btn_3, change_btn_back_1;
    RadioButton pose_1, pose_2, pose_3;
    RadioGroup radioGroup;
    ImageView imageview_capture3;
    private Handler mHandler;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String ip = "서버IP";
    private int port = 8081;
    private String img_path;
    int pose_number;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pose_selection);

        change_btn_3 = (Button) findViewById(R.id.change_btn_3);
        change_btn_back_1 = (Button) findViewById(R.id.change_btn_back_1);
        imageview_capture3 = (ImageView) findViewById(R.id.imageview_capture3);
        pose_1 = (RadioButton) findViewById(R.id.pose_1);
        pose_2 = (RadioButton) findViewById(R.id.pose_2);
        pose_3 = (RadioButton) findViewById(R.id.pose_3);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageview_capture3.setImageBitmap(bitmap);

        pose_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                imageview_capture3.setImageResource(R.drawable.test2);
            }
        });

        pose_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                imageview_capture3.setImageResource(R.drawable.test2);
            }
        });

        pose_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                imageview_capture3.setImageResource(R.drawable.test2);
            }
        });

        change_btn_back_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), Image_Square_Selection_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        change_btn_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Change_Pose_Selection_Activity.this, Result_Activity.class);
                if(imageview_capture3.getDrawable() != null){
                    Bitmap bitmap = ((BitmapDrawable)imageview_capture3.getDrawable()).getBitmap();
                    float scale = (float) (1024/(float)bitmap.getWidth());
                    int image_w = (int) (bitmap.getWidth() * scale);
                    int image_h = (int) (bitmap.getHeight() * scale);
                    Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                    resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("image", byteArray);

                    startActivity(intent);
                }
                else if(imageview_capture3.getDrawable() == null){
                    String msg = "이미지를 꼭 추가해주세요";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        });

    }



}
