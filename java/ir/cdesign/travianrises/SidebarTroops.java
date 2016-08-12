package ir.cdesign.travianrises;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SidebarTroops extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sidebar_troops, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).main.showTroops();
    }
}
