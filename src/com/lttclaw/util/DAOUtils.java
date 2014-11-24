package com.lttclaw.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lttclaw.greendao.DaoMaster;
import com.lttclaw.greendao.DaoSession;
import com.lttclaw.greendao.accountDao;

public class DAOUtils {

	public static accountDao getAccountDao(Context c){
		SQLiteOpenHelper helper=new DaoMaster.DevOpenHelper(c, "account-db", null);
		SQLiteDatabase db=helper.getWritableDatabase();
		DaoMaster daoMaster=new DaoMaster(db);
		DaoSession daoSession=daoMaster.newSession();
		return daoSession.getAccountDao();
	}
}
