package com.app.tuan88291.testapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import com.nightonke.boommenu.C0763R;
import java.util.ArrayList;

public class Main_grid extends Activity implements OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 234;
    Adapter_grid customGridAdapter;
    private Uri filePath;
    ArrayList<list_grid> gridArray;
    GridView gridView;
    Button nut;

    public Main_grid() {
        this.gridArray = new ArrayList();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0336R.layout.main_grid_layout);
        Bitmap homeIcon = BitmapFactory.decodeResource(getResources(), C0336R.drawable.chef);
        Bitmap userIcon = BitmapFactory.decodeResource(getResources(), C0336R.drawable.logo);
        this.nut = (Button) findViewById(C0763R.id.button);
        this.nut.setOnClickListener(this);
        this.gridView = (GridView) findViewById(C0336R.id.gridView1);
        this.customGridAdapter = new Adapter_grid(this, C0336R.layout.custom_grid, this.gridArray);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null && data.getData() != null) {
            this.filePath = data.getData();
            this.filePath.toString();
        }
    }

    public void onClick(View v) {
        showFileChooser();
    }
}
