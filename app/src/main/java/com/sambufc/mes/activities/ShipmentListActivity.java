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
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.R;
import com.sambufc.mes.common.BarcodeHandler;
import com.sambufc.mes.common.ExecuteStackPalletToRack;
import com.sambufc.mes.common.InquiryPallet;
import com.sambufc.mes.common.InquiryShipment;
import com.sambufc.mes.common.MenuHandler;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.model.MesResponse;
import com.sambufc.mes.model.Row;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShipmentListActivity extends BaseActivity {

    RecyclerView recyclerView;
    MesResponse mesResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_list);
        recyclerView = findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        showProgress(R.string.data_now);
        new InquiryShipment(this, "").excute(
            (Response.Listener<MesResponse>) response -> {
                mesResponse = response;
                recyclerView.setAdapter(new SimpleRecyclerAdapter(ShipmentListActivity.this));
                dismissProgress();
            }, new VollyerErrorListener(ShipmentListActivity.this, "출하예정목록이 없습니다.", () -> {
                ShipmentListActivity.this.finish();
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
            Map<String, String> row = mesResponse.dataset.get(0).rows.row.get(i).getMap();
            versionViewHolder.title.setText(String.format("%s / %s-%s-%s", row.get("SI_NO"), row.get("REQ_SHIP_DT").substring(0, 4), row.get("REQ_SHIP_DT").substring(4, 6), row.get("REQ_SHIP_DT").substring(6, 8)));
            versionViewHolder.content.setText(String.format("%s / %s / %s / %,.0f%s", row.get("CLIENT_NM"), row.get("FACTORY_CD"), row.get("ITEM_NM"), Double.parseDouble(row.get("REQ_WGT")), row.get("WGT_CD")));
            versionViewHolder.itemView.setOnClickListener(new OnItemClickListener(row));

        }

        @Override
        public int getItemCount() {
            return mesResponse.dataset.get(0).rows.row.size();
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
        Map<String, String> row;
        public OnItemClickListener(Map<String, String> row){
            this.row = row;
        }
        @Override
        public void onClick(View view) {
            ArrayList<Map> list = new ArrayList<>();
            list.add(row);
            Bundle bundle = new Bundle();
            bundle.putString("SINO", row.get("SI_NO"));
            BarcodeHandler.startIntent(ShipmentListActivity.this, "com.sambufc.mes.activities.ShipmentActivity", bundle);
        }
    };

}
