package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import edu.wm.cs.cs301.duketran.generation.Factory;
import edu.wm.cs.cs301.duketran.generation.Maze;
import edu.wm.cs.cs301.duketran.generation.MazeFactory;
import edu.wm.cs.cs301.duketran.generation.MazeSingleton;
import edu.wm.cs.cs301.duketran.generation.Order;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;import android.content.Intent;

/**
 * Class: GeneratingActivity
 * <br>
 * Responsibilities: intermediate screen while maze is generating, user can select a driver
 * and robot, navigates back to AMazeActivity (which then navigates to a Play activity)
 * <br>
 * Collaborators: AMazeActivity
 */
public class GeneratingActivity extends AppCompatActivity implements Runnable, Order {
    private Handler handler;
    private Thread generationThread;

    private int seed;
    private int skillLevel;
    private Builder builder;
    private boolean perfect;

    private Factory factory;
    private int currentProgress;

    /**
     * Overrides the onCreate method of Activity, starts the background animation and maze generation
     * thread, fills the driver and robot spinners with their options, sets up listeners for the
     * driver spinner and play button
     * @param savedInstanceState the bundle object containing the saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);
        // instantiate fields
        reset();
        // start the background animation
        AnimationDrawable progressAnimation = (AnimationDrawable) findViewById(R.id.parentView).getBackground();
        progressAnimation.start();
        // start the maze generation thread
        handler = new Handler();
        generationThread = new Thread(this);
        generationThread.start();
        // set up the UI components
        setUpComponents();
    }

    /**
     * Resets the values of the fields once the activity is dismissed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        reset();
    }

    /**
     * If the user presses the back button, the maze generation thread is stopped and the title
     * activity is notified that the intent was canceled
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // stop the maze generation thread
        factory.cancel();
        generationThread.interrupt();
        // send the canceled intent result back to the title screen
        setResult(RESULT_CANCELED, null);
        finish();
        Log.v("Maze Generation", "Cancelling generation");
    }

    /**
     * Resets all the fields to the default values
     */
    private void reset() {
        factory = new MazeFactory() ;
        skillLevel = 0; // default size for maze
        builder = Order.Builder.DFS; // default algorithm
        perfect = false; // default: maze can have rooms
        currentProgress = 0;
        seed = 13; // default: an arbitrary fixed value
        View minotaurProgress = findViewById(R.id.minotaurProgress);
        minotaurProgress.getBackground().setLevel(0);
    }

    /**
     * Sets up the UI components for the spinners and play button
     */
    private void setUpComponents() {
        // set up the spinners with their respective options
        setUpSpinners();
        // set up a listener for the play button to start the game when clicked
        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // obtain the selected driver
                Spinner driverSpinner = findViewById(R.id.driverSpinner);
                String driver = driverSpinner.getSelectedItem().toString();
                // obtain the selected robot (Demigod has 4 reliable sensors, Warrior has unreliable
                // left and right sensors, Captain has unreliable front and back sensors, Soldier
                // has 4 unreliable sensors)
                Spinner robotSpinner = findViewById(R.id.robotSpinner);
                String robot = robotSpinner.getSelectedItem().toString().split(" ")[0];

                // create a new intent containing the selected driver, robot, and seed
                Intent result = new Intent(GeneratingActivity.this, AMazeActivity.class);
                result.putExtra("Driver", driver);
                result.putExtra("Robot", robot);
                result.putExtra("Seed", seed);
                // send the result back to the title activity
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    /**
     * Fills the spinners with their respective options
     */
    private void setUpSpinners() {
        // fill the driver spinner with driver options
        Spinner driverSpinner = findViewById(R.id.driverSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.driver, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(adapter);
        // fill the robot spinner with robot options
        Spinner robotSpinner = findViewById(R.id.robotSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.robot, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        robotSpinner.setAdapter(adapter);
    }

    @Override
    public int getSkillLevel() {
        return skillLevel;
    }

    @Override
    public Builder getBuilder() {
        return builder;
    }

    @Override
    public boolean isPerfect() {
        return perfect;
    }

    @Override
    public int getSeed() {
        return seed;
    }

    @Override
    public void deliver(Maze mazeConfig) {
        // use a singleton to assign and deliver the maze
        MazeSingleton.getInstance().setMaze(mazeConfig);
    }

    /**
     * Updates the progress bar of the UI
     * @param percentage of maze generation
     */
    @Override
    public void updateProgress(int percentage) {
        if (currentProgress < percentage && percentage <= 100) {
            currentProgress = percentage;
            View minotaurProgress = findViewById(R.id.minotaurProgress);
            minotaurProgress.getBackground().setLevel(currentProgress*100);
        }
    }

    /**
     * Part of the thread that controls the maze generation
     */
    @Override
    public void run() {
        // fetch the intent sent from the title screen containing the skill level, builder, and rooms
        Bundle generationSettings = getIntent().getExtras();
        skillLevel = generationSettings.getInt("SkillLevel", 0);
        switch (generationSettings.getString("Builder")) {
            case "Random":
                builder = Builder.DFS;
                break;
            case "Prim":
                builder = Builder.Prim;
                break;
            case "Eller":
                builder = Builder.Eller;
                break;
        }
        perfect = !generationSettings.getBoolean("Rooms", false);
        seed = generationSettings.getInt("Seed");
        // order the maze factory to start generating the maze
        factory.order(this);
        Log.v("SkillLevel", ""+skillLevel);
        Log.v("Builder", ""+builder);
        Log.v("Rooms", ""+perfect);
        Log.v("Seed", ""+seed);

        // once the progress reaches 100, update the UI to show the play button
        factory.waitTillDelivered();
        handler.post(new Runnable() {
            public void run() {
                Log.v("Maze Generation", "Done!");
                assert (MazeSingleton.getInstance().getMaze() != null) : "Maze Generation Failed!";
                Button playButton = findViewById(R.id.playButton);
                playButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
