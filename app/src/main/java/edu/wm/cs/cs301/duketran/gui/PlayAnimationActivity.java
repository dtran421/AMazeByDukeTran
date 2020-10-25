package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;

import java.util.Objects;

/**
 * Class: PlayAnimationActivity
 * <br>
 * Responsibilities: animates the driver playing through the maze with the selected robot
 * <br>
 * Collaborators: AMazeActivity, WinningActivity, LosingActivity
 */
public class PlayAnimationActivity extends PlayActivity {
    final private float energyConsumption = 3500;

    private boolean isAnimating = true;
    private int animationSpeed = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_animation);

        Intent mazeGame = getIntent();
        Log.v("Game driver", Objects.requireNonNull(mazeGame.getStringExtra("Driver")));
        Log.v("Game robot", Objects.requireNonNull(mazeGame.getStringExtra("Robot")));

        setPathLength();

        setUpButtons();
    }

    private void setUpButtons() {
        setUpMenuButton(this, (ImageView) findViewById(R.id.menuButton));
        setUpZoomButtons((ImageView) findViewById(R.id.zoomInButton), (ImageView) findViewById(R.id.zoomOutButton));
        setUpAnimationComponents();

        Button winningButton = findViewById(R.id.winningButton);
        winningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Animation Play", "Proceeding to WinningActivity");
                Intent winningState = new Intent(PlayAnimationActivity.this, WinningActivity.class);
                winningState.putExtra("Manual", false);
                winningState.putExtra("Path Length", pathLength);
                winningState.putExtra("Energy Consumption", energyConsumption);

                Toast toast = Toast.makeText(PlayAnimationActivity.this, "You escaped!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(winningState);
                finish();
            }
        });
        Button losingButton = findViewById(R.id.losingButton);
        losingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Animation Play", "Proceeding to LosingActivity");
            }
        });
    }

    private void setUpAnimationComponents() {
        final Button animationButton = findViewById(R.id.animationButton);
        animationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAnimating = !isAnimating;
                animationButton.setBackgroundColor(isAnimating ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorRepair)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorOperational));
                animationButton.setText(isAnimating ? R.string.stopAnimationText : R.string.startAnimationText);
                animationButton.setTextColor(isAnimating ? ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorPrimary)
                        : ContextCompat.getColor(PlayAnimationActivity.this, R.color.colorPrimaryDark));
                Log.v("Currently animating", ""+isAnimating);
            }
        });

        Slider animationSlider = findViewById(R.id.animationSpeedSlider);
        animationSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                animationSpeed = (int) value;
                Log.v("Animation speed", ""+animationSpeed);
            }
        });
    }
}
