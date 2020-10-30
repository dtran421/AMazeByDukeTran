package edu.wm.cs.cs301.duketran.gui;

import java.util.Objects;
import edu.wm.cs.cs301.duketran.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Class: AMazeActivity
 * <br>
 * Responsibilities: title screen for the maze game, select a skill level, maze builder,
 * and whether there are rooms, navigate to the other states
 * <br>
 * Collaborators: GeneratingActivity, PlayManuallyActivity, PlayAnimationActivity
 */
public class AMazeActivity extends AppCompatActivity {
    private static final int KEY_GENERATION = 1;

    /**
     * Overrides the onCreate method of Activity, starts the background animation, fills the builder
     * spinner with the builder options, sets up the navigation buttons (to start maze generation)
     * @param savedInstanceState the bundle object containing the saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        // start the background animation
        AnimationDrawable progressAnimation = (AnimationDrawable) findViewById(R.id.parentView).getBackground();
        progressAnimation.start();
        Log.v("App Launch", "Successful");

        // fill the builder spinner with the builder options
        Spinner builderSpinner = findViewById(R.id.builderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.builder, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        builderSpinner.setAdapter(adapter);

        // set up the listeners for the buttons to start the maze generation
        setUpNavigationButton((Button) findViewById(R.id.revisitButton));
        setUpNavigationButton((Button) findViewById(R.id.exploreButton));
    }

    @Override
    protected void onStop() {
        super.onStop();
        SeekBar skillLevelSeekBar = findViewById(R.id.seekBar);
        skillLevelSeekBar.setProgress(0);
        Spinner builderSpinner = findViewById(R.id.builderSpinner);
        builderSpinner.setSelection(0);
        SwitchMaterial roomSwitch = findViewById(R.id.roomSwitch);
        roomSwitch.setChecked(false);
    }

    /**
     * Once the maze generation is complete, it returns to this method with the user inputted driver
     * and robot if applicable, then starts the game based on the mode
     * @param requestCode ensure that it matches the provided key for generation
     * @param resultCode result status (OK/canceled)
     * @param data returned from the maze generation (inputted driver and robot and generated maze)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check that the request code matches the predefined key for generation
        if (requestCode == KEY_GENERATION)
            // check that the result status is ok
            if (resultCode == Activity.RESULT_OK) {
                // obtain maze data from the data bundle
                Bundle mazeData = data.getExtras();
                // obtain the driver, robot, and maze from the maze data
                assert(mazeData != null) : "Error: mazeData is not supposed to be null!";
                String driver = Objects.requireNonNull(mazeData.get("Driver")).toString();
                String robot = Objects.requireNonNull(mazeData.get("Robot")).toString();
                Log.v("Inputted Driver", driver);
                Log.v("Inputted Robot", robot);

                // make a new intent for the game containing the driver, robot, and maze
                Intent mazeGame = new Intent(AMazeActivity.this,
                        (driver.equals("Manual") ? PlayManuallyActivity.class : PlayAnimationActivity.class));
                mazeGame.putExtra("Driver", driver);
                mazeGame.putExtra("Robot", robot);
                mazeGame.putExtra("Maze", "");

                // make a new toast to alert the user that the game is starting
                Toast toast = Toast.makeText(AMazeActivity.this, "Loading game...", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(mazeGame);
            }
            // if the user exited the activity (canceled the intent), then do nothing
            else if (resultCode == Activity.RESULT_CANCELED) {
                Log.w("Maze Generation", "Canceled");
            }
    }

    /**
     * Sets up the listeners for the navigation buttons in charge of starting the maze generation
     * @param navigationButton the button for which the listener is being set
     */
    public void setUpNavigationButton(Button navigationButton) {
        // TODO: implement different functions for the Revisit and Explore buttons
        navigationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // obtain the selected skill level
                SeekBar skillLevelSeekBar = findViewById(R.id.seekBar);
                int skillLevel = skillLevelSeekBar.getProgress();
                // obtain the selected builder
                Spinner builderSpinner = findViewById(R.id.builderSpinner);
                String builder = builderSpinner.getSelectedItem().toString();
                // obtain the selection for whether rooms are going to be present
                SwitchMaterial roomSwitch = findViewById(R.id.roomSwitch);
                boolean rooms = roomSwitch.isChecked();

                // create a new intent and fill it with the inputted data
                Intent mazeGeneration = new Intent(AMazeActivity.this, GeneratingActivity.class);
                mazeGeneration.putExtra("SkillLevel", skillLevel);
                mazeGeneration.putExtra("Builder", builder);
                mazeGeneration.putExtra("Rooms", rooms);

                // make a new toast to alert the user of the new activity, start the new activity
                // and await the result
                Toast toast = Toast.makeText(AMazeActivity.this, "Generating maze...", Toast.LENGTH_SHORT);
                toast.show();
                startActivityForResult(mazeGeneration, KEY_GENERATION);
            }
        });
    }
}
