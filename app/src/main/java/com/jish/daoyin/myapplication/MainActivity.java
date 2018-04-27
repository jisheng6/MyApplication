package com.jish.daoyin.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    protected static final int CAMERA_CODE = 100; // 拍照请求码
    protected static final int ALBUM_CODE = 101; // 相册请求码
    protected static final int ZOOM_CODE = 102; // 剪裁请求码
    private String tempPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/tempPhoto.jpg"; //临时文件路径
    private CircleImageView mImageView;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (CircleImageView) findViewById(R.id.iv);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoUtils.getPhoto(MainActivity.this, CAMERA_CODE, ALBUM_CODE, tempPath);
            }
        });

    }

    //处理请求码
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            //拍照回来进入剪裁activity
            PhotoUtils.photoZoom(this, Uri.fromFile(new File(tempPath)),tempPath, ZOOM_CODE, 1, 1);
        }
        if (requestCode == 101 && resultCode == RESULT_OK) {
            //相册回来进入裁剪activity
            Uri uri = data.getData(); //获取选择图片的uri
            PhotoUtils.photoZoom(this, uri, tempPath, ZOOM_CODE, 1, 1);
        }
        if (requestCode == 102 && resultCode == RESULT_OK) {
            Bitmap bitmap = PhotoUtils.convertToBitmap(tempPath, 500, 500);
            mImageView.setImageBitmap(bitmap);
            //在这里可以把临时图片上传到服务器保存，方便下次登录从服务器获取头像
        }
    }

}
