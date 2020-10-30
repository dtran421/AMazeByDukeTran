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

import java.util.Objects;

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
    private boolean started;

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
        // set up a listener for the driver spinner to determine when to show the robot layout
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toggleRobotLayout(parent.getItemAtPosition(position).toString());
            }
            public void onNothingSelected(AdapterView<?> parent) {
                toggleRobotLayout(parent.getSelectedItem().toString());
            }
        });

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

                // create a new intent containing the selected driver and robot
                Intent result = new Intent(GeneratingActivity.this, AMazeActivity.class);
                result.putExtra("Driver", driver);
                result.putExtra("Robot", robot);
                // send the result back to the title activity
                GeneratingActivity.this.setResult(RESULT_OK, result);
                GeneratingActivity.this.finish();
            }
        });
    }

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
        generationThread.interrupt();
        factory.cancel();
        // send the canceled intent result back to the title screen
        GeneratingActivity.this.setResult(RESULT_CANCELED, null);
        GeneratingActivity.this.finish();
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
        started = false;
        seed = 13; // default: an arbitrary fixed value
        View minotaurProgress = findViewById(R.id.minotaurProgress);
        minotaurProgress.getBackground().setLevel(0);
    }

    /**
     * Toggles the robot layout based on which drier was selected
     * @param selectedItem the driver that was selected
     */
    private void toggleRobotLayout(String selectedItem) {
        LinearLayout robotLayout = findViewById(R.id.robotLayout);
        // if the selected driver is Wallfollwer, then allow the user to select a robot
        if(!selectedItem.equals("Wallfollower")) robotLayout.setVisibility(View.INVISIBLE);
        // else the robot is assumed to be reliable, so the user doesn't have to select one
        else robotLayout.setVisibility(View.VISIBLE);
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
        Intent intent = getIntent();
        skillLevel = intent.getIntExtra("SkillLevel", 0);
        switch (Objects.requireNonNull(intent.getStringExtra("Builder"))) {
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
        perfect = !intent.getBooleanExtra("Rooms", false);
        factory.order(this);
        Log.v("SkillLevel", ""+skillLevel);
        Log.v("Builder", ""+builder);
        Log.v("Rooms", ""+perfect);

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
