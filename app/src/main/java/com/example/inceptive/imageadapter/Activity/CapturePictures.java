package com.example.inceptive.imageadapter.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.inceptive.imageadapter.Interface.ImageCaptureAPI;
import com.example.inceptive.imageadapter.R;
import com.example.inceptive.imageadapter.SQLiteDatabase.SQLiteHelper;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CapturePictures extends AppCompatActivity
{
    public static String IMAGE_UPLOAD="/api/SRFormsAPI/AddImage";
    String getIpUrlAllBarcode;
    public static final String IMAGE_DIRECTORY = "ImageScalling";
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMAGE_EXTENSION = "jpg";
    private static String imageStoragePath;
    private String imagePath = "";

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static int RESULT_LOAD_IMAGE = 1;
    public static final int CAMERA_REQUEST = 12;

    public static SQLiteHelper sqLiteHelper;

    Button captureImages;

    private File destFile;
    private File file;
    Uri fileUri;
    Bitmap bmpCompressImage;
    ImageView img;

    private SimpleDateFormat dateFormatter;

    SharedPreferences.Editor editor;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_pictures);
        init();

        //create SQLite database.
        sqLiteHelper = new SQLiteHelper(this, "Imagedb.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS BOXIMAGE(Id INTEGER PRIMARY KEY AUTOINCREMENT, image BLOB)");
        CaptureImage();
    }
    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

//      Load images from gridview stored in database.

//    private void LoadImage() {
//        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                    Intent intent=new Intent(CapturePictures.this,GridPictures.class);
//                    startActivity(intent);
//
//            }
//        });
//    }

    private void CaptureImage() {

        captureImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
                 }
        });
    }


    private void init()
    {
        img = (ImageView) findViewById(R.id.img);
        captureImages=(Button)findViewById(R.id.captureImages);
        file = new File(Environment.getExternalStorageDirectory()+ "/" + IMAGE_DIRECTORY);

        if (!file.exists())
            {
                file.mkdirs();
            }

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        getIpUrlAllBarcode = pref.getString("BaseUrlIp", "0");

        dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    }
//Camera Permission
    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            }
                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }


    private void showPermissionsAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(CapturePictures.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

//ADD IMAGE INTO DATABASE.
//            try{
//
//                sqLiteHelper.insertData(
//                        imageViewToByte(img)
//                );
////                Toast.makeText(getApplicationContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
//                img.setVisibility(ImageView.GONE);
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bmpCompressImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byteArray = stream.toByteArray();


    //MultiPArt API using Retrofit
    public void uploadImagemultipart(MultipartBody requestBody) throws JSONException {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getIpUrlAllBarcode)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ImageCaptureAPI api = retrofit.create(ImageCaptureAPI.class);
            api.UploadMultipleImages(requestBody).enqueue(new Callback<Imageuploadresponse>() {

                @Override
                public void onResponse(Call<Imageuploadresponse> call, retrofit2.Response<Imageuploadresponse> response) {
                    if (response.isSuccessful()) {

                        Toast.makeText(getApplicationContext(),"Image Uploaded Successfully",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<Imageuploadresponse> call, Throwable t) {

                    Toast.makeText(getApplicationContext(),"Images select at least one image to upload",Toast.LENGTH_SHORT).show();

                    t.printStackTrace();
                }

            });
            }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            img.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            File imgFile = new  File("file://storage/emulated/0/DCIM/Camera/IMG_20151102_193132.jpg");
            if(imgFile.exists())
            {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            };

        };

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);
                //Compress image.
                bmpCompressImage = decodeFile(imageStoragePath);
                //set image to imageview
                img.setVisibility(ImageView.VISIBLE);
                img.setImageBitmap(bmpCompressImage);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bmpCompressImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File destination = new File(
                        Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imagePath = destination.getAbsolutePath();
                File firstFille=new File(imagePath);

                MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("image_url","Image",RequestBody.create(MediaType.parse("multipart/form-data"), firstFille));
            builder.addFormDataPart("part_no",AllBarcodeScan.prtno);
            builder.addFormDataPart("sr_no", AllBarcodeScan.srno);

            MultipartBody requestBody = builder.build();

            try {
                uploadImagemultipart(requestBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        }



//Compress image.
    private Bitmap decodeFile(String f) {
        Bitmap b = null;
        File ff=new File(f);
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(ff);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1800;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        destFile = new File(file, "img_"
                + dateFormatter.format(new Date()).toString() + ".png");
        try {
            FileOutputStream out = new FileOutputStream(destFile);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }


//capture image through camera
    private void captureImage()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (file != null)
        {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri imageFilePath = Uri.parse(imageStoragePath);
        fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(CapturePictures.this,AllBarcodeScan.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
