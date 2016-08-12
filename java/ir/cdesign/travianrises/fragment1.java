package ir.cdesign.travianrises;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

public class fragment1 extends Fragment {

    View upgradeBtn;
    View infoBtn;
    GestureDetector gesture;
    RelativeLayout container;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gesture = new GestureDetector(getActivity(),new GestureListener());
        return inflater.inflate(R.layout.fragment1, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        BackgroundTask.showMain();

        /*container = (RelativeLayout) getView().findViewById(R.id.resources_container);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gesture.onTouchEvent(event);
                return true;
            }
        });
*/

        upgradeBtn = getView().findViewById(R.id.build_upgrade);
        infoBtn = getView().findViewById(R.id.build_info);
        upgradeBtn.setTranslationY(100f);
        infoBtn.setTranslationY(100f);
        upgradeBtn.setAlpha(0f);
        infoBtn.setAlpha(0f);

        ToggleButton field1 = (ToggleButton) getView().findViewById(R.id.field_1);

        field1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {

                    showUpgradeBtn(buttonView);

                } else {

                    hideUpgradeBtn();
                }
            }
        });
    }
    public void showUpgradeBtn(final View vBtn) {
        upgradeBtn.animate().setDuration(200).yBy(-100f).alpha(1.0f).start();
        infoBtn.animate().setDuration(300).yBy(-100f).alpha(1.0f).start();
        upgradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public void hideUpgradeBtn() {
        upgradeBtn.animate().yBy(100f).alpha(0.2f).start();
        infoBtn.animate().yBy(100f).alpha(0.2f).start();
    }

    private class GestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}
