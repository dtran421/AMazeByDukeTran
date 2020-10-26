package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;

/**
 * Class: LosingActivity
 * <br>
 * Responsibilities: displays the losing screen along with path length and energy consumption,
 * allows the user to navigate back to the title screen  and play again
 * <br>
 * Collaborators: AMazeActivity, PlayManuallyActivity, PlayAnimationActivity
 */
public class LosingActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_losing);

        AnimationDrawable progressAnimation = (AnimationDrawable) findViewById(R.id.parentView).getBackground();
        progressAnimation.start();

        Intent losingState = getIntent();
        Resources res = getResources();
        if (!losingState.getBooleanExtra("Manual", true)) {
            String energyConsumption = String.format(Locale.US, "%.1f", losingState.getFloatExtra("Energy Consumption", 0));
            String totalEnergyConsumptionText = res.getString(R.string.totalEnergyConsumptionText);
            TextView energyConsumptionText = findViewById(R.id.energyConsumptionText);
            energyConsumptionText.setVisibility(View.VISIBLE);
            energyConsumptionText.setText(String.format(totalEnergyConsumptionText, energyConsumption));
        }
        String pathLengthString = res.getString(R.string.pathLengthText);
        TextView pathLengthText = findViewById(R.id.pathLengthText);
        pathLengthText.setText(String.format(pathLengthString, losingState.getIntExtra("Path Length", 0)));

        Button newQuestButton = findViewById(R.id.newQuestButton);
        newQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Losing State", "Returning to title");
                Intent returnToTitle = new Intent(LosingActivity.this, AMazeActivity.class);
                startActivity(returnToTitle);
                finish();
            }
        });
    }
}
