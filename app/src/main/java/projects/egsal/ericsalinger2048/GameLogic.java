package projects.egsal.ericsalinger2048;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by egsal on 8/18/15.
 */
public class GameLogic {


    private static final int VERTICAL = 4;
    private static final int HORIZONTAL = 1;
    private int highScore = 2;

    private BoardUpdateListener listener;

    private HashMap<Integer, Integer> newBoard;
    final int BoardWidth = 4;
    final int BoardHeight = 4;
    private Random rand;

    public GameLogic() {
        newBoard = new HashMap <Integer, Integer>();


        rand = new Random();
        setupForNextMove();
        printBoard();
    }

    public void setBoardUpdateListener (BoardUpdateListener l)
    {
        listener = l;
    }



    /**
     * Returns true if it's possible to add a two to the board. False otherwise.
     * @return true if we can add a two. False otherwise.
     */
    public void setupForNextMove()
    {
        int toAdd = -1;

        // return if impossible. We should never get here.
        if (newBoard.keySet().size() == BoardHeight*BoardWidth)
            return;

        // this is somewhat inefficient (worst case O (infinity)), but it's good enough generally
        while (!newBoard.containsKey(toAdd)){
            toAdd = rand.nextInt(BoardHeight * BoardWidth);
            if (!newBoard.containsKey(toAdd))
                newBoard.put (toAdd, 2);
            // don't terminate because we randomly picked a key in the HashMap already.
            else
                toAdd = -1;
        }
        if (listener != null) {
            listener.onBoardUpdate();
        }
    }

    public boolean moveWithDirection (int rotations)
    {
        boolean moved = false;
        // calculate positions based on location.
        for (int i = 0; i < BoardWidth*BoardHeight; i += BoardWidth)
        {
            int lastValue = -1;
            int lastOccupiedIndex = i;
            int lastEmpty = i + BoardWidth;
            for (int k = 0; k < BoardWidth; k++)
            {
                int index = i + k;
                int value = getValueWithRotation(index, rotations);

                // if value != 0
                if (value != 0) {
                    if (lastValue == value) {
                        moved = true;
                        value = value *2;
                        setIndexWithRotation(lastOccupiedIndex, rotations, value);
                        setIndexWithRotation(index, rotations, 0);
                        if (value > highScore)
                        {
                            highScore = value;
                        }
                        lastValue = -1;
                        lastEmpty = lastOccupiedIndex + 1;
                        lastOccupiedIndex = i+ BoardWidth;
                    } else if (lastEmpty < index) {
                        setIndexWithRotation(lastEmpty, rotations, value);
                        setIndexWithRotation(index, rotations, 0);
                        lastOccupiedIndex = lastEmpty;
                        lastEmpty += 1;
                        lastValue = value;
                        moved = true;
                    } else if (lastOccupiedIndex <= index) {
                        lastOccupiedIndex = index;
                        lastValue = value;
                    }
                }
                else {
                    if (lastEmpty > index) {
                        lastEmpty = index;
                    }
                }
            }
        }




        return moved;
    }

    private void setIndexWithRotation(int indexToRotate, int rotations, int value) {
        int realIndex = getIndexWithRotation(indexToRotate, rotations);
        if (value == 0)
            newBoard.remove(realIndex);
        else {
            newBoard.put(realIndex, value);
        }
    }

    private int getIndexWithRotation(int indexToRotate, int rotations) {

        int row1 = indexToRotate / BoardWidth;
        int col1 = indexToRotate % BoardWidth;

        // Sanity checking here -- only 4 directions we can rotate.
        rotations = rotations %4;


        for (int i = 0; i < rotations; i++)
        {
            // row2, col2, are rotated row1, col1.
            int row2 = col1;
            int col2 = (BoardWidth-1) - row1;

            //reset for next step
            row1 = row2;
            col1 = col2;
        }


        return row1*BoardWidth+col1;
    }

    private int getValueWithRotation (int indexToRotate, int rotations)
    {
        int rotatedIndex = getIndexWithRotation(indexToRotate, rotations);
        if (newBoard.containsKey(rotatedIndex))
        {
            return newBoard.get(rotatedIndex);
        }
        else
        {
            return 0;
        }
    }

    public boolean hasNextMove()
    {
        // if the board is not full, we can definitely move.
        if (newBoard.size() != BoardWidth*BoardHeight)
            return true;
        // if the board is full.
        else
        {
            // start on 0,0
            for (int i = 0; i < (BoardHeight*BoardWidth); i++)
            {
                // no item to the left of us in this case.
                if (i%BoardWidth == 0)
                    continue;
                int value = newBoard.get(i);
                int leftValue = newBoard.get(i-1);
                int topValue = -1;
                // special check to make sure were not on the first row.
                if (i >= BoardWidth)
                {
                    topValue = newBoard.get(i - BoardWidth);
                }
                // if we match a value to the left of us, or a value to the top of us, return true.
                // We don't need to check for empty spaces because to get here, the board must be
                // full.
                if (value == leftValue || value == topValue)
                {
                    return true;
                }
            }
        }
        // we've checked every element in the array
        return false;
    }




    public void resetBoard ()
    {
        newBoard.clear();
        setupForNextMove();
    }


    /**
     * great method for debugging.
     */
    public void printBoard ()
    {
        String output = "";
       for (int i = 0; i < BoardWidth*BoardHeight; i++)
       {
           if (i%4 == 0)
               output += "\n";
           if (newBoard.containsKey(i))
           output += newBoard.get(i);
           else
               output += "0";

       }
        Log.d("printBoard", "Board:\n" +output);
    }

    public int getHighScore()
    {
        return highScore;
    }

    public ArrayList<Integer> getBoard()
    {
        ArrayList <Integer> toReturn = new ArrayList <Integer>();
        for (int i = 0; i < BoardHeight*BoardWidth; i++)
        {
            if (newBoard.containsKey(i))
                toReturn.add(newBoard.get(i));
            else
                toReturn.add (0);
        }
        return toReturn;
    }

}
