package com.sambufc.mes.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.R;
import com.sambufc.mes.common.BarcodeHandler;
import com.sambufc.mes.common.ExecuteBoxesIntoPallet;
import com.sambufc.mes.common.ExecuteStackPalletToRack;
import com.sambufc.mes.common.InquiryBox;
import com.sambufc.mes.common.InquiryPallet;
import com.sambufc.mes.listeners.VollyerErrorCallbackListener;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.model.BaseModel;
import com.sambufc.mes.model.MesResponse;
import com.sambufc.mes.model.Row;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class BoxInOutActivity extends BaseActivity {
    AlertDialog scanDialog;
    RecyclerView recyclerView;
    String palletNo;
    List<Row> itemList;
    int boxCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_in_out);

        palletNo = getIntent().getBundleExtra("bundle").getString("PALLET_NO");

        recyclerView = findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        AlertDialog.Builder ab = new AlertDialog.Builder(BoxInOutActivity.this);
        ab.setCancelable(false);
        ab.setTitle("알림");
        ab.setMessage("PALLET를 스캔하세요.");
        ab.setNegativeButton("종료", (dialogInterface, i) -> {
            BoxInOutActivity.this.finish();
        });
        scanDialog = ab.create();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("data");

                if(palletNo == null) {
                    if(!data.startsWith("U")){
                        Toast.makeText(BoxInOutActivity.this, "잘못된 PALLET 입니다.", Toast.LENGTH_LONG).show();
                        BoxInOutActivity.this.finish();
                    }
                    scanDialog.dismiss();
                    palletNo = data;
                    searchPallet();
                }else {
                    AtomicBoolean hasSameBoxNo = new AtomicBoolean(false);
                    itemList.forEach(x -> {
                        if (x.getMap().get("BOX_NO").equals(data)) {
                            toggleRemovedBox(data);
                            hasSameBoxNo.set(true);
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });

                    if (!hasSameBoxNo.get()) {
                        new InquiryBox(BoxInOutActivity.this, data).excute((Response.Listener<MesResponse>) success -> {
                            Row item = success.dataset.get(0).rows.row.get(0);
                            if (!item.getMap().get("ITEM_CD").equals(itemList.get(0).getMap().get("ITEM_CD"))) {
                                Toast.makeText(BoxInOutActivity.this, "PALLET에 적재된 품목과 다릅니다.", Toast.LENGTH_LONG).show();
                            } else if (item.getMap().get("PALLET_NO") != null && !item.getMap().get("PALLET_NO").equals("")) {
                                Toast.makeText(BoxInOutActivity.this, "이미 다른 PALLET에 적재된 품목입니다.", Toast.LENGTH_LONG).show();
                            } else {
                                Row resItem = success.dataset.get(0).rows.row.get(0);
                                resItem.getMap().put("PALLET_NO", "");
                                itemList.add(0, resItem);
                                toggleRemovedBox(resItem.getMap().get("BOX_NO"));
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }
                            BoxInOutActivity.this.dismissProgress();
                        }, new VollyerErrorListener(BoxInOutActivity.this, "존재하지 않는 BOX 입니다.", () -> {
                            BoxInOutActivity.this.dismissProgress();
                        }));
                    }
                }
            }
        };

        findViewById(R.id.btn_save).setOnClickListener(view -> {
            new ExecuteBoxesIntoPallet(BoxInOutActivity.this, itemList).excute((Response.Listener<BaseModel>) response -> {
                Bundle b = new Bundle();
                b.putString("PALLET_NO", palletNo);
                BoxInOutActivity.this.dismissProgress();
                BoxInOutActivity.this.finish();
                Toast.makeText(BoxInOutActivity.this, "PALLET 정보를 저장하였습니다.", Toast.LENGTH_LONG).show();
                BarcodeHandler.startIntent(BoxInOutActivity.this, "com.sambufc.mes.activities.PalletActivity", b);
            });
        });

        findViewById(R.id.btn_cancel).setOnClickListener(view -> {
            BoxInOutActivity.this.finish();
        });

        if(palletNo == null || !palletNo.startsWith("U")){
            scanDialog.show();
        }else if(palletNo.startsWith("U")) {
            searchPallet();
        }

        receiverStart();
    }

    private void searchPallet(){
        ((TextView)findViewById(R.id.header).findViewById(R.id.listitem_title)).setTextSize(24);
        ((TextView)findViewById(R.id.header).findViewById(R.id.listitem_title)).setText(palletNo);
        showProgress(R.string.data_now);
        new InquiryPallet(this, palletNo).excute(
                (Response.Listener<MesResponse>) response -> {
                    itemList = response.dataset.get(0).rows.row;
                    recyclerView.setAdapter(new SimpleRecyclerAdapter(BoxInOutActivity.this));
                    boxCount = itemList.size();
                    ((TextView)findViewById(R.id.header).findViewById(R.id.listitem_name)).setText(boxCount + " BOX");
                    BoxInOutActivity.this.dismissProgress();
                }, new VollyerErrorListener(BoxInOutActivity.this, "존재하지 않는 PALLET 입니다.", () -> {
                    BoxInOutActivity.this.dismissProgress();
                    BoxInOutActivity.this.finish();
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiverStop();
    }

    class SimpleRecyclerAdapter extends RecyclerView.Adapter<VersionViewHolder> {
        Context context;

        public SimpleRecyclerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view, i);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
            Row item = itemList.get(i);
            versionViewHolder.content.setText(item.getMap().get("BOX_NO"));
            versionViewHolder.itemView.setOnLongClickListener(new OnItemLongClickListener(i));

            if(item.getMap().get("PALLET_NO").equals(palletNo)){
                versionViewHolder.content.setPaintFlags(0);
                versionViewHolder.content.setTextColor(getResources().getColor(android.R.color.black, getTheme()));
                versionViewHolder.icon_add.setImageResource(R.drawable.remove_box_24);
            }
            else{
                versionViewHolder.content.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                versionViewHolder.content.setTextColor(getResources().getColor(R.color.red, getTheme()));
                versionViewHolder.icon_add.setImageResource(R.drawable.add_box_24);
            }
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

    }

    class VersionViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        ImageView icon_add;

        public VersionViewHolder(View itemView, int i) {
            super(itemView);
            content = itemView.findViewById(R.id.listitem_name);
            content.setTextSize(20);
            icon_add = itemView.findViewById(R.id.icon_add);
            icon_add.setVisibility(View.VISIBLE);
        }
    }

    class OnItemLongClickListener implements View.OnLongClickListener {
        int idx;
        public OnItemLongClickListener(int idx){
            this.idx = idx;
        }
        @Override
        public boolean onLongClick(View view) {
            Row item = itemList.get(idx);
            toggleRemovedBox(item.getMap().get("BOX_NO"));
            return false;
        }
    };

    public void toggleRemovedBox(String boxNo){
        Optional<Row> box = itemList.stream().filter(x -> x.getMap().get("BOX_NO").equals(boxNo)).findFirst();
        Row item = box.get();
        
        if(item.getMap().get("PALLET_NO").equals(palletNo)) {
            item.getMap().put("PALLET_NO", "");
            ((TextView)findViewById(R.id.header).findViewById(R.id.listitem_name)).setText(--boxCount + " BOX");
        }else {
            item.getMap().put("PALLET_NO", palletNo);
            ((TextView)findViewById(R.id.header).findViewById(R.id.listitem_name)).setText(++boxCount + " BOX");
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }


}
