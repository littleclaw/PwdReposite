package com.lttclaw.pwdrepository;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

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
import android.widget.TextView;

public class GesturelockSetActivity extends Activity {

	private GestureLockViewGroup gesturelock;
	private List<Integer> gesturePwdList;
	private boolean confirm=false;
	private TextView description;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesturelock_set);
		description=(TextView) findViewById(R.id.tv_description);
		description.setText("设置手势密码");
		gesturelock=(GestureLockViewGroup) findViewById(R.id.gesturelock);
		gesturePwdList=new ArrayList<Integer>();
		gesturelock.setOnGestureLockViewListener(new OnGestureLockViewListener() {
			@Override
			public void onUnmatchedExceedBoundary() {
			}
			
			@Override
			public void onGestureEvent(boolean matched) {

				if(!confirm){
					confirm=true;
					ToastUtils.show(GesturelockSetActivity.this, "再输一次");
					gesturelock.setAnswer(convertList(gesturePwdList));
					gesturePwdList.clear();
				}else if(matched){
					gesturelock.setAnswer(convertList(gesturePwdList));
					ToastUtils.show(GesturelockSetActivity.this, "手势密码设置成功");
					//TODO 持久化密码
					saveLock();
					Intent i=new Intent(GesturelockSetActivity.this,PwdListActivity.class);
					startActivity(i);
					finish();
				}else{
					confirm=false;
					ToastUtils.show(GesturelockSetActivity.this, "两次设置不匹配，请重新设置");
					gesturePwdList.clear();
				}
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						gesturelock.reset();
					}
				}, 300);
			}
			private int[] convertList(List<Integer> gesturePwdList) {
				int[] pwd=new int[gesturePwdList.size()];
				int i=0;
				for(i=0;i<pwd.length;i++){
					pwd[i]=gesturePwdList.get(i);
				}
				return pwd;
			}
			
			@Override
			public void onBlockSelected(int cId) {
				gesturePwdList.add(cId);
			}
		});
	}
	/**
	 * 持久化手势锁密码
	 */
	protected void saveLock() {
		JSONArray jarr=new JSONArray();
		for(int n:gesturePwdList){
			jarr.put(n);
		}
		SharedPreferences repository=getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
		Editor editor=repository.edit();
		editor.putString(Constants.SP_GESTURELOCK, AES.enc(jarr.toString()));
		editor.putBoolean(Constants.SP_INITIALED, true);
		editor.commit();
		
	}
}
