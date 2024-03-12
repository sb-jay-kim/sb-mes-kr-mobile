package com.sambufc.mes;

import static com.navercorp.volleyextensions.volleyer.Volleyer.volleyer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.navercorp.volleyextensions.request.SimpleXmlRequest;
import com.navercorp.volleyextensions.volleyer.response.parser.Jackson2NetworkResponseParser;
import com.navercorp.volleyextensions.volleyer.response.parser.SimpleXmlNetworkResponseParser;
import com.sambufc.mes.common.AuthRequest;
import com.sambufc.mes.common.QueryXml;
import com.sambufc.mes.common.UpdateProgressDialog;
import com.sambufc.mes.listeners.VollyerErrorListener;
import com.sambufc.mes.model.Data;
import com.sambufc.mes.model.MesResponse;
import com.sambufc.mes.model.Version;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LoginActivity extends Activity {
	PackageInfo packageInfo;
	SharedPreferences preferences;
	LinearLayout warning_layout;
	//ImageView errorMessageDivider;
	EditText edtUid, edtPwd;
	CheckBox autoidCheck, autopwdCheck, autologinCheck, fingerCheck;
	//TextView warningMsg1, warningMsg2;
	Button loginOk;

	String menuString;
//	FingerprintHelper fingerprintChecker;

	ProgressDialog pDialog;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		}catch (PackageManager.NameNotFoundException e){
			e.printStackTrace();
		}

		//checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		checkPermission(Manifest.permission.CAMERA);
		onCheckUpdate();

//		String timeout = preferences.getString("servertimeout", "60");
//		NetworkConnector.SERVER_TIMEOUT = Integer.parseInt(timeout) * 1000;
	}

	public void onCheckUpdate(){
		pDialog = ProgressDialog.show(LoginActivity.this, "", "업데이트 체크중입니다...", true);
		volleyer().get("http://192.168.0.235:88/pda/version.json")
				.withTargetClass(Version.class)
				.withResponseParser(new Jackson2NetworkResponseParser())
				.withListener(new Response.Listener<Version>() {
					@Override
					public void onResponse(Version response) {
						if(packageInfo.versionCode < response.ver){
							pDialog.dismiss();
							UpdateProgressDialog dialog = new UpdateProgressDialog("업데이트", "업데이트 파일을 받고 있습니다.", response.size, LoginActivity.this, "http://192.168.0.235:88/pda/", response.name);
							dialog.show();
						}else{
							pDialog.dismiss();
							onInit();
						}
					}
				})
				.withErrorListener(new VollyerErrorListener(LoginActivity.this, "오류가 발생했습니다. 전산팀으로 문의바랍니다.", () -> {
					pDialog.dismiss();
					AlertDialog.Builder ab = new AlertDialog.Builder(LoginActivity.this);
					ab.setTitle("알림");
					ab.setMessage("업데이트가 실패하였습니다. 전산팀으로 문의바랍니다.");
					ab.setPositiveButton("닫기", (dialogInterface, i) -> {
						LoginActivity.this.finish();
					});
					ab.show();
				}))
				.execute();
	}

	public void onInit() {
//		fingerprintChecker = new FingerprintHelper(this);
		preferences = getSharedPreferences("sambumobilemes", Context.MODE_PRIVATE);

		try {
			((TextView) findViewById(R.id.ver)).setText("ver " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		edtUid = findViewById(R.id.uid);
		edtPwd = findViewById(R.id.pwd);
		autoidCheck = findViewById(R.id.autoid_check);
		autopwdCheck = findViewById(R.id.autopwd_check);
		autologinCheck = findViewById(R.id.autologin_check);
		fingerCheck = findViewById(R.id.finger_check);
		loginOk = findViewById(R.id.login_ok);
		//warningMsg1 = findViewById(R.id.warning_msg1);
		//warningMsg2 = findViewById(R.id.warning_msg2);
		//warning_layout = findViewById(R.id.warning_layout);
		//errorMessageDivider = findViewById(R.id.error_message_divider);

		autoidCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = preferences.edit();
				editor.putBoolean("autoidCheck", isChecked);
				editor.commit();
				autopwdCheck.setEnabled(isChecked);
			}
		});

		autopwdCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = preferences.edit();
				editor.putBoolean("autopwdCheck", isChecked);
				editor.commit();

				autoidCheck.setEnabled(!isChecked);
				autologinCheck.setEnabled(isChecked);
			}
		});

		autologinCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = preferences.edit();
				editor.putBoolean("autologinCheck", isChecked);
				editor.commit();

				autopwdCheck.setEnabled(!isChecked);
			}
		});

		loginOk.setOnClickListener(new LoginOkClickListener());

		Application.app.SERVER_URL = "http://192.168.18.240:8080";
		findViewById(R.id.ver).setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Application.app.SERVER_URL = "http://192.168.0.122/";
				((TextView) findViewById(R.id.ver)).setTextColor(Color.RED);
				return false;
			}
		});

		if (preferences.getBoolean("autoidCheck", false)) {
			autoidCheck.setChecked(true);
			edtUid.setText(preferences.getString("uid", ""));

			if (preferences.getBoolean("autopwdCheck", false)) {
				autopwdCheck.setChecked(true);
				edtPwd.setText(preferences.getString("pwd", ""));

				if (preferences.getBoolean("autologinCheck", false)) {
					autologinCheck.setChecked(true);
					fingerCheck.setChecked(preferences.getBoolean("finger", false));
//					if (fingerprintChecker.isSupportDevice() && fingerCheck.isChecked())
//						Toast.makeText(this, "aaa", Toast.LENGTH_LONG).show();
//					else
					onClick();
				}
			} else {
				autologinCheck.setEnabled(false);
			}
		} else {
			autopwdCheck.setEnabled(false);
			autologinCheck.setEnabled(false);
		}

		//if (fingerprintChecker.isSupportDevice())
		//	fingerCheck.setEnabled(true);

