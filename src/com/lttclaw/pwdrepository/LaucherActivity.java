package com.lttclaw.pwdrepository;

import com.lttclaw.util.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

public class LaucherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreferences repositorySP=getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
		boolean initialed=repositorySP.getBoolean(Constants.SP_INITIALED, false);
		if(initialed){
			Intent i=new Intent(this,GesturelockCheckActivity.class);
			startActivity(i);
		}else{
			Intent i=new Intent(this,GesturelockSetActivity.class);
			startActivity(i);
		}
		initImageLoader(this);
		finish();
	}
	
	public void initImageLoader(Context context) {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.cacheInMemory(true).cacheOnDisc(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(

		context).threadPriority(Thread.NORM_PRIORITY - 1)
		// 设置线程的优先级
				.denyCacheImageMultipleSizesInMemory()
				// 当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 设置缓存文件的名字
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// 设置图片下载和显示的工作队列排序
				.writeDebugLogs() // Remove for release app
				.defaultDisplayImageOptions(defaultOptions)// 默认加载的图片
				.discCacheSize(2 * 1024 * 1024).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

}
