package com.lttclaw.pwdrepository;

import java.util.List;

import com.lttclaw.encryption.AES;
import com.lttclaw.greendao.account;
import com.lttclaw.greendao.accountDao;
import com.lttclaw.util.DAOUtils;
import com.lttclaw.util.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PwdListActivity extends Activity implements OnClickListener {

	private ListView listv;
	private BaseAdapter adapter;
	private List<account> accountlist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pwdlist);
		
		listv = (ListView) findViewById(R.id.listview);
		
		listv.setOnItemClickListener(new MonItemClickListener());
		registerForContextMenu(listv);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.pwditem_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int position=((AdapterContextMenuInfo) item.getMenuInfo()).position;
		account acc=accountlist.get(position);
		switch (item.getItemId()) {
		case R.id.edit:
			Intent inedit = new Intent(this, EditAccoutActivity.class);
			inedit.putExtra("edit", acc.getId());
			startActivity(inedit);
			break;
		case R.id.delete:
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("确定删除？").setMessage("点击确定删除该账户信息")
					.setPositiveButton("确定", new MPositiveOnClickListener(acc))
					.setNegativeButton("取消", null);
			builder.create().show();
			break;
		case R.id.viewpwd:
			String shadow = acc.getShadow();
			ToastUtils.show(PwdListActivity.this, AES.dec(shadow));
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	@Override
	protected void onResume() {
		accountDao aDao = DAOUtils.getAccountDao(this);
		accountlist = aDao.queryBuilder().list();
		adapter = new PwdAdapter();
		listv.setAdapter(adapter);
		super.onResume();
	}

	private class PwdAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return accountlist.size();
		}

		@Override
		public Object getItem(int position) {
			return accountlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View cv, ViewGroup group) {
			ViewHolder holder = null;
			account acc = accountlist.get(position);
			if (cv != null) {
				holder = (ViewHolder) cv.getTag();
			} else {
				cv = LayoutInflater.from(PwdListActivity.this).inflate(
						R.layout.listitem, null);
				holder = new ViewHolder();
				holder.img = (ImageView) cv.findViewById(R.id.img);
				holder.tv_name = (TextView) cv.findViewById(R.id.tv_title);
				holder.tv_account = (TextView) cv.findViewById(R.id.tv_account);
				cv.setTag(holder);
			}
			try {
				Log.d("pwdlist", acc.getImguri() + " " + acc.getProvider()
						+ " ");
				if(!TextUtils.isEmpty(acc.getImguri()))
					ImageLoader.getInstance().displayImage(acc.getImguri(),
							holder.img);
				else 
					holder.img.setImageResource(R.drawable.ic_launcher);
				holder.tv_name.setText(acc.getProvider());
				holder.tv_account.setText(acc.getUname());
			} catch (Exception e) {
			}
			return cv;
		}

		private class ViewHolder {
			ImageView img;
			TextView tv_name, tv_account;
		}
	}

	private class MonItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> listv, View v, int position,
				long id) {
			account acc = (account) listv.getAdapter().getItem(position);
			String shadow = acc.getShadow();
			Log.d("shadow pwd", shadow);
			ToastUtils.show(PwdListActivity.this, AES.dec(shadow));
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add:
			Intent i = new Intent(this, EditAccoutActivity.class);
			startActivity(i);
			break;
		case R.id.reset_gesture:
			Intent reset=new Intent(this,GesturelockCheckActivity.class);
			reset.putExtra("reset", true);
			startActivity(reset);
			finish();
		}
	}

	private class MPositiveOnClickListener implements DialogInterface.OnClickListener{
		account acc;
		public MPositiveOnClickListener(account acc) {
			this.acc=acc;
		}

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			accountDao accDao=DAOUtils.getAccountDao(PwdListActivity.this);
			accDao.delete(acc);
			accountlist = DAOUtils.getAccountDao(PwdListActivity.this).queryBuilder().list();
			adapter = new PwdAdapter();
			listv.setAdapter(adapter);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager manager=(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchview=(SearchView)menu.findItem(R.id.searchview).getActionView();//XXX
		searchview.setSearchableInfo(manager.getSearchableInfo(new ComponentName("com.lttclaw.pwdrepository", "com.lttclaw.pwdrepository.SearchActivity")));
		searchview.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String arg0) {
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String arg0) {
				return false;
			}
		});
		searchview.setSubmitButtonEnabled(true);
		return true;
	}
}
