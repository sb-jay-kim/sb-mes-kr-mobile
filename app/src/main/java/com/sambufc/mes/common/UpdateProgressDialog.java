package com.sambufc.mes.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

public class UpdateProgressDialog extends ProgressDialog{
	
	Activity act;
	
	public UpdateProgressDialog(Context context, final String url, final String name) {
		super(context);
		
		new Thread(new Runnable() {
			public void run() {
				getFile(url, name);
			}
		}).start();
	}
	
	public UpdateProgressDialog(String title, String message, int maxVal, Activity act, final String url, final String name) {
		this(act, url, name);
		this.act = act;
		this.setCancelable(false);
		this.setTitle(title);
		this.setMessage(message);
		this.setMax(maxVal);
		
		this.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}
	
	public void getFile(String url, String name){

        try    {   
     
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url + name).openConnection();
            //urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
                File file = new File(act.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), name);
                if (file.exists()) {
                    file.delete();
                }

                FileOutputStream fileOutput = new FileOutputStream(file);

                InputStream inputStream = urlConnection.getInputStream();
                //int totalSize = urlConnection.getContentLength();
                int downloadedSize = 0;

                byte[] buffer = new byte[1024];
                int bufferLength = 0;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    this.setProgress(downloadedSize);
                }
                fileOutput.close();

                if(Build.VERSION.SDK_INT >= 26)
                    installAPK1(name);
                else
                    installAPK(name);
            }
        } catch (MalformedURLException e)     {   
            e.printStackTrace();   
        } catch (IOException e)     {   
            e.printStackTrace();   
        }

        //this.dismiss();

        act.finish();
	}

    public void installAPK(String name){
        File apkFile = new File(act.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), name);
        Uri apkUri = Uri.fromFile(apkFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        act.startActivity(intent);
    }

    public void installAPK1(String name) {
        Uri apkUri = FileProvider.getUriForFile(act, "com.sambufc.mes.fileprovider", new File(act.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), name));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.setData(apkUri);
        //intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        act.startActivity(intent);
    }



}
