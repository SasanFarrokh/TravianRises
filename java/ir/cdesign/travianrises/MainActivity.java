package ir.cdesign.travianrises;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ToggleButton;



public class MainActivity extends Activity {

    public final static String PREF = "pref";
    public final static String MVOLUME = "MVolume";
    public static final String SVOLUME = "SVolume";

    public static SharedPreferences pref;

    BackgroundTask main;
    public MediaPlayer mediaPlayer;
    SoundPool soundPool = new SoundPool(10,AudioManager.STREAM_MUSIC,1);
    int clickSound;
    float soundfxVol = 1.0f;

    ToggleButton selectedTab;
    View sidebar,sidebarCon, sidebar2, sidebar3, sidebar4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_main);

        pref = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        mediaPlayer = MediaPlayer.create(this,R.raw.bgmusic);
        float mVolume = pref.getFloat(MVOLUME,3.0f);
        soundfxVol = pref.getFloat(SVOLUME,3.0f);
        if ( mVolume > 1 || soundfxVol > 1 ) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putFloat(MVOLUME,0.5f);
            editor.putFloat(SVOLUME,1.0f);
            editor.apply();
            mediaPlayer.setVolume(0.5f,0.5f);
        } else {
            mediaPlayer.setVolume(mVolume,mVolume);
        }
        mediaPlayer.start();

        this.tabClick(findViewById(R.id.res_btn));
        main = new BackgroundTask(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build())
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        }


    }
    @Override
    protected void onStart() {
        super.onStart();
        sidebar = findViewById(R.id.sidebar_btn);
        sidebar2 = findViewById(R.id.sidebar_btn2);
        sidebar3 = findViewById(R.id.sidebar_btn3);
        sidebar4 = findViewById(R.id.sidebar_btn4);
        sidebarCon = findViewById(R.id.sidebar);
        sidebar.setOnTouchListener(sidebarAction);
        sidebar2.setOnTouchListener(sidebarAction);
        sidebar3.setOnTouchListener(sidebarAction);
        sidebar4.setOnTouchListener(sidebarAction);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();

        clickSound = soundPool.load(this,R.raw.simple_click,1);
        (new BackgroundTask(this)).execute("messages.php", "4");
    }

    @Override
    protected void onDestroy() {
        soundPool.release();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    public void tabClick(View v) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()) {

            case R.id.res_btn:
                fragment1 fragment1 = new fragment1();
                fragmentTransaction.replace(R.id.mainFrag,fragment1);
                fragmentTransaction.commit();

                break;
            case R.id.vil_btn:
                fragment2 fragment2 = new fragment2();
                fragmentTransaction.replace(R.id.mainFrag,fragment2);
                fragmentTransaction.commit();

                break;
            case R.id.map_btn:
                fragment3 fragment3 = new fragment3();
                fragmentTransaction.replace(R.id.mainFrag,fragment3);
                fragmentTransaction.commit();

                break;
            case R.id.rank_btn:
                fragment4 fragment4 = new fragment4();
                fragmentTransaction.replace(R.id.mainFrag,fragment4);
                fragmentTransaction.commit();

                break;
            case R.id.notice_btn:
                fragment5 fragment5 = new fragment5();
                fragmentTransaction.replace(R.id.mainFrag,fragment5);
                fragmentTransaction.commit();

                break;
            default: return;
        }
        if (selectedTab != null) {
            selectedTab.setChecked(false);
        }
        ToggleButton toggleButton = (ToggleButton) v;
        if ( toggleButton.isChecked() ) soundPool.play(clickSound,soundfxVol,soundfxVol,1,0,1);
        toggleButton.setChecked(true);
        selectedTab = toggleButton;
    }
    public void openWindow(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        View framelayout = findViewById(R.id.window);
        switch (v.getId()) {

            case R.id.village_changer:
                WindowVillageChanger villageChanger = new WindowVillageChanger();
                fragmentTransaction.replace(R.id.window, villageChanger);
                fragmentTransaction.commit();
                break;
            case R.id.train_troops:
                WindowTrainTroops trainTroops = new WindowTrainTroops();
                fragmentTransaction.replace(R.id.window, trainTroops);
                fragmentTransaction.commit();
                break;
            case R.id.market:
                WindowMarket market = new WindowMarket();
                fragmentTransaction.replace(R.id.window, market);
                fragmentTransaction.commit();
                break;
            case R.id.alliance:
                WindowAlliance alliance = new WindowAlliance();
                fragmentTransaction.replace(R.id.window, alliance);
                fragmentTransaction.commit();
                break;
            case R.id.quests:
                WindowQuests quests = new WindowQuests();
                fragmentTransaction.replace(R.id.window, quests);
                fragmentTransaction.commit();
                break;
            case R.id.settings:
                WindowSettings settings = new WindowSettings();
                fragmentTransaction.replace(R.id.window, settings);
                fragmentTransaction.commit();
                break;
            default: return;
        }
        if ( framelayout.getVisibility() == View.GONE ) {
            framelayout.setScaleX(1.2f);
            framelayout.setScaleY(1.2f);
            framelayout.setAlpha(0.2f);
            framelayout.animate().setDuration(200).scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setListener(null).start();
            framelayout.setVisibility(View.VISIBLE);
        }
    }

    public void closeWindow(View v) {
        final View framelayout = findViewById(R.id.window);
        framelayout.animate().setDuration(300).alpha(0.1f).scaleX(1.2f).scaleY(1.2f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                framelayout.setVisibility(View.GONE);
            }
        }).start();

    }

    private View.OnTouchListener sidebarAction =  new View.OnTouchListener() {
        float downX = 0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN  ) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (v.getId()) {

                    case R.id.sidebar_btn4:

                        SidebarChat sidebarChat = new SidebarChat();
                        fragmentTransaction.replace(R.id.sidebar, sidebarChat);
                        fragmentTransaction.commit();
                        break;
                    case R.id.sidebar_btn3:

                        SidebarRes sidebarRes = new SidebarRes();
                        fragmentTransaction.replace(R.id.sidebar, sidebarRes);
                        fragmentTransaction.commit();
                        break;
                    case R.id.sidebar_btn2:

                        SidebarTroops sidebarTroops = new SidebarTroops();
                        fragmentTransaction.replace(R.id.sidebar, sidebarTroops);
                        fragmentTransaction.commit();
                        break;
                    case R.id.sidebar_btn:

                        SidebarHero sidebarHero = new SidebarHero();
                        fragmentTransaction.replace(R.id.sidebar, sidebarHero);
                        fragmentTransaction.commit();
                        break;
                    default: return true;
                }
                v.setPressed(true);
                downX = event.getX();
                v.bringToFront();
                MainActivity.this.sidebarCon.bringToFront();
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE  ){
                float X = event.getRawX() - downX - 10;
                if ( X > 307) X = 307;
                else if ( X < 0 ) X = 0;
                v.setX(X);
                MainActivity.this.sidebarCon.setX(X - MainActivity.this.sidebarCon.getWidth()+8);
            }
            if (event.getAction() == MotionEvent.ACTION_UP  ){
                float X = event.getRawX();

                if ( X > 150) X = 307;
                else if ( X <= 150) X = -10;
                if ( event.getEventTime() - event.getDownTime() < 170 ) {
                    if ( X > 150) X = -10;
                    else if ( X < 150) X = 307;
                }
                v.animate().x(X);
                MainActivity.this.sidebarCon.animate().x(X - MainActivity.this.sidebarCon.getWidth()+8);
                v.setPressed(false);
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            return true;
        }
    };


}
