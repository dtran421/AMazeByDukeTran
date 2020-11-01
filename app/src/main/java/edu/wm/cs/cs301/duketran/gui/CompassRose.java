package edu.wm.cs.cs301.duketran.gui;

import android.util.Log;

import edu.wm.cs.cs301.duketran.generation.CardinalDirection;

/**
 * A component that draws a compass rose.  This class has no other functionality, but superclasses
 * may add functionality to it.
 * 
 * @author Sampo Niskanen <sampo.niskanen@iki.fi>
 * Code copied from http://www.soupwizard.com/openrocket/code-coverage/eclemma-20121216/OpenRocket/src/net.sf.openrocket.gui.components.compass/CompassRose.java.html
 * adjusted for Maze setting by
 * @author Peter Kemper
 */
public class CompassRose {
	private static final long serialVersionUID = 1916497172430988388L;
	//private static final Color MAIN_COLOR = greenWM; //new Color(0.4f, 0.4f, 1.0f);
	private static final int MAIN_COLOR = MazePanel.greenWM;
    private static final float MAIN_LENGTH = 0.95f;
    private static final float MAIN_WIDTH = 0.15f;
    
    private static final int CIRCLE_BORDER = 2;
    private static final int CIRCLE_HIGHLIGHT = MazePanel.WHITE;
    private static final int CIRCLE_SHADE = MazePanel.WHITE;
    		
    //private static final Color MARKER_COLOR = Color.black; //Color.WHITE; //Color.BLACK;
    private static final int MARKER_COLOR = MazePanel.BLACK;
    
    private double scaler;
    
    private double markerRadius;
    private String markerFont;
    
    // (x,y) coordinates of center point on overall area
    private int centerX; // x coordinate of center point
    private int centerY; // y coordinate of center point
    private int size; // size of compass rose
    private CardinalDirection currentDir; 
    
    /**
     * Construct a compass rose with the default settings.
     */
    public CompassRose(MazePanel mazePanel) {
    	this(mazePanel, 0.9, 1.7, "Serif-PLAIN-16");
    }
    
    /**
     * Construct a compass rose with the specified settings.
     * 
     * @param scaler        The scaler of the rose.  The bordering circle will be this portion of the component dimensions.
     * @param markerRadius  The radius for the marker positions (N/E/S/W), or NaN for no markers.  A value greater than one
     *                      will position the markers outside of the bordering circle.
     * @param markerFont    The font used for the markers.
     */
    public CompassRose(MazePanel mazePanel, double scaler, double markerRadius, String markerFont) {
        this.scaler = scaler;
        this.markerRadius = markerRadius;
        setMarkerFont(mazePanel, markerFont);
    }
    
    public void setPositionAndSize(int x, int y, int size) {
    	centerX = x;
    	centerY = y;
    	this.size = size;
    }
    /**
     * Set the current direction such that it can
     * be highlighted on the display
     * @param cd
     */
    public void setCurrentDirection(CardinalDirection cd) {
    	currentDir = cd;
    }

    public void paintComponent(MazePanel mazePanel) {                
        /* Original code
        Dimension dimension = this.getSize();
        int width = Math.min(dimension.width, dimension.height);
        int mid = width / 2;
        width = (int) (scaler * width);
        */
        int width = (int) (scaler * size);
        final int mainLength = (int) (width * MAIN_LENGTH / 2);
        final int mainWidth = (int) (width * MAIN_WIDTH / 2);
        Log.v("CR", mainLength+" "+mainWidth);

        drawBorderCircle(mazePanel, width);

        // draw background disc
        drawBackground(mazePanel, width);
        // draw main part in all 4 directions in same color
        // x, y arrays used for drawing polygons
        // starting point is always (centerX, centerY)
        //g2.setColor(MAIN_COLOR);
        mazePanel.setColor(MAIN_COLOR);
        final int[] x = new int[3];
        final int[] y = new int[3];
        x[0] = centerX;
        y[0] = centerY;
        Log.v("CR Center", centerX+" "+centerY);
        drawMainNorth(mazePanel, mainLength, mainWidth, x, y);
        drawMainEast(mazePanel, mainLength, mainWidth, x, y);
        drawMainSouth(mazePanel, mainLength, mainWidth, x, y);
        drawMainWest(mazePanel, mainLength, mainWidth, x, y);

        drawDirectionMarker(mazePanel, width);
    }


	private void drawBackground(MazePanel mazePanel, int width) {
		mazePanel.setColor(MazePanel.WHITE);
		final int x = centerX - size;
		final int y = centerY - size;
		final int w = 2 * size;// - 2 * CIRCLE_BORDER;
        mazePanel.addFilledOval(x,y,w,w);
	}


