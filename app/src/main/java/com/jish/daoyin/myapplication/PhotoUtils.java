package com.jish.daoyin.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Adminjs on 2018/4/27.
 */

public class PhotoUtils {

    /**
     * 弹出对话框,可以选择从相册或者拍照获取图片
     * @param activity    上下文
     * @param takePhotoCode 跳转到拍照activity的请求码
     * @param imageCode 跳转到相册activity的请求码
     * @param tempFile 拍出的照片的临时文件路径
     * @return
     */
    public static void getPhoto(final Activity activity,
                                final int cameraCode, final int albumCode, final String tempPath) {
        final CharSequence[] items = { "相册", "拍照" };
        AlertDialog dlg = new AlertDialog.Builder(activity).setTitle("选择图片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 1) {
                            getPhotoFromCamera(activity,cameraCode,tempPath);
                        } else {
                            getPhotoFromAlbum(activity,albumCode);
                        }
                    }
                }).create();
        dlg.show();
    }

    /**
     * 拍照获取图片
     * @param activity 上下文
     * @param cameraCode 跳转到拍照activity的请求码
     * @param tempPath 拍出的照片的临时文件路径
     */
    public static void getPhotoFromCamera(Activity activity, int cameraCode,
                                          String tempPath) {
        Intent getImageByCamera = new Intent(
                "android.media.action.IMAGE_CAPTURE");
        getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(tempPath)));
        activity.startActivityForResult(getImageByCamera, cameraCode);
    }

    /**
     * 相册获取图片
     * @param activity 上下文
     * @param cameraCode 跳转到相册activity的请求码
     */
    public static void getPhotoFromAlbum(Activity activity, int albumCode) {
        Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
        getImage.addCategory(Intent.CATEGORY_OPENABLE);
        getImage.setType("image/jpeg");
        activity.startActivityForResult(getImage, albumCode);
    }

    /**
     * 裁剪图片
     * @param activity    上下文
     * @param uri 需要剪裁图片的uri
     * @param path 裁剪后的保存的图片路径
     * @param zoomCode 跳转系统裁剪activity的请求码
     * @param aspectX 宽高比
     * @param aspectY 宽高比
     */
    public static void photoZoom(Activity activity, Uri uri, String path, int zoomCode, int aspectX, int aspectY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        if (aspectY > 0) {
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
        }
        intent.putExtra("scale", aspectX == aspectY);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, zoomCode);
    }

    /**
     * 将SD卡图片路径转为Bitmap对象
     * @param path 图片路径
     * @param w    转为bitmap后图片的宽
     * @param h 转为bitmap后图片的高
     * @return bitmap对象
     */
    public static Bitmap convertToBitmap(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int)scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }
}

