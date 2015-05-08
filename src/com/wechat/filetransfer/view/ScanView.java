package com.wechat.filetransfer.view;

import com.wechat.filetransfer.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * 浠垮井淇￠浄杈炬壂鎻忕晫闈�
 * 
 * @author ccz
 * 
 */
public class ScanView extends View {

	private Paint circlePaint;
	private Paint sweepPaint;

	/**
	 * 姊害娓叉煋
	 */
	private SweepGradient sweepGradient;
	private int degree = 0;

	public ScanView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public ScanView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		initPaint();
	}

	private void initPaint() {
		Resources r = this.getResources();

		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setARGB(255, 192, 192, 192);
		circlePaint.setStrokeWidth(2);
		circlePaint.setStyle(Paint.Style.STROKE);

		sweepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		sweepPaint.setStrokeCap(Paint.Cap.ROUND);
		sweepPaint.setStrokeWidth(4);
		sweepGradient = new SweepGradient(10, 0,
				r.getColor(R.color.scan_start_color),
				r.getColor(R.color.scan_end_color));
		sweepPaint.setShader(sweepGradient);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int Width = getMeasuredWidth();
		int Height = getMeasuredHeight();

		int pointX = Width / 2;
		int pointY = Height / 2;
		int radius = pointX + 25;

		canvas.save();
		/**
		 * 姣忔鎵弿鏃嬭浆鐨勫害鏁帮紝瓒婂皬鍒欓�熷害瓒婃參
		 */
		degree += 1;
		canvas.translate(pointX, pointY);
		canvas.rotate(270 + degree);
		canvas.drawCircle(0, 0, radius * 4 / 3, sweepPaint);
		canvas.restore();

		canvas.drawCircle(pointX, pointY, radius, circlePaint);
		canvas.drawCircle(pointX, pointY, radius / 3, circlePaint);
		canvas.drawCircle(pointX, pointY, radius * 2 / 3, circlePaint);
		canvas.drawCircle(pointX, pointY, radius * 4 / 3, circlePaint);
	}
}