	private void drawMainWest(MazePanel mazePanel, int length, int width, int[] x, int[] y) {
		x[1] = centerX - length;
        y[1] = centerY;
        x[2] = centerX - width;
        y[2] = centerY + width;
        mazePanel.addFilledPolygon(x, y, 3);

        y[2] = centerY - width;
        mazePanel.addPolygon(x, y, 3);
	}
	private void drawMainEast(MazePanel mazePanel, int length, int width, int[] x, int[] y) {
		// observation: the 2 triangles to the right are drawn the same
		// way as for the left if one inverts the sign for length and width
		// i.e. exchanges addition and subtraction
		drawMainWest(mazePanel, -length, -width, x, y);
	}
	private void drawMainSouth(MazePanel mazePanel, int length, int width, int[] x, int[] y) {
		x[1] = centerX;
        y[1] = centerY + length;
        x[2] = centerX + width;
        y[2] = centerY + width;
        mazePanel.addFilledPolygon(x, y, 3);
        
        x[2] = centerX - width;
        mazePanel.addPolygon(x, y, 3);
	}
	private void drawMainNorth(MazePanel mazePanel, int length, int width, int[] x, int[] y) {
		// observation: the 2 triangles to the top are drawn the same
		// way as for the bottom if one inverts the sign for length and width
		// i.e. exchanges addition and subtraction
		drawMainSouth(mazePanel, -length, -width, x, y);
	}

	private void drawBorderCircle(MazePanel mazePanel, int width) {
		final int x = centerX - width / 2 + CIRCLE_BORDER;
		final int y = centerY - width / 2 + CIRCLE_BORDER;
		final int w = width - 2 * CIRCLE_BORDER;
		mazePanel.setColor(CIRCLE_SHADE);
        mazePanel.addArc(x, y, w, w, 45, 180);
        mazePanel.setColor(CIRCLE_HIGHLIGHT);
        mazePanel.addArc(x, y, w, w, 180 + 45, 180);
	}


	private void drawDirectionMarker(MazePanel mazePanel, int width) {
		if (!Double.isNaN(markerRadius) && mazePanel.getFont() != null) {
            
            int pos = (int) (width * markerRadius / 2);
            
            mazePanel.setColor(MARKER_COLOR);
            /* original code
            drawMarker(g2, mid, mid - pos, trans.get("lbl.north"));
            drawMarker(g2, mid + pos, mid, trans.get("lbl.east"));
            drawMarker(g2, mid, mid + pos, trans.get("lbl.south"));
            drawMarker(g2, mid - pos, mid, trans.get("lbl.west"));
            */
            /* version with color highlighting but stable orientation 
             * Highlighting with MarkerColor which is white
             * use gold as color for others */
            // WARNING: north south confusion
            // currendDir South is going upward on the map
            if (CardinalDirection.South == currentDir)
            	mazePanel.setColor(MARKER_COLOR);
            else
            	mazePanel.setColor(MazePanel.goldWM);
            drawMarker(mazePanel, centerX, centerY - pos, "N");
            if (CardinalDirection.East == currentDir)
            	mazePanel.setColor(MARKER_COLOR);
            else
            	mazePanel.setColor(MazePanel.goldWM);
            drawMarker(mazePanel, centerX + pos, centerY, "E");
            // WARNING: north south confusion
            // currendDir North is going downwards on the map
            if (CardinalDirection.North == currentDir)
            	mazePanel.setColor(MARKER_COLOR);
            else
            	mazePanel.setColor(MazePanel.goldWM);
            drawMarker(mazePanel, centerX, centerY + pos, "S");
            if (CardinalDirection.West == currentDir)
            	mazePanel.setColor(MARKER_COLOR);
            else
            	mazePanel.setColor(MazePanel.goldWM);
            drawMarker(mazePanel, centerX - pos, centerY, "W");
        }
	}
 
    private void drawMarker(MazePanel mazePanel, float x, float y, String str) {
        mazePanel.addMarker(x, y, str);
    }
 
    public double getScaler() {
        return scaler;
    }
    
    public void setScaler(double scaler) {
        this.scaler = scaler;
        //repaint();
    }
    
    public double getMarkerRadius() {
        return markerRadius;
    }
    
    public void setMarkerRadius(double markerRadius) {
        this.markerRadius = markerRadius;
    }
    
    public String getMarkerFont() {
        return markerFont;
    }
    
    
    public void setMarkerFont(MazePanel mazePanel, String markerFont) {
        this.markerFont = markerFont;
        mazePanel.setFont(markerFont);
        mazePanel.update();
    }
}
