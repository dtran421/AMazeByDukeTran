package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import edu.wm.cs.cs301.duketran.generation.Maze;
import edu.wm.cs.cs301.duketran.generation.MazeSingleton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
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
    private Handler animationHandler;
    private Runnable animation;

    private boolean isAnimating = true;
    private double animationSpeed = 940;

    private Robot robot;
    private float origEnergyLevel; // set this to the robot's initial battery level

    final private int MEAN_TIME_BETWEEN_FAILURES = 4000;
    final private int MEAN_TIME_TO_REPAIR = 2000;

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
        String driver = mazeGame.getStringExtra("Driver");
        String robot = mazeGame.getStringExtra("Robot");
        Log.v("Game driver", driver);
        Log.v("Game robot", robot);

        Maze maze = MazeSingleton.getInstance().getMaze();
        statePlaying = new StatePlaying();
        statePlaying.setMazeConfiguration(maze);
        setUpDriverAndRobot(statePlaying, maze, driver, robot);
        statePlaying.start(this, findViewById(R.id.mazePanel));
        animationHandler = new Handler();
        setUpAnimation();
        // sets up the path length text view and the UI components for controlling the animation
        setPathLength(this.driver.getPathLength());
        setUpComponents();
    }

    /**
     * If the user presses the back button, the handler is cleared and
     * everything is reset
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // clear the handler
        animationHandler.removeCallbacks(animation);
        Log.v("Play Animation", "Ending game");
        finish();
    }

    /**
     * Sets up the driver and robot fields
     * @param driver is the type of driver
     * @param robot is the type of robot
     */
    private void setUpDriverAndRobot(StatePlaying statePlaying, Maze maze, String driver, String robot) {
        if (driver.equals("Wizard")) {
            this.driver = new Wizard();
            this.robot = new ReliableRobot();
        } else {
            this.driver = new WallFollower();
            switch (robot) {
                case "Demigod":
                    this.robot = new UnreliableRobot(1, 1, 1, 1);
                    break;
                case "Warrior":
                    this.robot = new UnreliableRobot(1, 0, 0, 1);
                    break;
                case "Captain":
                    this.robot = new UnreliableRobot(0, 1, 1, 0);
                    break;
                case "Soldier":
                    this.robot = new UnreliableRobot(0, 0, 0, 0);
            }
            for (Robot.Direction direction: Robot.Direction.values())
                try {
                    this.robot.startFailureAndRepairProcess(direction, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
                    Thread.sleep(1300);
                } catch (Exception e) {
                    Log.v("Driver Setup", "Reliable Sensor, moving on...");
                }
        }
        this.robot.setController(statePlaying);
        this.driver.setRobot(this.robot);
        this.driver.setMaze(maze);
        // set the field for the initial energy level (for use in the energy progress bar)
        origEnergyLevel = this.robot.getBatteryLevel();
    }

    /**
     * Sets up the UI components
     */
    private void setUpComponents() {
        // set up the menu and zoom buttons and the animation components
        setUpMenuButton(this);
        setUpZoomButtons();
        setUpAnimationComponents();
    }

    /**
     * Sets up the start/stop animation button and the animation speed slider
     */
    private void setUpAnimationComponents() {
        // create a listener for the animation button to update when clicked on
        final Button animationButton = findViewById(R.id.animationButton);
        animationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toggle the animation
                isAnimating = !isAnimating;
                if (isAnimating) animationHandler.postDelayed(animation, (long) animationSpeed);
                else animationHandler.removeCallbacks(animation);

                // update the background color, text, and text color for start (red background,
                // white text) and stop (green background, black text)
                animationButton.setBackgroundColor(isAnimating ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational));
                animationButton.setText(isAnimating ? R.string.stopAnimationText : R.string.startAnimationText);
                animationButton.setTextColor(isAnimating ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorPrimary)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorPrimaryDark));
                Log.v("Currently animating", ""+isAnimating);
                Log.v("Animation speed", "" + animationSpeed);
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
                double newSpeed = ((int) slider.getValue() * -0.21 + 2.21) * 1000;
                if (animationSpeed != newSpeed) {
                    animationSpeed = newSpeed;
                    Log.v("Animation speed", "" + animationSpeed);
                    animationHandler.removeCallbacks(animation);
                    if (isAnimating) animationHandler.postDelayed(animation, (long) animationSpeed);
                }
            }
        });
    }

    /**
     * Defines the animation runnable and passes it to the animationHandler
     */
    public void setUpAnimation() {
        animation = () -> {
            try {
                driver.drive1Step2Exit();
                setPathLength(driver.getPathLength());
                updateEnergyConsumption(driver.getEnergyConsumption());
                for (Robot.Direction dir: Robot.Direction.values())
                    updateSensorDisplay(dir, isOperational(dir));
                if (robot.isAtExit()) {
                    ((Wizard)driver).crossExit2Win(robot.getCurrentPosition());
                    switchToEndgame(true);
                } else animationHandler.postDelayed(animation, (long) animationSpeed);
            } catch (Exception e) {
                Log.w("Driver Status", e.toString());
                switchToEndgame(false);
            }
        };
        animationHandler.postDelayed(animation, (long) animationSpeed);
    }

    /**
     * Sets up the endgame shortcut buttons
     * @param winning whether the driver beat the maze or lost
     */
    private void switchToEndgame(final boolean winning) {
        final Intent endgameState = winning ? new Intent(PlayAnimationActivity.this, WinningActivity.class)
                : new Intent(PlayAnimationActivity.this, LosingActivity.class);
        // create a new intent containing the mode, path length, shortest path, and
        // energy consumption
        endgameState.putExtra("Manual", false);
        endgameState.putExtra("Path Length", driver.getPathLength());
        endgameState.putExtra("Shortest Path", shortestPath);
        endgameState.putExtra("Energy Consumption", driver.getEnergyConsumption());
        Log.v("Animation Play", winning ? "Proceeding to WinningActivity" : "Proceeding to LosingActivity");
        // create a new toast to notify the user whether the driver won or lost
        // and start the winning or losing activity
        Toast toast = Toast.makeText(PlayAnimationActivity.this,
                winning ? "You escaped!" : "You died!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 50);
        toast.show();
        startActivity(endgameState);
        finish();
    }

    /**
     * Updates the energy consumption progress bar
     * @param energyConsumption the current energy level of the robot
     */
    private void updateEnergyConsumption(float energyConsumption) {
        ProgressBar energyConsumptionBar = findViewById(R.id.energyConsumptionBar);
        energyConsumptionBar.setProgress((int) (((origEnergyLevel-energyConsumption)/origEnergyLevel) * 100));
    }

    /**
     * Updates the sensor display to reflect whether the robot is operational or under repair
     * @param sensor the sensor that is going to be updated (should replace with Direction in P7)
     * @param isOperational status of the sensor (operational=T/under repair=F)
     */
    private void updateSensorDisplay(Robot.Direction sensor, boolean isOperational) {
        switch(sensor) {
            case FORWARD:
                View forwardSensor = findViewById(R.id.forwardSensor);
                forwardSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case LEFT:
                View leftSensor = findViewById(R.id.leftSensor);
                leftSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case RIGHT:
                View rightSensor = findViewById(R.id.rightSensor);
                rightSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
            case BACKWARD:
                View backwardSensor = findViewById(R.id.backwardSensor);
                backwardSensor.setBackgroundColor(isOperational ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair));
                break;
        }
    }

    /**
     * Determines if the sensor in the inputted direction is operational using the robot's distance to obstacle method
     * @param direction to check
     * @return whether it's operational
     */
    protected boolean isOperational(Robot.Direction direction) {
        try {
            // if no exception is thrown, then the sensor is operational
            float origEnergyLevel = robot.getBatteryLevel();
            robot.distanceToObstacle(direction);
            robot.setBatteryLevel(origEnergyLevel);
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }
}
