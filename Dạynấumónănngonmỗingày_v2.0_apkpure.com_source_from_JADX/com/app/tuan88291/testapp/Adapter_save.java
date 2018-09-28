package com.app.tuan88291.testapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build.VERSION;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.util.Collections;
import java.util.List;

public class Adapter_save extends Adapter<ViewHolder> {
    Adapter_save adapter;
    private Context context;
    List<Data_save> data;
    DBhelper db;
    private LayoutInflater inflater;

    /* renamed from: com.app.tuan88291.testapp.Adapter_save.1 */
    class C02811 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02811(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_save.this.chuyen(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_save.2 */
    class C02822 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02822(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_save.this.chuyen(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_save.3 */
    class C02833 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02833(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_save.this.xoa(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_save.4 */
    class C02844 implements DialogInterface.OnClickListener {
        String idbv;
        final /* synthetic */ int val$position;

        C02844(int i) {
            this.val$position = i;
            this.idbv = ((Data_save) Adapter_save.this.data.get(this.val$position)).getIdbv();
        }

        public void onClick(DialogInterface arg0, int arg1) {
            Adapter_save.this.data.remove(this.val$position);
            Adapter_save.this.adapter.notifyDataSetChanged();
            Adapter_save.this.db.deletecook(this.idbv);
            Adapter_save.this.db.deleteimg(this.idbv);
            Toast.makeText(Adapter_save.this.context, "\u0111\u00e3 x\u00f3a kh\u1ecfi m\u00e1y", 0).show();
            arg0.dismiss();
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_save.5 */
    class C02855 implements DialogInterface.OnClickListener {
        C02855() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    class MyHolder extends ViewHolder {
        ImageButton del;
        ImageView imtit;
        TextView namefood;
        TextView theloai;

        public MyHolder(View itemView) {
            super(itemView);
            this.namefood = (TextView) itemView.findViewById(C0336R.id.namefood);
            this.theloai = (TextView) itemView.findViewById(C0336R.id.theloai);
            this.imtit = (ImageView) itemView.findViewById(C0336R.id.imtit);
            this.del = (ImageButton) itemView.findViewById(C0336R.id.del);
        }
    }

    public Adapter_save(Context context, List<Data_save> data) {
        this.data = Collections.emptyList();
        this.adapter = this;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(this.inflater.inflate(C0336R.layout.custom_save, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        this.db = new DBhelper(this.context);
        MyHolder myHolder = (MyHolder) holder;
        Data_save current = (Data_save) this.data.get(position);
        if (VERSION.SDK_INT >= 24) {
            myHolder.namefood.setText(Html.fromHtml(current.getTit(), 0));
        } else {
            myHolder.namefood.setText(Html.fromHtml(current.getTit()));
        }
        if (this.db.checkimg(current.getIdbv()) <= 0) {
            ((MyHolder) holder).imtit.setImageResource(C0336R.drawable.resize);
        } else if (this.db.getlink(current.getIdbv()).indexOf("youtube.com") < 0) {
            Glide.with(this.context).load(Base64.decode(this.db.getlink(current.getIdbv()).substring(this.db.getlink(current.getIdbv()).indexOf(",") + 1), 0)).override((int) Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 350).fitCenter().into(((MyHolder) holder).imtit);
        } else {
            ((MyHolder) holder).imtit.setImageResource(C0336R.drawable.resize);
        }
        myHolder.theloai.setText(current.getTheloai());
        myHolder.imtit.setOnClickListener(new C02811(position));
        myHolder.namefood.setOnClickListener(new C02822(position));
        myHolder.del.setOnClickListener(new C02833(position));
    }

    public void chuyen(int position) {
        String tit = ((Data_save) this.data.get(position)).getTit();
        String noidung = ((Data_save) this.data.get(position)).getNoidung();
        String theloai = ((Data_save) this.data.get(position)).getTheloai();
        String idbv = ((Data_save) this.data.get(position)).getIdbv();
        Intent scd = new Intent(this.context, Detail_save.class);
        scd.putExtra("idbv", idbv);
        scd.putExtra("noidung", noidung);
        scd.putExtra("tit", tit);
        scd.putExtra("theloai", theloai);
        this.context.startActivity(scd);
    }

    public void xoa(int position) {
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setMessage((CharSequence) "B\u1ea1n c\u00f3 mu\u1ed1n x\u00f3a m\u00f3n \u0103n n\u00e0y?");
        alertDialogBuilder.setPositiveButton((CharSequence) "C\u00f3", new C02844(position));
        alertDialogBuilder.setNegativeButton((CharSequence) "Kh\u00f4ng", new C02855());
        alertDialogBuilder.create().show();
    }

    public int getItemCount() {
        return this.data.size();
    }
}
