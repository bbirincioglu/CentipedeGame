package com.example.bbirincioglu.centipedegame;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;
/*
    The class for obtaining game results from Parse servers, and display them in a List View. Then, the user is allowed to extract them into excel format by clicking
    a button. It implements ParseConnectionObserver because it displays a waiting dialog to the user until, game results are fully obtained from the server.
 */
public class GameResultsActivity extends AppCompatActivity implements ParseConnectionObserver {
    private GameResultsController gameResultsController;
    private ArrayList<Dialog> dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_results);
        setDialogs(new ArrayList<Dialog>());
        ParseObject.registerSubclass(GameResult.class); //Introduce GameResult class to Parse Servers for recognition.

        setGameResultsController(new GameResultsController());
        DialogFactory dialogFactory = DialogFactory.getInstance();
        dialogFactory.setContext(this);
        dialogFactory.create(DialogFactory.DIALOG_PASSWORD).show(); //This section of the application is protected via password. In order to see all the game results
                                                                    //user has to enter correct password. Thus, we first display a password dialog, and check whether
                                                                    // he typed correct password or not.
        updateButtonSizes(0.45, 0);
    }

    private void updateButtonSizes(double widthRatio, double heightRatio) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int buttonWidth = (int) (screenWidth * widthRatio);
        int buttonHeight = (int) (screenHeight * heightRatio);
        int[] buttonIDs = new int[]{R.id.gameResultsGetResultsButton, R.id.gameResultsSaveResultsButton};

        if (buttonWidth != 0) {
            for (int buttonID : buttonIDs) {
                Button button = ((Button) findViewById(buttonID));
                button.setWidth(buttonWidth);
            }
        }

        if (buttonHeight != 0) {
            for (int buttonID : buttonIDs) {
                Button button = ((Button) findViewById(buttonID));
                button.setHeight(buttonHeight);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(ParseConnection parseConnection) {
        int currentState = parseConnection.getCurrentState();

        if (currentState == ParseConnection.STATE_NO_BACKGROUND_JOB) {

        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_STARTED) {

        } else if (currentState == ParseConnection.STATE_BACKGROUND_JOB_FINISHED) {
            ListView listView = (ListView) findViewById(R.id.gameResultsListView); //When the game results are fully obtained, create a list view in which game results
                                                                                    // are displayed.
            ArrayList<GameResult> gameResultList = (ArrayList) parseConnection.getObjects();    //Get the game results.
            GameResultListAdapter adapter = new GameResultListAdapter(this, R.layout.game_result_list_row, gameResultList); // Create the adapter for list view.
            listView.setAdapter(adapter); //bind adapter to list view.
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = (TextView) view;
                    System.out.println(textView.getText());
                }
            });

            adapter.notifyDataSetChanged(); //notifies the GUI object (list view) for updating its appearance.
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Keys.RETURN_FROM_ACTIVITY, true);
        new ActivitySwitcher().fromPreviousToNext(this, MainMenuActivity.class, bundle, true);
    }

    //ButtonListener method for "Get Results", and "Save Results" buttons.
    public void onClick(View v) {
        int buttonID = v.getId();
        GameResultsController controller = getGameResultsController();

        if (buttonID == R.id.gameResultsGetResultsButton) {
            controller.doGetResults(this);
        } else if (buttonID == R.id.gameResultsSaveResultsButton) {
            controller.doSaveResults(this);
        }
    }

    public GameResultsController getGameResultsController() {
        return gameResultsController;
    }

    public void setGameResultsController(GameResultsController gameResultsController) {
        this.gameResultsController = gameResultsController;
    }

    public ArrayList<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(ArrayList<Dialog> dialogs) {
        this.dialogs = dialogs;
    }
}
