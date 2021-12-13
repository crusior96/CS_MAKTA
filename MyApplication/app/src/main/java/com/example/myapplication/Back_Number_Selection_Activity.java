package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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

public class Back_Number_Selection_Activity extends AppCompatActivity {

    private final int GET_GALLERY_IMAGE = 200;
    ImageView img_temp;
    ImageButton button_1, button_2, button_3;
    JSONObject jsonObject;
    Bitmap res, back_bitmap;
    String file_path, result_path;
    private String base_url;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.back_number_selection);

        button_1 = (ImageButton) findViewById(R.id.button1);
        button_2 = (ImageButton) findViewById(R.id.button2);
        button_3 = (ImageButton) findViewById(R.id.button3);
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
                Intent intent = new Intent(Back_Number_Selection_Activity.this, Select_Option_Activity.class);
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
                Intent intent = new Intent(Back_Number_Selection_Activity.this, Result_Activity.class);
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

        //갤러리에서 배경 화면을 선택하는 버특
        button_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent_temp = new Intent(Intent.ACTION_PICK);
                intent_temp.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent_temp, GET_GALLERY_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            img_temp.setImageURI(selectedImageUri);
            BitmapDrawable drawable = (BitmapDrawable) img_temp.getDrawable();
            back_bitmap = drawable.getBitmap();
            connect("/change_background");
        }
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
                    InputStream response = null; // 요청 결과를 저장할 변수.
                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    back_bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                    byte[] image = byteArrayOutputStream.toByteArray();
                    String byteStream = Base64.encodeToString(image, 0);

                    jsonObject = new JSONObject();
                    jsonObject.put("background", byteStream);

                    String data = jsonObject.toString();




                    URL url = new URL(base_url+option);
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

            img_temp.setImageBitmap(res);
            img_temp.invalidate();

        }catch (InterruptedException e){

            e.printStackTrace();

        }
    }

}
