package com.example.inceptive.imageadapter.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
    LinearLayout linearTotalQuantity,linearScanQuantity,quantityScan,linearboxno;
    LinearLayout linearpart,linearSubmit;

    //URL API
    String ALL_BARCODE_SCAN="/api/SRFormsAPI/GetParameterDetails";
    String VALIDATE_QUANTITY="/api/SRFormsAPI/GetParameterDetails";
    public static String SUBMIT = "/api/SRFormsAPI/AddScanningInfo";
    String QUANTITY_TAG="quantity";

    String msgFailedBarcode,msgSuccessBarcode;
    String partSuccessMessage,partFailedMessage,poSuccessMessage,poFailedMessage;
    String totScan,scannedQty;
    String boxNoString,secondPartNoString,partNoString,secondQtyString,qtyString,poNoString,newString;
    String PART_NO_STRING="part_no";
    String PO_NO_STRING="po_number";
    String numbersOnly = "^[0-9]*$",scanned_qty,total_qty,shareTotalScan,shareScanQuantity;
    String getflagcode,getIpUrlAllBarcode;
    String flag="0",flag1="1",flag2="2",flag3="3",flag4="4",flag5="5",bxno,qy,ponos;
    static String prtno,srno,secondndpartnoString,secondqtyString;

    Boolean Partimage=false,QuantityImage=false,BoxnoImage=false,PonoImage=false,SecondPartnoImage=false;
    Boolean submitPartnoCheck,submitPonoCheck,submitSecondPartnoCheck;
//    Boolean submitQtyCheck,submitBoxnoCheck;

    static ImageView piccgallary,partno_check_cross,pono_chk,pono_check_cross,partno_check;
    static ImageView qty_check_cross,qty_check,second_partno_check,second_partno_cross;

    static TextView partNo,qty,boxno,pono,submit,totalScanQuantity,ScanQuantity,srNumber,partnoSecond,qtySecond;

    Intent intentr;

    public static final int PART_NO = 0;
    public static final int QUANTITY =1;
    public static final int BOX_NO = 2;
    public static final int PO_NO = 3;
    public static final int Second_Quantity = 5;
    public static final int Second_Part_No = 4;

    int barcodeFlag=0;
    Context context;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private String firstQuantityFlag;
    private String secondQuantityFlag;

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
        SecondPartnoImage=pref.getBoolean("secondPartnochk",false);
