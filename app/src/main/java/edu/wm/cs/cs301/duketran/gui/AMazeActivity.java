package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AMazeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner builderSpinner = findViewById(R.id.builderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.builder, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        builderSpinner.setAdapter(adapter);

        Spinner driverSpinner = findViewById(R.id.driverSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.driver, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(adapter);
    }
}
