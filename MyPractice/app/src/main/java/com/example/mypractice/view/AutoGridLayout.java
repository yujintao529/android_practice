package com.example.mypractice.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yujintao
 * @date 2015-2-5 下午9:59:39
 * @description 废弃
 */
public class AutoGridLayout extends ViewGroup {

	private static final String TAG = "AutoGridLayout";
	private static final boolean DEBUG = true;

	private static int ROWPX_UNIT = 200;//px;

	private static int VIEW_STATUS_MASK = 0x00;

	private static int MEASURE_ROW = 0x01;

	private int mViewStatus;
	private boolean mShowDividingLine=true;
	private int mDividingLineWidth=1;//px
	
	private int mHorizontalLinesNumber;
	private int mVerticalLinesNumber;
	
	private int mLinePX=1;
	private List<Point> mHorizontalLines; 
	private List<Point> mVerticalLines;
	
	public AutoGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mViewStatus = 0x00;
		setWillNotDraw(false);
		mHorizontalLines=new ArrayList<Point>();
		mVerticalLines=new ArrayList<Point>();
	}

	/* (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 * 自己测量应该怎样设置每行每列个数。
	 * 由于是流失布局所以他所给予子视图的宽度都是确定的。也就是都是MeasureSpec.Exactly模式。
	 * 但是高度则不是MeasureSpec.Exactly模式
	 * 目前不考虑UNSPECIFIED情况。不是用margin。
	 * 目前所实现的版本是记录没行最大高度。进行设置。宽度都是确定的
	 */
	private int mRowNumber;
	private int mRowAVGWidth;
	
	//why? onMeasure method is called by twice?
	@Override
	protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		if (DEBUG) {
			final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
			final int heigth = MeasureSpec.getSize(heightMeasureSpec);
			switch (heightMode) {
				case MeasureSpec.AT_MOST:
					log("height mode at_most %d", heigth);
					break;
				case MeasureSpec.EXACTLY:
					log("height mode EXACTLY %d", heigth);
					break;
				case MeasureSpec.UNSPECIFIED:
					log("height mode UNSPECIFIED %d", heigth);
				default:
					break;
			}
		}
		//进行宽度测量。atMost 和 Exactly 都是用widthSize。
		int realWidthContent = widthSize - getPaddingLeft() - getPaddingRight();
		measureRowNumber(realWidthContent, ROWPX_UNIT);
		final int childCount = getChildCount();
		final int childWidthSpec = MeasureSpec.makeMeasureSpec(mRowAVGWidth, MeasureSpec.EXACTLY);

		int heightTotal = 0;

		for (int i = 0, row = 0; i < childCount;) {
			int maxHeight = 0;
			for (int j = 0; j < mRowNumber; j++) {
				final View child = getChildAt(row * mRowNumber + j);
				if (child == null) {
					break;
				}
				ViewGroup.LayoutParams params = child.getLayoutParams();
				final int childHeightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, getPaddingBottom() + getPaddingTop(), params.height);
				child.measure(childWidthSpec, childHeightSpec);
				final int childMeasureHeigth = child.getMeasuredHeight();
				if (DEBUG) {
					log("chiled %d %d measure height mode %s size %d",row,j,specModeToString(MeasureSpec.getMode(childHeightSpec)),MeasureSpec.getSize(childHeightSpec));
				}
				int childSize = Math.max(childMeasureHeigth, getSuggestedMinimumHeight());
				maxHeight = maxHeight > childSize ? maxHeight : childSize;
			}
			heightTotal += maxHeight;
			for (int j = 0; j < mRowNumber; j++) {
				final View child = getChildAt(row * mRowNumber + j);
				if (child == null) {
					break;
				}
				child.measure(childWidthSpec, ViewGroup.getChildMeasureSpec(heightMeasureSpec, getPaddingBottom() + getPaddingTop(), maxHeight));
				if (DEBUG) {
					int measureHeight = child.getMeasuredHeight();
					int measureWidth = child.getMeasuredWidth();
					log("final child %d %d width %d heigth %d", row, j, measureWidth, measureHeight);
				}
			}
			i += mRowNumber;
			row += 1;
		}
		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
		int resultHeight = 0;
		switch (specMode) {
			case MeasureSpec.UNSPECIFIED:
				resultHeight = heightTotal;
				break;
			case MeasureSpec.AT_MOST:
				if (specSize < heightTotal) {
					resultHeight = specSize | MEASURED_STATE_TOO_SMALL;
				}
				else {
					resultHeight = heightTotal;
				}
				break;
			case MeasureSpec.EXACTLY:
				resultHeight = specSize;
				break;
		}
		setMeasuredDimension(widthSize, resultHeight);
		log("layout width %d heigth %d", widthSize, resultHeight);
	}

	/**
	 * @author: jintao
	 * @param width
	 * @param unit
	 * @return: void
	 * @date: 2015-1-26 下午3:19:05
	 * 测量每行数量函数，必须要在onMeasure调用
	*/

	private void measureRowNumber(int width, int unit) {
		mViewStatus |= MEASURE_ROW;
		mRowNumber = width / unit == 0 ? 1 : width / unit;
		mVerticalLinesNumber=mRowNumber-1;
		final double widthAvg = (width * 1.0-mVerticalLinesNumber*mLinePX) / mRowNumber;
		mRowAVGWidth = (int) (widthAvg + 1);
	}

	private String specModeToString(int mode) {
		final String modeDescription;
		switch (mode) {
			case 0 << 30:
				modeDescription = "UNSPECIFIED";
				break;
			case 1 << 30:
				modeDescription = "EXACTLY";
				break;
			case 2 << 30:
				modeDescription = "AT_MOST";
				break;
			default:
				modeDescription = "unknow";
				break;
		}
		return modeDescription;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		//draw dividingLine
		if(!mShowDividingLine){
			return;
		}
		log("begin draw dividingLine");
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int widthChild = mRowAVGWidth;
		final int rowNumber = mRowNumber;
		mVerticalLines.clear();
		mHorizontalLines.clear();
		final int childCount = getChildCount();
		final int columnNumber = childCount / rowNumber + (childCount % rowNumber == 0 ? 0 : 1);
		if (DEBUG) {
			log("gridlayout rowNumber %d columnNumber %d", mRowNumber, columnNumber);
			log("onlayout l %d t %d r %d b %d", l, t, r, b);
		}
		int left = l;
		int top = t;
		for (int row = 0, column = 0; row + column * rowNumber < childCount;) {
			final View child = getChildAt(row + column * rowNumber);
			final int measureHeight = child.getMeasuredHeight();
			child.layout(left, top, left + widthChild, top + measureHeight);
			if (DEBUG) {
				log("child %d %d left %d top %d right %d bottom %d",row,column, left, top, left + widthChild,top + measureHeight);
			}
			if((row+1)==rowNumber){
				column+=1;
				row=0;
				top+=measureHeight;
				left=l;
			}else{
				row+=1;
				left+=widthChild;
			}
		}
		
		
		
		for(int i=0;i<(rowNumber-1)*(columnNumber-1);i++){
			
		}
		
	}

	/**
	 * 2015-2-5
	 * @param message
	 * @param args
	 * void
	 */
	public void log(String message, Object... args) {
		message = String.format(message, args);
		Log.d(TAG, message);
	}
	
	/**
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 * @return
	 */
	private Line makeLine(int sx,int sy,int ex ,int ey){
		return new Line(sx, sy, ex, ey);
	}
	
	/**
	 * @param px
	 * @param py
	 * @return
	 */
	private Point makePoint(int px,int py){
		return new Point(px,py);
	}
	public class Line{
		int sx;
		int sy;
		int ex;
		int ey;
		public Line(int sx, int sy, int ex, int ey) {
			super();
			this.sx = sx;
			this.sy = sy;
			this.ex = ex;
			this.ey = ey;
		}
		
	}
}
