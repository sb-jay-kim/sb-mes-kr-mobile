package com.sambufc.mes.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.MainActivity;
import com.sambufc.mes.R;
import com.sambufc.mes.common.BarcodeHandler;
import com.sambufc.mes.common.ExecutePickingShipment;
import com.sambufc.mes.common.ExecuteStackPalletToRack;
import com.sambufc.mes.common.InquiryPallet;
import com.sambufc.mes.common.InquiryPickingShipment;
import com.sambufc.mes.common.InquiryShipment;
import com.sambufc.mes.common.MenuHandler;
import com.sambufc.mes.listeners.VollyerErrorCallbackListener;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.model.BaseModel;
import com.sambufc.mes.model.MesResponse;
import com.sambufc.mes.model.Row;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShipmentActivity extends BaseActivity {

    String sino = null;
    RecyclerView recyclerView;
    MesResponse mesResponse;

    Map<String, String> header = new HashMap<>();
    ArrayList<Row> list = new ArrayList<>();

    TextView main_title;
    TextView main_content;
    TextView sub_content;

    AlertDialog scanDialog;

    int total_wgt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment);

        sino = getIntent().getBundleExtra("bundle").getString("SINO");

//        btn_scan = findViewById(R.id.btn_scan);
        main_title = findViewById(R.id.listitem_title);
        main_content = findViewById(R.id.listitem_name);
        sub_content = findViewById(R.id.listsub_name);
        recyclerView = findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new SimpleRecyclerAdapter(ShipmentActivity.this));

        showProgress(R.string.data_now);
        new InquiryShipment(this, sino).excute(
            (Response.Listener<MesResponse>) response -> {
                mesResponse = response;
                header = mesResponse.dataset.get(0).rows.row.get(0).getMap();
                main_title.setText(String.format("%s / %s-%s-%s", header.get("SI_NO"), header.get("REQ_SHIP_DT").substring(0, 4), header.get("REQ_SHIP_DT").substring(4, 6), header.get("REQ_SHIP_DT").substring(6, 8)));
                main_content.setText(String.format("%s / %s / %s / %,.0f%s", header.get("CLIENT_NM"), header.get("FACTORY_CD"), header.get("ITEM_NM"), Double.parseDouble(header.get("REQ_WGT")), header.get("WGT_CD")));
                updateSubContent(0);
                inqueryDetail();
            }, new VollyerErrorListener(ShipmentActivity.this, "존재하지 않는 출고요청 입니다.", () -> {
                ShipmentActivity.this.finish();
            }));

        AlertDialog.Builder ab = new AlertDialog.Builder(ShipmentActivity.this);
        ab.setCancelable(false);
        ab.setTitle("알림");
        ab.setMessage("바코드를 스캔하세요.");
        ab.setNegativeButton("종료", (dialogInterface, i) -> {
            receiverStop();
            dialogInterface.dismiss();
        });
        scanDialog = ab.create();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");

            if (data.startsWith("U")) {
                inquiryPallet(data);
            } else if (data.length() > 0) {
                Toast.makeText(context, "잘못된 PALLET NO입니다.", Toast.LENGTH_SHORT).show();
            }
                receiverStop();
                scanDialog.dismiss();
            //BarcodeHandler.handler(PalletMoveActivity.this, data);
            }
        };

        findViewById(R.id.view_scan).setOnClickListener((view) -> {
            if(getUseCamera()){
                new IntentIntegrator(ShipmentActivity.this).initiateScan();
            }else {
                receiverStart();
                scanDialog.show();
            }
        });
    }

    public void updateSubContent(int total){
        sub_content.setText(String.format("피킹량: %,dKG", total));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result.getContents() != null && result.getContents().startsWith("U")){
            inquiryPallet(result.getContents());

        }else{
            Toast.makeText(this, "잘못된 PALLET NO입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiverStop();
    }

    void inqueryDetail(){
        //showProgress(R.string.data_now);
        list = new ArrayList<>();
        total_wgt = 0;
        new InquiryPickingShipment(this, sino).excute(
            (Response.Listener<MesResponse>) response -> {
                List<Row> rows = response.dataset.get(0).rows.row;
                for(int i = 0; i < rows.size(); i++){
                    total_wgt += (int) Double.parseDouble(rows.get(i).getMap().get("WGT"));
                    list.add(rows.get(i));
                }

                if((int)Double.parseDouble(header.get("REQ_WGT")) > total_wgt){
                    main_content.setTextColor(Color.RED);
                }else if((int)Double.parseDouble(header.get("REQ_WGT")) == total_wgt){
                    main_content.setTextColor(Color.BLUE);
                }

                updateSubContent(total_wgt);
                recyclerView.getAdapter().notifyDataSetChanged();
                dismissProgress();
            }, new VollyerErrorListener(ShipmentActivity.this, "", () -> {
                //ShipmentActivity.this.finish();
                    dismissProgress();
            }));
    }

    class SimpleRecyclerAdapter extends RecyclerView.Adapter<VersionViewHolder> {
        Context context;

        public SimpleRecyclerAdapter(Context context) {
            this.context = context;
            setDefault();
        }

        public void setDefault() {
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_header_item, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
            //Map<String, String> firstRow = list.get(i).get(0).getMap();

//            int totwgt = 0;
            int qty = (int) (Double.parseDouble(list.get(i).getMap().get("WGT")) / Double.parseDouble(list.get(i).getMap().get("UNIT_WEIGHT")));
//            for(int idx = 0; idx < list.get(i).size(); idx++){
//                totwgt +=
//            }

            versionViewHolder.title.setText(list.get(i).getMap().get("PALLET_NO"));
            versionViewHolder.content.setText(String.format("%d%s / %,d%s", qty, list.get(i).getMap().get("PRIMARY_UOM_CD"), (int)Double.parseDouble(list.get(i).getMap().get("WGT")), list.get(i).getMap().get("WGT_CD")));
            //versionViewHolder.itemView.setOnLongClickListener(new OnItemClickListener(row));

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        CardView cardItemLayout;
        TextView title;
        TextView content;
        ImageView icon_add;

        public VersionViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
            title = (TextView) itemView.findViewById(R.id.listitem_title);
            content = (TextView) itemView.findViewById(R.id.listitem_name);
            icon_add = itemView.findViewById(R.id.icon_add);
        }
    }

    class OnItemLongClickListener implements View.OnLongClickListener {
        int idx;
        String key;
        String value;
        public OnItemLongClickListener(int idx, String key, String value){
            this.idx =idx;
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean onLongClick(View view) {
            Row fristItem = mesResponse.dataset.get(0).rows.row.get(0);
            boolean isMoving = mesResponse.dataset.get(0).rows.row.get(0).getMap().get("ITEM_MOVING_NOW").equals("Y");

            if(idx == 0 && !isMoving){
                Bundle b = new Bundle();
                b.putString("PALLET_NO", fristItem.getMap().get("PALLET_NO"));
                MenuHandler.handler(ShipmentActivity.this, "BOX_INOUT", b);
                //PalletInOutActivity
            }
            else if(idx == 3 && !fristItem.getMap().get("PALLET_NO").equals("")) {
                Bundle b = new Bundle();
                b.putString("BARCODE", fristItem.getMap().get("PALLET_NO"));
                MenuHandler.handler(ShipmentActivity.this, "PALLET_MOVE", b);
                ShipmentActivity.this.finish();
            }else if(idx == 4 && !isMoving){
                boolean flag = fristItem.getMap().get("STACK_LOCATION_CD") == null || fristItem.getMap().get("STACK_LOCATION_CD").equals("");

                if(flag){
                    receiverStart();
                }else{
                    AlertDialog.Builder ab = new AlertDialog.Builder(ShipmentActivity.this);
                    ab.setCancelable(false);
                    ab.setTitle("알림");
                    ab.setMessage("파레트를 적출합니다. 계속하시겠습니까?");
                    ab.setPositiveButton("확인", (dialogInterface, i) -> {
                        new ExecuteStackPalletToRack(ShipmentActivity.this, "OUT", fristItem.getMap().get("STACK_LOCATION_CD"), fristItem.getMap().get("PALLET_NO")).excute(new Response.Listener<MesResponse>(){
                            @Override
                            public void onResponse(MesResponse response) {
                                Bundle b = new Bundle();
                                b.putString("PALLET_NO", fristItem.getMap().get("PALLET_NO"));
                                b.putBoolean("STACK_OUT_COMPLETED", true);
                                ShipmentActivity.this.dismissProgress();
                                ShipmentActivity.this.finish();
                                BarcodeHandler.startIntent(ShipmentActivity.this, "com.sambufc.mes.activities.PalletActivity", b);
                            }
                        }, new VollyerErrorListener(ShipmentActivity.this, "해당위치에 존재하지 않는 PALLET 입니다.", () -> {

                        }));
                        dialogInterface.dismiss();
                    });
                    ab.setNegativeButton("취소", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    });
                    ab.show();
                }

            }
//                if(key.startsWith("PALLET NO")) {
//                    receiverStart();
//                    AlertDialog.Builder ab = new AlertDialog.Builder(PalletActivity.this);
//                    ab.setTitle("PALLET - BOX 추가");
//                    ab.setMessage("추가할 BOX 바코드를 스캔하세요.");
//                    ab.setPositiveButton("취소", (dialogInterface, i) -> {
//                        receiverStop();
//                        dialogInterface.dismiss();
//                    });
//                    ab.show();
//                }
            return false;
        }
    };

    public void inquiryPallet(String data){
        if(data.startsWith("U")) {
            showProgress(R.string.data_now);
            new InquiryPallet(this, data).excute((Response.Listener<MesResponse>) response -> {
                List<Row> rows = response.dataset.get(0).rows.row;
                boolean checkItemcd = true;
                int totwgt = 0;

                for(int i = 0; i < rows.size(); i++){
                    totwgt += (int) Double.parseDouble(rows.get(i).getMap().get("WGT"));
                    if(!header.get("ITEM_CD").equals(rows.get(i).getMap().get("ITEM_CD")))
                        checkItemcd = false;
                }

                if(!checkItemcd){
                    Toast.makeText(ShipmentActivity.this, "출하요청된 품목과 다른 품목입니다.", Toast.LENGTH_LONG).show();
                    ShipmentActivity.this.dismissProgress();
                    return;
                }else if(!header.get("WHOUSE_CD").equals(rows.get(0).getMap().get("WHOUSE_CD"))){
                    Toast.makeText(ShipmentActivity.this, "다른창고에 적재된 PALLET 입니다.", Toast.LENGTH_LONG).show();
                    ShipmentActivity.this.dismissProgress();
                    return;
                }else if((int) Double.parseDouble(header.get("REQ_WGT")) < total_wgt + totwgt){
                    Toast.makeText(ShipmentActivity.this, "출하요청된 중량이 초과되었습니다.", Toast.LENGTH_LONG).show();
                    ShipmentActivity.this.dismissProgress();
                    return;
                }else{
                    new ExecutePickingShipment(ShipmentActivity.this, header.get("SI_NO"), rows.get(0).getMap().get("PALLET_NO"))
                    .excute((Response.Listener<BaseModel>) res -> {
                        inqueryDetail();
                    });
                    //list.add(rows);
                    //recyclerView.getAdapter().notifyDataSetChanged();
                }

            }, new VollyerErrorListener(ShipmentActivity.this, "잘못된 PALLET 입니다.", new VollyerErrorCallbackListener() {
                @Override
                public void OnFinished() {
                    dismissProgress();
                }
            }));

        }else{
            Toast.makeText(ShipmentActivity.this, "잘못된 PALLET 입니다.", Toast.LENGTH_LONG).show();
        }
    }

}
