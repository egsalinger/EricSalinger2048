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

    public boolean moveDown()
    {
        // only add extra tiles if we can move
        boolean canMove = false;
        for (int from = 0; from < BoardWidth; from++)
        {
            // Given a StartIndex
            // A column is the array (el) defined by
            // (i = 0; i < BoardHeight; i++)
            // el [i] = i*BoardWidth+StartIndex
            int to = from+((BoardHeight-1)*BoardWidth);
            // if we haven't moved, this condition is true.
            // if we haven't moved and we do move, moveRowDown returns true.
            // therefore canMove will be true iff at least one row column moved
            if (!canMove)
            {
                canMove = move(from, (from+3*BoardHeight), VERTICAL);
            }
            else
                move(from, from+3*BoardHeight, VERTICAL);
        }
        return canMove;
    }

    public boolean moveUp()
    { // only add extra tiles if we can move
        boolean canMove = false;
        for (int to = 0; to < BoardWidth; to++)
        {
            // if we haven't moved, this condition is true.
            // if we haven't moved and we do move, moveRowDown returns true.
            // therefore canMove will be true iff at least one row column moved
            int from = to+ (BoardHeight-1)*BoardHeight;

            if (!canMove)
            {
                canMove = move(from, to, VERTICAL);
            }
            else
                move(from, to, VERTICAL);
        }
        return canMove;
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

    public boolean moveLeft()
    { // only add extra tiles if we can move
        boolean canMove = false;
        for (int i = 0; i < BoardHeight; i++)
        {
            int to = i*BoardWidth;
            // if we haven't moved, this condition is true.
            // if we haven't moved and we do move, moveRowDown returns true.
            // therefore canMove will be true iff at least one row column moved
            int from = to + BoardWidth-1;

            if (!canMove)
            {
                canMove = move(from, to, HORIZONTAL);
            }
            else
                move(from, to, HORIZONTAL);
        }
        return canMove;
    }

    public boolean moveRight()
    {
    // only add extra tiles if we can move
        boolean canMove = false;
        for (int i = 0; i < BoardHeight; i++)
        {
            int from = i*BoardWidth;
            // if we haven't moved, this condition is true.
            // if we haven't moved and we do move, moveRowDown returns true.
            // therefore canMove will be true iff at least one row column moved
            int to = from + (BoardWidth-1);

            if (!canMove)
            {
                canMove = move(from, to, HORIZONTAL);
            }
            else
                move(from, to, HORIZONTAL);
        }
        return canMove;
    }


    private boolean move (int from, int to, int dir)
    {
        // if from is before to
        if (from > to){
            return moveNegative (from, to, dir);
        }
        else {
           return movePositive(from, to, dir);
        }
    }

    /**
     * This is split into two methods, for moving in the positive and negative directions respectively
     * @param from where we're moving from
     * @param to where we're moving to
     * @param dir the direction/amount
     * @return true if something actually moved, false otherwise.
     */

    private boolean moveNegative (int from, int to, int dir)
    {
        // from > to
        boolean didMove = false;
        int lastIndex = from+1;
        int lastValue = -1;
        int lastFree = from+1;

        // for each element past our destination at currentIndex
        for (int currentIndex = to; currentIndex <= from ; currentIndex += dir)
        {
            int value = 0;
            // if the board contains you as a key
            if (newBoard.containsKey(currentIndex))
            {
                // if we haven't seen a number in the row
                value = newBoard.get(currentIndex);
                // if we haven't seen a number in the row we could add to
                if (lastValue == -1)
                {
                    // set the last index and value
                    lastIndex = currentIndex;
                    lastValue = value;
                    // if we can move, we also need to move..
                    if (lastFree < currentIndex)
                    {
                        lastIndex = lastFree;
                        lastFree = lastIndex + dir;
                        didMove = true;
                        // Update the board
                        newBoard.remove(currentIndex);
                        newBoard.put(lastIndex, value);
                    }
                }
                // if we have seen a number in the row, and they match
                else if (lastValue == value)
                {
                    // increment the value
                    lastValue+= value;
                    // put the new value into the board at the previous index.
                    newBoard.put(lastIndex, lastValue);
                    // remove the current value from the board.
                    newBoard.remove(currentIndex);
                    // perform some cleanup
                    lastFree = lastIndex + dir;
                    lastIndex = from+1;
                    lastValue = -1;
                    didMove = true;
                    // Goal-checking calculation.
                    if (lastValue > highScore)
                        highScore = lastValue;
                }
                // If there's no match, but we have at least one free space;
                else if (lastFree < currentIndex ) {
                    newBoard.put(lastFree, newBoard.get(currentIndex));
                    newBoard.remove(currentIndex);
                    // modify where the number is.
                    lastIndex = lastFree;
                    // modify what the number is
                    lastValue = value;
                    // we move the tile as far in our direction as we can, so the last free
                    // space is the location of the last tile minus 1 in that direction.
                    lastFree = lastIndex + dir;
                    didMove = true;
                }
                // if there's no match, we need to do some updates
                else
                {
                    lastValue = value;
                    lastIndex = currentIndex;
                }

            }
            // The current cell is empty
            else
            {
                // and we don't have a lastFree item...
                // we do now.
                if (lastFree > currentIndex)
                {
                    lastFree = currentIndex;
                }

            }
        }
        return didMove;
    }


    private boolean movePositive (int from, int to, int dir)
    {
        // to > from
        boolean didMove = false;
        int lastIndex = -1;
        int lastValue = -1;
        int lastFree = -1;

        // for each element past our destination at currentIndex
        for (int currentIndex = to; currentIndex >= from; currentIndex -= dir)
        {
            int value = 0;
            // if the board contains you as a key
            if (newBoard.containsKey(currentIndex))
            {
                value = newBoard.get(currentIndex);
                // if we haven't seen a number in the row we could add to
                if (lastValue == -1)
                {
                    // set the last index and value
                    lastIndex = currentIndex;
                    lastValue = value;
                    // if we can move, we also need to move..
                    if (lastFree > currentIndex)
                    {
                        lastIndex = lastFree;
                        lastFree = lastIndex - dir;
                        didMove = true;
                        // Update the board
                        newBoard.remove(currentIndex);
                        newBoard.put(lastIndex, value);
                    }
                }
                // if we have seen a number in the row, and they match
                else if (lastValue == value)
                {
                    // increment the value
                    lastValue+= value;
                    // put the new value into the board at the previous index.
                    newBoard.put(lastIndex, lastValue);
                    // remove the current value from the board.
                    newBoard.remove(currentIndex);
                    // perform some cleanup
                    lastFree = lastIndex - dir;
                    lastIndex = -1;
                    lastValue = -1;
                    didMove = true;

                    //afterthought scoring calculation
                    if (lastValue > highScore)
                        highScore = lastValue;
                }
                // If there's no match, but we have at least one free space;
                else if (lastFree > currentIndex ) {
                    newBoard.put(lastFree, newBoard.get(currentIndex));
                    newBoard.remove(currentIndex);
                    // modify where the number is.
                    lastIndex = lastFree;
                    // modify what the number is
                    lastValue = value;
                    // we move the tile as far in our direction as we can, so the last free
                    // space is the location of the last tile minus 1 in that direction.
                    lastFree = lastIndex - dir;
                    didMove = true;
                }
                // if there's no match, we need to do some updates
                else
                {
                    lastValue = value;
                    lastIndex = currentIndex;
                }

            }
            // The current cell is empty
            else
            {
                // and we don't have a lastFree item...
                // we do now.
                if (lastFree < currentIndex)
                {
                    lastFree = currentIndex;
                }

            }
        }
        return didMove;
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
