package com.example.bbirincioglu.centipedegame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Another adapter for displaying game results in a list view with custom layout. Each row in the listView is constructed via getView() method.
 * The instance of this class is used in the GameResultsActivity.
 */
public class GameResultListAdapter extends ArrayAdapter<GameResult> {
    private Context context;
    private ArrayList<GameResult> gameResults;

    public GameResultListAdapter(Context context, int rowResourceID, ArrayList<GameResult> gameResults) {
        super(context, rowResourceID, gameResults);
        this.context = context;
        this.gameResults = gameResults;
    }

    //Each content of game result in the gameResults arrayList is inserted into a GUI object (View row) via this method.
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        LayoutInflater inflater = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        row = inflater.inflate(R.layout.game_result_list_row, parent, false);
        GameResult gameResult = getGameResults().get(position);

        LinearLayout rowContainer = (LinearLayout) row.findViewById(R.id.game_result_list_row_container);
        TextView allInOneTextView = (TextView) rowContainer.getChildAt(0);
        allInOneTextView.setText(gameResult.toString().replace(GameResult.SPLIT_WITH, ", "));
        return row;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<GameResult> getGameResults() {
        return gameResults;
    }

    public void setGameResults(ArrayList<GameResult> gameResults) {
        this.gameResults = gameResults;
    }
}
