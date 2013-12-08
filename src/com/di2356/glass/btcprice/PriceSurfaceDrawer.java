package com.di2356.glass.btcprice;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;


public class PriceSurfaceDrawer implements SurfaceHolder.Callback {
    private static final String TAG = "PriceDrawer";

    private final PriceView mPriceView;

    private SurfaceHolder mHolder;

    public PriceSurfaceDrawer(Context context) {
        mPriceView = new PriceView(context);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        mPriceView.measure(measuredWidth, measuredHeight);
        mPriceView.layout(
                0, 0, mPriceView.getMeasuredWidth(), mPriceView.getMeasuredHeight());
        
        draw();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Surface created");
        mHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed");
        mHolder = null;
    }
    
    public void draw() {
        Canvas canvas;
        try {
            canvas = mHolder.lockCanvas();
        } catch (Exception e) {
            return;
        }
        if (canvas != null) {
            mPriceView.draw(canvas);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void draw(String price) {
       mPriceView.setPrice("$"+price);
       draw();
    }
}