//        QuantityImage = pref.getBoolean("quantitychk", false);
//        BoxnoImage=pref.getBoolean("boxnochk",false);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();
            }
        });

        quantityScan.setVisibility(ImageView.GONE);

        //scanning all barcodes.
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

        qtySecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                editor.putString("flagforscanbarcode", "5");
                editor.apply();
                Intent intent = new Intent(AllBarcodeScan.this, ScanAllBarcode.class);
                intent.putExtra("BarcodeKey", flag5);
                startActivityForResult(intent, QUANTITY);

            }
        });
        partnoSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("flagforscanbarcode", "6");
                editor.apply();
                Intent intent = new Intent(AllBarcodeScan.this, ScanAllBarcode.class);
                intent.putExtra("BarcodeKey", flag4);
                startActivityForResult(intent, Second_Part_No);

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

        //get shared preferance values for barcode scanning
        partNoString = pref.getString("Part_No", null);
        secondPartNoString = pref.getString("Second_Part_No", null);
        qtyString = pref.getString("Quantity", null);
        secondQtyString = pref.getString("Second_Quantity", null);

        boxNoString = pref.getString("Box_No", null);
        poNoString = pref.getString("Po_No", null);

        partNo.setText(partNoString);
        qty.setText(qtyString);
        boxno.setText(boxNoString);
        pono.setText(poNoString);
        partnoSecond.setText(secondPartNoString);
        qtySecond.setText(secondQtyString);

        prtno=partNo.getText().toString();
        qy=qty.getText().toString();
        bxno=boxno.getText().toString();
        ponos=pono.getText().toString();
        secondndpartnoString=partnoSecond.getText().toString();
        secondqtyString=qtySecond.getText().toString();

        if((bxno==null || bxno.equals("")))
        {
        }
        else
        {
            if(getflagcode.equals("3"))

            {
//                boxno_check.setVisibility(ImageView.VISIBLE);
                editor.putBoolean("boxnochk", true);
                editor.apply();

                flagCheck();
//                if(QuantityImage)
//                {
//                    qty_check.setVisibility(ImageView.VISIBLE);
//                }
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
//                    editor.putBoolean("quantitychk", true);
//                    editor.apply();
                    firstQuantityFlag="1";
                    ValidateQuantity(firstQuantityFlag);
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

//                if(BoxnoImage)
//                {
//                    boxno_check.setVisibility(ImageView.VISIBLE);
//
//                }

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
        if(secondndpartnoString==null || secondndpartnoString.equals("")) {
        }
        else
        {


            if(getflagcode.equals("6"))
            {

                if(prtno.equals(secondndpartnoString))
                {
                    Toast.makeText(getApplicationContext(),"Part is matched",Toast.LENGTH_SHORT).show();
                    flagCheck();
                    second_partno_check.setVisibility(View.VISIBLE);
                    second_partno_cross.setVisibility(View.GONE);

                }
                else
                {
                    final AppCompatDialog dialog = new AppCompatDialog(AllBarcodeScan.this);
                    dialog.setContentView(R.layout.partnomatchedpopup);
                    dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    Button partnoOk = (Button) dialog.findViewById(R.id.partnoOk);

                    dialog.setCanceledOnTouchOutside(true);

                    partnoOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(AllBarcodeScan.this, ScanAllBarcode.class);
                            intent.putExtra("BarcodeKey", flag4);
                            startActivityForResult(intent, Second_Part_No);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    flagCheck();
                    second_partno_cross.setVisibility(View.VISIBLE);
                    second_partno_check.setVisibility(View.GONE);

                }
//                barcodeFlag=3;
//                checkBarcode(barcodeFlag);
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
if(secondqtyString==null || secondqtyString.equals(""))
{

}
else
{
    if(getflagcode.equals("5"))
    {
        if(qtySecond.getText().toString().trim().matches(numbersOnly))
        {
            String validate_second_qty=qtySecond.getText().toString();
//            editor.putBoolean("quantitychk", true);
//            editor.apply();
            secondQuantityFlag="2";
            if(qy.equals(validate_second_qty))
            {
                Toast.makeText(getApplicationContext(),"Quantity is matched",Toast.LENGTH_SHORT).show();
            }
            else
            {
                final AppCompatDialog dialog = new AppCompatDialog(AllBarcodeScan.this);
                dialog.setContentView(R.layout.quantitymatchepopup);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                Button quantityOk = (Button) dialog.findViewById(R.id.quantityOk);

                dialog.setCanceledOnTouchOutside(true);

                quantityOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AllBarcodeScan.this, ScanAllBarcode.class);
                        intent.putExtra("BarcodeKey", flag5);
                        startActivityForResult(intent, Second_Quantity);
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
//            ValidateQuantity(secondQuantityFlag);
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
                    intent.putExtra("BarcodeKey", flag5);
                    startActivityForResult(intent, QUANTITY);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    }
}

    }

    //show all partno's of particular SR no's are scanned.
    private void dialogueMethod()
    {
        quantityScan.setVisibility(View.GONE);
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
        if(validate_qty=="1")
        {
            params.put("type",QUANTITY_TAG);
            params.put("part_no", prtno);
            params.put("sr_no",srno );
            params.put("po_number",ponos );
            params.put("quantity",qy );

        }else if(validate_qty=="2")
        {
            params.put("type",QUANTITY_TAG);
            params.put("part_no", prtno);
            params.put("sr_no",srno );
            params.put("po_number",ponos );
            params.put("quantity",secondqtyString );

        }

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

                        submit.setEnabled(true);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//                           qty_check.setVisibility(ImageView.VISIBLE);
//                        qty_check_cross.setVisibility(ImageView.GONE);

                    }
                    else {

                        if (jObj.getString("status").equals("Failed"))
                        {
                            String msgFailed = jObj.getString("message");

                            Toast.makeText(getApplicationContext(), msgFailed, Toast.LENGTH_SHORT).show();
                            disabledSubmitButton();
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

    private void disabledSubmitButton() {
//        submit.setEnabled(false);

        submit.setActivated(false);
        submit.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.disabled_background_color));
        submit.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.disabled_text_color));
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                if(!submit.isActivated()){
                    Toast.makeText(AllBarcodeScan.this, "Check Quantity",Toast.LENGTH_LONG).show();
                    return;
                }
                //else do your stuff
            }
    });
    }

    private void BoxNo() {
        boxno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        submitPartnoCheck=partno_check.isShown();
        submitPonoCheck=pono_chk.isShown();
        submitSecondPartnoCheck=second_partno_check.isShown();

        if(prtno==null && prtno.equals("") && qy==null && qy.equals("") && bxno==null && bxno.equals("") && ponos==null && ponos.equals("") && secondqtyString==null && secondqtyString.equals("") && secondndpartnoString==null && secondndpartnoString.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Please fill all details",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (submitPonoCheck && submitPartnoCheck && submitSecondPartnoCheck) {

                editor.putString("flagforscanbarcode", "0");
                if((secondndpartnoString.equals(prtno)) && (secondqtyString.equals(qy)))
                {
                    submitData();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please check Part No. & Quantity",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Please check fields",Toast.LENGTH_SHORT).show();

            }
        }
