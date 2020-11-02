package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

/**
 * Class: PlayActivity
 * <br>
 * Responsibilities: parent class of the different play activities (states - Manually/Animation)
 * <br>
 * Collaborators: None
 */
public class PlayActivity extends AppCompatActivity {
    protected StatePlaying statePlaying;
    protected Intent winningState;
    protected RobotDriver driver = null;

    protected boolean showMap = false;
    protected boolean showSolution = false;
    protected boolean showAllWalls = false;

    protected int zoom = 100;
    protected int shortestPath = 69;

    /**
     * Public accessor for the robot driver
     * @return driver
     */
    public RobotDriver getDriver() {
        return driver;
    }

    /**
     * Updates the path length text view on the UI
     */
    protected void setPathLength(int pathLength) {
        Resources res = getResources();
        String pathLengthString = res.getString(R.string.pathLengthText);
        TextView pathLengthText = findViewById(R.id.pathLengthText);
        pathLengthText.setText(String.format(pathLengthString, pathLength));
    }

    /**
     * Sets up the menu button with a listener
     * @param context activity in which the popup will be generated and bound to
     */
    protected void setUpMenuButton(final Context context) {
        ImageView menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> {
            // create a new popup menu within the inputted context
            PopupMenu popup = new PopupMenu(context, v);
            // set up the listener for the popup
            setUpPopup(popup);
            // inflate (set up) the menu and show the popup menu
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_game, popup.getMenu());
            popup.show();

            // update the menu with the options that have been previously defined or set
            // (since the menu is automatically reset and closed after a selection is made)
            Menu menu = popup.getMenu();
            menu.findItem(R.id.showMapItem).setChecked(showMap);
            menu.findItem(R.id.showSolutionItem).setChecked(showSolution);
            menu.findItem(R.id.showAllWallsItem).setChecked(showAllWalls);
        });
    }

    /**
     * Sets up the popup by with a listener
     * @param popup PopUpMenu object for which we'll be setting up the listener
     */
    private void setUpPopup(PopupMenu popup) {
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.showMapItem) {
                // toggle the field and update the UI to reflect the change
                showMap = !showMap;
                item.setChecked(showMap);
                statePlaying.keyDown(Constants.UserInput.ToggleLocalMap, 0);
                Log.v("Show map", "" + showMap);
                return true;
            } else if (id == R.id.showSolutionItem) {
                showSolution = !showSolution;
                item.setChecked(showSolution);
                statePlaying.keyDown(Constants.UserInput.ToggleSolution, 0);
                Log.v("Show solution", "" + showSolution);
                return true;
            } else if (id == R.id.showAllWallsItem) {
                showAllWalls = !showAllWalls;
                item.setChecked(showAllWalls);
                statePlaying.keyDown(Constants.UserInput.ToggleFullMap, 0);
                Log.v("Show all walls", "" + showAllWalls);
                return true;
            } else return false;
        });
    }

    /**
     * Sets up the zoom buttons with listeners
     */
    protected void setUpZoomButtons() {
        ImageView zoomInButton = findViewById(R.id.zoomInButton);
        zoomInButton.setOnClickListener(v -> {
            // increase the zoom on click
            zoom += 5;
            statePlaying.keyDown(Constants.UserInput.ZoomIn, 0);
            Log.v("Zoom", ""+zoom);
        });
        ImageView zoomOutButton = findViewById(R.id.zoomOutButton);
        zoomOutButton.setOnClickListener(v -> {
            // decrease the zoom on click
            zoom -= 5;
            statePlaying.keyDown(Constants.UserInput.ZoomOut, 0);
            Log.v("Zoom", ""+zoom);
        });
    }

    public void switchToWinning(Context context, int distTraveled) {
        winningState = new Intent(context, WinningActivity.class);
        winningState.putExtra("Path Length", distTraveled);
        winningState.putExtra("Shortest Path", shortestPath);
        Log.v("Distance Traveled", ""+distTraveled);
    }
}
