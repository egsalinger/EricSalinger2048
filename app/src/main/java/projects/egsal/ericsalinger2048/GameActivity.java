package projects.egsal.ericsalinger2048;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity{

    private static final int GOAL_SCORE = 2048;
    private boolean won = false;
    private GameLogic backend;
    private GameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        adapter = new GameAdapter(this, 0, new ArrayList<Integer>());

        backend = new GameLogic();

        backend.setBoardUpdateListener (new BoardUpdateListener() {
            @Override
            public void onBoardUpdate()
            {
                adapter.update(backend.getBoard());
            }
        });
        GridView gridview = (GridView) findViewById(R.id.main_view);
        gridview.setAdapter(adapter);
        // update display with what's going on.
        backend.resetBoard();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_game)
        {
            backend.resetBoard();
            won = false;
//            adapter.update(backend.getBoard());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void moveUp (View v)
    {
        if (backend.moveUp())
        {
            finishRound();
        }

    }

    public void moveDown (View v)
    {
        if (backend.moveDown())
        {
            finishRound();
        }

    }

    public void moveLeft (View v)
    {
        if (backend.moveLeft())
        {
            finishRound();
        }

    }

    public void moveRight (View v)
    {
        if (backend.moveRight())
        {
            finishRound();
        }

    }

    public void finishRound()
    {
        if (backend.getHighScore() == GOAL_SCORE && !won)
        {
            reportVictory();
        }
        backend.setupForNextMove();
        // have to check to see if game is over after adding a new element to the board.
        if (!backend.hasNextMove())
        {
            reportGameOver();
        }
//        adapter.update(backend.getBoard());
    }

    private void reportVictory()
    {
        won = true;
        Toast.makeText(this, getString(R.string.victory), Toast.LENGTH_LONG).show();
    }

    private void reportGameOver() {
        Toast.makeText(this, getString(R.string.game_over_man), Toast.LENGTH_LONG).show();
    }

}