//        if (submitQtyCheck && submitPonoCheck && submitPartnoCheck && submitBoxnoCheck) {


    }



    //initialization
    private void init() {

        srNumber = (TextView) findViewById(R.id.srNumber);
        pono = (TextView) findViewById(R.id.pono);
        boxno = (TextView) findViewById(R.id.boxNo);
        qty = (TextView) findViewById(R.id.qty);
        partNo = (TextView) findViewById(R.id.partNo);
        partnoSecond=(TextView)findViewById(R.id.partNoSecond);
        qtySecond=(TextView)findViewById(R.id.qtySecond);

        submit = (TextView) findViewById(R.id.submit);
        totalScanQuantity=(TextView)findViewById(R.id.totalScanQuantity);
        ScanQuantity=(TextView)findViewById(R.id.ScanQuantity);

        second_partno_check=(ImageView) findViewById(R.id.second_partno_check);
        second_partno_cross=(ImageView) findViewById(R.id.second_partno_cross);

        partno_check_cross = (ImageView) findViewById(R.id.partno_check_cross);
        partno_check = (ImageView) findViewById(R.id.partno_check);
//        qty_check = (ImageView) findViewById(R.id.qty_check);
//        qty_check_cross=(ImageView)findViewById(R.id.qty_check_cross);
//        boxno_check = (ImageView) findViewById(R.id.boxno_check);
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
            params.put("part_no",prtno );

        }else if(flag == 3)
        {
            params.put("type", PART_NO_STRING);
            params.put("part_no",secondndpartnoString);
            params.put("sr_no",srno );

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
                                                quantityScan.setVisibility(View.GONE);
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

                            if(secondndpartnoString != null) {
                                if (secondndpartnoString.equals("")) {
                                    second_partno_cross.setVisibility(ImageView.GONE);
                                    second_partno_check.setVisibility(ImageView.GONE);
                                } else {

                                    if(prtno.equals(secondndpartnoString))
                                    {
                                        second_partno_check.setVisibility(View.VISIBLE);
                                        second_partno_cross.setVisibility(View.GONE);

                                    }
                                    else
                                    {
                                        second_partno_cross.setVisibility(View.VISIBLE);
                                        second_partno_check.setVisibility(View.GONE);


                                    }
//                                    if (SecondPartnoImage) {
//                                        second_partno_cross.setVisibility(ImageView.GONE);
//                                        second_partno_check.setVisibility(ImageView.VISIBLE);
//
//                                        if (totScan.equals(scanned_qty)) {
//                                            quantityScan.setVisibility(View.GONE);
//                                            dialogueMethod();
//                                        }
//                                    } else {
//                                        second_partno_cross.setVisibility(ImageView.VISIBLE);
//                                        second_partno_check.setVisibility(ImageView.GONE);
//
//                                    }
                                }
                            }

//                                if(QuantityImage)
//                             {
//                                 qty_check.setVisibility(ImageView.VISIBLE);
//
//                             }
//                            if(BoxnoImage)
//                            {
//                                boxno_check.setVisibility(ImageView.VISIBLE);
//                            }

                            }
                            else if(flag==2)
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
                                quantityScan.setVisibility(View.GONE);
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
                                        if(totScan.equals(scanned_qty))
                                        {
                                            quantityScan.setVisibility(View.GONE);
                                            dialogueMethod();
                                        }
                                        else
                                        {
                                            quantityScan.setVisibility(View.VISIBLE);

                                        }
                                        totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
                                        ScanQuantity.setText(pref.getString("SCANNED",""));
                                        totScan=totalScanQuantity.getText().toString();
                                        scannedQty=ScanQuantity.getText().toString();

                                    }
                                    else
                                    {
                                        partno_check_cross.setVisibility(ImageView.VISIBLE);
                                        partno_check.setVisibility(ImageView.GONE);

                                    }
                                }


                            }
                                if(secondndpartnoString != null) {
                                    if (secondndpartnoString.equals("")) {
                                        second_partno_cross.setVisibility(ImageView.GONE);
                                        second_partno_check.setVisibility(ImageView.GONE);
                                    } else {

                                        if(prtno.equals(secondndpartnoString))
                                        {
                                            second_partno_check.setVisibility(View.VISIBLE);
                                            second_partno_cross.setVisibility(View.GONE);

                                        }
                                        else
                                        {
                                            second_partno_cross.setVisibility(View.VISIBLE);
                                            second_partno_check.setVisibility(View.GONE);


                                        }
//                                        if (SecondPartnoImage) {
//                                            second_partno_cross.setVisibility(ImageView.GONE);
//                                            second_partno_check.setVisibility(ImageView.VISIBLE);
//
//                                            if (totScan.equals(scanned_qty)) {
//                                                quantityScan.setVisibility(View.GONE);
//                                                dialogueMethod();
//                                            }
//                                        } else {
//                                            second_partno_cross.setVisibility(ImageView.VISIBLE);
//                                            second_partno_check.setVisibility(ImageView.GONE);
//
//                                        }
                                    }
                                }
