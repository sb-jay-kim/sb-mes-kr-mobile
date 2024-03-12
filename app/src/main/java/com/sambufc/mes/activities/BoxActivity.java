package com.sambufc.mes.activities;

import static com.navercorp.volleyextensions.volleyer.Volleyer.volleyer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navercorp.volleyextensions.volleyer.response.parser.SimpleXmlNetworkResponseParser;
import com.sambufc.mes.Application;
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.R;
import com.sambufc.mes.common.InquiryBox;
import com.sambufc.mes.common.InquiryPallet;
import com.sambufc.mes.common.MenuHandler;
import com.sambufc.mes.common.QueryXml;
import com.sambufc.mes.listeners.VollyerErrorCallbackListener;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.model.MesResponse;
import com.sambufc.mes.model.Row;

import java.util.LinkedHashMap;

public class BoxActivity extends BaseActivity {

    RecyclerView recyclerView;
    String boxNo;
    boolean isViewMode;
    MesResponse mesResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        boxNo = getIntent().getBundleExtra("bundle").getString("BOX_NO");
        isViewMode = getIntent().getBundleExtra("bundle").getBoolean("VIEW_MODE", false);
//        boxno = savedInstanceState.getString("BOX_NO");

        recyclerView = (RecyclerView) findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        showProgress(R.string.data_now);
        new InquiryBox(this, boxNo).excute((Response.Listener<MesResponse>) response -> {
            mesResponse = response;
            recyclerView.setAdapter(new SimpleRecyclerAdapter(BoxActivity.this));
            dismissProgress();
        }, new VollyerErrorListener(BoxActivity.this, "존재하지 않는 BOX 입니다.", new VollyerErrorCallbackListener() {
            @Override
            public void OnFinished() {
                dismissProgress();
            }
        }));
    }

    class SimpleRecyclerAdapter extends RecyclerView.Adapter<VersionViewHolder> {
        Context context;
        LinkedHashMap<String, String> list;

        public SimpleRecyclerAdapter(Context context) {
            this.context = context;
            setDefault();
        }

        public void setDefault() {
            Row item = mesResponse.dataset.get(0).rows.row.get(0);

            list = new LinkedHashMap<>();
            list.put("BOX NO", item.getMap().get("BOX_NO"));

            list.put("PALLET NO", item.getMap().get("PALLET_NO"));
            list.put("코드", item.getMap().get("ITEM_CD"));
            list.put("품명", item.getMap().get("ITEM_NM"));
            list.put("중량", (int)Double.parseDouble(item.getMap().get("WGT")) + item.getMap().get("WGT_CD"));
            list.put("생산일자", item.getMap().get("RECEIVE_DATE").substring(0, 4) + "-" + item.getMap().get("RECEIVE_DATE").substring(4, 6) + "-" + item.getMap().get("RECEIVE_DATE").substring(6, 8));
            if(item.getMap().get("ITEM_MOVING_NOW").equals("Y"))
                list.put("적재창고", item.getMap().get("WHOUSE_NM") + " - " + "이동중");
            else
                list.put("적재창고", item.getMap().get("WHOUSE_NM"));
            list.put("적치위치", item.getMap().get("STACK_LOCATION_CD"));
            list.put("LOT NO", item.getMap().get("LOT_NO"));
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_header_item, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view, i);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
            String key = list.keySet().toArray(new String[list.size()])[i];
            versionViewHolder.title.setText(key);
            versionViewHolder.content.setText(list.get(key));
            //versionViewHolder.itemView.setOnClickListener(new OnItemClickListener(i, key, list.get(key)));

            boolean isMoving = mesResponse.dataset.get(0).rows.row.get(0).getMap().get("ITEM_MOVING_NOW").equals("Y");
            if (!isViewMode) {
                versionViewHolder.itemView.setOnLongClickListener(new OnItemLongClickListener(i, key, list.get(key)));
                if (i == 4 && !isMoving) {
                    versionViewHolder.icon_add.setImageResource(R.drawable.split_box_24);
                    versionViewHolder.icon_add.setVisibility(View.VISIBLE);
                } else if (i == 6) {
                    versionViewHolder.icon_add.setImageResource(R.drawable.output_box_24);
                    versionViewHolder.icon_add.setVisibility(View.VISIBLE);
                } else {
                    versionViewHolder.icon_add.setVisibility(View.GONE);
                }
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

        public VersionViewHolder(View itemView, int i) {
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
            Row item = mesResponse.dataset.get(0).rows.row.get(0);
            boolean isMoving = mesResponse.dataset.get(0).rows.row.get(0).getMap().get("ITEM_MOVING_NOW").equals("Y");

            if(idx == 4 && !isMoving){

            }else if(idx == 6) {
                Bundle b = new Bundle();
                b.putString("BARCODE", item.getMap().get("BOX_NO"));
                MenuHandler.handler(BoxActivity.this, "PALLET_MOVE", b);
                BoxActivity.this.finish();
            }
//            Uri uri = Uri.parse(list.get("WEB LINK") + "?code=" + list.get("CODE"));
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(uri);
//            context.startActivity(intent);
//            if(key.equals("BARCODE_SEARCH")){
//                BoxActivity.showBarcodeInputDialog();
//            }else{
//                MenuHandler.handler(context, key);
//            }
            return false;
        }
    };

}
