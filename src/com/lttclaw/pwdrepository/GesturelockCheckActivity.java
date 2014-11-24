package com.lttclaw.pwdrepository;

import org.json.JSONArray;
import org.json.JSONException;

import com.lttclaw.encryption.AES;
import com.lttclaw.util.Constants;
import com.lttclaw.util.ToastUtils;
import com.lttclaw.widget.GestureLockViewGroup;
import com.lttclaw.widget.GestureLockViewGroup.OnGestureLockViewListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class GesturelockCheckActivity extends Activity {

	private GestureLockViewGroup gesturelock;
	private TextView description;
	private boolean reset;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesturelock_set);
		description=(TextView) findViewById(R.id.tv_description);
		description.setText("手势验证");
		gesturelock=(GestureLockViewGroup) findViewById(R.id.gesturelock);
		int[] pwd=getGesturePwd();
		gesturelock.setAnswer(pwd);
		gesturelock.setOnGestureLockViewListener(new MgestureLockListener());
		reset=getIntent().getBooleanExtra("reset", false);
	}
	
	/** 从sharedpreference中提取出保存的用户手势密码
	 * @return
	 */
	private int[] getGesturePwd() {
		int[] pwd = null;
		SharedPreferences repository=getSharedPreferences(Constants.SP_NAME,MODE_PRIVATE);
		String shadowedJarr=repository.getString(Constants.SP_GESTURELOCK, "");
		try {
			JSONArray jarr=new JSONArray(AES.dec(shadowedJarr));
			pwd=new int[jarr.length()];
			for(int i=0;i<jarr.length();i++){
				pwd[i]=jarr.getInt(i);
			}
		} catch (JSONException e) {
			Log.e("json err", e.getLocalizedMessage());
		}
		return pwd;
	}
	
	private class MgestureLockListener implements OnGestureLockViewListener{

		@Override
		public void onBlockSelected(int position) {
		}

		@Override
		public void onGestureEvent(boolean matched) {
			if(matched&&!reset){
				Intent i=new Intent(GesturelockCheckActivity.this,PwdListActivity.class);
				startActivity(i);
				finish();
			}else if(matched&&reset){
				clearLock();
				Intent i=new Intent(GesturelockCheckActivity.this,GesturelockSetActivity.class);
				startActivity(i);
				finish();
			}else{
				ToastUtils.show(GesturelockCheckActivity.this, "手势错误");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						gesturelock.reset();
					}
				}, 500);
				
			}
		}

		@Override
		public void onUnmatchedExceedBoundary() {
			ToastUtils.show(GesturelockCheckActivity.this, "连续密码输入错误，请稍后再试");
			gesturelock.setVisibility(View.GONE);;
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run(){
					gesturelock.setVisibility(View.VISIBLE);
				}
			}, 5000);
			
		}
		
	}

	public void clearLock() {
		Editor editor=getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE).edit();
		editor.putBoolean(Constants.SP_INITIALED, false);
		editor.putString(Constants.SP_GESTURELOCK, "");
		editor.commit();
		
	}
}
