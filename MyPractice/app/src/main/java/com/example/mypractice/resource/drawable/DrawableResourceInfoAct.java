package com.example.mypractice.resource.drawable;

import java.lang.reflect.TypeVariable;

import com.example.mypractice.R;

import android.R.drawable;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawableResourceInfoAct  extends Activity{
	
	public static final String TAG=DrawableResourceInfoAct.class.getSimpleName();
	
	
	public static final int TARGET_DENSITY_DEFAULT=-1;
	public static final int TARGET_DENSITY_SELF=-2;
	
	private TextView textView;
	private ImageView imageView1;
	private TextView resource1;
	private ImageView imageView2;
	private TextView resource2;
	private ImageView imageView3;
	private TextView resource3;
	private ImageView imageView4;
	private TextView resource4;
	private ImageView imageView5;
	private TextView resource5;
	private ImageView imageView6;
	private TextView resource6;
	DisplayMetrics outMetrics=new DisplayMetrics();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawable_resource);
		textView=(TextView) findViewById(R.id.info);
		imageView1=(ImageView) findViewById(R.id.image_1);
		resource1=(TextView) findViewById(R.id.resource_1);
		imageView2=(ImageView) findViewById(R.id.image_2);
		resource2=(TextView) findViewById(R.id.resource_2);
		imageView3=(ImageView) findViewById(R.id.image_3);
		resource3=(TextView) findViewById(R.id.resource_3);
		imageView4=(ImageView) findViewById(R.id.image_4);
		resource4=(TextView) findViewById(R.id.resource_4);
		imageView5=(ImageView) findViewById(R.id.image_5);
		resource5=(TextView) findViewById(R.id.resource_5);
		rebuildInfo();
	}
	private void rebuildInfo(){
		//手机信息
		Display display=getWindowManager().getDefaultDisplay();
		display.getMetrics(outMetrics);
		StringBuilder builder=new StringBuilder();
		builder.append(" density    ").append(outMetrics.density).append("\n");
		builder.append("densitydpi   ").append(outMetrics.densityDpi).append("\n");
		builder.append("scaledDensity   ").append(outMetrics.scaledDensity).append("\n");
		builder.append("xdpi  ").append(outMetrics.xdpi).append("\n");
		builder.append("ydpi  ").append(outMetrics.ydpi).append("\n");
		textView.setText(builder.toString());
		//图片的信息
		Drawable drawable=imageView1.getDrawable();
		builder=new StringBuilder();
		append(builder, "IntrinsicWidth",drawable.getIntrinsicWidth());
		append(builder,"intrinsicHeight",drawable.getIntrinsicHeight());
		append(builder,"width",drawable.getBounds().width());
		append(builder,"height",drawable.getBounds().height());
		append(builder,"density",((BitmapDrawable) drawable).getBitmap().getDensity());
		resource1.setText(builder.toString());
		drawable=imageView2.getDrawable();
		builder=new StringBuilder();
		append(builder, "IntrinsicWidth",drawable.getIntrinsicWidth());
		append(builder,"intrinsicHeight",drawable.getIntrinsicHeight());
		append(builder,"width",drawable.getBounds().width());
		append(builder,"height",drawable.getBounds().height());
		append(builder,"density",((BitmapDrawable) drawable).getBitmap().getDensity());
		resource2.setText(builder.toString());
		drawable=imageView3.getDrawable();
		builder=new StringBuilder();
		append(builder, "IntrinsicWidth",drawable.getIntrinsicWidth());
		append(builder,"intrinsicHeight",drawable.getIntrinsicHeight());
		append(builder,"width",drawable.getBounds().width());
		append(builder,"height",drawable.getBounds().height());
		append(builder,"density",((BitmapDrawable) drawable).getBitmap().getDensity());
		resource3.setText(builder.toString());
		
		
		/*
		 * 下面我用320作为targetDensity，同时indensity用的是图片默认的和inscale为true，那么
		 * 产生的bitmap，就会按照320和默认的density就行缩放，同时这个bitmap的getDensity就是inTargetDensity了
		 * 但是如果我们直接使用这个bitmap的话，会有个问题，
		 * 因为setImageBitmap，会再次根据bitmap的density进行缩放，来能够显示正确大小的图片。所以如果
		 * 我们需要显示原始图片的大小，
		 * 有两点需要做：
		 * 1.用图片默认的inTargetDensity来解析出来bitmap
		 * 2.在返回的bitmap，调用setDensity来设置density为当前手机的density。这样就可以显示原始图片大小了
		 * 
		 * 上面用的产生bitmap方法为decodeResource。其他方法会有不同
		 */
		
		
		Bitmap bitmap=decodeBitmap(R.drawable.resource_1,"resource_1",TARGET_DENSITY_DEFAULT,false);
		imageView4.setImageBitmap(bitmap);
		drawable=imageView4.getDrawable();
		builder=new StringBuilder();
		append(builder, "IntrinsicWidth",drawable.getIntrinsicWidth());
		append(builder,"intrinsicHeight",drawable.getIntrinsicHeight());
		append(builder,"width",drawable.getBounds().width());
		append(builder,"height",drawable.getBounds().height());
		append(builder,"density",((BitmapDrawable) drawable).getBitmap().getDensity());
		resource4.setText(builder.toString());
		
		
		
		
		//由于已知
		 bitmap=decodeBitmap(R.drawable.resource_1,"resource_1",TARGET_DENSITY_SELF,true);
		imageView5.setImageBitmap(bitmap);
		
		drawable=imageView5.getDrawable();
		builder=new StringBuilder();
		append(builder, "IntrinsicWidth",drawable.getIntrinsicWidth());
		append(builder,"intrinsicHeight",drawable.getIntrinsicHeight());
		append(builder,"width",drawable.getBounds().width());
		append(builder,"height",drawable.getBounds().height());
		append(builder,"density",((BitmapDrawable) drawable).getBitmap().getDensity());
		resource5.setText(builder.toString());
		
	}
	
	
	/**
	 * 
	 * @date 2015-6-2 下午10:08:30
	 * @description 如果xhdpi,xxhdpi都含有个相同名字的资源文件，
	 *                         那么下面方法会找到手机最贴近的dpi的文件夹中的文件
	 *                         xxhdpi有个256*256的图片 resource_4
	 *                         xhdpi有个100*100的图片 resource_4
	 * 
	 * @return
	 */
	public  Bitmap decodeBitmap(int drawable,String logName,int targetDensity,boolean isResetDensity){
		Bitmap bitmap=null;
		Options opts=new Options();
		opts.inJustDecodeBounds=true;
		TypedValue typedValue=new TypedValue();
		getResources().getValue(drawable, typedValue, true);
		
		Log.d(TAG,logName+"  typedvalue density  "+typedValue.density+" come from  "+typedValue.toString());
		bitmap=BitmapFactory.decodeResource(getResources(),drawable, opts);
		Log.d(TAG,logName+"  bitmap  outweight  "+opts.outWidth+"  outheight " + opts.outHeight);
		opts.inJustDecodeBounds=false;
//		opts.inDensity=inDensity;
		opts.inScaled=true;
		switch (targetDensity) {
		case TARGET_DENSITY_DEFAULT:
			opts.inTargetDensity=0;
			break;
		case TARGET_DENSITY_SELF:
			opts.inTargetDensity=typedValue.density;
			break;
		default:
			opts.inTargetDensity=targetDensity;
			break;
		}
		
		bitmap=BitmapFactory.decodeResource(getResources(),drawable, opts);
		Log.d(TAG,logName +"  bitmap  outweight  "+opts.outWidth+"  outheight " + opts.outHeight);
		Log.d(TAG,logName+ "  bitmap density "+bitmap.getDensity()+" size "+bitmap.getByteCount());
		if(isResetDensity){
			bitmap.setDensity(outMetrics.densityDpi);
		}
		return bitmap;
	}
	
	public static StringBuilder append(StringBuilder builder,String key,String value){
		builder.append(key).append("  ").append(value).append("\n");
		
		return builder;
	}
	public static StringBuilder append(StringBuilder builder,String key,int value){
		builder.append(key).append("  ").append(value).append("\n");
		return builder;
	}
	public static StringBuilder append(StringBuilder builder,String key,float value){
		builder.append(key).append("  ").append(value).append("\n");
		return builder;
	}

}
