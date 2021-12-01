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
    ImageButton button_1, button_2;
    Bitmap original_bitmap;
    private String base_url;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.segmentation_selection);

        button_1 = (ImageButton) findViewById(R.id.button1);
        button_2 = (ImageButton) findViewById(R.id.button2);
        img_temp = (ImageView) findViewById(R.id.image_temp);

        Bundle extras = getIntent().getExtras();

        //Select_Option_Activity로부터 받아온 편집된 이미지
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        //Select_Option_Acitivity로부터 받아온 원본 이미지
        byte[] byteArray_2 = getIntent().getByteArrayExtra("original");
        Bitmap bitmap_2 = BitmapFactory.decodeByteArray(byteArray_2, 0, byteArray_2.length);

        //이전 화면(편집메뉴 선택)으로 돌아가기 위해 마련해둔 원본 이미지 비트맵
        original_bitmap = bitmap_2;

        img_temp.setImageBitmap(bitmap_2);
        base_url = getIntent().getStringExtra("url_for_network");



        //이전 화면으로 돌아가는 버튼
        button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Segmentataion_Selection_Activity.this, Select_Option_Activity.class);
                float scale = (float) (1024/(float)bitmap_2.getWidth());
                int image_w = (int) (bitmap_2.getWidth() * scale);
                int image_h = (int) (bitmap_2.getHeight() * scale);
                Bitmap resize = Bitmap.createScaledBitmap(bitmap_2, image_w, image_h, true);
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

    }

}