//                            if(QuantityImage)
//                            {
//                                qty_check.setVisibility(ImageView.VISIBLE);
//                            }
//                            if(BoxnoImage)
//                            {
//                                boxno_check.setVisibility(ImageView.VISIBLE);
//                            }


                        }
                        else {
                            editor.putBoolean("secondPartnochk", true);
                            editor.apply();
                            second_partno_check.setVisibility(ImageView.VISIBLE);
                            second_partno_cross.setVisibility(ImageView.GONE);

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
                                            quantityScan.setVisibility(View.GONE);
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
                                    if(Partimage)
                                    {
                                        partno_check_cross.setVisibility(ImageView.GONE);
                                        partno_check.setVisibility(ImageView.VISIBLE);
//                                        quantityScan.setVisibility(View.VISIBLE);
                                        totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
                                        ScanQuantity.setText(pref.getString("SCANNED",""));
                                        totScan=totalScanQuantity.getText().toString();
                                        scannedQty=ScanQuantity.getText().toString();
                                        if(totScan.equals(scanned_qty))
                                        {
                                            quantityScan.setVisibility(View.GONE);
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
//                                         quantityScan.setVisibility(View.VISIBLE);
//
//                                         totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
//                                         ScanQuantity.setText(pref.getString("SCANNED",""));
//                                         totScan=totalScanQuantity.getText().toString();
//                                         scannedQty=ScanQuantity.getText().toString();
//                                         if(totScan.equals(scanned_qty))
//                                         {
//                                             dialogueMethod();
//                                         }
                                         }
                                 }

                             }
                                if(secondndpartnoString != null)
                                {
                                    if(secondndpartnoString.equals(""))
                                    {
                                        second_partno_cross.setVisibility(ImageView.GONE);
                                        second_partno_check.setVisibility(ImageView.GONE);
                                    }
                                    else
                                    {
                                        if(prtno.equals(secondndpartnoString))
                                        {
                                            second_partno_check.setVisibility(View.VISIBLE);
                                            second_partno_cross.setVisibility(View.GONE);

                                        }
                                        else
                                        {
                                            second_partno_cross.setVisibility(View.VISIBLE);
                                            second_partno_check.setVisibility(View.GONE);


                                        }
                                    }

                                }



                            }
                            else if(flag==2)
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

                                if(secondndpartnoString != null)
                                {
                                    if(secondndpartnoString.equals(""))
                                    {
                                        second_partno_cross.setVisibility(ImageView.GONE);
                                        second_partno_check.setVisibility(ImageView.GONE);
                                    }
                                    else
                                    {
                                        if(prtno.equals(secondndpartnoString))
                                        {
                                            second_partno_check.setVisibility(View.VISIBLE);
                                            second_partno_cross.setVisibility(View.GONE);

                                        }
                                        else
                                        {
                                            second_partno_cross.setVisibility(View.VISIBLE);
                                            second_partno_check.setVisibility(View.GONE);


                                        }
                                    }

                                }

