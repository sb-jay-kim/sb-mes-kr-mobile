package com.sambufc.mes.common;

import static com.navercorp.volleyextensions.volleyer.Volleyer.volleyer;

import com.android.volley.Response;
import com.navercorp.volleyextensions.volleyer.response.parser.SimpleXmlNetworkResponseParser;
import com.sambufc.mes.Application;
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.R;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.model.BaseModel;
import com.sambufc.mes.model.Row;

import java.util.ArrayList;
import java.util.List;

public class ExecutePickingShipment {
    BaseActivity context;

    String sino;

    String palletno;

    public ExecutePickingShipment(BaseActivity context, String sino, String palletno) {
        this.context = context;
        this.sino = sino;
        this.palletno = palletno;
    }

    public void excute(Response.Listener successListener) {
        context.showProgress(R.string.data_now);

        volleyer()
                .post(Application.SERVER_URL + "/WM_Controller.do")
                .addHeader("Content-Type", "text/xml")
                .addHeader("Cookie", Application.app.getSessionCookie())
                .withBody(QueryXml.ExcutePickingShipment(sino, palletno))
                .withTargetClass(BaseModel.class)
                .withResponseParser(new SimpleXmlNetworkResponseParser())
                .withListener(successListener)
                .withErrorListener(new VollyerErrorListener(context, "재고이동이 실패하였습니다. 전산팀으로 문의바랍니다."))
                .execute();
    }
}

//    Response.Listener<BaseModel> successListener = new Response.Listener<BaseModel>() {
//        @Override
//        public void onResponse(BaseModel response) {
////            box = response;
////            recyclerView.setAdapter(new BoxActivity.SimpleRecyclerAdapter(BoxActivity.this));
////            pDialog.dismiss();
//            int a = 1;
//        }
//    };
