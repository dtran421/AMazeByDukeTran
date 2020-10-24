package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        Log.v("App Launch", "Successful");

        Spinner builderSpinner = findViewById(R.id.builderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.builder, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        builderSpinner.setAdapter(adapter);

        setUpNavigationButton((Button) findViewById(R.id.exploreButton));
    }

    public void setUpNavigationButton(Button navigationButton) {
        // TODO: implement different functions for the Revisit and Explore buttons
        navigationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SeekBar skillLevelSeekBar = findViewById(R.id.seekBar);
                int skillLevel = skillLevelSeekBar.getProgress();

                Spinner builderSpinner = findViewById(R.id.builderSpinner);
                String builder = builderSpinner.getSelectedItem().toString();

                SwitchMaterial roomSwitch = findViewById(R.id.roomSwitch);
                boolean rooms = roomSwitch.isChecked();

                Intent mazeGeneration = new Intent(AMazeActivity.this, GeneratingActivity.class);
                mazeGeneration.putExtra("SkillLevel", skillLevel);
                mazeGeneration.putExtra("Builder", builder);
                mazeGeneration.putExtra("Rooms", rooms);

                Toast toast = Toast.makeText(AMazeActivity.this, "Generating maze...", Toast.LENGTH_SHORT);
                toast.show();
                startActivityForResult(mazeGeneration, KEY_GENERATION);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_GENERATION)
            if (resultCode == Activity.RESULT_OK) {
                String driver = Objects.requireNonNull(Objects.requireNonNull(data.getExtras()).get("Driver")).toString();
                String robot = Objects.requireNonNull(data.getExtras().get("Robot")).toString();
                Log.v("Inputted Driver", driver);
                Log.v("Inputted Robot", robot);

                Intent mazeGame = new Intent(AMazeActivity.this,
                        (driver.equals("Manual") ? PlayManuallyActivity.class : PlayAnimationActivity.class));
                mazeGame.putExtra("Driver", driver);
                mazeGame.putExtra("Robot", robot);

                Toast toast = Toast.makeText(AMazeActivity.this, "Loading game...", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(mazeGame);
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("Maze Generation", "Canceled");
            }
    }


}
