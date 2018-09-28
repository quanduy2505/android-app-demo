package com.app.tuan88291.testapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.share.internal.ShareConstants;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.Collections;
import java.util.List;

public class Adapter_comment_1 extends Adapter<ViewHolder> {
    private Context context;
    Data_home current;
    int currentPos;
    List<Data_comment_1> data;
    private DBhelper db;
    private LayoutInflater inflater;

    /* renamed from: com.app.tuan88291.testapp.Adapter_comment_1.1 */
    class C02521 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02521(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_comment_1.this.info(this.val$position);
        }
    }

    /* renamed from: com.app.tuan88291.testapp.Adapter_comment_1.2 */
    class C02532 implements OnClickListener {
        final /* synthetic */ int val$position;

        C02532(int i) {
            this.val$position = i;
        }

        public void onClick(View v) {
            Adapter_comment_1.this.info(this.val$position);
        }
    }

    class MyHolder extends ViewHolder {
        CircleImageView avatar;
        TextView date;
        TextView nickname;
        TextView noidung;

        public MyHolder(View itemView) {
            super(itemView);
            this.nickname = (TextView) itemView.findViewById(C0336R.id.nickname);
            this.date = (TextView) itemView.findViewById(C0336R.id.date);
            this.noidung = (TextView) itemView.findViewById(C0336R.id.noidung);
            this.avatar = (CircleImageView) itemView.findViewById(C0336R.id.avatar);
        }
    }

    public Adapter_comment_1(Context context, List<Data_comment_1> data) {
        this.data = Collections.emptyList();
        this.currentPos = 0;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(this.inflater.inflate(C0336R.layout.custom_comment_layout, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        Data_comment_1 current = (Data_comment_1) this.data.get(position);
        this.db = new DBhelper(this.context);
        UrlImageViewHelper.setUrlDrawable(((MyHolder) holder).avatar, "http://graph.facebook.com/" + current.idfb + "/picture?type=large");
        myHolder.nickname.setText(current.name);
        myHolder.date.setText(current.date);
        myHolder.nickname.setOnClickListener(new C02521(position));
        myHolder.avatar.setOnClickListener(new C02532(position));
        myHolder.noidung.setText(current.noidung);
        if (current.stt.equals("yes")) {
            myHolder.nickname.setTextColor(SupportMenu.CATEGORY_MASK);
        } else {
            myHolder.nickname.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        }
    }

    public void info(int position) {
        Data_comment_1 item = (Data_comment_1) this.data.get(position);
        Intent scd = new Intent(this.context, Info_user.class);
        scd.putExtra("idfb", item.idfb);
        scd.putExtra(ShareConstants.WEB_DIALOG_PARAM_NAME, item.name);
        this.context.startActivity(scd);
    }

    public int getItemCount() {
        return this.data.size();
    }
}
