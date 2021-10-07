package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button gallerybtn;
    Button camerabtn;
    Button change_btn_1;
    ImageView imageview_capture;
    File file;

    String file_name;
    final int GET_GALLERY_IMAGE = 200;
    final int GET_CAMERA_IMAGE = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        gallerybtn = (Button) findViewById(R.id.gallerybtn);
        camerabtn = (Button) findViewById(R.id.camerabtn);
        change_btn_1 = (Button) findViewById(R.id.change_btn_1);
        imageview_capture = (ImageView) findViewById(R.id.imageview_capture);

        File sdcard = Environment.getExternalStorageDirectory();
        file = new File(sdcard, "captured.jpg");
        Uri photoUri;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        //갤러리 접속하는 버튼
        //접속되는것 확인 + 갤러리에서 사진 들고오는 거 실행되는 것 확인
        //사진 사이즈가 imageview에 맞춰서 나오는 것인지는 의문
        gallerybtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent,GET_GALLERY_IMAGE);
            }
        });

        //카메라 기능 사용하는 버튼
        //작동되는것 확인, 화면의 가로세로 상태는 아무래도 노트북 테스트로 하다보니 문제 좀 있는듯
        //화질 개선 관련해서 찾아봐야하는 상황
        camerabtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                startActivityForResult(intent, GET_CAMERA_IMAGE);
            }
        });

        //다음 화면으로 전환
        //imageview에 사진 없으면 진행안되게 설정함
        //다음 화면으로 사진은 잘 전송되는데 일부 케이스는 돌아오는 버튼 누를때 에러 발생함
        //뒤로가기 버튼 부분은 상의해서 빼버리든지 하면 좋을 것 같음
        change_btn_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(MainActivity.this, Image_Square_Selection_Activity.class);
                if(imageview_capture.getDrawable() != null){
                    Bitmap bitmap = ((BitmapDrawable)imageview_capture.getDrawable()).getBitmap();
                    float scale = (float) (1024/(float)bitmap.getWidth());
                    int image_w = (int) (bitmap.getWidth() * scale);
                    int image_h = (int) (bitmap.getHeight() * scale);
                    Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                    resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("image", byteArray);

                    startActivity(intent);
                }
                else if(imageview_capture.getDrawable() == null){
                    String msg = "이미지를 꼭 추가해주세요";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String name = getName(selectedImageUri);
            Log.e("###","파일이름 : " + name + "\n");
            imageview_capture.setImageURI(selectedImageUri);
        }
        else if(requestCode == GET_CAMERA_IMAGE && resultCode == RESULT_OK && data.hasExtra("data")){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            if(bitmap != null){
                imageview_capture.setImageBitmap(bitmap);
            }
        }
    }

    private String getName(Uri uri){
        String[] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}