package com.sambufc.mes;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sambufc.mes.common.BarcodeHandler;
import com.sambufc.mes.common.DeviceInfoUtil;

public class BaseActivity extends AppCompatActivity {
    ProgressDialog pDialog = null;
    public BroadcastReceiver receiver;
    public Class ResultForActivity = null;

    public BaseActivity(){
        initReceiver();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void initReceiver(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            if(ResultForActivity == null)
                BarcodeHandler.handler(BaseActivity.this, data);
            else {
                Bundle b = intent.getBundleExtra("Location");
            }
            }
        };
    }

    public void setResultActivity(Class cls){
        this.ResultForActivity = cls;
    }

    IntentFilter filter;
    public void receiverStart(){
        if(!DeviceInfoUtil.getDeviceName().equals("alps-PDA")) return;

        if(receiver == null){
            initReceiver();
        }
        if (filter == null) {
            filter = new IntentFilter();
            filter.addAction("action.sb.scan");
        }

        registerReceiver(receiver, filter);
    }

    public void receiverStop(){
        if(!DeviceInfoUtil.getDeviceName().equals("alps-PDA")) return;

        try {
            unregisterReceiver(receiver);
        }catch (Exception e){

        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//    }

    public void showProgress(int msg){
        if(pDialog == null) {
            pDialog = new ProgressDialog(this, android.R.style.Theme_Material_Dialog_Alert);
            pDialog.setCancelable(false);
        }
            pDialog.setMessage(getResources().getString(msg));
        pDialog.show();
    }

    public void dismissProgress(){
        if(pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiverStop();
    }

    public boolean getUseCamera(){
        return getSharedPreferences("sambumobilemes", Context.MODE_PRIVATE).getBoolean("useCamera", false);
    }
}