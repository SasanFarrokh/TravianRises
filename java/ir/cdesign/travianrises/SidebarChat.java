package ir.cdesign.travianrises;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SidebarChat extends Fragment {
    ListView chatContacts;
    BackgroundTask messages;
    View container;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container =  inflater.inflate(R.layout.sidebar_chat, container, false);
        return this.container;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        chatContacts = (ListView) getActivity().findViewById(R.id.chat_listview);
        ProgressBar progressBar = (ProgressBar) container.findViewById(R.id.chat_loading);
        chatContacts.setEmptyView(progressBar);
        if ( MessageAdapter.adapter != null ) {
            chatContacts.setAdapter(MessageAdapter.adapter);
        }
        messages = new BackgroundTask(getActivity());
        messages.execute("messages.php", "4");
        chatContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                View textView = view.findViewById(R.id.contact_name);
                Object mId = view.getTag();
                if (textView instanceof TextView && mId instanceof Integer) {
                    String name = ((TextView) textView).getText().toString();
                    openChat((Integer) mId,name);
                }
            }
        });
    }
    public void openChat(int id,String name) {

        (new BackgroundTask(getActivity())).execute("messages.php?id="+id, "5");
        ViewGroup container = (ViewGroup) getView();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.chat_message, container, false);
        ListView chats = (ListView) layout.findViewById(R.id.chat_msgview);
        TextView contactName = (TextView) layout.findViewById(R.id.contact_name);
        final Button closeChat = (Button) layout.findViewById(R.id.close_chat);
        closeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeChat();
            }
        });
        contactName.setText(name);
        chats.setEmptyView(layout.findViewById(R.id.chat_loading));
        container.removeAllViews();
        container.addView(layout);
    }
    public void closeChat() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SidebarChat sidebarChat = new SidebarChat();
        fragmentTransaction.replace(R.id.sidebar, sidebarChat);
        fragmentTransaction.commit();
    }
}
