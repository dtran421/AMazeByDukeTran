package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

/**
 * Class: PlayManuallyActivity
 * <br>
 * Responsibilities: allows user to play through the maze with their selected robot and driver
 * <br>
 * Collaborators: AMazeActivity, WinningActivity, LosingActivity
 */
public class PlayManuallyActivity extends AppCompatActivity {
    private final Context context = this;
    protected boolean showMap = false;
    protected boolean showSolution = false;
    protected boolean showAllWalls = false;

    protected int pathLength = 0;
    protected int zoom = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_manual);

        Intent mazeGame = getIntent();
        Log.v("Game driver", mazeGame.getStringExtra("Driver"));
        Log.v("Game robot", mazeGame.getStringExtra("Robot"));

        setPathLength();

        setUpButtons();
    }

    private void setUpButtons() {
        setUpMenuButton();
        setUpZoomButtons();
        setUpMoveButtons();

        MaterialButton shortcutButton = findViewById(R.id.shortcutButton);
        shortcutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Manual Play", "Proceeding to WinningState");
            }
        });
    }

    private void setUpMenuButton() {
        ImageView menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                setUpPopup(popup);

                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_game, popup.getMenu());
                popup.show();

                Menu menu = popup.getMenu();
                menu.findItem(R.id.showMapItem).setChecked(showMap);
                menu.findItem(R.id.showSolutionItem).setChecked(showSolution);
                menu.findItem(R.id.showAllWallsItem).setChecked(showAllWalls);
            }
        });
    }

    private void setUpPopup(PopupMenu popup) {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast toast;
                switch (item.getItemId()) {
                    case R.id.showMapItem:
                        showMap = !showMap;
                        item.setChecked(showMap);
                        Log.v("Show map", ""+showMap);
                        toast = Toast.makeText(context, "Toggling map", Toast.LENGTH_SHORT);
                        toast.show();
                        return true;
                    case R.id.showSolutionItem:
                        showSolution = !showSolution;
                        item.setChecked(showSolution);
                        Log.v("Show solution", ""+showSolution);
                        toast = Toast.makeText(context, "Toggling solution", Toast.LENGTH_SHORT);
                        toast.show();
                        return true;
                    case R.id.showAllWallsItem:
                        showAllWalls = !showAllWalls;
                        item.setChecked(showAllWalls);
                        Log.v("Show all walls", ""+showAllWalls);
                        toast = Toast.makeText(context, "Toggling walls", Toast.LENGTH_SHORT);
                        toast.show();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void setUpZoomButtons() {
        ImageView zoomInButton = findViewById(R.id.zoomInButton);
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom += 5;
                Log.v("Zoom", ""+zoom);
            }
        });

        ImageView zoomOutButton = findViewById(R.id.zoomOutButton);
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom -= 5;
                Log.v("Zoom", ""+zoom);
            }
        });
    }

    private void setUpMoveButtons() {
        ImageView forwardButton = findViewById(R.id.forwardButton);
        setUpMovementButton(forwardButton);
        ImageView rightButton = findViewById(R.id.rightButton);
        setUpMovementButton(rightButton);
        ImageView backButton = findViewById(R.id.backButton);
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

    private void setPathLength() {
        Resources res = getResources();
        String pathLengthString = res.getString(R.string.pathLengthText);
        TextView pathLengthText = findViewById(R.id.pathLengthText);
        pathLengthText.setText(String.format(pathLengthString, pathLength));
    }
}
