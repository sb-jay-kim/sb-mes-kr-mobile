package com.sambufc.mes.common;

import static com.navercorp.volleyextensions.volleyer.Volleyer.volleyer;

import com.android.volley.Response;
import com.navercorp.volleyextensions.volleyer.response.parser.SimpleXmlNetworkResponseParser;
import com.sambufc.mes.Application;
import com.sambufc.mes.BaseActivity;
import com.sambufc.mes.R;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.model.MesResponse;

public class InquiryRect {
    BaseActivity context;
    String rectNo;

    public InquiryRect(BaseActivity context, String rectNo) {
        this.context = context;
        this.rectNo = rectNo;
    }

    public void excute(Response.Listener successListener) {
        excute(successListener, new VollyerErrorListener(context, "존재하지 않는 PALLET 입니다."));
    }

    public void excute(Response.Listener successListener, VollyerErrorListener errorListener) {
        context.showProgress(R.string.data_now);

        volleyer().post(Application.SERVER_URL + "/nexacroController.do")
                .addHeader("Content-Type", "text/xml")
                .addHeader("Cookie", Application.app.getSessionCookie())
                .withBody(QueryXml.StackLocationInfo(rectNo))
                .withTargetClass(MesResponse.class)
                .withResponseParser(new SimpleXmlNetworkResponseParser())
                .withListener(successListener)
                .withErrorListener(errorListener)
                .execute();
    }
}
