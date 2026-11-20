package com.example.avmtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class AssistPreviewView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int cameraCount = 4;
    private String model = "";
    private String viewId = "";

    public AssistPreviewView(Context c) { super(c); init(); }
    public AssistPreviewView(Context c, AttributeSet a) { super(c, a); init(); }
    public AssistPreviewView(Context c, AttributeSet a, int d) { super(c, a, d); init(); }

    private void init() {
        text.setColor(Color.WHITE);
        text.setTextAlign(Paint.Align.CENTER);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public void setProfile(DeviceProfile p, String v) {
        cameraCount = p.cameras.size();
        model = p.model;
        viewId = v;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        paint.setColor(Color.parseColor("#0B1220"));
        canvas.drawRoundRect(new RectF(0, 0, w, h), dp(12), dp(12), paint);
        paint.setColor(Color.parseColor("#1E3A5F"));
        for (int i = 1; i < 4; i++) canvas.drawRect(w * i / 4f, 0, w * i / 4f + dp(1), h, paint);
        for (int i = 1; i < 3; i++) canvas.drawRect(0, h * i / 3f, w, h * i / 3f + dp(1), paint);
        paint.setColor(Color.parseColor("#94A3B8"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        RectF body = new RectF(w * 0.40f, h * 0.28f, w * 0.60f, h * 0.72f);
        canvas.drawRoundRect(body, dp(6), dp(6), paint);
        RectF cabin = new RectF(w * 0.42f, h * 0.22f, w * 0.58f, h * 0.36f);
        canvas.drawRoundRect(cabin, dp(5), dp(5), paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#2DD4BF"));
        point(canvas, w * 0.14f, h * 0.32f);
        point(canvas, w * 0.86f, h * 0.32f);
        point(canvas, w * 0.14f, h * 0.68f);
        point(canvas, w * 0.86f, h * 0.68f);
        if (cameraCount >= 5) point(canvas, w * 0.50f, h * 0.14f);
        if (cameraCount >= 6) point(canvas, w * 0.50f, h * 0.86f);
        float base = Math.min(w, h);
        text.setTextSize(Math.min(dp(36), Math.max(dp(14), base * 0.075f)));
        text.setFakeBoldText(true);
        canvas.drawText("AVM 影像", w / 2f, h * 0.50f, text);
        text.setFakeBoldText(false);
        text.setTextSize(Math.min(dp(22), Math.max(dp(11), base * 0.045f)));
        canvas.drawText(model + " · " + cameraCount + " 路相机", w / 2f, h * 0.58f, text);
        text.setTextSize(Math.min(dp(16), Math.max(dp(9), base * 0.035f)));
        text.setColor(Color.parseColor("#94A3B8"));
        canvas.drawText(viewId, w / 2f, h * 0.82f, text);
    }

    private void point(Canvas c, float x, float y) {
        c.drawCircle(x, y, dp(5), paint);
        paint.setColor(Color.parseColor("#0B1220"));
        c.drawCircle(x, y, dp(2), paint);
        paint.setColor(Color.parseColor("#2DD4BF"));
    }

    private float dp(int v) {
        return v * getResources().getDisplayMetrics().density;
    }
}
