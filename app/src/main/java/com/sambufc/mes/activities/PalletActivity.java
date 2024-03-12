package com.sambufc.mes.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.android.volley.VolleyError;
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.R;
import com.sambufc.mes.common.BarcodeHandler;
import com.sambufc.mes.common.ExecuteStackPalletToRack;
import com.sambufc.mes.common.InquiryPallet;
import com.sambufc.mes.common.MenuHandler;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.model.BaseModel;
import com.sambufc.mes.model.MesResponse;
import com.sambufc.mes.model.Row;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PalletActivity extends BaseActivity {

    RecyclerView recyclerView;
    String palletNo;
    boolean isViewMode;
    MesResponse mesResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallet);

        palletNo = getIntent().getBundleExtra("bundle").getString("PALLET_NO");
        isViewMode = getIntent().getBundleExtra("bundle").getBoolean("VIEW_MODE", false);

        recyclerView = findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        if(palletNo.startsWith("U")) {
            showProgress(R.string.data_now);
            new InquiryPallet(this, palletNo).excute(
                    (Response.Listener<MesResponse>) response -> {
                        mesResponse = response;
                        recyclerView.setAdapter(new SimpleRecyclerAdapter(PalletActivity.this));
                        dismissProgress();
                    }, new VollyerErrorListener(PalletActivity.this, "존재하지 않는 PALLET 입니다.", () -> {PalletActivity.this.finish();}));

            if(getIntent().getBundleExtra("bundle").getBoolean("STACK_IN_COMPLETED")){
                Toast.makeText(PalletActivity.this, "PALLET 적치완료.", Toast.LENGTH_LONG).show();
            }else if(getIntent().getBundleExtra("bundle").getBoolean("STACK_OUT_COMPLETED")){
                Toast.makeText(PalletActivity.this, "PALLET 적출완료.", Toast.LENGTH_LONG).show();
            }

            /** 바코드 출력 */
            findViewById(R.id.icon_barcode).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putString("BARCODE", palletNo);
                    MenuHandler.handler(PalletActivity.this, "PRINT_BARCODE", b);
                }
            });
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receiverStop();
                receiveDialog.dismiss();

                String data = intent.getStringExtra("data");
                if(data.startsWith("W"))
                    new ExecuteStackPalletToRack(PalletActivity.this, "IN", data, palletNo).excute((Response.Listener<MesResponse>) success -> {
                        Row item = success.dataset.get(0).rows.row.get(0);
                        String result = item.getMap().get("MESSAGE");
                        dismissProgress();

                        if(result.equals("DIFF"))
                            Toast.makeText(PalletActivity.this, "적치장소가 위치한 창고와 다른 위치에 있는 PALLET 입니다.", Toast.LENGTH_LONG).show();
                        else if(result.equals("ALREADY"))
                            Toast.makeText(PalletActivity.this, item.getMap().get("STACK_LOCATION_CD") + " 에 적치되어있는 PALLET 입니다.", Toast.LENGTH_LONG).show();
                        else if(result.equals("SUCCESS")){
                            Bundle b = new Bundle();
                            b.putString("PALLET_NO", palletNo);
                            b.putBoolean("STACK_IN_COMPLETED", true);
                            PalletActivity.this.dismissProgress();
                            PalletActivity.this.finish();
                            BarcodeHandler.startIntent(PalletActivity.this, "com.sambufc.mes.activities.PalletActivity", b);
                        }else
                            Toast.makeText(PalletActivity.this, "알수없는 오류입니다. 전산팀으로 문의바랍니다.", Toast.LENGTH_LONG).show();
                    }, new VollyerErrorListener(PalletActivity.this, "알수없는 오류입니다. 전산팀으로 문의바랍니다.", () ->  {
                        receiverStop();
                        receiveDialog.dismiss();
                        dismissProgress();
                    }));
                else {
                    Toast.makeText(PalletActivity.this, "RECT 바코드를 스캔하세요.", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    AlertDialog receiveDialog;
    @Override
    public void receiverStart() {
        super.receiverStart();

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setCancelable(false);
        ab.setTitle("바코드");
        ab.setMessage("바코드를 스캔하세요.");
        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                receiverStop();
                dialogInterface.dismiss();
            }
        });
        receiveDialog = ab.create();
        receiveDialog.show();
    }

    LinkedHashMap<String, String> list;
    class SimpleRecyclerAdapter extends RecyclerView.Adapter<VersionViewHolder> {
        Context context;


        public SimpleRecyclerAdapter(Context context) {
            this.context = context;
            setDefault();
        }

        public void setDefault() {
            Row fristItem = mesResponse.dataset.get(0).rows.row.get(0);
            LinkedHashMap<String, ArrayList<Row>> group = new LinkedHashMap<>();
            int boxcnt = 0;
            int boxwgt = 0;
            for (Row item : mesResponse.dataset.get(0).rows.row) {
                boxcnt++;
                boxwgt += (int)Double.parseDouble(item.getMap().get("WGT"));
                String key = item.getMap().get("ITEM_CD");
                if(group.containsKey(key)){
                    group.get(key).add(item);
                }else{
                    ArrayList<Row> list = new ArrayList<>();
                    list.add(item);
                    group.put(key, list);
                }
            }

            list = new LinkedHashMap<>();
            list.put("PALLET NO", fristItem.getMap().get("PALLET_NO"));
            list.put("수량", boxcnt + fristItem.getMap().get("UOM_CD"));
            list.put("총중량", boxwgt + fristItem.getMap().get("WGT_CD"));
            if(fristItem.getMap().get("ITEM_MOVING_NOW").equals("Y"))
                list.put("적재창고", fristItem.getMap().get("WHOUSE_NM") + " - " + "이동중");
            else
                list.put("적재창고", fristItem.getMap().get("WHOUSE_NM"));
            list.put("적치위치", fristItem.getMap().get("STACK_LOCATION_CD"));

            int idx = 1;

            for (String key : group.keySet()){
                ArrayList<Row> rows = group.get(key);
                //list.put("코드 " + idx, rows.get(0).getMap().get("ITEM_CD"));
                list.put("품목 " + idx, rows.get(0).getMap().get("ITEM_NM"));
                //list.put("중량", (int)Double.parseDouble(item.getMap().get("WGT")) + item.getMap().get("WGT_CD"));
                //list.put("생산일자", item.getMap().get("RECEIVE_DATE").substring(0, 4) + "-" + item.getMap().get("RECEIVE_DATE").substring(4, 6) + "-" + item.getMap().get("RECEIVE_DATE").substring(6, 8));
                int rowidx = 1;
                for (Row row : rows) {
                    list.put("BOX NO " + idx + "-" + rowidx++, row.getMap().get("BOX_NO"));
                    //list.put("LOT NO", item.getMap().get("LOT_NO"));
                }
                idx++;
            }
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_header_item, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
            String key = list.keySet().toArray(new String[list.size()])[i];
            versionViewHolder.title.setText(key);
            versionViewHolder.content.setText(list.get(key));
            versionViewHolder.itemView.setOnClickListener(new OnItemClickListener(i, key, list.get(key)));

            boolean isMoving = mesResponse.dataset.get(0).rows.row.get(0).getMap().get("ITEM_MOVING_NOW").equals("Y");
            if (!isViewMode) {
                versionViewHolder.itemView.setOnLongClickListener(new OnItemLongClickListener(i, key, list.get(key)));
                if (i == 0 && !isMoving) {
                    versionViewHolder.icon_add.setImageResource(R.drawable.edit_pallet_24);
                    versionViewHolder.icon_add.setVisibility(View.VISIBLE);
                } else if (i == 3){
                    versionViewHolder.icon_add.setImageResource(R.drawable.output_box_24);
                    versionViewHolder.icon_add.setVisibility(View.VISIBLE);
                } else if (i == 4 && !isMoving){
                    if(list.get(key) == null || list.get(key).equals(""))
                        versionViewHolder.icon_add.setImageResource(R.drawable.input_box_24);
                    else
                        versionViewHolder.icon_add.setImageResource(R.drawable.output_box_24);
                    versionViewHolder.icon_add.setVisibility(View.VISIBLE);
                }else
                    versionViewHolder.icon_add.setVisibility(View.GONE);
                }
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

    class OnItemClickListener implements View.OnClickListener {
        int idx;
        String key;
        String value;
        public OnItemClickListener(int idx, String key, String value){
            this.idx =idx;
            this.key = key;
            this.value = value;
        }
        @Override
        public void onClick(View view) {
            if(idx == 0) {

            }else if(idx == 3){

            }else if(idx == 4) {

            }else{
                if (key.startsWith("BOX NO")) {
                    BarcodeHandler.handler(PalletActivity.this, value);
                }
            }
        }
    };

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
                MenuHandler.handler(PalletActivity.this, "BOX_INOUT", b);
                //PalletInOutActivity
            }
            else if(idx == 3 && !fristItem.getMap().get("PALLET_NO").equals("")) {
                Bundle b = new Bundle();
                b.putString("BARCODE", fristItem.getMap().get("PALLET_NO"));
                MenuHandler.handler(PalletActivity.this, "PALLET_MOVE", b);
                PalletActivity.this.finish();
            }else if(idx == 4 && !isMoving){
                boolean flag = fristItem.getMap().get("STACK_LOCATION_CD") == null || fristItem.getMap().get("STACK_LOCATION_CD").equals("");

                if(flag){
                    receiverStart();
                }else{
                    AlertDialog.Builder ab = new AlertDialog.Builder(PalletActivity.this);
                    ab.setCancelable(false);
                    ab.setTitle("알림");
                    ab.setMessage("파레트를 적출합니다. 계속하시겠습니까?");
                    ab.setPositiveButton("확인", (dialogInterface, i) -> {
                        new ExecuteStackPalletToRack(PalletActivity.this, "OUT", fristItem.getMap().get("STACK_LOCATION_CD"), fristItem.getMap().get("PALLET_NO")).excute(new Response.Listener<MesResponse>(){
                            @Override
                            public void onResponse(MesResponse response) {
                                Bundle b = new Bundle();
                                b.putString("PALLET_NO", fristItem.getMap().get("PALLET_NO"));
                                b.putBoolean("STACK_OUT_COMPLETED", true);
                                PalletActivity.this.dismissProgress();
                                PalletActivity.this.finish();
                                BarcodeHandler.startIntent(PalletActivity.this, "com.sambufc.mes.activities.PalletActivity", b);
                            }
                        }, new VollyerErrorListener(PalletActivity.this, "해당위치에 존재하지 않는 PALLET 입니다.", () -> {

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

}
