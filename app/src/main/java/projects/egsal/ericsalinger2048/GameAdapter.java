package projects.egsal.ericsalinger2048;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by egsal on 8/19/15.
 */
public class GameAdapter extends ArrayAdapter {


    private List<Integer> ints;
    private Context context;

    public GameAdapter(Context ctx, int resource, List <Integer> objects)
    {
        super(ctx, resource, objects);
        context = ctx;
        ints = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Integer item = ints.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.game_tile, null);
        }

        if (item != null && item != 0) {

            ((TextView) convertView).setText(ints.get(position).toString());
            ((TextView) convertView).setTextColor(Color.WHITE);
            ((TextView) convertView).setBackgroundColor(Color.DKGRAY);
        }
        else if (item != null && item == 0)
        {
            ((TextView) convertView).setBackgroundColor(Color.WHITE);
            ((TextView) convertView).setText("");
        }
        return convertView;
    }

    public void update(ArrayList<Integer> board)
    {
        super.clear();
        ints.clear();
        ints.addAll(board);
        notifyDataSetChanged();
    }
}
