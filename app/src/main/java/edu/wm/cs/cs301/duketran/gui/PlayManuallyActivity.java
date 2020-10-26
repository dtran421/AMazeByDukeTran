package edu.wm.cs.cs301.duketran.gui;

import java.util.Objects;

import edu.wm.cs.cs301.duketran.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Class: PlayManuallyActivity
 * <br>
 * Responsibilities: allows user to play through the maze with their selected robot and driver
 * <br>
 * Collaborators: AMazeActivity, WinningActivity, LosingActivity
 */
public class PlayManuallyActivity extends PlayActivity {

    /**
     * Overrides the onCreate method of Activity, sets up the path length text view and UI buttons
     * @param savedInstanceState the bundle object containing the saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manual);
        // obtain the intent containing the driver (no robot in manual mode) and maze sent from
        // the title activity
        Intent mazeGame = getIntent();
        Log.v("Game driver", Objects.requireNonNull(mazeGame.getStringExtra("Driver")));
        Log.v("Game maze", Objects.requireNonNull(mazeGame.getStringExtra("Maze")));
        // set up the path length text view and UI buttons
        setPathLength();
        setUpButtons();
    }

    /**
     * Sets up the UI buttons for the menu, zoom, and movement
     */
    private void setUpButtons() {
        // set up the menu button, zoom buttons, and movement buttons
        setUpMenuButton(this);
        setUpZoomButtons();
        setUpMoveButtons();

        // set up the shortcut button to allow navigation to the winning activity
        Button shortcutButton = findViewById(R.id.shortcutButton);
        shortcutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new intent containing the play mode, the path length, and the shortest path
                Intent winningState = new Intent(PlayManuallyActivity.this, WinningActivity.class);
                winningState.putExtra("Manual", true);
                winningState.putExtra("Path Length", pathLength);
                winningState.putExtra("Shortest Path", shortestPath);
                Log.v("Manual Play", "Proceeding to WinningActivity");
                // create a new toast to notify the user that they've won and start the winning activity
                Toast toast = Toast.makeText(PlayManuallyActivity.this, "You escaped!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(winningState);
                finish();
            }
        });
    }

    /**
     * Sets up the movement buttons with listeners
     */
    private void setUpMoveButtons() {
        ImageView forwardButton = findViewById(R.id.forwardButton);
        setUpMovementButton(forwardButton);
        ImageView rightButton = findViewById(R.id.rightButton);
        setUpMovementButton(rightButton);
        ImageView backButton = findViewById(R.id.backwardButton);
        setUpMovementButton(backButton);
        ImageView leftButton = findViewById(R.id.leftButton);
        setUpMovementButton(leftButton);
        ImageView jumpButton = findViewById(R.id.jumpButton);
        setUpMovementButton(jumpButton);
    }

    /**
     * Sets up a listener for the given button
     * @param button the ImageView object of the button being set up
     */
    private void setUpMovementButton(ImageView button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update the path length when the given button is clicked
                pathLength += 1;
                setPathLength();
            }
        });
    }
}
