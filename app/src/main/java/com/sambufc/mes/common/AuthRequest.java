package com.sambufc.mes.common;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.navercorp.volleyextensions.volleyer.response.parser.SimpleXmlNetworkResponseParser;
import com.sambufc.mes.Application;

import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AuthRequest<T> extends Request<T> {
    Class<T> mClazz;
    Response.Listener<T> mListener;
    String mUid;
    String mPwd;

    public AuthRequest(Class<T> clazz, String url, String uid, String pwd, Response.Listener<T> listener, Response.ErrorListener errorListener){
        super(Method.POST, url, errorListener);
        mClazz = clazz;
        mListener = listener;
        mUid= uid;
        mPwd= pwd;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return QueryXml.Login(mUid, mPwd).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getBodyContentType() {
        return "text/xml";
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String res = new String(response.data, StandardCharsets.UTF_8);

            Map<String, String> responseHeaders = response.headers;
            String rawCookies = responseHeaders.get("Set-Cookie");
            Application.app.setSessionCookie(rawCookies);
            return Response.success((T) new SimpleXmlNetworkResponseParser().parseNetworkResponse(response, mClazz).result, HttpHeaderParser.parseCacheHeaders(response));
        }catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }
}
