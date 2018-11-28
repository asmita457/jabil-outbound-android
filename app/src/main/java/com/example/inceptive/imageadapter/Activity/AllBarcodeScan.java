package com.example.inceptive.imageadapter.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.inceptive.imageadapter.R;
import com.example.inceptive.imageadapter.ScanBarcode.ScanAllBarcode;

import org.json.JSONObject;

import java.util.HashMap;

public class AllBarcodeScan extends AppCompatActivity implements  View.OnClickListener
{
    LinearLayout linearTotalQuantity,linearScanQuantity,quantityScan;
    LinearLayout linearpart,linearSubmit;

    String ALL_BARCODE_SCAN="/api/SRFormsAPI/GetParameterDetails";
    String VALIDATE_QUANTITY="/api/SRFormsAPI/GetParameterDetails";
    public static String SUBMIT = "/api/SRFormsAPI/AddScanningInfo";
    String QUANTITY_TAG="quantity";

    String msgFailedBarcode,msgSuccessBarcode;
    String partSuccessMessage,partFailedMessage,poSuccessMessage,poFailedMessage;
    String totScan,scannedQty;
    String boxNoString,partNoString,qtyString,poNoString,newString;
    String PART_NO_STRING="part_no";
    String PO_NO_STRING="po_number";
    String TAG = "AllBarcodeScan" ;
    String numbersOnly = "^[0-9]*$",scanned_qty,total_qty,shareTotalScan,shareScanQuantity;
    String getflagcode,getIpUrlAllBarcode;
    String flag="0",flag1="1",flag2="2",flag3="3",bxno,qy,ponos;
    static String prtno,srno;

    Boolean Partimage=false,QuantityImage=false,BoxnoImage=false,PonoImage=false;
    Boolean submitPartnoCheck,submitPonoCheck,submitQtyCheck,submitBoxnoCheck;

    static ImageView piccgallary,partno_check_cross,pono_chk,pono_check_cross,qty_check_cross;
    static ImageView partno_check,qty_check,boxno_check;

    static TextView partNo,qty,boxno,pono,submit,totalScanQuantity,ScanQuantity,srNumber;

    Intent intentr;

    public static final int PART_NO = 0;
    public static final int QUANTITY =1;
    public static final int BOX_NO = 2;
    public static final int PO_NO = 3;

