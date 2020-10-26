package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    protected boolean showMap = false;
    protected boolean showSolution = false;
    protected boolean showAllWalls = false;

    protected int zoom = 100;
    protected int pathLength = 0;
    protected int shortestPath = 69;

    /**
     * Updates the path length text view on the UI
     */
    protected void setPathLength() {
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
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new popup menu within the inputted context
                PopupMenu popup = new PopupMenu(context, v);
                // set up the listener for the popup
                setUpPopup(context, popup);
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
            }
        });
    }

    /**
     * Sets up the popup by with a listener
     * @param context context in which the popup will be created and bound to
     * @param popup PopUpMenu object for which we'll be setting up the listener
     */
    private void setUpPopup(final Context context, PopupMenu popup) {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast toast;
                switch (item.getItemId()) {
                    case R.id.showMapItem:
                        // toggle the field and update the UI to reflect the change
                        showMap = !showMap;
                        item.setChecked(showMap);
                        Log.v("Show map", ""+showMap);
                        toast = Toast.makeText(context, "Toggling map", Toast.LENGTH_SHORT);
                        toast.show();
                        return true;
                    case R.id.showSolutionItem:
                        showSolution = !showSolution;
                        item.setChecked(showSolution);
                        Log.v("Show solution", ""+showSolution);
                        toast = Toast.makeText(context, "Toggling solution", Toast.LENGTH_SHORT);
                        toast.show();
                        return true;
                    case R.id.showAllWallsItem:
                        showAllWalls = !showAllWalls;
                        item.setChecked(showAllWalls);
                        Log.v("Show all walls", ""+showAllWalls);
                        toast = Toast.makeText(context, "Toggling walls", Toast.LENGTH_SHORT);
                        toast.show();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    /**
     * Sets up the zoom buttons with listeners
     */
    protected void setUpZoomButtons() {
        ImageView zoomInButton = findViewById(R.id.zoomInButton);
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // increase the zoom on click
                zoom += 5;
                Log.v("Zoom", ""+zoom);
            }
        });
        ImageView zoomOutButton = findViewById(R.id.zoomOutButton);
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // decrease the zoom on click
                zoom -= 5;
                Log.v("Zoom", ""+zoom);
            }
        });
    }
}
