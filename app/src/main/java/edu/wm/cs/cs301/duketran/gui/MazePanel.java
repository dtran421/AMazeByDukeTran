package edu.wm.cs.cs301.duketran.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.Nullable;

public class MazePanel extends View {
    private Paint painter;

    public MazePanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        painter = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        Log.v("Draw status", "Working...");
        painter.setColor(Color.CYAN);
        canvas.drawRect(0,0, 100, 100, painter);
    }
}
