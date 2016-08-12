package ir.cdesign.travianrises;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


public class MessageAdapter extends ArrayAdapter {

    public JSONArray jsonArray;
    public static MessageAdapter adapter;
    private MainActivity sidebarChat;

    public MessageAdapter(Context context, JSONArray jsonArray) {
        super(context, -1, new String[jsonArray.length()]);
        this.jsonArray = jsonArray;
        sidebarChat = (MainActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if ( sidebarChat.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ) {
            switch (parent.getId()) {
                case R.id.chat_listview:
                    LayoutInflater inflater = (LayoutInflater) getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View rowView = inflater.inflate(R.layout.chat_contact, parent, false);
                    TextView name = (TextView) rowView.findViewById(R.id.contact_name);
                    TextView newmsg = (TextView) rowView.findViewById(R.id.contact_new);
                    try {
                        name.setText(jsonArray.getJSONObject(position).getString("name"));
                        rowView.setTag(jsonArray.getJSONObject(position).getInt("id"));
                        if (jsonArray.getJSONObject(position).getInt("new") != 0)
                            newmsg.setText(String.valueOf(jsonArray.getJSONObject(position).getInt("new")));
                    } catch (JSONException e) {

                    }
                    return rowView;
                case R.id.chat_msgview:

                    LayoutInflater inflater2 = (LayoutInflater) getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View rowView2 = inflater2.inflate(R.layout.chat_contact, parent, false);
                    TextView name2 = (TextView) rowView2.findViewById(R.id.contact_name);
                    TextView newmsg2 = (TextView) rowView2.findViewById(R.id.contact_new);
                    try {
                        name2.setText(jsonArray.getJSONObject(position).getString("msg"));
                        if (jsonArray.getJSONObject(position).getLong("date") != 0)
                            newmsg2.setText(String.valueOf(jsonArray.getJSONObject(position).getInt("new")));
                    } catch (JSONException e) {

                    }
                    return rowView2;
                default:
                    break;
            }
        }
        return new View(sidebarChat);
    }

}
