package com.app.tuan88291.testapp;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.C0598R;
import java.util.ArrayList;

public class Detail_save extends AppCompatActivity {
    private String _idbv;
    private String _noidung;
    private String _theloai;
    private String _tit;
    private AppBarLayout appbar;
    private TextView content;
    DBhelper db;
    private Gallery gallery;
    ArrayList<Data_detail> list;
    private TextView namef;
    private ImageButton next;
    private CollapsingToolbarLayout titcus;
    Toolbar toolbar;

    /* renamed from: com.app.tuan88291.testapp.Detail_save.1 */
    class C03181 implements OnClickListener {
        C03181() {
        }

        public void onClick(View v) {
            Detail_save.this.finish();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Detail_save.2 */
    class C03192 implements OnItemClickListener {
        C03192() {
        }

        public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            Data_detail item = (Data_detail) Detail_save.this.list.get(position);
            Toast.makeText(Detail_save.this, "\u1ea3nh", 0).show();
        }
    }

    public Detail_save() {
        this.list = new ArrayList();
    }

    @TargetApi(21)
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView((int) C0336R.layout.detail_save_layout);
        this.db = new DBhelper(this);
        this.namef = (TextView) findViewById(C0336R.id.namefood);
        this.content = (TextView) findViewById(C0336R.id.noidung);
        this.gallery = (Gallery) findViewById(C0336R.id.gallery);
        this.toolbar = (Toolbar) findViewById(C0598R.id.toolbar);
        this.next = (ImageButton) findViewById(C0336R.id.next);
        this.toolbar.setNavigationIcon((int) C0336R.drawable.back);
        this.toolbar.setNavigationOnClickListener(new C03181());
        this.titcus = (CollapsingToolbarLayout) findViewById(C0336R.id.collapsingToolbar);
        Bundle ext = getIntent().getExtras();
        if (ext != null) {
            this._idbv = ext.getString("idbv");
            this._noidung = ext.getString("noidung");
            this._tit = ext.getString("tit");
            this._theloai = ext.getString("theloai");
        }
        if (VERSION.SDK_INT >= 24) {
            this.titcus.setTitle(Html.fromHtml(this._tit, 0));
        } else {
            this.titcus.setTitle(Html.fromHtml(this._tit));
        }
        if (VERSION.SDK_INT >= 24) {
            this.namef.setText(Html.fromHtml(this._tit, 0));
        } else {
            this.namef.setText(Html.fromHtml(this._tit));
        }
        if (VERSION.SDK_INT >= 24) {
            this.content.setText(Html.fromHtml(this._noidung, 0));
        } else {
            this.content.setText(Html.fromHtml(this._noidung));
        }
        this.gallery.setSpacing(10);
        this.list = this.db.getimg(this._idbv);
        this.gallery.setAdapter(new Adapter_detail_save(this, C0336R.layout.custom_detail_layout, this.list));
        if (this.list.size() > 1) {
            this.next.setVisibility(0);
        }
        this.gallery.setOnItemClickListener(new C03192());
    }
}
