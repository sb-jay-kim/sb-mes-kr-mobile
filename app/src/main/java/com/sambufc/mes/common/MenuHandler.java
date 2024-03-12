package com.sambufc.mes.common;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.sambufc.mes.BaseActivity;

public class MenuHandler {

    public static void handler(BaseActivity context, String key, Bundle bundle){
        switch (key){
            case "BOX":
                BarcodeHandler.startIntent(context, "com.sambufc.mes.activities.BoxActivity", bundle);
                break;
            case "BOX_INOUT":
                BarcodeHandler.startIntent(context, "com.sambufc.mes.activities.BoxInOutActivity", bundle);
                break;
            case "PALLET":
                BarcodeHandler.startIntent(context, "com.sambufc.mes.activities.PalletActivity", bundle);
                break;
            case "PALLET_MOVE" :
                BarcodeHandler.startIntent(context, "com.sambufc.mes.activities.ItemMoveActivity", bundle);
                break;
            case "PRINT_BARCODE" :
                BarcodeHandler.startIntent(context, "com.sambufc.mes.activities.PrintBarcodeActivity", bundle);
                break;
            case "SHIPMENT" :
                BarcodeHandler.startIntent(context, "com.sambufc.mes.activities.ShipmentListActivity", bundle);
                break;

            default: Toast.makeText(context, "준비중입니다.", Toast.LENGTH_LONG).show();
        }
    }

}
