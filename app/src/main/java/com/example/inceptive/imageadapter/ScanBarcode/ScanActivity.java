package com.example.inceptive.imageadapter.ScanBarcode;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.inceptive.imageadapter.Activity.AllBarcodeScan;
import com.example.inceptive.imageadapter.R;
import com.example.inceptive.imageadapter.Activity.ScanSrNumber;
import com.google.zxing.Result;

import org.json.JSONObject;

import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler

{
    String Type_SR_NO="sr_no";
    String barcod,codeScan,getIpUrl;
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String ALL_BARCODE_SCAN="/api/SRFormsAPI/GetParameterDetails";
    private String sr_no;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //Scanner
        scannerView = new ZXingScannerView(this);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relative_scan_take_single);
        rl.addView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
        scannerView.setSoundEffectsEnabled(true);
        scannerView.setAutoFocus(true);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        getIpUrl = pref.getString("BaseUrlIp", "0");

    }

    @Override
    public void handleResult(final Result result) {
        Log.d("QRCodeScanner", result.getText());
         barcod=result.getText();
        codeScan=result.getText();
        if(codeScan==null)
        {
            Toast.makeText(getApplicationContext(),"Barcode null",Toast.LENGTH_SHORT).show();

        }
        else
        {

            CheckSrNo(codeScan);

        }
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                if(codeScan==null)
//                {
//                    Toast.makeText(getApplicationContext(),"Barcode null",Toast.LENGTH_SHORT).show();
//
//                }
//                else
//                {
//
//                    CheckSrNo(codeScan);
//
//                }
//
//            }
//        });
//        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                scannerView.resumeCameraPreview(ScanActivity.this);
//
//            }
//        });
////        builder.setMessage(result.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();
    }

    private void CheckSrNo(String scanBar) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type", Type_SR_NO);
        params.put("srid",scanBar );
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,getIpUrl+ALL_BARCODE_SCAN,
                new JSONObject(params), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    if (jObj.getString("status").equals("Success"))

                    {
                        String msg = jObj.getString("message");

                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        sr_no=jObj.getString("sr_no");

                        Intent intent = new Intent(ScanActivity.this, AllBarcodeScan.class);
                        editor.putString("Sr_Number", sr_no);
                        editor.apply();
                        intent.putExtra("Sr_Number", sr_no);
                        startActivity(intent);

                    }
                    else {

                        if (jObj.getString("status").equals("Failed"))
                        {
                            String msgFailed = jObj.getString("message");

                            Toast.makeText(getApplicationContext(), msgFailed, Toast.LENGTH_SHORT).show();

                            Intent intent=new Intent(ScanActivity.this,ScanSrNumber.class);
                            startActivity(intent);
                        }

                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ScanActivity.this,ScanSrNumber.class);
                    startActivity(intent);

                }

            }

        }, new Response.ErrorListener()
        {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(ScanActivity.this,ScanSrNumber.class);
                startActivity(intent);
            }

        });
        requestQueue.add(jsonObjReq);

    }

}