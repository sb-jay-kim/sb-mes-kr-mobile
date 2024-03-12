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
import com.sambufc.mes.R;
import com.sambufc.mes.common.BarcodeHandler;
import com.sambufc.mes.common.ExecuteStackPalletToRack;
import com.sambufc.mes.common.InquiryPallet;
import com.sambufc.mes.common.InquiryRect;
import com.sambufc.mes.common.MenuHandler;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.model.MesResponse;
import com.sambufc.mes.model.Row;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class RectActivity extends BaseActivity {

    RecyclerView recyclerView;
    String rectNo;
    boolean isViewMode;
    List<Row> mesResponse;

    AlertDialog scanDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rect);

        rectNo = getIntent().getBundleExtra("bundle").getString("RECT_NO");
//        isViewMode = getIntent().getBundleExtra("bundle").getBoolean("VIEW_MODE", false);

        recyclerView = findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ((TextView) findViewById(R.id.txt_rect_no)).setText(rectNo);


        if(rectNo.startsWith("W")) {
            onRequestRect();

            /** 바코드 출력 */
            findViewById(R.id.icon_barcode).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putString("BARCODE", rectNo);
                    MenuHandler.handler(RectActivity.this, "PRINT_BARCODE", b);
                }
            });

            /** RECT 적재 */
            findViewById(R.id.add_rect).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(getUseCamera()){
                        new IntentIntegrator(RectActivity.this).initiateScan();
                    }else {
                        receiverStart();
                        scanDialog.show();
                    }
                }
            });
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receiverStop();
                scanDialog.dismiss();

                String data = intent.getStringExtra("data");
                onReadBarcode(data);

            }
        };

        AlertDialog.Builder ab = new AlertDialog.Builder(RectActivity.this);
        ab.setCancelable(false);
        ab.setTitle("알림");
        ab.setMessage("바코드를 스캔하세요.");
        ab.setNegativeButton("종료", (dialogInterface, i) -> {
            receiverStop();
            dialogInterface.dismiss();
        });
        scanDialog = ab.create();
    }

    private void onReadBarcode(String data){
        if(data.startsWith("U"))
            new ExecuteStackPalletToRack(RectActivity.this, "IN", rectNo, data).excute((Response.Listener<MesResponse>) success -> {
                Row item = success.dataset.get(0).rows.row.get(0);
                String result = item.getMap().get("MESSAGE");
                dismissProgress();

                if(result.equals("DIFF"))
                    Toast.makeText(RectActivity.this, "적치장소가 위치한 창고와 다른 위치에 있는 PALLET 입니다.", Toast.LENGTH_LONG).show();
                else if(result.equals("ALREADY"))
                    Toast.makeText(RectActivity.this, item.getMap().get("STACK_LOCATION_CD") + " 에 적치되어있는 PALLET 입니다.", Toast.LENGTH_LONG).show();
                else if(result.equals("SUCCESS")){
                    onRequestRect();
                }else
                    Toast.makeText(RectActivity.this, "알수없는 오류입니다. 전산팀으로 문의바랍니다.", Toast.LENGTH_LONG).show();
            }, new VollyerErrorListener(RectActivity.this, "알수없는 오류입니다. 전산팀으로 문의바랍니다.", () ->  {
                receiverStop();
                scanDialog.dismiss();
                dismissProgress();
            }));
        else {
            Toast.makeText(RectActivity.this, "PALLET 외 다른 품목은 적치할 수 없습니다.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result.getContents() != null){
            onReadBarcode(result.getContents());

        }else{
            Toast.makeText(this, "잘못된 PALLET NO입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void onRequestRect(){
        showProgress(R.string.data_now);
        new InquiryRect(this, rectNo).excute(
            (Response.Listener<MesResponse>) response -> {
                mesResponse = response.dataset.get(0).rows.row;
                if(mesResponse.get(0).getMap().get("PALLET_NO") == null || mesResponse.get(0).getMap().get("PALLET_NO").equals(""))
                    mesResponse = new ArrayList<>();

                recyclerView.setAdapter(new SimpleRecyclerAdapter(RectActivity.this));
                dismissProgress();
            }, new VollyerErrorListener(RectActivity.this, "존재하지 않는 RECT 입니다.", () -> {
                RectActivity.this.finish();}
            ));
    }
    //AlertDialog receiveDialog;
//    @Override
//    public void receiverStart() {
//        super.receiverStart();
//
//        AlertDialog.Builder ab = new AlertDialog.Builder(this);
//        ab.setCancelable(false);
//        ab.setTitle("바코드");
//        ab.setMessage("바코드를 스캔하세요.");
//        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                receiverStop();
//                dialogInterface.dismiss();
//            }
//        });
//        receiveDialog = ab.create();
//        receiveDialog.show();
//    }

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
            Row item = mesResponse.get(i);
            String key = item.getMap().get("PALLET_NO"); //list.keySet().toArray(new String[list.size()])[i];
            StringBuilder sb = new StringBuilder();
            sb.append("품목코드: ").append(item.getMap().get("ITEM_CD")).append("\n")
              .append("품목명: ").append(item.getMap().get("ITEM_NM")).append("\n")
              .append("BOX 수량: ").append((int)Double.parseDouble(item.getMap().get("QTY"))).append(item.getMap().get("UOM_CD")).append("\n")
              .append("총중량: ").append((int)Double.parseDouble(item.getMap().get("WGT"))).append(item.getMap().get("WGT_CD"));

            versionViewHolder.title.setText(key);
            versionViewHolder.content.setText(sb.toString());
            versionViewHolder.icon_add.setImageResource(R.drawable.output_box_24);
            versionViewHolder.icon_add.setVisibility(View.VISIBLE);

            versionViewHolder.itemView.setOnClickListener(new OnItemClickListener(i, key));
            versionViewHolder.itemView.setOnLongClickListener(new OnItemLongClickListener(i, key));
        }

        @Override
        public int getItemCount() {
            return mesResponse.size();
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
        public OnItemClickListener(int idx, String key){
            this.idx =idx;
            this.key = key;
        }
        @Override
        public void onClick(View view) {
            if (key.startsWith("U")) {
                BarcodeHandler.handler(RectActivity.this, key);
            }
        }
    };

    class OnItemLongClickListener implements View.OnLongClickListener {
        int idx;
        String key;
        String value;
        public OnItemLongClickListener(int idx, String key){
            this.idx =idx;
            this.key = key;
        }

        @Override
        public boolean onLongClick(View view) {
            AlertDialog.Builder ab = new AlertDialog.Builder(RectActivity.this);
            ab.setCancelable(false);
            ab.setTitle("알림");
            ab.setMessage("파레트를 적출합니다. 계속하시겠습니까?");
            ab.setPositiveButton("확인", (dialogInterface, i) -> {
                new ExecuteStackPalletToRack(RectActivity.this, "OUT", rectNo, key).excute(new Response.Listener<MesResponse>(){
                    @Override
                    public void onResponse(MesResponse response) {
                        onRequestRect();
                    }
                }, new VollyerErrorListener(RectActivity.this, "해당위치에 존재하지 않는 PALLET 입니다.", () -> {

                }));
                dialogInterface.dismiss();
            });
            ab.setNegativeButton("취소", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            ab.show();
            return false;
        }
    };

}
