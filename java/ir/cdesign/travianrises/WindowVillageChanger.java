package ir.cdesign.travianrises;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class WindowVillageChanger extends Fragment {

    GridView gridView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.window_villagechanger, container, false);
    }
    @Override
    public void onResume() {
        super.onResume();
        gridView = (GridView) getActivity().findViewById(R.id.villages_gridview);
        if ( GridAdapter.adapter != null ) {
            gridView.setAdapter(GridAdapter.adapter);
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        BackgroundTask.showVillages();

    }
}