//
//                                if(QuantityImage)
//                                {
//                                    qty_check.setVisibility(ImageView.VISIBLE);
//                                }
//                                if(BoxnoImage)
//                                {
//                                    boxno_check.setVisibility(ImageView.VISIBLE);
//                                }

                            }
                            else
                            {
                                editor.putBoolean("secondPartnochk", false);
                                editor.apply();

                                second_partno_cross.setVisibility(ImageView.VISIBLE);
                                second_partno_check.setVisibility(ImageView.GONE);
//
//                                if(secondndpartnoString != null)
//                                {
//                                    if(secondndpartnoString.equals(""))
//                                    {
//                                        second_partno_cross.setVisibility(ImageView.GONE);
//                                        second_partno_check.setVisibility(ImageView.GONE);
//                                    }
//                                    else
//                                    {
//                                        if(!SecondPartnoImage)
//                                        {
//                                            second_partno_cross.setVisibility(ImageView.VISIBLE);
//                                            second_partno_check.setVisibility(ImageView.GONE);
//                                        }
//                                        else
//                                        {
//                                            second_partno_cross.setVisibility(ImageView.GONE);
//                                            second_partno_check.setVisibility(ImageView.VISIBLE);
//                                        }
//                                    }
//
//                                }

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
                                if(ponos != null) {
                                    if (ponos.equals("")) {

                                    } else {
                                        if (!PonoImage) {
                                            pono_check_cross.setVisibility(ImageView.VISIBLE);
                                            pono_chk.setVisibility(ImageView.GONE);
                                        } else if (PonoImage) {
                                            pono_check_cross.setVisibility(ImageView.GONE);
                                            pono_chk.setVisibility(ImageView.VISIBLE);
//                                         quantityScan.setVisibility(View.VISIBLE);
//
//                                         totalScanQuantity.setText( pref.getString("TOTAL_SCAN", ""));
//                                         ScanQuantity.setText(pref.getString("SCANNED",""));
//                                         totScan=totalScanQuantity.getText().toString();
//                                         scannedQty=ScanQuantity.getText().toString();
//                                         if(totScan.equals(scanned_qty))
//                                         {
//                                             dialogueMethod();
//                                         }
                                        }
                                    }
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
                        quantityScan.setVisibility(View.GONE);
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

        if(secondndpartnoString != null)
        {
            if(secondndpartnoString.equals(""))
            {
                second_partno_check.setVisibility(ImageView.GONE);
                second_partno_cross.setVisibility(ImageView.GONE);
            }
            else
            {

                if(prtno.equals(secondndpartnoString))
                {
                    second_partno_check.setVisibility(View.VISIBLE);
                    second_partno_cross.setVisibility(View.GONE);

                }
                else
                {
                    second_partno_cross.setVisibility(View.VISIBLE);
                    second_partno_check.setVisibility(View.GONE);


                }
//                if(!SecondPartnoImage)
//                {
//                    second_partno_cross.setVisibility(ImageView.VISIBLE);
//                    second_partno_check.setVisibility(ImageView.GONE);
//                }
//                else
//                {
//                    second_partno_cross.setVisibility(ImageView.GONE);
//                    second_partno_check.setVisibility(ImageView.VISIBLE);
//                }
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


                        quantityScan.setVisibility(View.GONE);
                        partno_check.setVisibility(View.INVISIBLE);
                        pono_chk.setVisibility(View.INVISIBLE);
                        second_partno_check.setVisibility(View.INVISIBLE);

                        editor.remove("Quantity");
                        editor.remove("Part_No");
                        editor.remove("Box_No");
                        editor.remove("Po_No");
                        editor.remove("Second_Quantity");
                        editor.remove("Second_Part_No");
                        editor.commit();
                        editor.apply();
                        partNo.setText("");
                        qty.setText("");
                        boxno.setText("");
                        pono.setText("");
                        partnoSecond.setText("");
                        qtySecond.setText("");

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
                Toast.makeText(getApplicationContext(),"Please fill all details",Toast.LENGTH_SHORT).show();
//                partno_check.setVisibility(ImageView.GONE);
//                partno_check_cross.setVisibility(ImageView.GONE);
//                pono_check_cross.setVisibility(ImageView.GONE);
//                pono_chk.setVisibility(ImageView.GONE);
//                boxno_check.setVisibility(ImageView.GONE);
//                qty_check.setVisibility(ImageView.GONE);
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
