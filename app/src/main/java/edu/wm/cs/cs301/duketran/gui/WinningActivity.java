package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class WinningActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

        Intent winningState = getIntent();
        Resources res = getResources();
        if (!winningState.getBooleanExtra("Manual", true)) {
            String energyConsumption = String.format(Locale.US, "%.1f", winningState.getFloatExtra("Energy Consumption", 0));
            String totalEnergyConsumptionText = res.getString(R.string.totalEnergyConsumptionText);
            TextView energyConsumptionText = findViewById(R.id.energyConsumptionText);
            energyConsumptionText.setVisibility(View.VISIBLE);
            energyConsumptionText.setText(String.format(totalEnergyConsumptionText, energyConsumption));
        }
        String pathLengthString = res.getString(R.string.pathLengthText);
        TextView pathLengthText = findViewById(R.id.pathLengthText);
        pathLengthText.setText(String.format(pathLengthString, winningState.getIntExtra("Path Length", 0)));

        Button newQuestButton = findViewById(R.id.newQuestButton);
        newQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Winning State", "Returning to title");
                Intent returnToTitle = new Intent(WinningActivity.this, AMazeActivity.class);
                startActivity(returnToTitle);
                finish();
            }
        });
    }
}
