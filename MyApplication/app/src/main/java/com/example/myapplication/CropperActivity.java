package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Output;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;



public class CropperActivity extends AppCompatActivity {
    private String base_url;
    ImageView imageview_Crop;
    ImageButton btBrowse, btReset, btNext_1;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.cropper_activity);

        btBrowse = findViewById(R.id.bt_browse);
        btReset = findViewById(R.id.bt_reset);
        btNext_1 = findViewById(R.id.bt_next_1);
        imageview_Crop = findViewById(R.id.imageview_crop);



        //저장부분 구현 완료..
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 버전과 같거나 이상이라면
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "외부 저장소 사용을 위해 읽기/쓰기 필요", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        2);  //마지막 인자는 체크해야될 권한 갯수

            } else {

            }

            if(checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    Toast.makeText(this, "카메라 사용을 위해 승인허가 필요", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            }
        }

        //Permission 관련 이슈 해결한 이후 url입력하기기
        if(base_url == null){
            show_urlpopup();
        }


        btReset.setOnClickListener(view -> imageview_Crop.setImageBitmap(null));

        btNext_1.setOnClickListener(view -> {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Intent intent = new Intent(CropperActivity.this, Select_Option_Activity.class);

            if(base_url == "") {
                String msg = "URL을 꼭 추가해주세요";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
            else{
                if(imageview_Crop.getDrawable() != null){
                    Bitmap bitmap = ((BitmapDrawable)imageview_Crop.getDrawable()).getBitmap();
                    //HTTP_POST(bitmap);
                    connect(bitmap);
                    //먼저 원본 이미지를 보낸 다음 crop 작업 작동하는게 좋을듯하다
                    float scale = (float) (1024/(float)bitmap.getWidth());
                    int image_w = (int) (bitmap.getWidth() * scale);
                    int image_h = (int) (bitmap.getHeight() * scale);
                    Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                    resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("image", byteArray);
                    intent.putExtra("url_for_network", base_url);
                    startActivity(intent);
                }
                else if(imageview_Crop.getDrawable() == null){
                    String msg = "이미지를 꼭 추가해주세요";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                imageview_Crop.setImageURI(result.getUri());
            }
        }

    }

    //url을 입력받아야 할 때 진행하는 함수라고 보면 된다
    void show_urlpopup()
    {
        final EditText edittext = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("URL INPUT");
        builder.setMessage("테스트로 사용 할 URL을 입력해주세요");
        builder.setView(edittext);
        builder.setPositiveButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        base_url = edittext.getText().toString();
                        Toast.makeText(getApplicationContext(),edittext.getText().toString() ,Toast.LENGTH_LONG).show();

                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


    //형태님 버전으로 change.. 이거 5천배 낫다! 쾌적하다!
    //일반 이미지만을 보내는 버전의 connect 함수라고 보면 된다
    public void connect(Bitmap bitm){
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
                    bitm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                    byte[] image = byteArrayOutputStream.toByteArray();
                    String byteStream = Base64.encodeToString(image, 0);

                    jsonObject = new JSONObject();
                    jsonObject.put("image", byteStream);
                    String data = jsonObject.toString();


                    URL url = new URL(base_url + "/original_image_file");
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


                    //dos.writeBytes(lineEnd);
                    // Bitmap을 ByteBuffer로 전환


                    //dos.write(data);
                    //dos.writeBytes(lineEnd);
                    //dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    writer.flush(); // finish upload...
                    out.close();
                    Log.v("http", "send");
                    //response

                    response = con.getInputStream();

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




    public void onChooseFile(View v){
        CropImage.activity().start(CropperActivity.this);
    }
}