//		fingerCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//				if(isChecked){
//					fingerCheck.setChecked(fingerprintChecker.showFingerprintDialog());
//				}
////				Editor editor = preferences.edit();
////				editor.putBoolean("finger", isChecked);
////				editor.commit();
//			}
//		});


	}

	public void viewWarning() {
//		warningMsg1.setText(getResources().getString(R.string.login_error));
//		warningMsg2.setText(getResources().getString(R.string.not_regd));
//		warning_layout.setVisibility(View.VISIBLE);
//		errorMessageDivider.setVisibility(View.VISIBLE);
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("오류");
		ab.setMessage(getResources().getString(R.string.not_regd));
		ab.show();
	}

	public void onClick() {
		if (edtUid.getText().toString().equals("") || edtPwd.getText().toString().equals("")) {
			viewWarning();
			return;
		}

		final String uid = edtUid.getText().toString();
		final String pwd = edtPwd.getText().toString().toUpperCase();

		//자동로그인 설정
		Editor editor = preferences.edit();
		editor.putBoolean("autoidCheck", autoidCheck.isChecked());
		editor.putBoolean("autopwdCheck", autopwdCheck.isChecked());
		editor.putBoolean("autologinCheck", autologinCheck.isChecked());
		editor.putString("uid", uid);
		editor.putString("pwd", pwd);
		editor.commit();

		pDialog = ProgressDialog.show(LoginActivity.this, "", getResources().getString(R.string.login_now), true);

		Application.app.getRequestQueue().add(new AuthRequest<MesResponse>(MesResponse.class, Application.SERVER_URL + "/loginCheck.do", uid, pwd, successListener, errorListener));
	}

	Response.Listener<MesResponse> successListener = new Response.Listener<MesResponse>() {
		@Override
		public void onResponse(MesResponse response) {
			if(response == null){
				viewWarning();
				pDialog.dismiss();
			}else {
 				for (Data data : response.dataset) {
					if (data.getId().equals("gds_userInfo"))
						Application.USER = data.rows.row.get(0);
				}

				if (Application.USER != null) {
					runMainActivity();
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							viewWarning();
						}
					});
				}
			}
			//pDialog.dismiss();
		}
	};

	Response.ErrorListener errorListener = new VollyerErrorListener(LoginActivity.this, "오류가 발생했습니다. 전산팀으로 문의바랍니다.", ()-> {
		pDialog.dismiss();
	});

	private void runMainActivity() {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		LoginActivity.this.finish();
		pDialog.dismiss();
	}

	class LoginOkClickListener implements OnClickListener {
		public void onClick(View v) {
			LoginActivity.this.onClick();
		}
	}

	private void checkPermission(String... permissions) {
//		if (!(Build.VERSION.SDK_INT < 23)) {
//			int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
//			if (permissionCheck == PackageManager.PERMISSION_DENIED) {
//				requestPermissions(permission);
//			}
//		}else{
//			requestPermissions(permission);
//		}
		requestPermissions(permissions);
	}

	private void requestPermissions(String... permission) {
		TedPermission.create().setPermissions(permission).setPermissionListener(new PermissionListener() {
			@Override
			public void onPermissionGranted() {

			}
			@Override
			public void onPermissionDenied(List<String> deniedPermissions) {
				LoginActivity.this.finish();
			}
		})
		.check();
	}

//	private void checkUseFingerprint() {
//		AlertDialog.Builder ab = new AlertDialog.Builder(this);
//		ab.setTitle("지문인증");
//		ab.setMessage(String.format("지문인증을 지원하는 기기에서는 비밀번호 저장을 할 수 없습니다.\r\n지문인증을 사용하시겠습니까?", ViewGroupActivity.version[1]));
//		ab.setPositiveButton("사용", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Editor editor = preferences.edit();
//				editor.putInt("useFingerprint", 1);
//				editor.commit();
//			}
//		}).setNegativeButton("사용안함", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Editor editor = preferences.edit();
//				editor.putInt("useFingerprint", 0);
//				editor.commit();
//			}
//		});
//
//		AlertDialogButtonReSizer.resize2Button(this, ab.show());
//	}

}
