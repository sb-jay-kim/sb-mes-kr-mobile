package com.sambufc.mes.common;

import static com.navercorp.volleyextensions.volleyer.Volleyer.volleyer;

import com.android.volley.Response;
import com.navercorp.volleyextensions.volleyer.response.parser.SimpleXmlNetworkResponseParser;
import com.sambufc.mes.Application;
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.R;
import com.sambufc.mes.model.MesResponse;

public class ExecuteStackPalletToRack {
    BaseActivity context;
    String flag;
    String StackLocationNo;
    String PalletNo;

    public ExecuteStackPalletToRack(BaseActivity context, String flag, String StackLocationNo, String PalletNo) {
        this.context = context;
        this.flag = flag;
        this.StackLocationNo = StackLocationNo;
        this.PalletNo = PalletNo;
    }

    public void excute(Response.Listener successListener, VollyerErrorListener errorListener){
        context.showProgress(R.string.data_now);

        volleyer()
                .post(Application.SERVER_URL + "/nexacroController.do")
                .addHeader("Content-Type", "text/xml")
                .addHeader("Cookie", Application.app.getSessionCookie())
                .withBody(QueryXml.StackPalletToRack(flag, StackLocationNo, PalletNo))
                .withTargetClass(MesResponse.class)
                .withResponseParser(new SimpleXmlNetworkResponseParser())
                .withListener(successListener)
                .withErrorListener(errorListener)
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
