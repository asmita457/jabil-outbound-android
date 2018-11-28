package com.example.inceptive.imageadapter.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.inceptive.imageadapter.Adapter.ImageListAdapter;
import com.example.inceptive.imageadapter.ModelClass.ImageModel;
import com.example.inceptive.imageadapter.R;

import java.util.ArrayList;

public class GridPictures extends AppCompatActivity {


    GridView gridView;
    ArrayList<ImageModel> list;
    ImageListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_pictures);

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new ImageListAdapter(this, R.layout.image_list_view, list);
        gridView.setAdapter(adapter);


        Cursor cursor = CapturePictures.sqLiteHelper.getData("SELECT * FROM BOXIMAGE");
        list.clear();
        while (cursor.moveToNext()) {
            int id=cursor.getInt(0);
//            String name = cursor.getString(1);
            byte[] image = cursor.getBlob(1);

            list.add(new ImageModel(id, image));
        }
        adapter.notifyDataSetChanged();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {"Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(GridPictures.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // delete
                            Cursor c = CapturePictures.sqLiteHelper.getData("SELECT id FROM BOXIMAGE");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private void showDialogDelete(final int idImage){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(GridPictures.this);

        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure want to delete ?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    CapturePictures.sqLiteHelper.deleteData(idImage);
                    Intent i=new Intent(GridPictures.this,GridPictures.class);
                    startActivity(i);
                    Toast.makeText(getApplicationContext(), "Delete successfully!!!",Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("error", e.getMessage());
                }
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(GridPictures.this,CapturePictures.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
