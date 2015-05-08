package com.wechat.filetransfer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * 自定义圆形的ImageView
 * 
 * @author ccz
 * 
 */
public class CircleImageView extends ImageView {
	private Paint paint;

	public CircleImageView(Context context) {
		this(context, null);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint();
		this.setVisibility(View.GONE);
	}

	/**
	 * 显示时会有动画
	 */
	public void showImage() {
		this.setVisibility(View.VISIBLE);
		AnimationSet set = new AnimationSet(true);
		ScaleAnimation scaleBig = new ScaleAnimation(0.0f, 1.5f, 0.0f, 1.5f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleBig.setDuration(300);
		scaleBig.setFillAfter(true);
		set.addAnimation(scaleBig);
		ScaleAnimation scaleSmall = new ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleSmall.setDuration(300);
		scaleSmall.setFillAfter(true);
		set.addAnimation(scaleSmall);
		this.setAnimation(set);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();
		if (null != drawable) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			Bitmap b = getCircleBitmap(bitmap, 14);
			final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
			final Rect rectDest = new Rect(0, 0, getWidth(), getHeight());
			paint.reset();
			canvas.drawBitmap(b, rectSrc, rectDest, paint);

		} else {
			super.onDraw(canvas);
		}
	}

	private Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
		final int color = 0xff424242;
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		int x = bitmap.getWidth();

		canvas.drawCircle(x / 2, x / 2, x / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
}