package com.sambufc.mes.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.R;

public class PrintBarcodeActivity extends BaseActivity {

    String barcode;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_barcode);
        img = findViewById(R.id.img_print_rqcode);

        barcode = getIntent().getBundleExtra("bundle").getString("BARCODE");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        encodeQr(generatQrData(barcode));

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintBarcodeActivity.this.finish();
            }
        });
    }

    private String generatQrData(String barcode){
        return "sb://print.barcode/" + this.barcode + "/";
    }

    private void encodeQr(String data){
        try {
            QRCodeWriter writer = new QRCodeWriter();
            img.setImageBitmap(toBitmap(writer.encode(data, BarcodeFormat.QR_CODE, 300, 300)));
        }catch (WriterException e){
            Toast.makeText(this, "오류가 발생했습니다. 전산팀으로 문의바랍니다.", Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    private Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

}
