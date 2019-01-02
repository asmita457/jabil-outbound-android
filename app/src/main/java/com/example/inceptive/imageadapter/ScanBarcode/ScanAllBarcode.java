package com.example.inceptive.imageadapter.ScanBarcode;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.example.inceptive.imageadapter.Activity.AllBarcodeScan;
import com.example.inceptive.imageadapter.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanAllBarcode extends AppCompatActivity implements  ZXingScannerView.ResultHandler{

    Intent intent;
    private ZXingScannerView scannerViewView;
    String partnoScan,quantityScan,boxnoScan,secondQuantity,secondPartnoScan,secondQty,ponoScan;
    String Flag;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_all_barcode);

//        AllBarcodeScan.partno_check.setVisibility(ImageView.GONE);
//        AllBarcodeScan.partno_check_cross.setVisibility(ImageView.GONE);
//        AllBarcodeScan.pono_check_cross.setVisibility(ImageView.GONE);
//        AllBarcodeScan.pono_check.setVisibility(ImageView.GONE);
//        AllBarcodeScan.boxno_check.setVisibility(ImageView.GONE);
//        AllBarcodeScan.qty_check.setVisibility(ImageView.GONE);

        //Scanner
        scannerViewView = new ZXingScannerView(this);
        RelativeLayout rll = (RelativeLayout) findViewById(R.id.relative_scan_take_multiple);
        rll.addView(scannerViewView);
        scannerViewView.setResultHandler(this);
        scannerViewView.startCamera();
        scannerViewView.setSoundEffectsEnabled(true);
        scannerViewView.setAutoFocus(true);
        scannerViewView.setMinimumHeight(50);
        scannerViewView.setMinimumWidth(50);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        Intent intentBarcode=getIntent();
        Flag = intentBarcode.getStringExtra("BarcodeKey");

        }

    @Override
    public void handleResult(final Result rslt) {

        AlertDialog.Builder builderr = new AlertDialog.Builder(this);
        builderr.setTitle("Scan Result");
        builderr.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               intent=new Intent(ScanAllBarcode.this,AllBarcodeScan.class);
                if (Flag.equals("0"))
                {
//                    CheckBarcode(Flag,rslt);
                    partnoScan=rslt.getText();
//                    intent.putExtra("Part_No", partnoScan);
                    editor.putString("Part_No", partnoScan);
                    editor.apply();
                    startActivity(intent);
                }
                else if(Flag.equals("1"))
                {
                    quantityScan=rslt.getText();
//                    intent.putExtra("Quantity",quantityScan);
                    editor.putString("Quantity", quantityScan);
                    editor.apply();

                    startActivity(intent);
                }
                else if(Flag.equals("2"))
                {
                    boxnoScan=rslt.getText();
//                    intent.putExtra("Box_No",boxnoScan);
                    editor.putString("Box_No", boxnoScan);

                    editor.apply();
                    startActivity(intent);
                }
                else if(Flag.equals("3"))
                {

                    ponoScan=rslt.getText();
//                    intent.putExtra("Po_No",ponoScan);
                    editor.putString("Po_No", ponoScan);
                    editor.apply();
                    startActivity(intent);
                }else if (Flag.equals("4"))
                {
//                    CheckBarcode(Flag,rslt);
                    secondPartnoScan=rslt.getText();
//                    intent.putExtra("Part_No", partnoScan);
                    editor.putString("Second_Part_No", secondPartnoScan);
                    editor.apply();
                    startActivity(intent);
                }else if (Flag.equals("5"))
                {
//                    CheckBarcode(Flag,rslt);
                    secondQuantity=rslt.getText();
//                    intent.putExtra("Part_No", partnoScan);
                    editor.putString("Second_Quantity", secondQuantity);
                    editor.apply();
                    startActivity(intent);
                }
            }
        });
        builderr.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerViewView.resumeCameraPreview(ScanAllBarcode.this);

            }
        });
        builderr.setMessage(rslt.getText());
        AlertDialog alert1 = builderr.create();
        alert1.show();
    }

}
