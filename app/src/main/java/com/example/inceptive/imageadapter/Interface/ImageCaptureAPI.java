package com.example.inceptive.imageadapter.Interface;

import com.example.inceptive.imageadapter.Activity.Imageuploadresponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImageCaptureAPI {
    @POST("/api/SRFormsAPI/AddImage")
    Call<Imageuploadresponse> UploadMultipleImages(@Body RequestBody file);
}
