package com.sambufc.mes.listeners;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.sambufc.mes.BaseActivity;

public class VollyerErrorListener implements Response.ErrorListener{
    Context context;
    VollyerErrorCallbackListener finish;
    String error;

    public VollyerErrorListener(Context context, String error){
        this(context, error, null);
    }

    public VollyerErrorListener(Context context, String error, VollyerErrorCallbackListener finish){
        this.context= context;
        this.error = error;
        this.finish = finish;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        String message = null;
        if (error instanceof NetworkError) {
            message = "인터넷에 연결할 수 없습니다. 연결을 확인하세요.";
        } else if (error instanceof ServerError) {
            message = "서버를 찾을 수 없습니다. 전산팀으로 문의바랍니다.";
        } else if (error instanceof AuthFailureError) {
            message = "인터넷에 연결할 수 없습니다. 연결을 확인하세요.";
        } else if (error instanceof ParseError) {
            message = "구문 분석 오류입니다. 전산팀으로 문의바랍니다.";
        } else if (error instanceof NoConnectionError) {
            message = "인터넷에 연결할 수 없습니다. 연결을 확인하세요.";
        } else if (error instanceof TimeoutError) {
            message = "접속 시간이 초과되었습니다. 인터넷 연결을 확인하세요.";
        }else {
            message = this.error;
        }

        if(!message.equals(""))
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        if(finish != null)
            finish.OnFinished();
        //context.dismissProgress();
    }
}
