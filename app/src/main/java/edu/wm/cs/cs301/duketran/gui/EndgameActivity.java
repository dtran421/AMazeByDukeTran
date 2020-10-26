package edu.wm.cs.cs301.duketran.gui;

import java.util.Locale;
import edu.wm.cs.cs301.duketran.R;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Class: EndgameActivity
 * <br>
 * Responsibilities: parent class of the different endgame activities (states - Winning/Losing)
 * <br>
 * Collaborators: AMazeActivity, PlayManuallyActivity, PlayAnimationActivity
 */
public class EndgameActivity extends AppCompatActivity {

    /**
     * Sets up the UI components with the data from the intent
     * @param context for which the components are being set up
     */
    protected void setUpComponents(final Context context, final boolean winning) {
        Intent endgameState = getIntent();
        Resources res = getResources();
        // format the shortest path data into the text view
        String shortestPathString = res.getString(R.string.shortestPathText);
        TextView shortestPathText = findViewById(R.id.shortestPathText);
        shortestPathText.setText(String.format(shortestPathString, endgameState.getIntExtra("Shortest Path", 0)));
        // format the path length data into the text view
        String pathLengthString = res.getString(R.string.pathLengthText);
        TextView pathLengthText = findViewById(R.id.pathLengthText);
        pathLengthText.setText(String.format(pathLengthString, endgameState.getIntExtra("Path Length", 0)));
        // if the game was not played in manual mode, format the energy consumption data into the text view
        if (!endgameState.getBooleanExtra("Manual", true)) {
            String totalEnergyConsumptionText = res.getString(R.string.totalEnergyConsumptionText);
            TextView energyConsumptionText = findViewById(R.id.energyConsumptionText);
            energyConsumptionText.setVisibility(View.VISIBLE);
            // obtain the float data from the intent and format it to show 1 decimal place
            String energyConsumption = String.format(Locale.US, "%.1f", endgameState.getFloatExtra("Energy Consumption", 0));
            energyConsumptionText.setText(String.format(totalEnergyConsumptionText, energyConsumption));
        }
        // create a new listener for the new quest button to navigate to the title activity
        Button newQuestButton = findViewById(R.id.newQuestButton);
        newQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(winning ? "Winning State" : "Losing State", "Returning to title");
                Intent returnToTitle = new Intent(context, AMazeActivity.class);
                startActivity(returnToTitle);
                finish();
            }
        });
    }
}
