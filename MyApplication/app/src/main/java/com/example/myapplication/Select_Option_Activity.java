package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.internal.http.multipart.MultipartEntity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class Select_Option_Activity extends AppCompatActivity{
    private String base_url;
    ImageButton btBack_1, btNext_2;
    Button Menu_1, Menu_2, Menu_3, Menu_4;
    ImageView imageview_captured;
    JSONObject jsonObject;
    Bitmap res;
    Bitmap forShoot;    //CropperActivity로부터 전송받은 원본 or 잘린 이미지

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pose_selection);

        btNext_2 = (ImageButton) findViewById(R.id.bt_next_2);
        btBack_1 = (ImageButton) findViewById(R.id.bt_back_1);
        imageview_captured = (ImageView) findViewById(R.id.imageview_captured);
        Menu_1 = (Button) findViewById(R.id.menu_1);
        Menu_2 = (Button) findViewById(R.id.menu_2);
        Menu_3 = (Button) findViewById(R.id.menu_3);
        Menu_4 = (Button) findViewById(R.id.menu_4);

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        forShoot = bitmap;  //Intent에서 이어받은 bitmap을 forShoot에 바로 이식해둔다
        base_url = getIntent().getStringExtra("url_for_network");

        imageview_captured.setImageBitmap(bitmap);

        //포즈변경(Pose_Number_Selection_Activity)로 이동하는 버튼
        Menu_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Select_Option_Activity.this, Pose_Number_Selection_Activity.class);

                Bitmap bitm = ((BitmapDrawable)imageview_captured.getDrawable()).getBitmap();
                float scale = (float) (1024/(float)bitm.getWidth());
                int image_w = (int) (bitm.getWidth() * scale);
                int image_h = (int) (bitm.getHeight() * scale);
                Bitmap resize = Bitmap.createScaledBitmap(bitm, image_w, image_h, true);
                resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();



                intent.putExtra("image", byteArray);
                intent.putExtra("url_for_network", base_url);
                startActivity(intent);
            }
        });

        //배경변경(Back_Number_Selection_Activity)로 이동하는 버튼
        Menu_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Select_Option_Activity.this, Back_Number_Selection_Activity.class);

                Bitmap bitm = ((BitmapDrawable)imageview_captured.getDrawable()).getBitmap();
                float scale = (float) (1024/(float)bitm.getWidth());
                int image_w = (int) (bitm.getWidth() * scale);
                int image_h = (int) (bitm.getHeight() * scale);
                Bitmap resize = Bitmap.createScaledBitmap(bitm, image_w, image_h, true);
                resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();


                intent.putExtra("image", byteArray);
                intent.putExtra("url_for_network", base_url);
                startActivity(intent);
            }
        });

        //인물이미지추출(Segmentataion_Selection_Activity)로 이동하는 버튼
        Menu_3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bitmap original_picture;
                Bitmap segmented_picture;
                original_picture = forShoot;//원본 사진

                connect("/segmentation");
                segmented_picture = res;    //변한 사진

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Select_Option_Activity.this, Segmentataion_Selection_Activity.class);

                float scale = (float) (1024/(float)segmented_picture.getWidth());
                int image_w = (int) (segmented_picture.getWidth() * scale);
                int image_h = (int) (segmented_picture.getHeight() * scale);
                Bitmap resize = Bitmap.createScaledBitmap(segmented_picture, image_w, image_h, true);
                resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();


                ByteArrayOutputStream stream_2 = new ByteArrayOutputStream();
                float scale_2 = (float) (1024/(float)original_picture.getWidth());
                int image_w_2 = (int) (original_picture.getWidth() * scale_2);
                int image_h_2 = (int) (original_picture.getHeight() * scale_2);
                Bitmap resize_2 = Bitmap.createScaledBitmap(original_picture, image_w_2, image_h_2, true);
                resize_2.compress(Bitmap.CompressFormat.JPEG, 100, stream_2);
                byte[] byteArray_2 = stream_2.toByteArray();



                intent.putExtra("image", byteArray);
                intent.putExtra("original", byteArray_2);
                intent.putExtra("url_for_network", base_url);
                startActivity(intent);

            }
        });

        //블러처리(Portrait_Selection_Activity)로 이동하는 버튼
        Menu_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Select_Option_Activity.this, Portrait_Selection_Activity.class);

                Bitmap bitm = ((BitmapDrawable)imageview_captured.getDrawable()).getBitmap();
                float scale = (float) (1024/(float)bitm.getWidth());
                int image_w = (int) (bitm.getWidth() * scale);
                int image_h = (int) (bitm.getHeight() * scale);
                Bitmap resize = Bitmap.createScaledBitmap(bitm, image_w, image_h, true);
                resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();


                intent.putExtra("image", byteArray);
                intent.putExtra("url_for_network", base_url);
                startActivity(intent);
            }
        });

        btBack_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), CropperActivity.class);
                startActivity(intent);
            }
        });

        btNext_2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Intent intent = new Intent(Select_Option_Activity.this, Result_Activity.class);
                if(imageview_captured.getDrawable() != null){
                    Bitmap bitmap = ((BitmapDrawable)imageview_captured.getDrawable()).getBitmap();
                    float scale = (float) (1024/(float)bitmap.getWidth());
                    int image_w = (int) (bitmap.getWidth() * scale);
                    int image_h = (int) (bitmap.getHeight() * scale);
                    Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                    resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("image", byteArray);

                    startActivity(intent);
                }
                else if(imageview_captured.getDrawable() == null){
                    String msg = "이미지를 꼭 추가해주세요";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //오로지 segmentation 명령을 위해서만 작동하는 POST 함수
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
                    jsonObject = new JSONObject();

                    if(option.equals("/segmentation")){
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        forShoot.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                        byte[] image = byteArrayOutputStream.toByteArray();
                        String byteStream = Base64.encodeToString(image, 0);
                        jsonObject.put("image", byteStream);
                    }



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

            //imageview_captured.setImageBitmap(res);
            //imageview_captured.invalidate();

        }catch (InterruptedException e){

            e.printStackTrace();

        }
    }


}
