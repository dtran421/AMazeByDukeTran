package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

/**
 * Class: PlayManuallyActivity
 * <br>
 * Responsibilities: allows user to play through the maze with their selected robot and driver
 * <br>
 * Collaborators: AMazeActivity, WinningActivity, LosingActivity
 */
public class PlayManuallyActivity extends PlayActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manual);

        Intent mazeGame = getIntent();
        Log.v("Game driver", Objects.requireNonNull(mazeGame.getStringExtra("Driver")));

        setPathLength();

        setUpButtons();
    }

    private void setUpButtons() {
        setUpMenuButton(this, (ImageView) findViewById(R.id.menuButton));
        setUpZoomButtons((ImageView) findViewById(R.id.zoomInButton), (ImageView) findViewById(R.id.zoomOutButton));
        setUpMoveButtons();

        Button shortcutButton = findViewById(R.id.shortcutButton);
        shortcutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Manual Play", "Proceeding to WinningActivity");
                Intent winningState = new Intent(PlayManuallyActivity.this, WinningActivity.class);
                winningState.putExtra("Manual", true);
                winningState.putExtra("Path Length", pathLength);

                Toast toast = Toast.makeText(PlayManuallyActivity.this, "You escaped!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(winningState);
                finish();
            }
        });
    }

    private void setUpMoveButtons() {
        ImageView forwardButton = findViewById(R.id.forwardButton);
        setUpMovementButton(forwardButton);
        ImageView rightButton = findViewById(R.id.rightButton);
        setUpMovementButton(rightButton);
        ImageView backButton = findViewById(R.id.backwardButton);
        setUpMovementButton(backButton);
        ImageView leftButton = findViewById(R.id.leftButton);
        setUpMovementButton(leftButton);
        ImageView jumpButton = findViewById(R.id.jumpButton);
        setUpMovementButton(jumpButton);
    }

    private void setUpMovementButton(ImageView button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathLength += 1;
                setPathLength();
            }
        });
    }
}
