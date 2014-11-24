package com.lttclaw.pwdrepository;

import java.util.ArrayList;
import java.util.List;

import com.lttclaw.encryption.AES;
import com.lttclaw.greendao.account;
import com.lttclaw.greendao.accountDao;
import com.lttclaw.greendao.accountDao.Properties;
import com.lttclaw.util.DAOUtils;
import com.lttclaw.util.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.dao.query.QueryBuilder;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SearchActivity extends Activity {

	private ListView listv;
	private ArrayList<account> accountlist=new ArrayList<account>();
	private BaseAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		listv=(ListView) findViewById(R.id.listv);
		adapter=new PwdAdapter();
		listv.setAdapter(adapter);
		listv.setOnItemClickListener(new MonItemClickListener());
		Intent intent=getIntent();
		if(Intent.ACTION_SEARCH.equals(intent.getAction())){
			String query=intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}
	}

	private void doSearch(String query) {
		accountDao aDao=DAOUtils.getAccountDao(this);
		QueryBuilder<account> qb=aDao.queryBuilder();
		String like="%"+query+"%";
		qb.whereOr(Properties.Provider.like(like), Properties.Uname.like(like));
		List<account> result=qb.list();
		if(result!=null&&!result.isEmpty()){
		accountlist.addAll(result);
		adapter.notifyDataSetChanged();
		SearchRecentSuggestions recentSuggestion=new SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
		recentSuggestion.saveRecentQuery(query, null);
		}else{
			Toast.makeText(this, "没有找到结果", Toast.LENGTH_SHORT).show();
			finish();
		}
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
				cv = LayoutInflater.from(SearchActivity.this).inflate(
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
			ToastUtils.show(SearchActivity.this, AES.dec(shadow));
		}

	}
}
