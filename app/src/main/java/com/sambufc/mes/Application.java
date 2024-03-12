package com.sambufc.mes;

import com.android.volley.RequestQueue;
import com.navercorp.volleyextensions.volleyer.factory.DefaultRequestQueueFactory;
import com.sambufc.mes.model.Row;

import static com.navercorp.volleyextensions.volleyer.Volleyer.volleyer;

/**
 * Created by 20090502 on 2016-04-01.
 */
public class Application extends android.app.Application {

    public static Application app;

    String cookies = "";
    RequestQueue rq;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        rq = DefaultRequestQueueFactory.create(this);
        rq.start();
        volleyer(rq).settings().setAsDefault().done();
    }

    public static RequestQueue getRequestQueue() {
        return app.rq;
    }

    public static String getSessionCookie() {
        return app.cookies;
    }

    public static void setSessionCookie(String cookies){
        app.cookies = cookies;
    }



    public static Row USER = null;
    public static String SERVER_URL = "http://192.168.18.240:8080";
    public static String COMMON_CONTROLLER = "nexacroController.do";
//    public static String UID = USER.getMap().get("USER_NO");
//    public static String PWD = null;

}
