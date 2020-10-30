package edu.wm.cs.cs301.duketran.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.Nullable;

public class MazePanel extends View implements P5Panel {
    // bufferImage can only be initialized if the container is displayable,
    // uses a delayed initialization and relies on client class to call initBufferImage()
    // before first use
    //private Image bufferImage;
    private Bitmap bufferBitmap;
    //private Graphics2D graphics; // obtained from bufferImage,
    private Canvas bufferCanvas;
    private Paint paint;
    // graphics is stored to allow clients to draw on the same graphics object repeatedly
    // has benefits if color settings should be remembered for subsequent drawing operations

    //private static final Color greenWM = Color.decode("#115740");
    static final int greenWM = 1136448;
    //private static final Color goldWM = Color.decode("#916f41");
    static final int goldWM = 9531201;
    static final int yellowWM = 16777113;

    static final int WHITE = 16777215;
    static final int LIGHT_GRAY = 13421772;
    static final int GRAY = 10066329;
    static final int RED = 16711680;
    static final int YELLOW = 16776960;

    static final int RGB_DEF = 20;
    static final int RGB_DEF_GREEN = 60;

    /**
     * Dimensions for the FirstPersonView
     */
    private int viewWidth;
    private int viewHeight;

    private int currentColor;
    private Typeface currentFont;

    public MazePanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bufferBitmap = null;
        bufferCanvas = null;
        paint = null;
        update();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.v("Draw status", "Working...");
        canvas.drawColor(Color.LTGRAY);
        myTestImage(canvas);
    }

    private void myTestImage(Canvas c) {
        setColor(Color.RED);
        addFilledOval(0, 0, 100, 100);
        setColor(Color.GREEN);
        addFilledOval(0, 100, 100, 100);
        setColor(Color.YELLOW);
        addFilledRectangle(100, 0, 100, 100);
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
     * Method to draw the buffer image on a graphics object that is
     * obtained from the superclass.
     * Warning: do not override getGraphics() or drawing might fail.
     */
    public void update() {
        paint(getBufferCanvas());
    }

    /**
     * Draws the buffer image to the given graphics object.
     * This method is called when this panel should redraw itself.
     * The given graphics object is the one that actually shows
     * on the screen.
     */
    public void paint(Canvas canvas) {
        if (null == canvas) {
            Log.e("MazePanel.paint", "No canvas object, skipping drawImage operation");
        }
        else {
            canvas.drawBitmap(bufferBitmap, 0, 0, paint);
        }
    }

    /**
     * Obtains a graphics object that can be used for drawing.
     * This MazePanel object internally stores the graphics object
     * and will return the same graphics object over multiple method calls.
     * The graphics object acts like a notepad where all clients draw
     * on to store their contribution to the overall image that is to be
     * delivered later.
     * To make the drawing visible on screen, one needs to trigger
     * a call of the paint method, which happens
     * when calling the update method.
     * @return graphics object to draw on, null if impossible to obtain image
     */

    public Canvas getBufferCanvas() {
        if (null == bufferCanvas) {
            // if necessary instantiate and store a bitmap object for later use
            if (null == bufferBitmap) {
                // (Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
                bufferBitmap = Bitmap.createBitmap(350, 350, Bitmap.Config.ARGB_8888);
                if (null == bufferBitmap) {
                    Log.e("Buffer Bitmap Error", "Creation of buffered bitmap failed, presumably container not displayable");
                    return null; // still no buffer image, give up
                }
            }
            bufferCanvas = new Canvas(bufferBitmap);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        return bufferCanvas;
    }

    /**
     * Commits all accumulated drawings to the UI.
     * Substitute for MazePanel.update method.
     */
    @Override
    public void commit() {
        update();
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
        if (null == getBufferCanvas()) {
            Log.e("MazePanel", "Can't get buffer canvas object to draw on, skipping draw operation");
            return false;
        }
        return true;
    }

    /**
     * Sets the color for future drawing requests. The color setting
     * will remain in effect till this method is called again and
     * with a different color.
     * Substitute for Graphics.setColor method.
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
     * Sets the current font for the graphics
     * @param font name
     */
    public void setFont(String font) {
        currentFont = Typeface.create(font, Typeface.NORMAL);//paint.setTypeface(Typeface.create(font, Typeface.NORMAL));
    }

    /**
     * Provides the current font of the graphics
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
        // black rectangle in upper half of screen
        // graphics.setColor(Color.black);
        // dynamic color setting:
        setColor(getBackgroundColor(percentToExit, true));
        addFilledRectangle(0, 0, viewWidth, viewHeight/2);
        // grey rectangle in lower half of screen
        // graphics.setColor(Color.darkGray);
        // dynamic color setting:
        setColor(getBackgroundColor(percentToExit, false));
        addFilledRectangle(0, viewHeight/2, viewWidth, viewHeight/2);
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
        return top ? blend(MazePanel.yellowWM, MazePanel.goldWM, percentToExit) :
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
        String hex0 = String.format("%06X", c0);
        String hex1 = String.format("%06X", c1);

        int r = (int) (weight0 * ((int)Long.parseLong(hex0.substring(0, 2), 16)) +
                (1-weight0) * ((int)Long.parseLong(hex1.substring(0, 2), 16)));
        int g = (int) (weight0 * ((int)Long.parseLong(hex0.substring(2, 4), 16)) +
                (1-weight0) * ((int)Long.parseLong(hex1.substring(2, 4), 16)));
        int b = (int) (weight0 * ((int)Long.parseLong(hex0.substring(4, 6), 16)) +
                (1-weight0) * ((int)Long.parseLong(hex1.substring(4, 6), 16)));

        String newHex = String.format("%02X%02X%02X", r, g, b);
        return Integer.parseInt(newHex,16);
    }

    /**
     * Adds a filled rectangle.
     * The rectangle is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis.
     * Substitute for Graphics.fillRect() method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the rectangle
     * @param height is the height of the rectangle
     */
    @Override
    public void addFilledRectangle(int x, int y, int width, int height) {
        bufferCanvas.drawRect(x, y, x+width, y+height, paint);
    }

    /**
     * Adds a filled polygon.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * Substitute for Graphics.fillPolygon() method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        addPolygon(xPoints, yPoints, nPoints);
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
     * Substitute for Graphics.drawPolygon method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    @Override
    public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Path polygonPath = new Path();
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
     * Substitute for Graphics.drawLine method
     * @param startX is the x-coordinate of the starting point
     * @param startY is the y-coordinate of the starting point
     * @param endX is the x-coordinate of the end point
     * @param endY is the y-coordinate of the end point
     */
    @Override
    public void addLine(int startX, int startY, int endX, int endY) {
        bufferCanvas.drawLine(startX, startY, endX, endY, paint);
    }

    /**
     * Adds a filled oval.
     * The oval is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis. An oval is
     * described like a rectangle.
     * Substitute for Graphics.fillOval method
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
     * Substitute for Graphics.drawArc method
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
        //GlyphVector gv = markerFont.createGlyphVector(g2.getFontRenderContext(), str);
        /*
        GlyphVector gv = getFont().createGlyphVector(graphics.getFontRenderContext(), str);
        Rectangle2D rect = gv.getVisualBounds();

        x -= rect.getWidth() / 2;
        y += rect.getHeight() / 2;

        graphics.drawGlyphVector(gv, x, y);*/
        bufferCanvas.drawText(str, x, y, paint);
    }
}
