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
        // obtain the intent containing the driver (no robot needed in manual mode) and maze sent from
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
        // for left and right, the robot should rotate (no steps taken)
        if (dir == Constants.UserInput.Left || dir == Constants.UserInput.Right) {
            button.setOnClickListener(v -> {
                statePlaying.keyDown(dir, 0);
                Log.v("Turn", dir.toString());
            });
        }
        // for forward and backward, update the path length if needed
        else {
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

    /**
     * Prepares an intent with the game data (path length and shortest path)
     * and starts the winning activity
     * @param context this
     * @param winning always true (since it's manual)
     * @param distTraveled by the manual player
     */
    @Override
    public void switchToEndgame(Context context, boolean winning, int distTraveled) {
        super.switchToEndgame(context, true, distTraveled);
        // set the play mode to manual in the intent
        endgameData.putExtra("Manual", true);
        Log.v("Manual Play", "Proceeding to WinningActivity");
        // create a new toast to notify the user that they've won and start the winning activity
        Toast toast = Toast.makeText(PlayManuallyActivity.this, "You survived!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 50);
        toast.show();
        startActivity(endgameData);
        finish();
    }
}