    int barcodeFlag=0;
    Context context;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_barcode_scan);
        //initialization
        init();
        //
        Gallary();
        BoxNo();

        context = this ;
        getflagcode = pref.getString("flagforscanbarcode", "0");
        Partimage =  pref.getBoolean("partnocheck", false);
        PonoImage = pref.getBoolean("ponochk", false);
        QuantityImage = pref.getBoolean("quantitychk", false);
        BoxnoImage=pref.getBoolean("boxnochk",false);

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

        //get string from previous activity to next using intent
        intentr = getIntent();
        newString = intentr.getStringExtra("Sr_Number");
        String newSrString = pref.getString("Sr_Number", null);
        srNumber.setText(newSrString);
        srno=srNumber.getText().toString();

        //get shared preferance values
        partNoString = pref.getString("Part_No", null);
        qtyString = pref.getString("Quantity", null);
        boxNoString = pref.getString("Box_No", null);
        poNoString = pref.getString("Po_No", null);

        partNo.setText(partNoString);
        qty.setText(qtyString);
        boxno.setText(boxNoString);
        pono.setText(poNoString);

        prtno=partNo.getText().toString();
        qy=qty.getText().toString();
        bxno=boxno.getText().toString();
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
                    String validate_qty=qty.getText().toString();
                    editor.putBoolean("quantitychk", true);
                    editor.apply();
                    ValidateQuantity(validate_qty);
                    flagCheck();

                }
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
                    }

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

    //show all partno's of particular SR no's are scanned.
    private void dialogueMethod()
    {
        final AppCompatDialog dialog = new AppCompatDialog(AllBarcodeScan.this);
        dialog.setContentView(R.layout.dialoguepopup);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Button numbersOk = (Button) dialog.findViewById(R.id.backSr);

        dialog.setCanceledOnTouchOutside(true);

        numbersOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllBarcodeScan.this, ScanSrNumber.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
        editor.clear();
        editor.apply();
    }

    //Quantity validation.
    private void ValidateQuantity(String validate_qty) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("type",QUANTITY_TAG);
        params.put("part_no", prtno);
        params.put("sr_no",srno );
        params.put("quantity",qy );

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,getIpUrlAllBarcode+VALIDATE_QUANTITY,
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
                           qty_check.setVisibility(ImageView.VISIBLE);
                        qty_check_cross.setVisibility(ImageView.GONE);

                    }
                    else {

                        if (jObj.getString("status").equals("Failed"))
                        {
                            String msgFailed = jObj.getString("message");

                            Toast.makeText(getApplicationContext(), msgFailed, Toast.LENGTH_SHORT).show();
                            qty_check_cross.setVisibility(ImageView.VISIBLE);
                            qty_check.setVisibility(ImageView.GONE);
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

    private void BoxNo() {
        boxno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qty_check.isShown())
                {
                    editor.putString("flagforscanbarcode", "3");
                    editor.apply();

                    Intent intent = new Intent(AllBarcodeScan.this, ScanAllBarcode.class);
                    intent.putExtra("BarcodeKey", flag2);
                    startActivityForResult(intent, BOX_NO);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please get right quantity",Toast.LENGTH_SHORT).show();
                }

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
        pono = (TextView) findViewById(R.id.pono);
        boxno = (TextView) findViewById(R.id.boxNo);
        qty = (TextView) findViewById(R.id.qty);
        partNo = (TextView) findViewById(R.id.partNo);
        submit = (TextView) findViewById(R.id.submit);
        totalScanQuantity=(TextView)findViewById(R.id.totalScanQuantity);
        ScanQuantity=(TextView)findViewById(R.id.ScanQuantity);

        partno_check_cross = (ImageView) findViewById(R.id.partno_check_cross);
        partno_check = (ImageView) findViewById(R.id.partno_check);
        qty_check = (ImageView) findViewById(R.id.qty_check);
        qty_check_cross=(ImageView)findViewById(R.id.qty_check_cross);
        boxno_check = (ImageView) findViewById(R.id.boxno_check);
        piccgallary = (ImageView) findViewById(R.id.piccgalary);
        pono_check_cross=(ImageView)findViewById(R.id.pono_check_cross);
        pono_chk=(ImageView)findViewById(R.id.pono_chk);
        linearpart=(LinearLayout)findViewById(R.id.linearpart);

        linearScanQuantity=(LinearLayout)findViewById(R.id.linearScanQuantity);
        linearTotalQuantity=(LinearLayout)findViewById(R.id.linearTotalQuantity);
        quantityScan=(LinearLayout)findViewById(R.id.quantityScan);
        linearSubmit=(LinearLayout)findViewById(R.id.linearSubmit);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        getIpUrlAllBarcode = pref.getString("BaseUrlIp", "0");
        shareTotalScan = pref.getString("TOTAL_SCAN", "");
        shareScanQuantity = pref.getString("SCANNED", "");

        if(!shareTotalScan.equals("") && !shareScanQuantity.equals(""))
        {
            totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
            ScanQuantity.setText(pref.getString("SCANNED",""));
        }

    }

    //check partNO and poNo barcode API
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
                        //for success response
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
                                            totScan=totalScanQuantity.getText().toString();
                                            scannedQty=ScanQuantity.getText().toString();
                                            if(totScan.equals(scanned_qty))
                                            {
                                                dialogueMethod();
                                            }
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
                            poSuccessMessage=msgSuccessBarcode;
                             total_qty=jObj.getString("total_qty");
                            scanned_qty= jObj.getString("scanned_qty");
                            editor.putBoolean("ponochk", true);

                            editor.putString("TOTAL_SCAN",total_qty);
                            editor.putString("SCANNED",scanned_qty);
                            editor.apply();

                            totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
                            ScanQuantity.setText(pref.getString("SCANNED",""));
                            totScan=totalScanQuantity.getText().toString();
                            scannedQty=ScanQuantity.getText().toString();
                            if(totScan.equals(scanned_qty))
                            {
                                dialogueMethod();
                            }
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
                                        totScan=totalScanQuantity.getText().toString();
                                        scannedQty=ScanQuantity.getText().toString();
                                        if(totScan.equals(scanned_qty))
                                        {
                                            dialogueMethod();
                                        }
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
                                         totScan=totalScanQuantity.getText().toString();
                                         scannedQty=ScanQuantity.getText().toString();
                                         if(totScan.equals(scanned_qty))
                                         {
                                             dialogueMethod();
                                         }
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
        if(ponos != null && prtno!=null)
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

                     totScan=totalScanQuantity.getText().toString();
                     scannedQty=ScanQuantity.getText().toString();
                    if(totScan.equals(scanned_qty))
                    {
                        dialogueMethod();
                    }
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

    //POST API for submit button
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
                        partno_check.setVisibility(ImageView.GONE);
                        qty_check.setVisibility(ImageView.GONE);
                        boxno_check.setVisibility(ImageView.GONE);
                        partno_check_cross.setVisibility(ImageView.GONE);
                        pono_check_cross.setVisibility(ImageView.GONE);
                        pono_chk.setVisibility(ImageView.GONE);
                        partNo.setText("");
                        qty.setText("");
                        boxno.setText("");
                        pono.setText("");
                        editor.remove("Quantity");
                        editor.remove("Part_No");
                        editor.remove("Box_No");
                        editor.remove("Po_No");
                        editor.remove("partnocheck");
                        editor.remove("boxnochk");
                        editor.remove("quantitychk");
                        editor.remove("ponochk");
                        quantityScan.setVisibility(View.GONE);
                        editor.apply();
                    }
                    else {
                        if (jObj.getString("status").equals("Failed"))
                        {
                            int quantityLimit = Integer.parseInt(totScan) - Integer.parseInt(scannedQty);

                            String msgFailedSubmit = jObj.getString("message");

                            Toast.makeText(getApplicationContext(), msgFailedSubmit +"\nRemaining Quantity :- "+ quantityLimit, Toast.LENGTH_SHORT).show();
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


    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AllBarcodeScan.this,ScanSrNumber.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
