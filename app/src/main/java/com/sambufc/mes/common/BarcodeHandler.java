package com.sambufc.mes.common;

import static com.navercorp.volleyextensions.volleyer.Volleyer.volleyer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import com.android.volley.Response;
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.model.BaseModel;

public class BarcodeHandler {

    //public enum READER_MODE { NONE, BOX, PALLET, WHOUSE };

    //public static READER_MODE mode = READER_MODE.NONE;
    public static String prevBarcode = null;

    public static AlertDialog barcodeDialog;
    public static void handler(BaseActivity context, String data){
//        LinkedHashMap<String, String> list = new LinkedHashMap<>();
        String actName = "";
        Bundle bundle = new Bundle();
        switch (data.substring(0, 1)) {
            case "U":
                actName = "com.sambufc.mes.activities.PalletActivity";
                bundle.putString("PALLET_NO", data);
                break;
            case "W":
//                    prevBarcode = data;
//                    getStackLocationData(context, data);
                actName = "com.sambufc.mes.activities.RectActivity";
                bundle.putString("RECT_NO", data);
                break;
            case "F":
            case "G":
            case "H":
            case "M":
            case "R":
                actName = "com.sambufc.mes.activities.BoxActivity";
                bundle.putString("BOX_NO", data);
                break;
            default:
                Toast.makeText(context, "준비중입니다.", Toast.LENGTH_LONG).show();
        }
        if (!actName.equals("")) startIntent(context, actName, bundle);

    }

//    public static void getStackLocationData(BaseActivity context, String data){
//        volleyer().post(Application.SERVER_URL + "/nexacroController.do")
//            .addHeader("Content-Type", "text/xml")
//            .addHeader("Cookie", Application.app.getSessionCookie())
//            .withBody(QueryXml.StackLocationInfo(data))
//            .withTargetClass(StackLocation.class)
//            .withResponseParser(new SimpleXmlNetworkResponseParser())
//            .withListener(new StackLocationSuccessListener(context))
//            .withErrorListener(error -> {
//                    Toast.makeText(context, "오류가 발생했습니다. 전산팀으로 문의바랍니다.(E-9815)", Toast.LENGTH_LONG).show();
//            })
//            .execute();
//    }


    public static void startIntent(Context context, String actName, Bundle bundle){
        try {
            Intent intent = null;
            intent = new Intent(context, Class.forName(actName));
            intent.putExtra("bundle", bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        } catch (ClassNotFoundException e) {
            Toast.makeText(context, "준비중입니다.", Toast.LENGTH_LONG).show();
        }
    }

//    public static void showBarcodeDialog(Context context){
//        AlertDialog.Builder ab = new AlertDialog.Builder(context);
//        ab.setCancelable(false);
//        ab.setTitle("바코드");
//        ab.setMessage("바코드를 스캔하세요.");
//        ab.setCancelable(false);
//        ab.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                if(i == KeyEvent.KEYCODE_BACK) {
//                    mode = READER_MODE.NONE;
//                    prevBarcode = null;
//                    dialogInterface.dismiss();
//                }
//                return i == KeyEvent.KEYCODE_BACK;
//            }
//        });
//        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                mode = READER_MODE.NONE;
//                prevBarcode = null;
//                dialogInterface.dismiss();
//            }
//        });
//        barcodeDialog = ab.create();
//        barcodeDialog.show();
//    }

}
//
//class StackLocationSuccessListener implements Response.Listener<MesResponse> {
//    BaseActivity context;
//    public StackLocationSuccessListener(BaseActivity context){
//        this.context = context;
//    }
//    @Override
//    public void onResponse(StackLocation response) {
//        String palletno = response.dataset.get(0).rows.row.get(0).getMap().get("PALLET_NO");
//        if(palletno == null || palletno.equals("")){
//            BarcodeHandler.mode = BarcodeHandler.READER_MODE.WHOUSE;
//            BarcodeHandler.showBarcodeDialog(context);
//        }else{
//            BarcodeHandler.handler(context, palletno);
//        }
//    }
//};

