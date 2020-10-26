package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

/**
 * Class: LosingActivity
 * <br>
 * Responsibilities: displays the losing screen along with path length and energy consumption,
 * allows the user to navigate back to the title screen  and play again
 * <br>
 * Collaborators: AMazeActivity, PlayManuallyActivity, PlayAnimationActivity
 */
public class LosingActivity extends EndgameActivity {

    /**
     * Overrides the onCreate method of Activity, starts the background animation,
     * and sets up the UI components
     * @param savedInstanceState the bundle object containing the saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_losing);
        // start the background animation
        AnimationDrawable progressAnimation = (AnimationDrawable) findViewById(R.id.parentView).getBackground();
        progressAnimation.start();
        // set up the UI components to display the game data
        setUpComponents(this, false);
    }
}
