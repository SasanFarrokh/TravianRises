package ir.cdesign.travianrises;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SidebarHero extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sidebar_hero, container, false);
    }
    @Override
    public void onResume() {
        super.onResume();
        BackgroundTask.showHero();
    }
}
