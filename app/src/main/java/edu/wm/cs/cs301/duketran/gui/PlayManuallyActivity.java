package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import edu.wm.cs.cs301.duketran.generation.MazeSingleton;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
        Bundle gameSettings = getIntent().getExtras();
        Log.v("Game driver", gameSettings.getString("Driver"));
        statePlaying = new StatePlaying();
        statePlaying.setMazeConfiguration(MazeSingleton.getInstance().getMaze());
        statePlaying.start(this, findViewById(R.id.mazePanel));
        // set up the path length text view and UI buttons
        setPathLength(statePlaying.distTraveled);
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
    }

    /**
     * Sets up the movement buttons with listeners
     */
    private void setUpMoveButtons() {
        ImageView forwardButton = findViewById(R.id.forwardButton);
        setUpMovementButton(forwardButton, Constants.UserInput.Up);
        ImageView rightButton = findViewById(R.id.rightButton);
        setUpMovementButton(rightButton, Constants.UserInput.Right);
        ImageView backButton = findViewById(R.id.backwardButton);
        setUpMovementButton(backButton, Constants.UserInput.Down);
        ImageView leftButton = findViewById(R.id.leftButton);
        setUpMovementButton(leftButton, Constants.UserInput.Left);
        ImageView jumpButton = findViewById(R.id.jumpButton);
        setUpMovementButton(jumpButton, Constants.UserInput.Jump);
    }

    /**
     * Sets up a listener for the given button
     * @param button the ImageView object of the button being set up
     */
    private void setUpMovementButton(ImageView button, Constants.UserInput dir) {
        if (dir == Constants.UserInput.Left || dir == Constants.UserInput.Right) {
            button.setOnClickListener(v -> {
                statePlaying.keyDown(dir, 0);
                Log.v("Turn", dir.toString());
            });
        } else {
            button.setOnClickListener(v -> {
                int origDist = statePlaying.distTraveled;
                statePlaying.keyDown(dir, 0);
                Log.v("Move", dir.toString());
                // update the path length after successful move
                if (statePlaying.distTraveled > origDist) {
                    setPathLength(statePlaying.distTraveled);
                }
            });
        }
    }

    @Override
    public void switchToWinning(Context context, int distTraveled) {
        super.switchToWinning(context, distTraveled);
        // create a new intent containing the play mode, the path length, and the shortest path
        winningState.putExtra("Manual", true);
        Log.v("Manual Play", "Proceeding to WinningActivity");
        // create a new toast to notify the user that they've won and start the winning activity
        Toast toast = Toast.makeText(PlayManuallyActivity.this, "You escaped!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 50);
        toast.show();
        startActivity(winningState);
        finish();
    }
}
