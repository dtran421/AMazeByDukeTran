package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MazePanel extends View implements P5Panel {
    private Bitmap bufferBitmap;
    private Canvas bufferCanvas;
    private Paint paint;
    private float density;

    static final int greenWM = Color.rgb(17, 87, 64); // 1136448
    static final int goldWM = Color.rgb(145, 111, 65); // 9531201
    static final int yellowWM = Color.rgb(255, 255, 153); // 16777113
    static final int darkPrimary = Color.rgb(6, 9, 48);
    static final int lightPrimary = Color.rgb(31, 60, 136);

    static final int WHITE = Color.WHITE;
    static final int LIGHT_GRAY = Color.LTGRAY;
    static final int GRAY = Color.GRAY;
    static final int BLACK = Color.BLACK;
    static final int RED = Color.RED;
    static final int YELLOW = Color.YELLOW;

    static BitmapShader wallShader;
    static BitmapShader floorShader;

    /**
     * Dimensions for the FirstPersonView
     */
    private int viewWidth;
    private int viewHeight;

    private int currentColor;
    private Typeface currentFont;

    public MazePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        bufferBitmap = Bitmap.createBitmap(
                convertDpToPx(context, attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "layout_width", 350)),
                convertDpToPx(context, attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "layout_height", 350)),
                Bitmap.Config.ARGB_8888);
        bufferCanvas = new Canvas(bufferBitmap);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap wallTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.stone_wall);
        wallShader = new BitmapShader(wallTexture, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
        Bitmap floorTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.dirt);
        floorShader = new BitmapShader(floorTexture, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     * @param context Context to get resources and device specific display metrics
     * @param dp value which we need to convert into pixels
     * @return int value to represent px equivalent depending on device density
     */
    public int convertDpToPx(Context context, int dp) {
        density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     * @param px value which we need to convert into dp
     * @return int value to represent dp equivalent
     */
    public int convertPxToDp(int px) {
        return (int) (px / density);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.v("MazePanel", "Redrawing...");
        //canvas.drawColor(Color.LTGRAY);
        //myTestImage(canvas);
        update(canvas);
    }

    private void myTestImage(Canvas c) {
        c.drawColor(Color.LTGRAY);
        setColor(Color.RED);
        addFilledOval(0, 0, 400, 400);
        setColor(greenWM);
        addFilledOval(400, 0, 400, 400);
        setColor(Color.YELLOW);
        addFilledRectangle(800, 0, 400, 400);
        setColor(Color.BLUE);
        int[] xPoints = {150, 101, 125, 175, 200};
        int[] yPoints = {100, 125, 200, 200, 125};
        addFilledPolygon(xPoints, yPoints, 5);
        addLine(0, 200, 100, 300);
        update(c);
    }

    /**
     * Method to draw the buffer image on an inputted canvas object.
     */
    public void update(Canvas canvas) {
        paint(canvas);
    }

    /**
     * Method to draw the buffer image on the buffer canvas object.
     */
    public void update() { paint(bufferCanvas); }

    /**
     * Commits all accumulated drawings to the UI.
     * Substitute for View.invalidate method.
     */
    public void commit() { invalidate(); }

    /**
     * Draws the buffer image to the given canvas object.
     * This method is called when this panel should redraw itself.
     * The given canvas object is the one that actually shows
     * on the screen.
     */
    public void paint(Canvas canvas) {
        if (null == canvas) {
            Log.e("MazePanel.paint", "No canvas object, skipping drawImage operation");
        }
        else {
            canvas.drawBitmap(bufferBitmap, 0, 0, paint);
            bufferBitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
            bufferCanvas = new Canvas(bufferBitmap);
        }
    }

    /**
     * Tells if instance is able to draw. This ability depends on the
     * context, for instance, in a testing environment, drawing
     * may be not possible and not desired.
     * Substitute for code that checks if graphics object for drawing is not null.
     * @return true if drawing is possible, false if not.
     */
    @Override
    public boolean isOperational() {
        if (null == bufferCanvas) {
            Log.e("MazePanel", "Can't get buffer canvas object to draw on, skipping draw operation");
            return false;
        }
        return true;
    }

    /**
     * Sets the color for future drawing requests. The color setting
     * will remain in effect till this method is called again and
     * with a different color.
     * Substitute for Paint.setColor method.
     * @param rgb gives the red green and blue encoded value of the color
     */
    @Override
    public void setColor(int rgb) {
        currentColor = rgb;
        paint.setColor(rgb);
    }

    /**
     * Returns the RGB value for the current color setting.
     * @return integer RGB value
     */
    @Override
    public int getColor() {
        return currentColor;
    }

    /**
     * Sets the current font for the paint
     * @param font name
     */
    public void setFont(String font) {
        currentFont = Typeface.create(font, Typeface.BOLD);
        paint.setTypeface(currentFont);
    }

    /**
     * Provides the current font of the paint
     */
    public Typeface getFont() {
        return currentFont;
    }

    /**
     * Sets the view dimensions for the FirstPersonView
     */
    public void setViewDimensions(int width, int height) {
        viewWidth = width;
        viewHeight = height;
    }

    /**
     * Draws two solid rectangles to provide a background.
     * Note that this also erases any previous drawings.
     * The color setting adjusts to the distance to the exit to
     * provide an additional clue for the user.
     * Colors transition from black to gold and from grey to green.
     * Substitute for FirstPersonView.drawBackground method.
     * @param percentToExit gives the distance to exit
     */
    @Override
    public void addBackground(float percentToExit) {
        // dynamic color setting:
        setColor(getBackgroundColor(percentToExit, true));
        addFilledRectangle(0, 0, viewWidth, viewHeight/2);
        // dynamic color setting:
        //setColor(getBackgroundColor(percentToExit, false));
        // define a matrix to map the shader to the ground
        Matrix shaderMatrix = new Matrix();
        float[] points = {0, (float) (viewHeight/2.0), viewWidth, (float) (viewHeight/2.0)};
        shaderMatrix.mapPoints(points);
        floorShader.setLocalMatrix(shaderMatrix);
        paint.setShader(floorShader);
        addFilledRectangle(0, viewHeight/2, viewWidth, viewHeight/2);
        paint.setShader(null);
    }

    /**
     * Determine the background color for the top and bottom
     * rectangle as a blend between starting color settings
     * of black and grey towards gold and green as final
     * color settings close to the exit
     * @param percentToExit percent of the maze remaining to reach the exit
     * @param top is true for the top triangle, false for the bottom
     * @return the color to use for the background rectangle
     */
    private int getBackgroundColor(float percentToExit, boolean top) {
        //return top ? blend(MazePanel.yellowWM, MazePanel.goldWM, percentToExit) :
                //blend(MazePanel.LIGHT_GRAY, MazePanel.greenWM, percentToExit);
        return top ? blend(MazePanel.darkPrimary, MazePanel.lightPrimary, percentToExit) :
                blend(MazePanel.LIGHT_GRAY, MazePanel.greenWM, percentToExit);
    }

    /**
     * Calculates the weighted average of the two given colors
     * @param c0 the one color
     * @param c1 the other color
     * @param weight0 of c0
     * @return blend of colors c0 and c1 as weighted average
     */
    private int blend(int c0, int c1, double weight0) {
        if (weight0 < 0.1)
            return c1;
        if (weight0 > 0.95)
            return c0;

        int r = (int) (weight0 * Color.red(c0) + (1-weight0) * Color.red(c1));
        int g = (int) (weight0 * Color.green(c0) + (1-weight0) * Color.green(c1));
        int b = (int) (weight0 * Color.blue(c0) + (1-weight0) * Color.blue(c1));

        return Color.rgb(r, g, b);
    }

    /**
     * Adds a filled rectangle.
     * The rectangle is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis.
     * Substitute for Canvas.drawRect() method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the rectangle
     * @param height is the height of the rectangle
     */
    @Override
    public void addFilledRectangle(int x, int y, int width, int height) {
        bufferCanvas.drawRect(x, y, x+width, y+height, paint);
    }


    public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {

    }

    /**
     * Adds a filled wall.
     * The wall is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * The wall will be textured with a custom bitmap shader.
     * @param xPoints are the x-coordinates of points for the wall
     * @param yPoints are the y-coordinates of points for the wall
     * @param nPoints is the number of points, the length of the arrays
     */
    public void addWall(int[] xPoints, int[] yPoints, int nPoints) {
        Path polygonPath = new Path();
        paint.setStrokeWidth(5);
        // define a matrix to map the shader to the wall
        Matrix shaderMatrix = new Matrix();
        float[] points = new float[nPoints * 2];
        for (int i = 0; i < nPoints; i++) {
            points[i] = xPoints[i];
            points[i+1] = yPoints[i];
        }
        shaderMatrix.mapPoints(points);
        wallShader.setLocalMatrix(shaderMatrix);
        // apply the shader to the paint
        paint.setShader(wallShader);
        polygonPath.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            polygonPath.lineTo(xPoints[i], yPoints[i]);
        }
        polygonPath.close();
        bufferCanvas.drawPath(polygonPath, paint);
        paint.setShader(null);
    }

    /**
     * Adds a polygon.
     * The polygon is not filled.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * Substitute for Canvas.drawPolygon method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Path polygonPath = new Path();
        paint.setStrokeWidth(5);
        polygonPath.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            polygonPath.lineTo(xPoints[i], yPoints[i]);
        }
        polygonPath.close();
        bufferCanvas.drawPath(polygonPath, paint);
    }

    /**
     * Adds a line.
     * A line is described by {@code (x,y)} coordinates for its
     * starting point and its end point.
     * Substitute for Canvas.drawLine method
     * @param startX is the x-coordinate of the starting point
     * @param startY is the y-coordinate of the starting point
     * @param endX is the x-coordinate of the end point
     * @param endY is the y-coordinate of the end point
     */
    @Override
    public void addLine(int startX, int startY, int endX, int endY) {
        paint.setStrokeWidth(5);
        bufferCanvas.drawLine(startX, startY, endX, endY, paint);
    }

    /**
     * Adds a filled oval.
     * The oval is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis. An oval is
     * described like a rectangle.
     * Substitute for Canvas.drawOval method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the oval
     * @param height is the height of the oval
     */
    @Override
    public void addFilledOval(int x, int y, int width, int height) {
        bufferCanvas.drawOval(x, y, x+width, y+height, paint);
    }

    /**
     * Adds the outline of a circular or elliptical arc covering the specified rectangle.
     * The resulting arc begins at startAngle and extends for arcAngle degrees,
     * using the current color. Angles are interpreted such that 0 degrees
     * is at the 3 o'clock position. A positive value indicates a counter-clockwise
     * rotation while a negative value indicates a clockwise rotation.
     * The center of the arc is the center of the rectangle whose origin is
     * (x, y) and whose size is specified by the width and height arguments.
     * The resulting arc covers an area width + 1 pixels wide
     * by height + 1 pixels tall.
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the start
     * and end of the arc segment will be skewed farther along the longer
     * axis of the bounds.
     * Substitute for Canvas.drawArc method
     * @param x the x coordinate of the upper-left corner of the arc to be drawn.
     * @param y the y coordinate of the upper-left corner of the arc to be drawn.
     * @param width the width of the arc to be drawn.
     * @param height the height of the arc to be drawn.
     * @param startAngle the beginning angle.
     * @param arcAngle the angular extent of the arc, relative to the start angle.
     */
    @Override
    public void addArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        //graphics.drawArc(x, y, width, height, startAngle, arcAngle);
        bufferCanvas.drawArc(x, y, x+width, y+height, startAngle, arcAngle, true, paint);
    }

    /**
     * Adds a string at the given position.
     * Substitute for CompassRose.drawMarker method
     * @param x the x coordinate
     * @param y the y coordinate
     * @param str the string
     */
    @Override
    public void addMarker(float x, float y, String str) {
        Rect rect = new Rect();
        paint.setTextSize(60);
        paint.getTextBounds(str, 0, str.length(), rect);
        x -= rect.width() / 2.0;
        y += rect.height() / 2.0;
        bufferCanvas.drawText(str, x, y, paint);
    }
}
