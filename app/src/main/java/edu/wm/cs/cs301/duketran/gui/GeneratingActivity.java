package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);

        handler = new Handler();
        generationThread = new Thread(this);
        generationThread.start();

        Spinner driverSpinner = findViewById(R.id.driverSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.driver, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(adapter);

        Spinner robotSpinner = findViewById(R.id.robotSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.robot, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        robotSpinner.setAdapter(adapter);

        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toggleRobotLayout(parent.getItemAtPosition(position).toString());
            }
            public void onNothingSelected(AdapterView<?> parent) {
                toggleRobotLayout(parent.getSelectedItem().toString());
            }
        });

        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Spinner driverSpinner = findViewById(R.id.driverSpinner);
                String driver = driverSpinner.getSelectedItem().toString();

                Spinner robotSpinner = findViewById(R.id.robotSpinner);
                String robot = robotSpinner.getSelectedItem().toString().split(" ")[0];

                Intent result = new Intent(GeneratingActivity.this, AMazeActivity.class);
                result.putExtra("Driver", driver);
                result.putExtra("Robot", robot);
                GeneratingActivity.this.setResult(RESULT_OK, result);
                GeneratingActivity.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        generationThread.interrupt();
        GeneratingActivity.this.setResult(RESULT_CANCELED, new Intent(GeneratingActivity.this, AMazeActivity.class));
        GeneratingActivity.this.finish();
        Log.v("Maze Generation", "Cancelling generation");
    }


    private void toggleRobotLayout(String selectedItem) {
        LinearLayout robotLayout = findViewById(R.id.robotLayout);
        if(!selectedItem.equals("Wallfollower")) robotLayout.setVisibility(View.INVISIBLE);
        else robotLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void run() {
        Intent intent = getIntent();
        Log.v("SkillLevel", ""+intent.getIntExtra("SkillLevel", 0));
        Log.v("Builder", ""+intent.getStringExtra("Builder"));
        Log.v("Rooms", ""+intent.getBooleanExtra("Rooms", false));
        ProgressBar progressBar = findViewById(R.id.progressBar);
        int currentProgress = 0;
        while (currentProgress < 100) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                setResult(RESULT_CANCELED, null);
                finish();
                return;
            }
            currentProgress += 15;
            progressBar.setProgress(currentProgress);
        }

        handler.post(new Runnable() {
            public void run() {
                Button playButton = findViewById(R.id.playButton);
                playButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
