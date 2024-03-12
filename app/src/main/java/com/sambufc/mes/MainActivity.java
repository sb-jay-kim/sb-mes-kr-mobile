package com.sambufc.mes;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gun0912.tedpermission.PermissionListener;

import java.util.LinkedHashMap;
import java.util.List;

import com.gun0912.tedpermission.normal.TedPermission;
import com.journeyapps.barcodescanner.ScanOptions;
import com.sambufc.mes.camera.CameraActivity;
import com.sambufc.mes.common.BarcodeHandler;
import com.sambufc.mes.common.MenuHandler;

import static com.navercorp.volleyextensions.volleyer.Volleyer.volleyer;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends BaseActivity{
    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView recyclerView;
    SimpleRecyclerAdapter simpleRecyclerAdapter;
    Intent carmeraIntent;
    AlertDialog ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getResources().getString(R.string.app_name));
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        recyclerView = (RecyclerView) findViewById(R.id.scrollableview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (simpleRecyclerAdapter == null) {
            simpleRecyclerAdapter = new SimpleRecyclerAdapter(this);
            recyclerView.setAdapter(simpleRecyclerAdapter);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.qr_camera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(Manifest.permission.CAMERA);
//                carmeraIntent = new Intent(MainActivity.this, CameraActivity.class);
//                MainActivity.this.startActivityForResult(carmeraIntent, 0);
                new IntentIntegrator(MainActivity.this).initiateScan();
//                ScanOptions options = new ScanOptions();
//                options.setOrientationLocked(false);
//                barcodeLauncher.launch(options);

            }
        });

        initReceiver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // QR코드/ 바코드를 스캔한 결과
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // result.getFormatName() : 바코드 종류
        // result.getContents() : 바코드 값
        //mTextMessage.setText( result.getContents() );
        if(result.getContents() != null)
            BarcodeHandler.handler(this, result.getContents());
//        Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
    }

//    @Override
//    public void initReceiver(){
//        super.initReceiver();
//        if(ad != null)
//            ad.dismiss();
//    }

    @Override
    protected void onPause() {
        super.onPause();
        receiverStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiverStart();
    }

    private void checkPermission(String permission) {
        if (!(Build.VERSION.SDK_INT < 23)) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permission);
            }
        }
    }

    private void requestPermissions(String... permission) {
        TedPermission.create().setPermissionListener(new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> arrayList) {
                MainActivity.this.finish();
            }
        }).setPermissions(permission)
        .check();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean fullyExpanded = ((AppBarLayout) findViewById(R.id.appbar)).getHeight() - ((AppBarLayout) findViewById(R.id.appbar)).getBottom() == 0;
//        outState.putBoolean("appbar", fullyExpanded);
//        outState.putParcelable("item", item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        boolean fullyExpanded = ((AppBarLayout) findViewById(R.id.appbar)).getHeight() - ((AppBarLayout) findViewById(R.id.appbar)).getBottom() == 0;
//        outState.putBoolean("appbar", fullyExpanded);
//        outState.putParcelable("item", item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ((AppBarLayout) findViewById(R.id.appbar)).setExpanded(savedInstanceState.getBoolean("appbar"), false);
//        item = (ProductItem) savedInstanceState.getParcelable("item");
//        simpleRecyclerAdapter.setList(item);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        simpleRecyclerAdapter.setDefault();
//        /*if(resultCode == RESULT_OK)
//            if(data.getBooleanExtra("gohome", false))
//                goHomepage();
//            else
//                getProductData(data.getStringExtra("cid"));
//*/
//        //getProductData("C67C197062264721BADDDCEE975111FB");
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_setting:
//                startActivity(new Intent(this, SettingActivity.class));
////                carmeraIntent = new Intent(MainActivity.this, CameraActivity.class);
////                MainActivity.this.startActivityForResult(carmeraIntent, 0);
//                break;
////            case R.id.action_homepage:
////                //goHomepage();
////                break;
////            case R.id.action_about:
////                simpleRecyclerAdapter.setDefault();
////                break;
//        }
        startActivity(new Intent(this, SettingActivity.class));
        return super.onOptionsItemSelected(item);
    }


    public void showBarcodeInputDialog(String key){
        if(ad == null) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setCancelable(false);
            ab.setTitle("알림");
            ab.setMessage("BARCODE를 스캔하세요.");
            ab.setNegativeButton("종료", (dialogInterface, i) -> {
                receiverStop();
                dialogInterface.dismiss();
            });
            ad = ab.create();
        }
        receiverStart();
        ad.show();
    }


    class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.VersionViewHolder> {
        MainActivity context;
        ImageView img;
        LinkedHashMap<String, String> list;

        public SimpleRecyclerAdapter(MainActivity context) {
            this.context = context;
            img = (ImageView)context.findViewById(R.id.header);
            setDefault();
        }

        public void setDefault(){
            list = new LinkedHashMap<>();
            if(800 == 800){
                //list.put("바코드조회", "BARCODE_SEARCH");
                list.put("출고처리", "SHIPMENT");
                list.put("재고이동", "PALLET_MOVE");
                list.put("파레트분할", "BOX_INOUT");
                //list.put("박스 병합 / 분할", "ㅁㅁㅁ");
                list.put("재고조사", "STOCK_TAKING");
            }

            this.notifyDataSetChanged();
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
            String key = list.keySet().toArray(new String[list.size()])[i];
            versionViewHolder.title.setText(key);
            versionViewHolder.itemView.setOnClickListener(new OnItemClickListener(list.get(key)));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class VersionViewHolder extends RecyclerView.ViewHolder {
            CardView cardItemLayout;
            TextView title;

            public VersionViewHolder(View itemView) {
                super(itemView);

                cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
                title = (TextView) itemView.findViewById(R.id.listitem_name);
            }
        }

        class OnItemClickListener implements View.OnClickListener {
            String key;
            public OnItemClickListener(String key){
                this.key = key;
            }
            @Override
            public void onClick(View view) {
//            Uri uri = Uri.parse(list.get("WEB LINK") + "?code=" + list.get("CODE"));
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(uri);
//            context.startActivity(intent);
                if(key.equals("BARCODE_SEARCH")) {
                    context.showBarcodeInputDialog("BARCODE_SEARCH");
                }else if(key.equals("STOCK_TAKING")){
                    Toast.makeText(MainActivity.this, "재고조사기간이 아닙니다.", Toast.LENGTH_LONG).show();
                }else{
                    MenuHandler.handler(context, key, new Bundle());
                }

            }
        };
    }

}
