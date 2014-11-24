package com.lttclaw.pwdrepository;

import java.util.Date;

import com.lttclaw.encryption.AES;
import com.lttclaw.greendao.account;
import com.lttclaw.greendao.accountDao;
import com.lttclaw.greendao.accountDao.Properties;
import com.lttclaw.util.DAOUtils;
import com.lttclaw.util.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.dao.query.QueryBuilder;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class EditAccoutActivity extends Activity implements OnClickListener {
	private EditText et_title, et_account, et_pwd;
	private ImageView img;
	private String imgPath;
	private long id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editaccount);

		et_title = (EditText) findViewById(R.id.et_title);
		et_account = (EditText) findViewById(R.id.et_account);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		img = (ImageView) findViewById(R.id.img_icon);

		id=getIntent().getLongExtra("edit", -1);
		if(id!=-1){
			accountDao accDao=DAOUtils.getAccountDao(this);
			QueryBuilder<account> builder=accDao.queryBuilder();
			builder.where(Properties.Id.eq(id));
			account acc=builder.list().get(0);
			
			et_title.setText(acc.getProvider());
			et_account.setText(acc.getUname());
			imgPath=acc.getImguri();
			ImageLoader.getInstance().displayImage(acc.getImguri(), img);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_icon:
			Intent getAlbum = new Intent(Intent.ACTION_PICK, null);
			getAlbum.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(getAlbum, 0);
			break;
		case R.id.bt_submit:
			if (checkInput()) {
				String title = et_title.getText().toString();
				String account = et_account.getText().toString();
				String pwd=et_pwd.getText().toString();
				account acc=new account();
				if(id!=-1)
					acc.setId(id);
				acc.setImguri(imgPath);
				acc.setProvider(title);
				acc.setUname(account);
				acc.setSetDate(new Date());
				acc.setShadow(AES.enc(pwd));
				accountDao accDao=DAOUtils.getAccountDao(this);
				accDao.insertOrReplace(acc);
				ToastUtils.show(this, "更新成功");
				finish();
			}
			break;
		default:
			break;
		}

	}

	private boolean checkInput() {
		String pwd = et_pwd.getText().toString();
		if (TextUtils.isEmpty(pwd)) {
			ToastUtils.show(this, "输入为空");
			et_pwd.setText("");
			return false;
		} else if (TextUtils.isEmpty(et_account.getText().toString())
				|| TextUtils.isEmpty(et_title.getText().toString())) {
			ToastUtils.show(this, "请填写完整~");
			return false;
		}else
			return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			imgPath = data.getData().toString();
			ImageLoader.getInstance().displayImage(imgPath, img);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
