package com.example.inceptive.imageadapter.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inceptive.imageadapter.BuildConfig;
import com.example.inceptive.imageadapter.R;
import com.example.inceptive.imageadapter.ScanBarcode.ScanActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class ScanSrNumber extends AppCompatActivity {


    View v;
    CardView scan;
    Button urlId;
//    String Type_SR_NO="sr_no";
    String code;
    String textString;
    Intent intnt;
    static TextView scanSrNumber;
    ImageView next;
    TextView textSrNumber;
    String baseurl;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public static final int MEDIA_TYPE_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_sr_number);
        init();
        ScanSrNumberBarcode();

        urlId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AppCompatDialog dialog = new AppCompatDialog(ScanSrNumber.this);
                dialog.setContentView(R.layout.popup);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final EditText urlIpEdit = (EditText) dialog.findViewById(R.id.urlIpEdit);

                Button buttonsubmit = (Button) dialog.findViewById(R.id.submitIp);

                dialog.setCanceledOnTouchOutside(true);

                buttonsubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                         baseurl = urlIpEdit.getText().toString();

                        if(baseurl==null || baseurl.equals(""))
                        {
                            Toast.makeText(getApplicationContext(),"You did not enter a IP",Toast.LENGTH_SHORT).show();
//                            urlIpEdit.setError("You did not enter a username");
                        }
                        else
                        {
                            editor.putString("BaseUrlIp",baseurl);
                            editor.apply();
                            dialog.dismiss();

                        }
                    }
                });
                dialog.show();
            }
        });
        requestCameraPermission(MEDIA_TYPE_IMAGE);

    }

    private void ScanSrNumberBarcode() {

        scanSrNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(baseurl==null || baseurl.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please select url",Toast.LENGTH_SHORT).show();
                }
                else {

                    scanBarcode(v);
                }
            }
        });
    }

    private void init() {
        scan=(CardView)findViewById(R.id.scan);
        scanSrNumber=(TextView)findViewById(R.id.scansrnumber);
        textSrNumber=(TextView)findViewById(R.id.textSrNumber);
        urlId=(Button)findViewById(R.id.urlId);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

    }

    private void getNextActivity() {
        if (textString.equals("Scan SR Number"))
        {
            scanBarcode(v);
            scanSrNumber.setText("Scan SR Number");
        }
        else if (textString.equals("No barcode found"))
        {
            scanBarcode(v);
            scanSrNumber.setText("Scan SR Number");
        }
        else
        {
//            CheckSrNumber();
            Intent intent = new Intent(ScanSrNumber.this, AllBarcodeScan.class);
//            intent.putExtra("Sr_Number", code);
            startActivity(intent);
            scanSrNumber.setText("Scan SR Number");
        }

    }

    public void scanBarcode(View v)
    {
//        requestCameraPermission(MEDIA_TYPE_IMAGE);
        Intent intent=new Intent(ScanSrNumber.this,ScanActivity.class);

         startActivityForResult(intent,0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==0)
        {
            if(requestCode==CommonStatusCodes.SUCCESS)
            {
                if (data!=null)
                {
                    Barcode barcode=data.getParcelableExtra("barcode");
                    scanSrNumber.setText(barcode.displayValue);
                    String textSR=scanSrNumber.getText().toString();

                    if (textSR!=null)
                    {
                        textSrNumber.setVisibility(View.VISIBLE);
                    }
                    code = barcode.displayValue;
                    textString=code.toString();
                    getNextActivity();
                }
                else
                {
                    scanSrNumber.setText("No barcode found");
                }
            }
        }else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

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
//                                captureImage();
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
    public static void openSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       openSettings(ScanSrNumber.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
}
