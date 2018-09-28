package com.example.quan.tieu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Input extends AppCompatActivity {

    private Dialog dialog;

    EditText textSoLuong;
    EditText textDauGia;
    EditText textDungTrong;
    EditText textDoAm;
    TextView ketQua;

    double soLuong;
    double dauGia;
    double dungTrong;
    double doAm;

    double thanhTien;

    Button btnSubmit, btnClear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //gan id

        try {

            textSoLuong = (EditText) findViewById(R.id.textSoLuong);
            textDauGia = (EditText) findViewById(R.id.textDauGia);
            textDungTrong = (EditText) findViewById(R.id.textDungTrong);
            textDoAm = (EditText) findViewById(R.id.textDoAm);
            btnSubmit = (Button) findViewById(R.id.btnSubmit);
            btnClear = (Button) findViewById(R.id.btnClear);

            //        //auto format number EditText dau gia
            //  textDauGia.addTextChangedListener(onTextChangedListener());

            //xu ly button ok
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textSoLuong.getText().toString().length() == 0 ||
                            textDauGia.getText().toString().length() == 0 ||
                            textDungTrong.getText().toString().length() == 0 ||
                            textDoAm.getText().toString().length() == 0) {
                        showAlertDialog();
                    } else {
                        soLuong = Double.parseDouble(textSoLuong.getText().toString());
                        dauGia = Double.parseDouble(textDauGia.getText().toString());
                        dungTrong = Double.parseDouble(textDungTrong.getText().toString());
                        doAm = Double.parseDouble(textDoAm.getText().toString());

                        thanhTien = soLuong * (dauGia + ((((dungTrong - 4) * 10) - (doAm - 13)) / 100) * dauGia);

//                NumberFormat format = getCurrencyInstance(Locale.US);
//                String ss = format.format(thanhTien).replace("$", "");

                        DecimalFormat decimal = new DecimalFormat("#,###");
                        String ss = decimal.format(thanhTien);


                        //  String ss = format.format(thanhTien);


                        //  Toast.makeText(getApplicationContext(),"saf"+ thanhTien, Toast.LENGTH_LONG).show();
                        ketQua = (TextView) findViewById(R.id.ketQua);
                        ketQua.setText(ss);
                    }
                }
            });


            //xu ly btn clear edit text
            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (textDauGia.getText().toString().length() == 0 ||
                            textDoAm.getText().toString().length() == 0 ||
                            textDungTrong.getText().toString().length() == 0 ||
                            textSoLuong.getText().toString().length() == 0) {
                        clear();

                    } else {
                        clear();
                    }


                }
            });

        } catch (Exception e) {

        }


    }


    //auto format number
    private TextWatcher onTextChangedListener() {

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textDauGia.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    textDauGia.setText(formattedString);
                    textDauGia.setSelection(textDauGia.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                textDauGia.addTextChangedListener(this);
            }


        };
    }

    //dialog

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông Báo");
        builder.setMessage("Bạn chưa nhập đầy đủ thông tin");
        builder.setCancelable(false);
//        builder.setPositiveButton("Ứ chịu", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(Input.this, "Không thoát được", Toast.LENGTH_SHORT).show();
//            }
//        });
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void clear() {
        textDauGia.getText().clear();
        textDungTrong.getText().clear();
        textDoAm.getText().clear();
        textSoLuong.getText().clear();
    }

}
