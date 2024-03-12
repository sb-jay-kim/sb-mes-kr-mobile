package com.sambufc.mes.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.R;
import com.sambufc.mes.common.ExecuteItemMove;
import com.sambufc.mes.common.InquiryBox;
import com.sambufc.mes.common.InquiryPallet;
import com.sambufc.mes.common.MenuHandler;
import com.sambufc.mes.listeners.VollyerErrorCallbackListener;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.model.BaseModel;
import com.sambufc.mes.model.MesResponse;
import com.sambufc.mes.model.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMoveActivity extends BaseActivity {

    enum MOVING_MODE { NONE, MOVE, STORE, DONE }

    MOVING_MODE mode = MOVING_MODE.NONE;
    RecyclerView recyclerView;
    ArrayList<List<Row>> items;
    CardView viewExecute;

    String barcode;
    String moveBoxNos;

    AlertDialog scanDialog;

    View viewWhouse;
    TextView viewTxtWhouse;
    String whouse = "W800";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallet_move);

        barcode = getIntent().getBundleExtra("bundle").getString("BARCODE");

        items = new ArrayList<>();
        moveBoxNos = "";
        whouse = "W800";

        viewExecute = findViewById(R.id.view_excute);
        viewExecute.setOnClickListener(view -> {
            onSave();
        });

        if(barcode != null) {
            if (barcode.startsWith("U")) {
                inquiryPallet(barcode);
            } else if (barcode.length() > 0) {
                inquiryBox(barcode);
            }
        }

        viewWhouse = findViewById(R.id.view_whouse);
        viewWhouse.setOnClickListener(new OnWhouseSelectListener());
        viewTxtWhouse = findViewById(R.id.txt_whouse);

        recyclerView = findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("data");

                if (data.startsWith("U")) {
                    inquiryPallet(data);
                } else if (data.length() > 0) {
                    inquiryBox(data);
                }
                scanDialog.dismiss();
                //BarcodeHandler.handler(PalletMoveActivity.this, data);
            }
        };

        recyclerView.setAdapter(new SimpleRecyclerAdapter(this));
