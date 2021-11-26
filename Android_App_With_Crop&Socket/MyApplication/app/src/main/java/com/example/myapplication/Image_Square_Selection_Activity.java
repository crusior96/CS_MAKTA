package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class Image_Square_Selection_Activity extends AppCompatActivity {
    Button change_btn_2;
    Button change_btn_back_0;
    ImageView imageview_capture2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_square_selection);

        change_btn_2 = (Button) findViewById(R.id.change_btn_2);
        change_btn_back_0 = (Button) findViewById(R.id.change_btn_back_0);
        imageview_capture2 = (ImageView) findViewById(R.id.imageview_capture2);
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        imageview_capture2.setImageBitmap(bitmap);

        change_btn_back_0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        change_btn_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Image_Square_Selection_Activity.this, Change_Pose_Selection_Activity.class);
                if(imageview_capture2.getDrawable() != null){
                    Bitmap bitmap = ((BitmapDrawable)imageview_capture2.getDrawable()).getBitmap();
                    float scale = (float) (1024/(float)bitmap.getWidth());
                    int image_w = (int) (bitmap.getWidth() * scale);
                    int image_h = (int) (bitmap.getHeight() * scale);
                    Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                    resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("image", byteArray);

                    startActivity(intent);
                }
                else if(imageview_capture2.getDrawable() == null){
                    String msg = "이미지를 꼭 추가해주세요";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
