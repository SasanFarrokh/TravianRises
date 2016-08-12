package ir.cdesign.travianrises;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


public class GridAdapter extends BaseAdapter {

    public JSONArray jsonArray;
    public static GridAdapter adapter;
    private MainActivity window;

    public GridAdapter(Context context, JSONArray jsonArray) {
        super();
        this.jsonArray = jsonArray;
        window = (MainActivity) context;
    }
    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ((Context) window)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup rowView = (ViewGroup) inflater.inflate(R.layout.villages_grid_tiles, parent, false);
        TextView name = (TextView) rowView.getChildAt(1);
        ImageView imageView = (ImageView) rowView.getChildAt(0);
        try {
            name.setText(jsonArray.getJSONObject(position).getString("name"));
            rowView.setTag(jsonArray.getJSONObject(position).getInt("id"));
            imageView.setImageResource(R.drawable.ic_optionsbar_resources);
        } catch (JSONException e) {

        }
        return rowView;
    }
}
