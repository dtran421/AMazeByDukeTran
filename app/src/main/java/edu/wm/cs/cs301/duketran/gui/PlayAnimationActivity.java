package edu.wm.cs.cs301.duketran.gui;

import java.util.Objects;

import edu.wm.cs.cs301.duketran.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.slider.Slider;

/**
 * Class: PlayAnimationActivity
 * <br>
 * Responsibilities: animates the driver playing through the maze with the selected robot
 * <br>
 * Collaborators: AMazeActivity, WinningActivity, LosingActivity
 */
public class PlayAnimationActivity extends PlayActivity {
    private final float origEnergyLevel = 3500; // set this to the robot's initial battery level
    private float energyConsumption = 3000;

    private boolean isAnimating = true;
    private int animationSpeed = 1;

    /**
     * Overrides the onCreate method of Activity, sets up the path length text view and UI components
     * @param savedInstanceState the bundle object containing the saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);

        // obtain the intent containing the driver, robot, and maze sent from
        // the title activity
        Intent mazeGame = getIntent();
        Log.v("Game driver", Objects.requireNonNull(mazeGame.getStringExtra("Driver")));
        Log.v("Game robot", Objects.requireNonNull(mazeGame.getStringExtra("Robot")));
        Log.v("Game maze", Objects.requireNonNull(mazeGame.getStringExtra("Maze")));
        // sets up the path length text view and the UI components for controlling the animation
        setPathLength();
        setUpComponents();
    }

    /**
     * Sets up the components
     */
    private void setUpComponents() {
        // set up the menu and zoom buttons and the animation components
        setUpMenuButton(this);
        setUpZoomButtons();
        setUpAnimationComponents();
        // set up the endgame buttons with listeners
        /*
        setUpEndgameButton(true, (Button) findViewById(R.id.winningButton));
        setUpEndgameButton(false, (Button) findViewById(R.id.losingButton));*/
    }

    /**
     * Sets up the start/stop animation button,
     */
    private void setUpAnimationComponents() {
        // create a listener for the animation button to update when clicked on
        final Button animationButton = findViewById(R.id.animationButton);
        animationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggle the animation
                isAnimating = !isAnimating;
                // update the background color, text, and text color for start (red background,
                // white text) and stop (green background, black text)
                animationButton.setBackgroundColor(isAnimating ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational));
                animationButton.setText(isAnimating ? R.string.stopAnimationText : R.string.startAnimationText);
                animationButton.setTextColor(isAnimating ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorPrimary)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorPrimaryDark));
                Log.v("Currently animating", ""+isAnimating);
            }
        });
        // create a listener for the slider to update the animation speed once the user has selected
        // a new speed
        Slider animationSlider = findViewById(R.id.animationSpeedSlider);
        animationSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                animationSpeed = (int) slider.getValue();
                Log.v("Animation speed", ""+animationSpeed);
            }
        });
    }

    /**
     * Sets up the endgame shortcut buttons
     * @param winning whether the driver beat the maze or lost
     * @param endgameButton the ImageView object of the given endgame button
     */
    private void setUpEndgameButton(final boolean winning, Button endgameButton) {
        final Intent endgameState = winning ? new Intent(PlayAnimationActivity.this, WinningActivity.class)
                : new Intent(PlayAnimationActivity.this, LosingActivity.class);
        endgameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new intent containing the mode, path length, shortest path, and
                // energy consumption
                endgameState.putExtra("Manual", false);
                endgameState.putExtra("Path Length", pathLength);
                endgameState.putExtra("Shortest Path", shortestPath);
                endgameState.putExtra("Energy Consumption", energyConsumption);
                Log.v("Animation Play", winning ? "Proceeding to WinningActivity" : "Proceeding to LosingActivity");
                // create a new toast to notify the user whether the driver won or lost
                // and start the winning or losing activity
                Toast toast = Toast.makeText(PlayAnimationActivity.this,
                        winning ? "You escaped!" : "You died!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(endgameState);
                finish();
            }
        });
    }

    /**
     * Updates the energy consumption progress bar
     * @param energyLevel the current energy level of the robot
     */
    private void updateEnergyConsumption(float energyLevel) {
        energyConsumption = energyLevel;
        ProgressBar energyConsumptionBar = findViewById(R.id.energyConsumptionBar);
        energyConsumptionBar.setProgress((int) ((origEnergyLevel-energyConsumption)/origEnergyLevel));
    }

    /**
     * Updates the sensor display to reflect whether the robot is operational or under repair
     * @param sensor the sensor that is going to be updated (should replace with Direction in P7)
     * @param isOperational status of the sensor (operational=T/under repair=F)
     */
    private void updateSensorDisplay(String sensor, boolean isOperational) {
        switch(sensor) {
            case "Forward":
                View forwardSensor = findViewById(R.id.forwardSensor);
                forwardSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case "Left":
                View leftSensor = findViewById(R.id.leftSensor);
                leftSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case "Right":
                View rightSensor = findViewById(R.id.rightSensor);
                rightSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case "Backward":
                View backwardSensor = findViewById(R.id.backwardSensor);
                backwardSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
        }
    }
}
