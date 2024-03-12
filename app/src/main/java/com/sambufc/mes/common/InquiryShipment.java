package com.sambufc.mes.common;

import static com.navercorp.volleyextensions.volleyer.Volleyer.volleyer;

import com.android.volley.Response;
import com.navercorp.volleyextensions.volleyer.response.parser.SimpleXmlNetworkResponseParser;
import com.sambufc.mes.Application;
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.R;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.model.MesResponse;

public class InquiryShipment {
    BaseActivity context;
    String sino;

    public InquiryShipment(BaseActivity context, String sino) {
        this.context = context;
        this.sino = sino;
    }

    public void excute(Response.Listener successListener) {
        excute(successListener, new VollyerErrorListener(context, "오류가 발생했습니다. 전산팀으로 문의바랍니다."));
    }

    public void excute(Response.Listener successListener, VollyerErrorListener errorListener) {
        context.showProgress(R.string.data_now);

        volleyer()
                .post(Application.SERVER_URL + "/nexacroController.do")
                .addHeader("Content-Type", "text/xml")
                .addHeader("Cookie", Application.app.getSessionCookie())
                .withBody(QueryXml.ShipmentList(sino))
                .withTargetClass(MesResponse.class)
                .withResponseParser(new SimpleXmlNetworkResponseParser())
                .withListener(successListener)
                .withErrorListener(errorListener)
                .execute();
    }
}
