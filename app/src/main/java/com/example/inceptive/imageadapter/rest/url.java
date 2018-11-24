package com.example.inceptive.imageadapter.rest;

import android.content.SharedPreferences;

public class url
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    //inceptive network
//        public static String BASE_URL = "http://192.168.0.46:8075/";


    //AndroidAP network

//    public static String BASE_URL = "http://192.168.43.61:8075/";


    //InceptiveQA network

//    public static String BASE_URL = "http://192.168.0.13:8075/";

public static String IMAGE_UPLOAD="api/SRFormsAPI/AddImage";
    public static String ALL_BARCODE_SCAN = "api/SRFormsAPI/GetParameterDetails";

    public static String SUBMIT = "api/SRFormsAPI/AddScanningInfo";


}
