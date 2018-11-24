package com.example.inceptive.imageadapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.inceptive.imageadapter.rest.url;
import com.google.zxing.Result;

import org.json.JSONObject;

import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AllBarcodeScan extends AppCompatActivity implements  View.OnClickListener{

    String partSuccessMessage,partFailedMessage,poSuccessMessage,poFailedMessage;
    View v;
    String PART_NO_STRING="part_no";
    String PO_NO_STRING="po_number";
    static TextView srNumber;
    Boolean Partimage=false,QuantityImage=false,BoxnoImage=false,PonoImage=false;
    String boxNoString,partNoString,qtyString,poNoString,newString;
    static ImageView piccgallary,partno_check_cross,pono_chk,pono_check_cross;
    static TextView partNo,qty,boxno,pono,submit,totalScanQuantity,ScanQuantity;
    Intent intentr,data;
    String msgFailedBarcode,msgSuccessBarcode;
    static String pno,bno,qno,partCheckNo;
    static ImageView partno_check,qty_check,boxno_check;
    public static final int PART_NO = 0;
    public static final int QUANTITY =1;
    public static final int BOX_NO = 2;
    public static final int PO_NO = 3;
    SharedPreferences pref;
    String getflagcode,getIpUrlAllBarcode;
    String ALL_BARCODE_SCAN="/api/SRFormsAPI/GetParameterDetails";
    SharedPreferences.Editor editor;
    LinearLayout linearTotalQuantity,linearScanQuantity,quantityScan;
    String flag="0",flag1="1",flag2="2",flag3="3",bxno,qy,ponos;
    static String prtno,srno;
    int barcodeFlag=0;
    Context context;
    String TAG = "AllBarcodeScan" ;
    public static String SUBMIT = "/api/SRFormsAPI/AddScanningInfo";
    String numbersOnly = "^[0-9]*$",scanned_qty,total_qty,shareTotalScan,shareScanQuantity;
    Boolean submitPartnoCheck,submitPonoCheck,submitQtyCheck,submitBoxnoCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_barcode_scan);
        init();
        Gallary();
        BoxNo();

        context = this ;
        getflagcode = pref.getString("flagforscanbarcode", "0");
        Partimage =  pref.getBoolean("partnocheck", false);
        PonoImage = pref.getBoolean("ponochk", false);
        QuantityImage = pref.getBoolean("quantitychk", false);
        BoxnoImage=pref.getBoolean("boxnochk",false);
        linearScanQuantity=(LinearLayout)findViewById(R.id.linearScanQuantity);
        linearTotalQuantity=(LinearLayout)findViewById(R.id.linearTotalQuantity);
        quantityScan=(LinearLayout)findViewById(R.id.quantityScan);
        totalScanQuantity=(TextView)findViewById(R.id.totalScanQuantity);
        ScanQuantity=(TextView)findViewById(R.id.ScanQuantity);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();
            }
        });
        quantityScan.setVisibility(ImageView.GONE);
        qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                scanBarcode(v);
                editor.putString("flagforscanbarcode", "2");
                editor.apply();
                Intent intent = new Intent(AllBarcodeScan.this, ScanAllBarcode.class);
                intent.putExtra("BarcodeKey", flag1);
                startActivityForResult(intent, QUANTITY);


               }
        });
        pono.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(prtno==null || prtno.equals(""))
                {

                    Toast.makeText(getApplicationContext(),"First select part no ",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    editor.putString("flagforscanbarcode", "4");
                    editor.apply();

                    Intent intent = new Intent(AllBarcodeScan.this, ScanAllBarcode.class);
                    intent.putExtra("BarcodeKey", flag3);
                    startActivityForResult(intent, PO_NO);
                }

            }
        });


        partNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putString("flagforscanbarcode", "1");
                editor.apply();
                Intent intent = new Intent(AllBarcodeScan.this, ScanAllBarcode.class);
                intent.putExtra("BarcodeKey", flag);
                startActivityForResult(intent, PART_NO);

}
        });

        intentr = getIntent();
        newString = intentr.getStringExtra("Sr_Number");
        String newSrString = pref.getString("Sr_Number", null);
        srNumber.setText(newSrString);
        srno=srNumber.getText().toString();


        partNoString = pref.getString("Part_No", null);
        qtyString = pref.getString("Quantity", null);
        boxNoString = pref.getString("Box_No", null);
        poNoString = pref.getString("Po_No", null);




        partNo.setText(partNoString);
        qty.setText(qtyString);
        boxno.setText(boxNoString);
        pono.setText(poNoString);

        bxno=boxno.getText().toString();
        qy=qty.getText().toString();
        prtno=partNo.getText().toString();
        ponos=pono.getText().toString();

        if((bxno==null || bxno.equals("")))
        {
        }
        else
        {
            if(getflagcode.equals("3"))

            {
                boxno_check.setVisibility(ImageView.VISIBLE);
                editor.putBoolean("boxnochk", true);
                editor.apply();

                flagCheck();
                if(QuantityImage)
                {
                    qty_check.setVisibility(ImageView.VISIBLE);
                }
            }
        }

        if(qy==null || qy.equals(""))
        {
        }
        else
        {

            if(getflagcode.equals("2"))
            {
                if(qty.getText().toString().trim().matches(numbersOnly))
                {
                    qty_check.setVisibility(ImageView.VISIBLE);
                    editor.putBoolean("quantitychk", true);
                    editor.apply();                }
                else
                    {
                        final AppCompatDialog dialog = new AppCompatDialog(AllBarcodeScan.this);
                        dialog.setContentView(R.layout.quantitypopup);
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        Button numbersOk = (Button) dialog.findViewById(R.id.numbersOk);

                        dialog.setCanceledOnTouchOutside(true);

                        numbersOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(AllBarcodeScan.this, ScanAllBarcode.class);
                                intent.putExtra("BarcodeKey", flag1);
                                startActivityForResult(intent, QUANTITY);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
//                    Toast.makeText(getApplicationContext(),"Accept only numbers",Toast.LENGTH_SHORT).show();
                    }

               flagCheck();
                if(BoxnoImage)
                {
                    boxno_check.setVisibility(ImageView.VISIBLE);

                }

            }
        }

        if(prtno==null || prtno.equals("")) {
        }
        else
        {


            if(getflagcode.equals("1"))
            {
                barcodeFlag=1;
                checkBarcode(barcodeFlag);
            }
        }

        if(ponos==null || ponos.equals("")) {
        }
        else
        {


            if(getflagcode.equals("4"))
            {
                barcodeFlag=2;
                checkBarcode(barcodeFlag);
            }
        }


    }

    private void BoxNo() {
        boxno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                scanBarcode(v);
                editor.putString("flagforscanbarcode", "3");
                editor.apply();

                Intent intent = new Intent(AllBarcodeScan.this, ScanAllBarcode.class);
                intent.putExtra("BarcodeKey", flag2);
                startActivityForResult(intent, BOX_NO);
            }
        });
    }

    private void Gallary() {

        piccgallary.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AllBarcodeScan.this, CapturePictures.class);
                startActivity(intent);
            }
        });

    }

    //Submit data
    private void Submit()
    {
        submitBoxnoCheck=boxno_check.isShown();
        submitPartnoCheck=partno_check.isShown();
        submitPonoCheck=pono_chk.isShown();
        submitQtyCheck=qty_check.isShown();

        if (submitQtyCheck && submitPonoCheck && submitPartnoCheck && submitBoxnoCheck) {
            editor.putString("flagforscanbarcode", "0");
            submitData();
            }
        else
        {
            Toast.makeText(getApplicationContext(),"Please check fields",Toast.LENGTH_SHORT).show();

        }

    }



    //initialization
    private void init() {

        srNumber = (TextView) findViewById(R.id.srNumber);
        piccgallary = (ImageView) findViewById(R.id.piccgalary);
        pono = (TextView) findViewById(R.id.pono);
        boxno = (TextView) findViewById(R.id.boxNo);
        qty = (TextView) findViewById(R.id.qty);
        partNo = (TextView) findViewById(R.id.partNo);
        submit = (TextView) findViewById(R.id.submit);
        partno_check_cross = (ImageView) findViewById(R.id.partno_check_cross);
        partno_check = (ImageView) findViewById(R.id.partno_check);
        qty_check = (ImageView) findViewById(R.id.qty_check);
        boxno_check = (ImageView) findViewById(R.id.boxno_check);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        getIpUrlAllBarcode = pref.getString("BaseUrlIp", "0");
//        shareTotalScan = pref.getString("TOTAL_SCAN", "");
//        shareScanQuantity = pref.getString("SCANNED", "");
//        if(!shareTotalScan.equals("") && !shareScanQuantity.equals(""))
//        {
//            totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
//            ScanQuantity.setText(pref.getString("SCANNED",""));
//        }

        pono_check_cross=(ImageView)findViewById(R.id.pono_check_cross);
        pono_chk=(ImageView)findViewById(R.id.pono_chk);
    }

    private void checkBarcode(final int flag)
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        HashMap<String, String> params = new HashMap<String, String>();
        if(flag == 1)
        {
            params.put("type", PART_NO_STRING);
            params.put("part_no",prtno );
            params.put("sr_no",srno );

        }else if(flag == 2)
        {
            params.put("type", PO_NO_STRING);
            params.put("sr_no",srno );
            params.put("po_number",ponos );

        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                getIpUrlAllBarcode+ALL_BARCODE_SCAN,
                new JSONObject(params), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    if (jObj.getString("status").equals("Success"))

                    {
                        msgSuccessBarcode = jObj.getString("message");

                        Toast.makeText(getApplicationContext(), msgSuccessBarcode, Toast.LENGTH_SHORT).show();
                        if(flag==1)
                        {
                            editor.putBoolean("partnocheck", true);
                            editor.apply();
                            partSuccessMessage=msgSuccessBarcode;
                            partno_check_cross.setVisibility(ImageView.GONE);
                            partno_check.setVisibility(ImageView.VISIBLE);

                            if(ponos != null)
                            {
                                if(ponos.equals(""))
                                {
                                    pono_check_cross.setVisibility(ImageView.GONE);
                                    pono_chk.setVisibility(ImageView.GONE);
                                }
                                else
                                    {
                                        if(PonoImage)
                                        {
                                            pono_check_cross.setVisibility(ImageView.GONE);
                                            pono_chk.setVisibility(ImageView.VISIBLE);
                                            quantityScan.setVisibility(View.VISIBLE);
                                            totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
                                            ScanQuantity.setText(pref.getString("SCANNED",""));

                                        }
                                        else
                                        {
                                            pono_check_cross.setVisibility(ImageView.VISIBLE);
                                            pono_chk.setVisibility(ImageView.GONE);
                                        }
                                }


                            }


                             if(QuantityImage)
                             {
                                 qty_check.setVisibility(ImageView.VISIBLE);

                             }
                            if(BoxnoImage)
                            {
                                boxno_check.setVisibility(ImageView.VISIBLE);
                            }


                        }
                        else
                        {
//                            editor.apply();
                            poSuccessMessage=msgSuccessBarcode;
                             total_qty=jObj.getString("total_qty");
                            scanned_qty= jObj.getString("scanned_qty");
                            editor.putBoolean("ponochk", true);

                            editor.putString("TOTAL_SCAN",total_qty);
                            editor.putString("SCANNED",scanned_qty);
                            editor.apply();

                            totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
                            ScanQuantity.setText(pref.getString("SCANNED",""));
                            pono_check_cross.setVisibility(ImageView.GONE);
                            pono_chk.setVisibility(ImageView.VISIBLE);
                            if(prtno != null)
                            {
                                if(prtno.equals(""))
                                {
                                    partno_check_cross.setVisibility(ImageView.GONE);
                                    partno_check.setVisibility(ImageView.GONE);
                                }
                                else
                                {
                                    if(Partimage)
                                    {
                                        partno_check_cross.setVisibility(ImageView.GONE);
                                        partno_check.setVisibility(ImageView.VISIBLE);
                                        quantityScan.setVisibility(View.VISIBLE);
                                        totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
                                        ScanQuantity.setText(pref.getString("SCANNED",""));

                                    }
                                    else
                                    {
                                        partno_check_cross.setVisibility(ImageView.VISIBLE);
                                        partno_check.setVisibility(ImageView.GONE);

                                    }
                                }

                            }
                            if(QuantityImage)
                            {
                                qty_check.setVisibility(ImageView.VISIBLE);
                            }
                            if(BoxnoImage)
                            {
                                boxno_check.setVisibility(ImageView.VISIBLE);
                            }

                        }

                    }
                    else
                        {

                        if (jObj.getString("status").equals("Failed"))
                        {
                            msgFailedBarcode = jObj.getString("message");

                            Toast.makeText(getApplicationContext(), msgFailedBarcode, Toast.LENGTH_SHORT).show();
                            if(flag==1)
                            {

                                editor.putBoolean("partnocheck",false);
                                editor.apply();

                                partFailedMessage=msgFailedBarcode;
                                    partno_check_cross.setVisibility(ImageView.VISIBLE);
                                    partno_check.setVisibility(ImageView.GONE);

                             if(ponos != null)
                             {
                                 if(ponos.equals(""))
                                 {

                                 }
                                 else
                                 {
                                     if(!PonoImage)
                                     {
                                         pono_check_cross.setVisibility(ImageView.VISIBLE);
                                         pono_chk.setVisibility(ImageView.GONE);
                                     }
                                     else if(PonoImage)
                                     {
                                         pono_check_cross.setVisibility(ImageView.GONE);
                                         pono_chk.setVisibility(ImageView.VISIBLE);
                                         quantityScan.setVisibility(View.VISIBLE);

                                         totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
                                         ScanQuantity.setText(pref.getString("SCANNED",""));
                                         }
                                 }

                             }

                                if(QuantityImage)
                                {
                                    qty_check.setVisibility(ImageView.VISIBLE);
                                }
                                if(BoxnoImage)
                                {
                                    boxno_check.setVisibility(ImageView.VISIBLE);
                                }

                            }
                            else
                            {
                                editor.putBoolean("ponochk", false);
                                editor.apply();

                                poFailedMessage=msgFailedBarcode;
                                    pono_check_cross.setVisibility(ImageView.VISIBLE);
                                    pono_chk.setVisibility(ImageView.GONE);

                                if(prtno != null)
                                {
                                    if(prtno.equals(""))
                                    {
                                        partno_check_cross.setVisibility(ImageView.GONE);
                                        partno_check.setVisibility(ImageView.GONE);
                                    }
                                    else
                                    {
                                        if(!Partimage)
                                        {
                                            partno_check_cross.setVisibility(ImageView.VISIBLE);
                                            partno_check.setVisibility(ImageView.GONE);
                                        }
                                        else
                                        {
                                            partno_check_cross.setVisibility(ImageView.GONE);
                                            partno_check.setVisibility(ImageView.VISIBLE);
                                        }
                                    }

                                }

                                if(QuantityImage)
                                {
                                    qty_check.setVisibility(ImageView.VISIBLE);
                                }
                                if(BoxnoImage)
                                {
                                    boxno_check.setVisibility(ImageView.VISIBLE);
                                }

                            }
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
                Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
            }

        });
        requestQueue.add(jsonObjReq);

    }

    public void flagCheck()
    {
        if(ponos != null)
        {
            if(ponos.equals(""))
            {
                pono_check_cross.setVisibility(ImageView.GONE);
                pono_chk.setVisibility(ImageView.GONE);
            }
            else
            {
                if(PonoImage)
                {

                    pono_check_cross.setVisibility(ImageView.GONE);
                    pono_chk.setVisibility(ImageView.VISIBLE);
                    quantityScan.setVisibility(View.VISIBLE);

                    totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
                    ScanQuantity.setText(pref.getString("SCANNED",""));

                }
                else
                {
                    pono_check_cross.setVisibility(ImageView.VISIBLE);
                    pono_chk.setVisibility(ImageView.GONE);
                }
            }


        }

        if(prtno != null)
        {
            if(prtno.equals(""))
            {
                partno_check_cross.setVisibility(ImageView.GONE);
                partno_check.setVisibility(ImageView.GONE);
            }
            else
            {
                if(!Partimage)
                {
                    partno_check_cross.setVisibility(ImageView.VISIBLE);
                    partno_check.setVisibility(ImageView.GONE);
                }
                else
                {
                    partno_check_cross.setVisibility(ImageView.GONE);
                    partno_check.setVisibility(ImageView.VISIBLE);
                }
            }

        }
    }

    private void submitData()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("part_number",prtno);
        params.put("box_number",bxno);
        params.put("sr_no", srno);
        params.put("quantity",qy );
        params.put("po_number",ponos);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                getIpUrlAllBarcode+SUBMIT,
                new JSONObject(params), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    if (jObj.getString("status").equals("Success"))

                    {
                        String msgSuccessSubmit = jObj.getString("message");

                        Toast.makeText(getApplicationContext(), msgSuccessSubmit, Toast.LENGTH_SHORT).show();
//

                        partno_check.setVisibility(ImageView.GONE);
                        qty_check.setVisibility(ImageView.GONE);
                        boxno_check.setVisibility(ImageView.GONE);
                        partno_check_cross.setVisibility(ImageView.GONE);
                        pono_check_cross.setVisibility(ImageView.GONE);
                        pono_chk.setVisibility(ImageView.GONE);

//                        editor.remove("Quantity");
//                        editor.remove("Part_No");
//                        editor.remove("Box_No");
//                        editor.remove("Po_No");
//                        editor.apply();
                        partNo.setText("");
                        qty.setText("");
                        boxno.setText("");
                        pono.setText("");
//                editor.clear();
//                editor.apply();

                        Intent intent=new Intent(AllBarcodeScan.this,CapturePictures.class);
                        startActivity(intent);

//                        String finalSrNo = pref.getString("Sr_Number", null);
//                        srNumber.setText(finalSrNo);

                    }
                    else {
                        if (jObj.getString("status").equals("Failed"))
                        {
                            String msgFailedSubmit = jObj.getString("message");

                            Toast.makeText(getApplicationContext(), msgFailedSubmit, Toast.LENGTH_SHORT).show();
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
                partno_check.setVisibility(ImageView.GONE);
                partno_check_cross.setVisibility(ImageView.GONE);
                pono_check_cross.setVisibility(ImageView.GONE);
                pono_chk.setVisibility(ImageView.GONE);
                boxno_check.setVisibility(ImageView.GONE);
                qty_check.setVisibility(ImageView.GONE);
            }

        });
        requestQueue.add(jsonObjReq);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }
    @Override
    public void onClick(View v) {
//
//        switch (v.getId())
//        {
//            case R.id.partNo:
//                Intent intent = new Intent(AllBarcodeScan.this, ScanAllBarcode.class);
//                intent.putExtra("BarcodeKey", flag);
//                startActivityForResult(intent, PART_NO);
//                break;
//
//        }

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AllBarcodeScan.this,ScanSrNumber.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
