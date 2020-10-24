package edu.wm.cs.cs301.duketran.gui;

import edu.wm.cs.cs301.duketran.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends AppCompatActivity {
    protected boolean showMap = false;
    protected boolean showSolution = false;
    protected boolean showAllWalls = false;

    protected int zoom = 100;
    protected int pathLength = 0;

    protected void setPathLength() {
        Resources res = getResources();
        String pathLengthString = res.getString(R.string.pathLengthText);
        TextView pathLengthText = findViewById(R.id.pathLengthText);
        pathLengthText.setText(String.format(pathLengthString, pathLength));
    }

    protected void setUpMenuButton(final Context context, ImageView menuButton) {
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                setUpPopup(context, popup);

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

    private void setUpPopup(final Context context, PopupMenu popup) {
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

    protected void setUpZoomButtons(ImageView zoomInButton, ImageView zoomOutButton) {
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom += 5;
                Log.v("Zoom", ""+zoom);
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom -= 5;
                Log.v("Zoom", ""+zoom);
            }
        });
    }
}
