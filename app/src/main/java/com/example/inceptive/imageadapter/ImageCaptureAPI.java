package com.example.inceptive.imageadapter;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImageCaptureAPI {
    @POST("/api/SRFormsAPI/AddImage")
    Call<Imageuploadresponse> UploadMultipleImages(@Body RequestBody file);
}
