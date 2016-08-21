package com.example.mypractice;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ApiLaunch extends ListActivity {
	public static String TAG = ApiLaunch.class.getSimpleName();
	private static String category = "com.example.apidemons.CATEGORY_SAMPLES";
	private static String TITLE = "title";
	private static String INTENT = "intent";
	public static String PREFIX = "prefix";
	private SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d("----pid---"+ Process.myPid());
		String prefix = getIntent().getStringExtra(PREFIX);
		if (prefix == null) {
			prefix = "";
		}else{
        	setTitle(prefix);
        }
		Log.d(TAG, "prefix " + prefix);
		adapter = new SimpleAdapter(this, createData(prefix),
				R.layout.activity_api_launch_item, new String[] { TITLE },
				new int[] { android.R.id.text1 });
		setListAdapter(adapter);
	}

	List<Map<String, Object>> createData(String prefix) {
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		PackageManager manager = getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(category);
		List<ResolveInfo> lists = manager.queryIntentActivities(intent, 0);
		if (lists == null) {
			return maps;
		}

		List<String> contain = new ArrayList<String>();
		for (int i = 0, size = lists.size(); i < size; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			ResolveInfo info = lists.get(i);
			String label = (String) (info.activityInfo.loadLabel(manager) == null ? info.activityInfo.name
					: info.activityInfo.loadLabel(manager));
			int index = -1;
			Log.d(TAG, i + " label " + label);

			if ((index = label.indexOf(prefix)) != -1) {
				String end;
				if (prefix.equals("")) {
					end = label.substring(index + prefix.length());
				} else {
					end = label.substring(index + prefix.length() + 1);
				}

				Log.d(TAG, i + "  sub " + end);
				Intent temp = null;
				String title = null;
				if ((index = end.indexOf("/")) != -1) {
					title = end.substring(0, index);
					if (!prefix.equals("")) {
						temp = browserIntent(prefix + "/" + title);
					} else {
						temp = browserIntent(title);
					}
				} else {
					title = end;
					temp = executeIntent(info.activityInfo.packageName,
							info.activityInfo.name);
				}
				if (!contain.contains(title)) {
					map.put(TITLE, title);
					map.put(INTENT, temp);
					maps.add(map);
					contain.add(title);
				}
			}

		}
		Collections.sort(maps, sDisplayNameComparator);
		return maps;
	}

	private Intent browserIntent(String prefix) {
		Intent intent = new Intent(this, ApiLaunch.class);
		intent.putExtra(PREFIX, prefix);
		return intent;
	}

	private Intent executeIntent(String packageName, String activityName) {
		Intent intent = new Intent();
		intent.setClassName(packageName, activityName);
		return intent;
	}

	private final static Comparator<Map<String, Object>> sDisplayNameComparator =
	        new Comparator<Map<String, Object>>() {
	        private final Collator   collator = Collator.getInstance();

	        public int compare(Map<String, Object> map1, Map<String, Object> map2) {
	            return collator.compare(map1.get("title"), map2.get("title"));
	        }
	    };
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Map<String, Object> maps = (Map<String, Object>) l
				.getItemAtPosition(position);
		startActivity((Intent) maps.get(INTENT));
	}

}
