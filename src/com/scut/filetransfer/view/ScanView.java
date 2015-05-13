package com.scut.filetransfer.view;
import com.scut.filetransfer.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @author ccz
 * 
 */
public class ScanView extends View {

	private Paint circlePaint;
	private Paint sweepPaint;

	/**
	 * 
	 */
	private SweepGradient sweepGradient;
	private int degree = 0;

	public ScanView(Context context) {
		this(context, null);
	}

	public ScanView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
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
		 * 
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
