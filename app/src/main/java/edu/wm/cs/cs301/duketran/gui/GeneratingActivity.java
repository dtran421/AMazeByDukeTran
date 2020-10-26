package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;

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
public class GeneratingActivity extends AppCompatActivity implements Runnable {
    private Handler handler;
    private Thread generationThread;

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
                // obtain the selected robot
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

    /**
     * If the user presses the back button, the maze generation thread is stopped and the title
     * activity is notified that the intent was canceled
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // stop the maze generation thread
        generationThread.interrupt();
        // send the canceled intent result back to the title screen
        GeneratingActivity.this.setResult(RESULT_CANCELED, new Intent(GeneratingActivity.this, AMazeActivity.class));
        GeneratingActivity.this.finish();
        Log.v("Maze Generation", "Cancelling generation");
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

    /**
     * Part of the thread that controls the maze generation
     */
    @Override
    public void run() {
        // fetch the intent sent from the title screen containing the skill level, builder, and rooms
        Intent intent = getIntent();
        Log.v("SkillLevel", ""+intent.getIntExtra("SkillLevel", 0));
        Log.v("Builder", ""+intent.getStringExtra("Builder"));
        Log.v("Rooms", ""+intent.getBooleanExtra("Rooms", false));
        // update the progress as the maze is being generated to keep the user posted
        View minotaurProgress = findViewById(R.id.minotaurProgress);
        int currentProgress = 0;
        while (currentProgress < 100) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                setResult(RESULT_CANCELED, null);
                finish();
                return;
            }
            // update the progress bar in the UI
            currentProgress += 15;
            minotaurProgress.getBackground().setLevel(currentProgress*100);
        }

        // once the progress reaches 100, update the UI to show the play button
        handler.post(new Runnable() {
            public void run() {
                Button playButton = findViewById(R.id.playButton);
                playButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
