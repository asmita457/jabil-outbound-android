package com.example.inceptive.imageadapter;

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
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public class CapturePictures extends AppCompatActivity {

    public static String IMAGE_UPLOAD="/api/SRFormsAPI/AddImage";
    ArrayList<ImageModel> list;
    public static final String IMAGE_DIRECTORY = "ImageScalling";
    Button captureImages,buttonLoadImage;
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    private File destFile;
    public static final String IMAGE_EXTENSION = "jpg";
    private File file;
    private static String imageStoragePath,picturePath;
    Uri fileUri,uri;
    Bitmap bmpCompressImage;
    ImageView img;
    private Uri imageCaptureUri;
    private SimpleDateFormat dateFormatter;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static int RESULT_LOAD_IMAGE = 1;
     String getIpUrlAllBarcode;
    EditText edtName;
    byte[] byteArray;
    public static SQLiteHelper sqLiteHelper;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    ArrayList<String> pictureList=new ArrayList<>();
    private String imagePath = "";
    String imagePaths;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_pictures);
        init();
        sqLiteHelper = new SQLiteHelper(this, "Imagedb.sqlite", null, 1);

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS BOXIMAGE(Id INTEGER PRIMARY KEY AUTOINCREMENT, image BLOB)");

        CaptureImage();
        LoadImage();

    }
    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void LoadImage() {
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent=new Intent(CapturePictures.this,GridPictures.class);
                    startActivity(intent);

            }
        });
    }

    private void CaptureImage() {

        captureImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    destFile = new File(file, "img_"
                            + dateFormatter.format(new Date()).toString() + ".jpg");

                    imageCaptureUri = Uri.fromFile(destFile);

                    if (CameraUtils.checkPermissions(getApplicationContext())) {
                        captureImage();

                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
                 }
        });
    }


    private void init()
    {
        img = (ImageView) findViewById(R.id.img);

        buttonLoadImage = (Button) findViewById(R.id.buttonLoadImage);
        captureImages=(Button)findViewById(R.id.captureImages);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),pictureList );
        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        getIpUrlAllBarcode = pref.getString("BaseUrlIp", "0");

        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);

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
   //Show captured image
    private void previewCapturedImage()
    {
        try
        {
            bmpCompressImage = decodeFile(imageStoragePath);



            img.setVisibility(ImageView.VISIBLE);
            img.setImageBitmap(bmpCompressImage);
//            imageViewToByte(img);




//                uploadimageurl(img1);

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

        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    public void uploadimageurl(final File fff)
    {
        final File imagePathUpload= new File("file://" + fff);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, getIpUrlAllBarcode+IMAGE_UPLOAD, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultresponse = new String(response.data);
                try {
                    JSONObject jObj = new JSONObject(resultresponse);
                    if (jObj.optBoolean("success"))
                    {
                        String msgSuccessUploadImage = jObj.getString("Message");

                        Toast.makeText(getApplicationContext(), msgSuccessUploadImage, Toast.LENGTH_SHORT).show();


                    } else {
                        if (jObj.optString("error_code").equals("401")) {
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("part_no",AllBarcodeScan.prtno);
                params.put("sr_no",AllBarcodeScan.srno);
                return params;
            }

            @Override
            protected Map<String, File> getByteData() {
                Map<String, File> params = new HashMap<>();
                params.put("image_url", fff);
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);

        multipartRequest.setShouldCache(false);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    private void uploadImageToDatabase(Bitmap fff) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        HashMap<String, String> params = new HashMap<>();
        String image = getStringImage(bmpCompressImage);
        File ff=new File(image);
        params.put("image_url", image);
        params.put("part_no",AllBarcodeScan.prtno);
        params.put("sr_no",AllBarcodeScan.srno);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                getIpUrlAllBarcode+IMAGE_UPLOAD,
                new JSONObject(params), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    if (jObj.getString("status").equals("Success"))

                    {
                        String msgSuccessUploadImage = jObj.getString("Message");

                        Toast.makeText(getApplicationContext(), msgSuccessUploadImage, Toast.LENGTH_SHORT).show();

                    }
                    else {
                        if (jObj.getString("status").equals("Failed"))
                        {
                            String msgFailedUploadImage = jObj.getString("Message");

                            Toast.makeText(getApplicationContext(), msgFailedUploadImage, Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();

            }

        });
        requestQueue.add(jsonObjReq);

    }

    private String getStringImage(Bitmap bmpCompressImage) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmpCompressImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);
                File ffile=new File(imageStoragePath);
//                Bitmap bit = (Bitmap) data.getExtras().get("data");
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                bit.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//                File destination = new File(
//                        Environment.getExternalStorageDirectory(),
//                        System.currentTimeMillis() + ".jpg");
//                FileOutputStream fo;
//                try {
//                    destination.createNewFile();
//                    fo = new FileOutputStream(destination);
//                    fo.write(bytes.toByteArray());
//                    fo.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                imagePath = destination.getAbsolutePath();
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
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

//compress image after getting from camera
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


//        // The new size we want to scale to
//        final int REQUIRED_SIZE=75;
//
//        // Find the correct scale value. It should be the power of 2.
//        int scale = 1;
//        while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
//                o.outHeight / scale / 2 >= REQUIRED_SIZE) {
//            scale *= 2;
//        }

        int IMAGE_MAX_SIZE = 800;
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
//        previewCapturedImage();

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