//        receiverStart();

        AlertDialog.Builder ab = new AlertDialog.Builder(ItemMoveActivity.this);
        ab.setCancelable(false);
        ab.setTitle("알림");
        ab.setMessage("바코드를 스캔하세요.");
        ab.setNegativeButton("종료", (dialogInterface, i) -> {
            receiverStop();
            dialogInterface.dismiss();
        });
        scanDialog = ab.create();

        findViewById(R.id.view_scan).setOnClickListener((view) -> {
            if(getUseCamera()){
                new IntentIntegrator(ItemMoveActivity.this).initiateScan();
            }else {
                receiverStart();
                scanDialog.show();
            }
        });
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
        receiverStop();
        receiver = null;
        super.onDestroy();
    }

    public void onSave(){
        if(mode == MOVING_MODE.DONE) { return; }

        if(items.size() == 0){
            Toast.makeText(this, "이동할 재고가 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean isSamefromStock = true;
        boolean isSameToStock = false;
        String fromStock = items.get(0).get(0).getMap().get("WHOUSE_CD");
        for (List<Row> rows : items) {
            if(!fromStock.equals(rows.get(0).getMap().get("WHOUSE_CD"))){
                isSamefromStock = false;
            }
            if(whouse.equals(rows.get(0).getMap().get("WHOUSE_CD"))){
                isSameToStock = true;
            }
        }

        if(mode != MOVING_MODE.STORE && !isSamefromStock){
            Toast.makeText(this, "출발지가 다른 재고입니다.", Toast.LENGTH_LONG).show();
            return;
        }

        if(mode == MOVING_MODE.STORE && isSameToStock){
            Toast.makeText(this, "출발지와 도착지가 같은 재고가 존재합니다.", Toast.LENGTH_LONG).show();
            return;
        }

        new ExecuteItemMove(this, items, whouse, mode.ordinal()).excute(new Response.Listener<BaseModel>(){
            @Override
            public void onResponse(BaseModel response) {
                if(response.parameters.getMap().get("ErrorCode").equals("0")){
                    Toast.makeText(ItemMoveActivity.this, "재고이동을 실행하였습니다.", Toast.LENGTH_LONG).show();
                    ItemMoveActivity.this.finish();
                }else{
                    Toast.makeText(ItemMoveActivity.this, "오류가 발생했습니다. 전산팀으로 문의바랍니다.(E-3651)", Toast.LENGTH_LONG).show();
                }

                dismissProgress();
            }
        });
    }

    public void inquiryPallet(String data){
        if(data.startsWith("U")) {
            showProgress(R.string.data_now);
            new InquiryPallet(this, data).excute((Response.Listener<MesResponse>) response -> {

                List<Row> rows = response.dataset.get(0).rows.row;

                boolean isMoving = false;
                for(Row item : rows){
                    if(item.getMap().get("ITEM_MOVING_NOW").equals("Y"))
                        isMoving = true;
                }

                if(mode == MOVING_MODE.NONE && isMoving){
                    mode = MOVING_MODE.STORE;
                    ((TextView)findViewById(R.id.txt_title)).setText("재고이동 - 입고");
                    viewWhouse.setVisibility(View.VISIBLE);
                }else if(mode == MOVING_MODE.NONE && !isMoving){
                    mode = MOVING_MODE.MOVE;
                    ((TextView)findViewById(R.id.txt_title)).setText("재고이동 - 출고");
                    viewWhouse.setVisibility(View.GONE);
                }

                if (mode == MOVING_MODE.STORE && !isMoving) {
                    Toast.makeText(ItemMoveActivity.this, "입고작업중에는 출고되지 않은 재고를 추가 할 수 없습니다.", Toast.LENGTH_LONG).show();
                    ItemMoveActivity.this.dismissProgress();
                    return;
                } else if (mode == MOVING_MODE.MOVE && isMoving) {
                    Toast.makeText(ItemMoveActivity.this, "출고작업중에는 이동중인 재고를 추가 할 수 없습니다.", Toast.LENGTH_LONG).show();
                    ItemMoveActivity.this.dismissProgress();
                    return;
                }

                boolean isContain = false;
                for (List<Row> item : items) {
                    if (item.get(0).getMap().get("PALLET_NO").equals(rows.get(0).getMap().get("PALLET_NO")))
                        isContain = true;
                }

                if (isContain) {
                    Toast.makeText(ItemMoveActivity.this, "중복된 PALLET 입니다.", Toast.LENGTH_LONG).show();
                } else {
                    for (Row item : rows) {
                        moveBoxNos += item.getMap().get("BOX_NO") + "|";
                    }

                    rows.get(0).getMap().put("TYPE", "PALLET");
                    items.add(rows);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
                ItemMoveActivity.this.dismissProgress();
            });
        }else{
            Toast.makeText(ItemMoveActivity.this, "잘못된 PALLET 입니다.", Toast.LENGTH_LONG).show();
        }
    }

    public void inquiryBox(String data){
        showProgress(R.string.data_now);
        new InquiryBox(this, data).excute((Response.Listener<MesResponse>) response -> {

            Row row = response.dataset.get(0).rows.row.get(0);

            if(moveBoxNos.contains(row.getMap().get("BOX_NO"))){
                Toast.makeText(ItemMoveActivity.this, "이미 추가된 BOX입니다.", Toast.LENGTH_LONG).show();
                ItemMoveActivity.this.dismissProgress();
                return;
            }


            boolean isMoving = row.getMap().get("ITEM_MOVING_NOW").equals("Y");

            if(mode == MOVING_MODE.NONE && isMoving){
                mode = MOVING_MODE.STORE;
                ((TextView)findViewById(R.id.txt_title)).setText("재고이동 - 입고");
                viewWhouse.setVisibility(View.VISIBLE);
            }else if(mode == MOVING_MODE.NONE){
                mode = MOVING_MODE.MOVE;
                ((TextView)findViewById(R.id.txt_title)).setText("재고이동 - 출고");
                viewWhouse.setVisibility(View.GONE);
            }

            if (mode == MOVING_MODE.STORE && !isMoving) {
                Toast.makeText(ItemMoveActivity.this, "입고작업중에는 출고되지 않은 재고를 추가 할 수 없습니다.", Toast.LENGTH_LONG).show();
                ItemMoveActivity.this.dismissProgress();
                return;
            } else if (mode == MOVING_MODE.MOVE && isMoving) {
                Toast.makeText(ItemMoveActivity.this, "출고작업중에는 이동중인 재고를 추가 할 수 없습니다.", Toast.LENGTH_LONG).show();
                ItemMoveActivity.this.dismissProgress();
                return;
            }

            if(mode == MOVING_MODE.MOVE && row.getMap().get("PALLET_NO") != null && row.getMap().get("PALLET_NO").length() > 0) {
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setTitle("알림");
                ab.setCancelable(false);
                ab.setMessage("BOX만 이동할 경우 PALLET에서 제외됩니다. 계속하시겠습니까?");
                ab.setPositiveButton("확인", (dialogInterface, i) -> {
                    moveBoxNos += row.getMap().get("BOX_NO") + "|";
                    row.getMap().put("TYPE", "BOX");
                    ArrayList<Row> item = new ArrayList<>();
                    item.add(row);
                    items.add(item);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    ItemMoveActivity.this.dismissProgress();
                    dialogInterface.dismiss();
                });
                ab.setNegativeButton("취소", (dialogInterface, i) -> {
                    ItemMoveActivity.this.dismissProgress();
                    dialogInterface.dismiss();
                });
                ab.show();
            }else{
                moveBoxNos += row.getMap().get("BOX_NO") + "|";
                row.getMap().put("TYPE", "BOX");
                ArrayList<Row> item = new ArrayList<>();
                item.add(row);
                items.add(item);
                recyclerView.getAdapter().notifyDataSetChanged();
                ItemMoveActivity.this.dismissProgress();
            }
        }, new VollyerErrorListener(ItemMoveActivity.this, "오류가 발생했습니다. 전산팀으로 문의바랍니다.", new VollyerErrorCallbackListener() {
            @Override
            public void OnFinished() {
                dismissProgress();
            }
        }));
//        Toast.makeText(ItemMoveActivity.this, "잘못된 PALLET 입니다.", Toast.LENGTH_LONG).show();
    }


    class SimpleRecyclerAdapter extends RecyclerView.Adapter<VersionViewHolder> {
        Context context;

        public SimpleRecyclerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_header_item, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
            boolean isPallet = items.get(i).get(0).getMap().get("TYPE").equals("PALLET");
            String barcode = isPallet ? items.get(i).get(0).getMap().get("PALLET_NO") : items.get(i).get(0).getMap().get("BOX_NO");
            versionViewHolder.title.setText(barcode + " - " + items.get(i).get(0).getMap().get("WHOUSE_NM") +
                    (items.get(i).get(0).getMap().get("ITEM_MOVING_NOW").equals("Y") ? " - 이동중" : ""));

            StringBuffer sb = new StringBuffer();
            String itemcd = items.get(i).get(0).getMap().get("ITEM_CD");
            String itemnm = "";
            int boxcnt = 0;
            int boxwgt = 0;

            if(isPallet) {
                for (Row item : items.get(i)) {
                    if (itemcd.equals(item.getMap().get("ITEM_CD"))) {
                        boxcnt++;
                        boxwgt += (int) Double.parseDouble(item.getMap().get("WGT"));
                        itemnm = item.getMap().get("ITEM_NM");
                    } else {
                        sb.append(itemnm)
                                .append("\n")
                                .append(boxcnt)
                                .append(item.getMap().get("UOM_CD"))
                                .append(" / ")
                                .append(boxwgt)
                                .append(item.getMap().get("WGT_CD"))
                                .append("\n\n");
                        boxcnt = 1;
                        boxwgt = (int) Double.parseDouble(item.getMap().get("WGT"));
                        itemcd = item.getMap().get("ITEM_CD");
                    }
                    if (items.get(i).get(items.get(i).size() - 1) == item) {
                        sb.append(item.getMap().get("ITEM_NM"))
                                .append("\n")
                                .append(boxcnt)
                                .append(item.getMap().get("UOM_CD"))
                                .append(" / ")
                                .append(boxwgt)
                                .append(item.getMap().get("WGT_CD"));
                    }
                }
            }else{
                Map<String, String> item = items.get(i).get(0).getMap();
                itemnm = item.get("ITEM_NM");
                sb.append(itemnm)
                        .append("\n")
                        .append((int) Double.parseDouble(item.get("WGT")))
                        .append(item.get("WGT_CD"));
            }
            versionViewHolder.content.setText(sb.toString());
            versionViewHolder.itemView.setOnClickListener(new OnItemClickListener(barcode));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        CardView cardItemLayout;
        TextView title;
        TextView content;

        public VersionViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
            title = (TextView) itemView.findViewById(R.id.listitem_title);
            content = (TextView) itemView.findViewById(R.id.listitem_name);
        }
    }

    class OnItemClickListener implements View.OnClickListener {
        String key;
        public OnItemClickListener(String key){
            this.key = key;
        }
        @Override
        public void onClick(View view) {
            Bundle b = new Bundle();
            b.putBoolean("VIEW_MODE", true);

            if(key.startsWith("U")) {
                b.putString("PALLET_NO", key);
                MenuHandler.handler(ItemMoveActivity.this, "PALLET", b);
            }else{
                b.putString("BOX_NO", key);
                MenuHandler.handler(ItemMoveActivity.this, "BOX", b);
            }
        }
    };

    class OnWhouseSelectListener implements View.OnClickListener {
        String[] list = new String[]{ "출하창고", "진영생산창고", "진영자재창고", "본사자재창고" };
        @Override
        public void onClick(View view) {
            AlertDialog.Builder ab = new AlertDialog.Builder(ItemMoveActivity.this);
            ab.setTitle("입고창고선택");
            ab.setCancelable(false);
            ab.setItems(list, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    viewTxtWhouse.setText("창고선택 - " + list[i]);
                    switch (i){
                        case 0:  whouse = "W800"; break;
                        case 1: whouse = "W050"; break;
                        case 2: whouse = "W040"; break;
                        case 3: whouse = "W010"; break;
                        default: whouse = "";
                    }
                }
            });
            ab.show();
        }
    }


}
